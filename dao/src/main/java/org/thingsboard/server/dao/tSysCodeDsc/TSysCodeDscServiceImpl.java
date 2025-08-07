package org.thingsboard.server.dao.tSysCodeDsc;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysCodeDscDto;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.vo.AppVersionVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TSysCodeDscServiceImpl implements TSysCodeDscService {
    @Autowired
    TSysCodeDscRepository tSysCodeDscRepository;


    @Override
    public List<TSysCodeDsc> tSysCodeDscGroupList() {
        List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findByCodeClId("GROUPCODE0000");
        return tSysCodeDscList;
    }

    @Override
    public void saveTSysCodeDsc(TSysCodeDsc tSysCodeDsc) {
        if (tSysCodeDsc.getCodeId() == null) {
            //新增
            tSysCodeDsc.setCrtUser(tSysCodeDsc.getUpdateUser());
            tSysCodeDsc.setCrtTime(tSysCodeDsc.getUpdateTime());
            if (StringUtils.isBlank(tSysCodeDsc.getEnabledSt())) {
                tSysCodeDsc.setEnabledSt("0");
            }

        }
        tSysCodeDscRepository.saveAndFlush(tSysCodeDsc);
    }

    @Transactional
    @Override
    public void saveCodeCl(TSysCodeDsc tSysCodeDsc) {
        if ("".equals(tSysCodeDsc.getCodeValue()) || tSysCodeDsc.getCodeValue() == null) {
            throw new RuntimeException("编码不能为空值或空字符串");
        }
        //同一分组下，字典编码不能重复
        TSysCodeDsc findTSysCodeDsc = this.getCodeByCodeClAndCodeVale("GROUPCODE0000", tSysCodeDsc.getCodeValue());
        if (findTSysCodeDsc != null) {
            if (findTSysCodeDsc.getCodeId().intValue() == tSysCodeDsc.getCodeId().intValue()) {
                throw new RuntimeException("同一分组下，字典编码不能重复");
            }
        }
        //是否字典分类
        tSysCodeDsc.setCodeClDsc("字典分类");
        tSysCodeDsc.setCodeClId("GROUPCODE0000");
        tSysCodeDsc.setIsGroup(1);
        this.saveTSysCodeDsc(tSysCodeDsc);
        //字典变动更新缓存
        TSysCodeDscDto tSysCodeDscDto=new TSysCodeDscDto();
        tSysCodeDscDto.setEnabledSt(GlobalConstant.enableTrue);
        List<TSysCodeDsc> content = this.tSysCodeDscList(0, Integer.MAX_VALUE, tSysCodeDscDto).getContent();
        GlobalConstant.setCodeDscTable(content);
    }

    @Transactional
    @Override
    public void saveCode(TSysCodeDsc tSysCodeDsc) {
        if (tSysCodeDsc.getCodeClDsc() == null || tSysCodeDsc.getCodeClId() == null) {
            if (StringUtils.isBlank(tSysCodeDsc.getCodeClDsc()) || StringUtils.isBlank(tSysCodeDsc.getCodeClId())) {
                throw new RuntimeException("字典分组编码和字典分组描述不能为空");
            }
        }
        //同一分组下，字典编码不能重复
        TSysCodeDsc findTSysCodeDsc = this.getCodeByCodeClAndCodeDsc(tSysCodeDsc.getCodeClId(), tSysCodeDsc.getCodeDsc());
        if (findTSysCodeDsc != null) {
            if (findTSysCodeDsc.getCodeId().intValue() != tSysCodeDsc.getCodeId().intValue()) {
                throw new RuntimeException("同一分组下，字典编码不能重复");
            }
        }
        tSysCodeDsc.setIsGroup(0);
        this.saveTSysCodeDsc(tSysCodeDsc);
        //字典变动更新缓存
        TSysCodeDscDto tSysCodeDscDto=new TSysCodeDscDto();
        tSysCodeDscDto.setEnabledSt(GlobalConstant.enableTrue);
        List<TSysCodeDsc> content = this.tSysCodeDscList(0, Integer.MAX_VALUE, tSysCodeDscDto).getContent();
        GlobalConstant.setCodeDscTable(content);
    }

    @Override
    public void deleteTSysCodeDsc(Integer codeId) {
        tSysCodeDscRepository.deleteById(codeId);
    }


    @Transactional
    @Override
    public void deleteCode(Integer codeId) {
        TSysCodeDsc tSysCodeDsc = this.getCodeById(codeId);
        if (null == tSysCodeDsc) {
            throw new RuntimeException("字典不存在");
        }
        if (tSysCodeDsc.getIsGroup().intValue() == 1) {
            var list = tSysCodeDscRepository.findByCodeClId(tSysCodeDsc.getCodeValue());
            if (null != list && list.size() > 0) {
                throw new RuntimeException("删除字典分类时，请先删除字典分类中的字典项");
            }
        }
        this.deleteTSysCodeDsc(codeId);
        //字典变动更新缓存
        TSysCodeDscDto tSysCodeDscDto=new TSysCodeDscDto();
        tSysCodeDscDto.setEnabledSt(GlobalConstant.enableTrue);
        List<TSysCodeDsc> content = this.tSysCodeDscList(0, Integer.MAX_VALUE, tSysCodeDscDto).getContent();
        GlobalConstant.setCodeDscTable(content);
    }

    @Override
    public Page<TSysCodeDsc> tSysCodeDscList(Integer current, Integer size, TSysCodeDscDto tSysCodeDscDto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crtTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("codeDsc", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("is_group", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        BeanUtils.copyProperties(tSysCodeDscDto, tSysCodeDsc);
        tSysCodeDsc.setIsGroup(0);
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        Page<TSysCodeDsc> tSysCodeDscPage = tSysCodeDscRepository.findAll(example, pageable);
        return tSysCodeDscPage;
    }

    @Override
    public Page<TSysCodeDsc> getCodeByCodeCl(Integer current, Integer size, String codeClId, String enabledSt) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "crtTime");
        //统一按照字段分组和字典编码从小到大排序
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order sort1 = new Sort.Order(Sort.Direction.ASC, "codeClId");
        Sort.Order sort2 = new Sort.Order(Sort.Direction.ASC, "codeValue");
        orders.add(sort1);
        orders.add(sort2);
        Sort sort = Sort.by(orders);

        Pageable pageable = PageRequest.of(current, size, sort);
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        tSysCodeDsc.setCodeClId(codeClId);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact());
        if (StringUtils.isNotBlank(enabledSt)) {
            tSysCodeDsc.setEnabledSt(enabledSt);
            matcher.withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        }
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        Page<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findAll(example, pageable);
        return tSysCodeDscList;
    }

    @Override
    public Page<TSysCodeDsc> getCodeByCodeClNotJudEt(Integer current, Integer size, String codeClId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crtTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        tSysCodeDsc.setCodeClId(codeClId);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        Page<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findAll(example, pageable);
        return tSysCodeDscList;
    }



    @Override
    public TSysCodeDsc getCodeById(Integer codeId) {
        return tSysCodeDscRepository.findById(codeId).orElse(null);
    }


    @Override
    public TSysCodeDsc getCodeByCodeClAndCodeVale(String codeClId, String codeValue) {

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("codeValue", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        tSysCodeDsc.setCodeClId(codeClId);
        tSysCodeDsc.setCodeValue(codeValue);
//        BeanUtils.copyProperties(tSysCodeDscDto,tSysCodeDsc);
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        Optional<TSysCodeDsc> o = tSysCodeDscRepository.findOne(example);
        TSysCodeDsc codeDsc = null;
        if (!o.isEmpty()) {
            codeDsc = o.get();
        }

        return codeDsc;
    }

    @Override
    public TSysCodeDsc getCodeByCodeClAndCodeDsc(String codeClId, String codeDscStr) {

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("codeDsc", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        tSysCodeDsc.setCodeClId(codeClId);
        tSysCodeDsc.setCodeValue(codeDscStr);
//        BeanUtils.copyProperties(tSysCodeDscDto,tSysCodeDsc);
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        Optional<TSysCodeDsc> o = tSysCodeDscRepository.findOne(example);
        TSysCodeDsc codeDsc = null;
        if (!o.isEmpty()) {
            codeDsc = o.get();
        }

        return codeDsc;
    }

    @Override
    public List<TSysCodeDsc> getCodeByCodeClId(String codeClId) {
        return tSysCodeDscRepository.findByCodeClIdAndEnabledSt(codeClId, GlobalConstant.enableTrue);
    }

    @Override
    public ResponseResult<AppVersionVo> getAPPVersion() {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        tSysCodeDsc.setCodeClId("APPVERSION0000");
        tSysCodeDsc.setEnabledSt("1");
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        List<TSysCodeDsc> codeDscs = tSysCodeDscRepository.findAll(example);
        AppVersionVo versionVo=new AppVersionVo();
        for (TSysCodeDsc codeDsc : codeDscs) {
            String codeValue=codeDsc.getCodeValue();
            JSONObject jsonObject=JSONObject.parseObject(codeValue);
            if(jsonObject.getString("key").equals("vrsnCode")){
                versionVo.setVrsnCode(jsonObject.getString("value"));
            }
            if(jsonObject.getString("key").equals("remark")){
                versionVo.setRemark(jsonObject.getString("value"));
            }
            if(jsonObject.getString("key").equals("upgFile")){
                versionVo.setUpgFile(jsonObject.getString("value"));
            }

        }
        return ResultUtil.success(versionVo);
    }


}
