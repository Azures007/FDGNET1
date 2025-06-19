/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.common.msg.tools.TbRateLimitsException;
import org.thingsboard.server.service.security.exception.AuthMethodNotSupportedException;
import org.thingsboard.server.service.security.exception.JwtExpiredTokenException;
import org.thingsboard.server.service.security.exception.UserPasswordExpiredException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ThingsboardErrorResponseHandler extends ResponseEntityExceptionHandler implements AccessDeniedHandler {

    private static final Map<HttpStatus, ThingsboardErrorCode> statusToErrorCodeMap = new HashMap<>();
    static {
        statusToErrorCodeMap.put(HttpStatus.BAD_REQUEST, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        statusToErrorCodeMap.put(HttpStatus.UNAUTHORIZED, ThingsboardErrorCode.AUTHENTICATION);
        statusToErrorCodeMap.put(HttpStatus.FORBIDDEN, ThingsboardErrorCode.PERMISSION_DENIED);
        statusToErrorCodeMap.put(HttpStatus.NOT_FOUND, ThingsboardErrorCode.ITEM_NOT_FOUND);
        statusToErrorCodeMap.put(HttpStatus.METHOD_NOT_ALLOWED, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        statusToErrorCodeMap.put(HttpStatus.NOT_ACCEPTABLE, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        statusToErrorCodeMap.put(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        statusToErrorCodeMap.put(HttpStatus.TOO_MANY_REQUESTS, ThingsboardErrorCode.TOO_MANY_REQUESTS);
        statusToErrorCodeMap.put(HttpStatus.INTERNAL_SERVER_ERROR, ThingsboardErrorCode.GENERAL);
        statusToErrorCodeMap.put(HttpStatus.SERVICE_UNAVAILABLE, ThingsboardErrorCode.GENERAL);
    }
    private static final Map<ThingsboardErrorCode, HttpStatus> errorCodeToStatusMap = new HashMap<>();
    static {
        errorCodeToStatusMap.put(ThingsboardErrorCode.GENERAL, HttpStatus.INTERNAL_SERVER_ERROR);
        errorCodeToStatusMap.put(ThingsboardErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED);
        errorCodeToStatusMap.put(ThingsboardErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        errorCodeToStatusMap.put(ThingsboardErrorCode.CREDENTIALS_EXPIRED, HttpStatus.UNAUTHORIZED);
        errorCodeToStatusMap.put(ThingsboardErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        errorCodeToStatusMap.put(ThingsboardErrorCode.INVALID_ARGUMENTS, HttpStatus.BAD_REQUEST);
        errorCodeToStatusMap.put(ThingsboardErrorCode.BAD_REQUEST_PARAMS, HttpStatus.BAD_REQUEST);
        errorCodeToStatusMap.put(ThingsboardErrorCode.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND);
        errorCodeToStatusMap.put(ThingsboardErrorCode.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS);
        errorCodeToStatusMap.put(ThingsboardErrorCode.TOO_MANY_UPDATES, HttpStatus.TOO_MANY_REQUESTS);
        errorCodeToStatusMap.put(ThingsboardErrorCode.SUBSCRIPTION_VIOLATION, HttpStatus.FORBIDDEN);
    }

    private static ThingsboardErrorCode statusToErrorCode(HttpStatus status) {
        return statusToErrorCodeMap.getOrDefault(status, ThingsboardErrorCode.GENERAL);
    }

    private static HttpStatus errorCodeToStatus(ThingsboardErrorCode errorCode) {
        return errorCodeToStatusMap.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Autowired
    private ObjectMapper mapper;
    private static final String YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION = "You don't have permission to perform this operation!";

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseResult dataIntegrityViolationException(DataIntegrityViolationException e){
        if (e.getCause() instanceof ConstraintViolationException){
            Throwable cause = e.getCause().getCause();
            if(cause instanceof PSQLException){
                System.out.println(cause.getMessage());

                String message = cause.getMessage();
                String sub="Key";
                String val= message.substring(message.indexOf(sub));
                System.out.println(val);
                logger.error(message);
                logger.error("，删除前请先删除相关数据");
                return ResultUtil.error("该数据已被其他数据关联使用，删除前请先删除相关数据");
            }
        }
        log.error("Processing exception ", e);
        return ResultUtil.error("数据库执行异常：" + e.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseResult runTimeException(Exception e, HttpServletResponse response) throws IOException, ThingsboardException {
        if(e.getMessage().equals("token jy")){
            return ResultUtil.TokenErr();
        }
        if(e.getMessage().equals("token sx")){
            return ResultUtil.tokenErrBySx();
        }
        if(e.getMessage().equals("token No Class")){
            return ResultUtil.noClass();
        }
        log.error("Processing exception ", e);
        return ResultUtil.error(e.getMessage());
    }

    @Override
    @ExceptionHandler(AccessDeniedException.class)
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException{
        if (!response.isCommitted()) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            mapper.writeValue(response.getWriter(),
                    ThingsboardErrorResponse.of("You don't have permission to perform this operation!",
                            ThingsboardErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN));
        }
    }

    @ExceptionHandler(Exception.class)
    public void handle(Exception exception, HttpServletResponse response) {
        log.debug("Processing exception {}", exception.getMessage(), exception);
        if (!response.isCommitted()) {
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                if (exception instanceof ThingsboardException) {
                    ThingsboardException thingsboardException = (ThingsboardException) exception;
                    if (thingsboardException.getErrorCode() == ThingsboardErrorCode.SUBSCRIPTION_VIOLATION) {
                        handleSubscriptionException((ThingsboardException) exception, response);
                    } else {
                        handleThingsboardException((ThingsboardException) exception, response);
                    }
                } else if (exception instanceof TbRateLimitsException) {
                    handleRateLimitException(response, (TbRateLimitsException) exception);
                } else if (exception instanceof AccessDeniedException) {
                    handleAccessDeniedException(response);
                } else if (exception instanceof AuthenticationException) {
                    handleAuthenticationException((AuthenticationException) exception, response);
                } else {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    mapper.writeValue(response.getWriter(), ThingsboardErrorResponse.of(exception.getMessage(),
                            ThingsboardErrorCode.GENERAL, HttpStatus.INTERNAL_SERVER_ERROR));
                }
            } catch (IOException e) {
                log.error("Can't handle exception", e);
            }
        }
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NotNull Exception ex, @Nullable Object body,
            @NotNull HttpHeaders headers, @NotNull HttpStatus status,
            @NotNull WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        ThingsboardErrorCode errorCode = statusToErrorCode(status);
        return new ResponseEntity<>(ThingsboardErrorResponse.of(ex.getMessage(), errorCode, status), headers, status);
    }

    private void handleThingsboardException(ThingsboardException thingsboardException, HttpServletResponse response) throws IOException {
        ThingsboardErrorCode errorCode = thingsboardException.getErrorCode();
        HttpStatus status = errorCodeToStatus(errorCode);
        response.setStatus(status.value());
        mapper.writeValue(response.getWriter(), ThingsboardErrorResponse.of(thingsboardException.getMessage(), errorCode, status));
    }

    private void handleRateLimitException(HttpServletResponse response, TbRateLimitsException exception) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        String message = "Too many requests for current " + exception.getEntityType().name().toLowerCase() + "!";
        mapper.writeValue(response.getWriter(),
                ThingsboardErrorResponse.of(message,
                        ThingsboardErrorCode.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS));
    }

    private void handleSubscriptionException(ThingsboardException subscriptionException, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        mapper.writeValue(response.getWriter(),
                (new ObjectMapper()).readValue(((HttpClientErrorException) subscriptionException.getCause()).getResponseBodyAsByteArray(), Object.class));
    }

    private void handleAccessDeniedException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        mapper.writeValue(response.getWriter(),
                ThingsboardErrorResponse.of("You don't have permission to perform this operation!",
                        ThingsboardErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN));

    }

    private void handleAuthenticationException(AuthenticationException authenticationException, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (authenticationException instanceof BadCredentialsException || authenticationException instanceof UsernameNotFoundException) {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            log.error("登录账号或密码错误");
            mapper.writeValue(response.getWriter(), ThingsboardErrorResponse.of("登录账号或密码错误", ThingsboardErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof DisabledException) {
            mapper.writeValue(response.getWriter(), ThingsboardErrorResponse.of("User account is not active", ThingsboardErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof LockedException) {
            mapper.writeValue(response.getWriter(), ThingsboardErrorResponse.of("User account is locked due to security policy", ThingsboardErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof JwtExpiredTokenException) {
            mapper.writeValue(response.getWriter(), ThingsboardErrorResponse.of("您的登陆信息已失效，请重新登陆！", ThingsboardErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof AuthMethodNotSupportedException) {
            mapper.writeValue(response.getWriter(), ThingsboardErrorResponse.of(authenticationException.getMessage(), ThingsboardErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof UserPasswordExpiredException) {
            UserPasswordExpiredException expiredException = (UserPasswordExpiredException) authenticationException;
            String resetToken = expiredException.getResetToken();
            mapper.writeValue(response.getWriter(), ThingsboardCredentialsExpiredResponse.of(expiredException.getMessage(), resetToken));
        } else {
            mapper.writeValue(response.getWriter(), ThingsboardErrorResponse.of("Authentication failed", ThingsboardErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        }
    }


}
