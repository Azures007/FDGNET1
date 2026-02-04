package org.thingsboard.server.dao.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * NC接口调用客户端
 * 提供统一的NC接口调用和token管理功能
 */
@Slf4j
@Component
public class NcApiClient {
    
    @Value("${nc.base-url:http://172.88.0.150:8077}")
    private String ncBaseUrl;
    
    @Value("${nc.app-id:yc9t8188f4e0j2ce13}")
    private String ncAppId;
    
    @Value("${nc.app-secret:e6eed684852d619a5292c4753628ed56}")
    private String ncAppSecret;
    
    private static final String GET_TOKEN_PATH = "/api/ycnc/mes/config/gettoken";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Token缓存
    private String cachedToken;
    private long tokenExpireTime;
    
    /**
     * 获取NC接口token（带缓存机制）
     * @return token
     */
    public String getToken() {
        // 检查缓存的token是否有效
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return cachedToken;
        }

        try {
            // 构建获取token的请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("appId", ncAppId);
            requestBody.put("appSecret", ncAppSecret);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 调用获取token接口
            String tokenUrl = ncBaseUrl + GET_TOKEN_PATH;
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    tokenUrl,
                    requestEntity,
                    JsonNode.class
            );

            // 处理响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseBody = response.getBody();
                int code = responseBody.has("code") ? responseBody.get("code").asInt() : -1;
                String msg = responseBody.has("msg") ? responseBody.get("msg").asText() : "";

                if (code == 1 && responseBody.has("data")) {
                    JsonNode data = responseBody.get("data");
                    if (data.has("token")) {
                        String token = data.get("token").asText();
                        // 获取有效期（秒），默认7200秒，提前5分钟过期
                        int expiresIn = data.has("expires_in") ? data.get("expires_in").asInt() : 7200;
                        // 计算过期时间（提前5分钟过期，避免边界情况）
                        tokenExpireTime = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
                        cachedToken = token;
                        log.info("成功获取NC接口token，有效期: {}秒", expiresIn);
                        return token;
                    }
                }
                String errorMsg = String.format("获取NC接口token失败，错误信息: %s", msg);
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            } else {
                String errorMsg = String.format("获取NC接口token失败，HTTP状态码: %s", response.getStatusCode());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        } catch (Exception e) {
            log.error("调用获取NC接口token异常", e);
            throw new RuntimeException("调用获取NC接口token异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 调用NC接口（POST方法）
     * @param path 接口路径（如：/api/ycnc/mes/mm/bad/stock/data/list）
     * @param requestBody 请求体
     * @return 响应的JsonNode
     */
    public JsonNode post(String path, Map<String, Object> requestBody) {
        try {
            // 获取token
            String token = getToken();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token", token);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 调用接口
            String url = ncBaseUrl + path;
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    JsonNode.class
            );
            
            // 处理响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                log.warn("调用NC接口失败，HTTP状态码: {}, 路径: {}", response.getStatusCode(), path);
                return null;
            }
        } catch (Exception e) {
            log.error("调用NC接口异常，路径: {}", path, e);
            return null;
        }
    }
    
    /**
     * 调用NC接口并检查返回码
     * @param path 接口路径
     * @param requestBody 请求体
     * @return 如果code=1，返回data节点；否则返回null
     */
    public JsonNode postAndGetData(String path, Map<String, Object> requestBody) {
        JsonNode responseBody = post(path, requestBody);
        if (responseBody != null) {
            int code = responseBody.has("code") ? responseBody.get("code").asInt() : -1;
            if (code == 1 && responseBody.has("data")) {
                return responseBody.get("data");
            } else {
                String msg = responseBody.has("msg") ? responseBody.get("msg").asText() : "";
                log.warn("NC接口返回失败，路径: {}, 错误信息: {}", path, msg);
            }
        }
        return null;
    }
    
    /**
     * 获取NC基础URL
     * @return NC基础URL
     */
    public String getNcBaseUrl() {
        return ncBaseUrl;
    }
    
    /**
     * 获取ObjectMapper实例
     * @return ObjectMapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    /**
     * 获取RestTemplate实例
     * @return RestTemplate
     */
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
