package org.thingsboard.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import javassist.bytecode.SignatureAttribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thingsboard.server.common.data.TSysLog;
import org.thingsboard.server.common.data.TSysPersonnelInfo;
import org.thingsboard.server.common.data.TSysRoleUser;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.sql.log.LogRepository;
import org.thingsboard.server.dao.sql.role.RoleUserRepository;
import org.thingsboard.server.dao.sql.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * token拦截器
 */
@Slf4j
@Aspect
@Component
public class UserAspect {

    @Autowired
    RoleUserRepository roleUserRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Autowired
    LogRepository logRepository;

    @Autowired
    private ObjectMapper mapper;

    @Value(value = "${log.enabled:0}")
    private String logEnabled;


    @Value(value = "${server.port}")
    private String port;

    @Value(value = "${server.mes_port}")
    private String mesPort;


    @Pointcut("execution(public * org.thingsboard.server.controller.*.*(..))")
    public void pointCut() {
    }


    protected SecurityUser getCurrentUser() throws ThingsboardException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        } else {
            throw new ThingsboardException("You aren't authorized to perform this operation!", ThingsboardErrorCode.AUTHENTICATION);
        }
    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) throws ThingsboardException, IOException, NoSuchMethodException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        String appKey = request.getHeader("app_key");
        if (StringUtils.isBlank(appKey)) {
            return;
        }
        // 获取当前链接
        String path = request.getRequestURL().toString();
        int i = path.indexOf(":" + port) == -1 ? -1 : path.indexOf(":" + port) + port.length() + 1;
        if (i == -1) {
            i = path.indexOf(":" + mesPort) + mesPort.length() + 1;
        }
        String substring = path.substring(i, i + 4);
        if (!substring.equals("/api")) {
            return;
        }
        SecurityUser currentUser = getCurrentUser();
        String userId = String.valueOf(currentUser.getId().getId());

        String tokenKey = request.getHeader(GlobalConstant.TOKEN_KEY);
        tokenKey = tokenKey.substring(GlobalConstant.TOKEN_HEARHER.length());
        //判断token有效性
//        isTokenValid(userId, tokenKey, appKey.equals("0"), response);
        //判断是否禁用
        isDisable(userId, appKey.equals("0"), response);
        if (appKey.equals("0")) {
            //判断是否绑定工序
            isBanClass(userId);
        }
        if ("1".equals(logEnabled)) {
            //记录操作日志
            addLog(joinPoint, currentUser);
        }
    }

    //记录操作日志
    private void addLog(JoinPoint joinPoint, SecurityUser currentUser) throws NoSuchMethodException {
        log.info("---------记录用户操作日志---------------");
        TSysLog tSysLog = new TSysLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Class<?> targetCls = joinPoint.getTarget().getClass();
        //获取方法签名(通过此签名获取目标方法信息)
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        //获取目标方法名(目标类型+方法名)
        String targetClsName = targetCls.getName();
        String name = ms.getName();
        String targetObjectMethodName = targetClsName + "." + name;
        tSysLog.setWay(targetObjectMethodName);
        //获取请求参数
        String targetMethodParams = Arrays.toString(joinPoint.getArgs());
        Method method = targetCls.getDeclaredMethod(name, ms.getParameterTypes());
        tSysLog.setInParam(targetMethodParams);
        ApiOperation annotation = method.getAnnotation(ApiOperation.class);
        if (annotation != null) {
            tSysLog.setMethods(annotation.value());
            if(annotation.value().equals("获取未读读消息接口")){
                return;
            }
        } else {
            log.info("------------系统日志不记录----------------");
            return;
        }
        tSysLog.setIp(getIpAddr(request));
        tSysLog.setCreatedName(currentUser.getFirstName());
        tSysLog.setCreatedUsername(currentUser.getName());
        tSysLog.setCreatedTime(new Date());
        tSysLog.setUserId(currentUser.getId().getId().toString());
        try {
            logRepository.save(tSysLog);
        }catch (Exception e){
            log.error("日志记录出错："+tSysLog);
        }
        log.info("用户信息：" + tSysLog);
        log.info("------------用户行为日志保存成功----------------");
    }

    //判断用户是否绑定帮组
    private void isBanClass(String userId) {
        TSysPersonnelInfo tSysPersonnelInfo = tSysPersonnelInfoRepository.getByUserId(userId);
        if (tSysPersonnelInfo == null) {
            throw new RuntimeException("token No Class");
        }
    }

    //禁用处理
    private void isDisable(String userId, Boolean flag, HttpServletResponse response) throws IOException {
        TSysRoleUser byUserId = roleUserRepository.getByUserId(userId);
        if (byUserId == null) {
            throw new RuntimeException("token jy");
        }
        if (byUserId.getUserStatus().equals(GlobalConstant.enableFalse)) {
            if (flag) {
                throw new RuntimeException("token jy");
            } else {
                throw new RuntimeException("token jy");
            }
        }
    }

    //token顺延
    private void isTokenValid(String userId, String token, Boolean flag, HttpServletResponse response) throws IOException {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object val = valueOperations.get(GlobalConstant.LOGIO_REDIS_KEY + userId + ":");
        if (val == null) {
            if (flag) {
                throw new RuntimeException("token sx");
            } else {
                throw new RuntimeException("token sx");
            }
        } else {
            if (val.toString().equals(token)) {
                valueOperations.set(GlobalConstant.LOGIO_REDIS_KEY + userId + ":", token, 7200, TimeUnit.SECONDS);
            } else {
                if (flag) {
                    throw new RuntimeException("token sx");
                } else {
                    throw new RuntimeException("token sx");
                }
            }
        }
    }

    /**
     * 获取当前网络ip
     *
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

}
