package org.thingsboard.server.dao.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.json.JSONException;
//import org.json.JSONObject;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.annotation.Dict;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.json.JSONObject;
//import com.zyxx.common.utils.LayTableResult;
//import com.zyxx.common.utils.ObjConvertUtils;

/**
 * 数据字典切面
 *
 * @author cms
 * @date 2022/4/22
 */
@Aspect
@Component
@Slf4j
public class DictAspect {

    /**
     * 字典后缀
     */
    private static String DICT_TEXT_SUFFIX = "Text";

    @Autowired
    private TSysCodeDscService sysCodeDscService;

    /**
     * 切点，切入 controller 包下面的所有方法
     */
    @Pointcut("execution( * org.thingsboard.*.controller.*.*(..))")
    public void dict() {

    }

    @Around("dict()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long time1 = System.currentTimeMillis();
        Object result = pjp.proceed();
        long time2 = System.currentTimeMillis();
        log.debug("获取JSON数据 耗时：" + (time2 - time1) + "ms");
        long start = System.currentTimeMillis();
        this.parseDictText(result);
        long end = System.currentTimeMillis();
        log.debug("解析注入JSON数据  耗时" + (end - start) + "ms");
        return result;
    }

    private void parseDictText(Object result) throws JSONException {
        //考虑返回参数不是ResponseResult的情况
        if (result instanceof ResponseResult) {
            List<JSONObject> items = new ArrayList<>();
            ResponseResult rr = (ResponseResult) result;

            if (rr.getData() instanceof PageImpl) {
                PageImpl data = (PageImpl)rr.getData();
                List<?> list = data.getContent();
//                List<?> list = (List<?>) rr.getData();
                for (Object record : list) {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = "{}";
                    try {
                        // 解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                        json = mapper.writeValueAsString(record);
                    } catch (JsonProcessingException e) {
                        log.error("Json解析失败：" + e);
                    }
//                    JSONObject item = new JSONObject(json);
                JSONObject item = JSONObject.parseObject(json);
                    // 解决继承实体字段无法翻译问题
                    for (Field field : ObjConvertUtils.getAllFields(record)) {
                        //解决继承实体字段无法翻译问题
                        // 如果该属性上面有@Dict注解，则进行翻译
                        if (field.getAnnotation(Dict.class) != null) {
                            // 拿到注解的dictDataSource属性的值
                            String dictType = field.getAnnotation(Dict.class).codeVale();
                            // 拿到注解的dictText属性的值
                            String text = field.getAnnotation(Dict.class).codeDsc();
                            //获取当前带翻译的值
                            String key = String.valueOf(item.get(field.getName()));
                            //翻译字典值对应的text值
                            String textValue = translateDictValue(dictType, key);
                            // DICT_TEXT_SUFFIX的值为，是默认值：
                            // public static final String DICT_TEXT_SUFFIX = "_dictText";
                            log.debug("字典Val: " + textValue);
                            log.debug("翻译字典字段：" + field.getName() + DICT_TEXT_SUFFIX + "： " + textValue);
                            //如果给了文本名
                            if (!StringUtils.isBlank(text)) {
                                item.put(text, textValue);
                            } else {
                                // 走默认策略
                                item.put(field.getName() + DICT_TEXT_SUFFIX, textValue);
                            }
                        }
                        // date类型默认转换string格式化日期
                        if ("java.util.Date".equals(field.getType().getName())
                                && field.getAnnotation(JsonFormat.class) == null
                                && item.get(field.getName()) != null) {
                            SimpleDateFormat aDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            item.put(field.getName(), aDate.format(new Date((Long) item.get(field.getName()))));
                        }
                    }
                    items.add(item);
                }
                PageImpl newData = new PageImpl(items,data.getPageable(),data.getTotalPages());
                rr.setData(newData);
            }

        }else {
            //暂时只对返回的列表数据做翻译
        }
    }

    /**
     * 翻译字典文本
     *
     * @param codeClId
     * @param codeValue
     * @return
     */
    private String translateDictValue(String codeClId,String codeValue) {
        if (ObjConvertUtils.isEmpty(codeValue)) {
            return null;
        }
        StringBuffer textValue = new StringBuffer();
        String[] keys = codeValue.split(",");
        for (String k : keys) {
            if (k.trim().length() == 0) {
                continue;
            }
            /**
             * 根据 dictCode 和 code 查询字典值，例如：dictCode:sex,code:1，返回:男
             * 应该放在redis，提高响应速度
             */
            TSysCodeDsc tSysCodeDsc = sysCodeDscService.getCodeByCodeClAndCodeVale(codeClId, codeValue);
            if (tSysCodeDsc.getCodeDsc() != null) {
                if (!"".equals(textValue.toString())) {
                    textValue.append(",");
                }
                textValue.append(tSysCodeDsc.getCodeDsc());
            }
            log.info("数据字典翻译: 字典类型：{}，当前翻译值：{}，翻译结果：{}", codeClId, k.trim(), tSysCodeDsc.getCodeDsc());
        }
        return textValue.toString();
    }
}
