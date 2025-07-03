package org.thingsboard.server.common.data.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/6/27 11:53:26
 */

/**
 * JSON处理工具类，提供JSON与Java对象之间的序列化和反序列化功能
 *
 * 该工具类封装了Jackson和Gson的核心功能，提供统一的接口处理JSON数据
 * 主要特点：
 * 1. 配置了默认的ObjectMapper，支持Java 8日期时间类型
 * 2. 提供了灵活的JSON转换方法，处理各种数据类型
 * 3. 支持美化输出JSON字符串
 * 4. 处理JSON转换过程中的异常情况
 */
public class JacksonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    /** 标准日期时间格式 */
    public static final String STANDARD_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /** 用于常规JSON处理的ObjectMapper */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** 用于忽略空值的JSON序列化的ObjectMapper */
    public static final ObjectMapper OBJECT_MAPPER_IGNORE_NULLS = new ObjectMapper();

    /** 用于美化输出的ObjectMapper */
    public static final ObjectMapper PRETTY_MAPPER = new ObjectMapper();

    /** Gson实例 */
    public static final Gson GSON = new GsonBuilder().serializeNulls().create();

    /** 美化输出的Gson实例 */
    public static final Gson PRETTY_GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    static {
        // 配置默认的ObjectMapper
        configureMapper(OBJECT_MAPPER);

        // 配置忽略空值的ObjectMapper
        configureMapper(OBJECT_MAPPER_IGNORE_NULLS);
        OBJECT_MAPPER_IGNORE_NULLS.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 配置美化输出的ObjectMapper
        configureMapper(PRETTY_MAPPER);
        PRETTY_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 配置ObjectMapper的通用设置
     * @param mapper 要配置的ObjectMapper
     */
    private static void configureMapper(ObjectMapper mapper) {
        // 配置日期格式
        mapper.setDateFormat(new SimpleDateFormat(STANDARD_DATE_TIME_FORMAT));
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));

        // 注册Java 8日期时间模块
        mapper.registerModule(new JavaTimeModule());

        // 禁用序列化日期为时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 配置反序列化设置
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        // 注册自定义序列化器和反序列化器
        SimpleModule module = new SimpleModule();
        // 这里可以注册自定义的序列化器和反序列化器
        mapper.registerModule(module);
    }

    /**
     * 将对象转换为JSON字符串
     * @param object 要转换的对象
     * @return JSON字符串
     */
    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to JSON: {}", object, e);
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * 将对象转换为美化后的JSON字符串
     * @param object 要转换的对象
     * @return 美化后的JSON字符串
     */
    public static String toPrettyJsonString(Object object) {
        try {
            return PRETTY_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to pretty JSON: {}", object, e);
            throw new RuntimeException("Failed to serialize object to pretty JSON", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     * @param json JSON字符串
     * @param clazz 目标对象类型
     * @param <T> 对象类型泛型
     * @return 转换后的对象
     */
    public static <T> T fromString(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("Failed to deserialize JSON to object: {}", json, e);
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象，使用TypeReference处理泛型类型
     * @param json JSON字符串
     * @param typeReference 目标对象类型引用
     * @param <T> 对象类型泛型
     * @return 转换后的对象
     */
    public static <T> T fromString(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            logger.error("Failed to deserialize JSON to object: {}", json, e);
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }
    }

    /**
     * 将JSON输入流转换为指定类型的对象
     * @param inputStream JSON输入流
     * @param clazz 目标对象类型
     * @param <T> 对象类型泛型
     * @return 转换后的对象
     */
    public static <T> T fromStream(InputStream inputStream, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            logger.error("Failed to deserialize JSON stream to object", e);
            throw new RuntimeException("Failed to deserialize JSON stream to object", e);
        }
    }

    /**
     * 将JSON Reader转换为指定类型的对象
     * @param reader JSON Reader
     * @param clazz 目标对象类型
     * @param <T> 对象类型泛型
     * @return 转换后的对象
     */
    public static <T> T fromReader(Reader reader, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(reader, clazz);
        } catch (IOException e) {
            logger.error("Failed to deserialize JSON reader to object", e);
            throw new RuntimeException("Failed to deserialize JSON reader to object", e);
        }
    }

    /**
     * 将对象转换为JsonNode
     * @param object 要转换的对象
     * @return JsonNode对象
     */
    public static JsonNode valueToTree(Object object) {
        return OBJECT_MAPPER.valueToTree(object);
    }

    /**
     * 将JsonNode转换为指定类型的对象
     * @param node JsonNode对象
     * @param clazz 目标对象类型
     * @param <T> 对象类型泛型
     * @return 转换后的对象
     */
    public static <T> T treeToValue(JsonNode node, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert JsonNode to object: {}", node, e);
            throw new RuntimeException("Failed to convert JsonNode to object", e);
        }
    }

    /**
     * 将Gson的JsonElement转换为Jackson的JsonNode
     * @param jsonElement Gson的JsonElement
     * @return Jackson的JsonNode
     */
    public static JsonNode convertGsonElementToJsonNode(JsonElement jsonElement) {
        try {
            return OBJECT_MAPPER.readTree(jsonElement.toString());
        } catch (IOException e) {
            logger.error("Failed to convert Gson JsonElement to JsonNode: {}", jsonElement, e);
            throw new RuntimeException("Failed to convert Gson JsonElement to JsonNode", e);
        }
    }

    /**
     * 将Jackson的JsonNode转换为Gson的JsonElement
     * @param jsonNode Jackson的JsonNode
     * @return Gson的JsonElement
     */
    public static JsonElement convertJsonNodeToGsonElement(JsonNode jsonNode) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(jsonNode.toString());
    }

    /**
     * 合并两个JSON对象
     * @param target 目标JSON对象
     * @param source 源JSON对象，将合并到目标对象
     * @return 合并后的JsonNode
     */
    public static JsonNode merge(JsonNode target, JsonNode source) {
        return OBJECT_MAPPER.valueToTree(mergeRecursive(target, source));
    }

    /**
     * 递归合并两个JsonNode
     * @param target 目标JsonNode
     * @param source 源JsonNode
     * @return 合并后的对象
     */
    private static Object mergeRecursive(JsonNode target, JsonNode source) {
        if (source.isObject()) {
            return mergeObjectRecursive(target, source);
        } else if (source.isArray()) {
            return mergeArrayRecursive(target, source);
        } else {
            return source;
        }
    }

    /**
     * 递归合并两个JSON对象
     * @param target 目标JsonNode
     * @param source 源JsonNode
     * @return 合并后的对象
     */
    private static Object mergeObjectRecursive(JsonNode target, JsonNode source) {
        com.fasterxml.jackson.databind.node.ObjectNode result = OBJECT_MAPPER.createObjectNode();

        // 复制目标对象的所有属性
        if (target != null && target.isObject()) {
            target.fieldNames().forEachRemaining(fieldName -> {
                result.set(fieldName, target.get(fieldName));
            });
        }

        // 合并源对象的属性
        source.fieldNames().forEachRemaining(fieldName -> {
            JsonNode sourceValue = source.get(fieldName);
            JsonNode targetValue = target != null ? target.get(fieldName) : null;

            if (targetValue != null && sourceValue.isObject() && targetValue.isObject()) {
                result.set(fieldName, valueToTree(mergeRecursive(targetValue, sourceValue)));
            } else if (targetValue != null && sourceValue.isArray() && targetValue.isArray()) {
                result.set(fieldName, valueToTree(mergeRecursive(targetValue, sourceValue)));
            } else {
                result.set(fieldName, sourceValue);
            }
        });

        return result;
    }

    /**
     * 合并两个JSON数组
     * @param target 目标JsonNode
     * @param source 源JsonNode
     * @return 合并后的数组
     */
    private static Object mergeArrayRecursive(JsonNode target, JsonNode source) {
        com.fasterxml.jackson.databind.node.ArrayNode result = OBJECT_MAPPER.createArrayNode();

        // 添加目标数组的所有元素
        if (target != null && target.isArray()) {
            target.elements().forEachRemaining(result::add);
        }

        // 添加源数组的所有元素
        source.elements().forEachRemaining(result::add);

        return result;
    }
}
