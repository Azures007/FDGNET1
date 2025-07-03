package org.thingsboard.server.common.data.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.thingsboard.server.common.data.json.JacksonUtil;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/6/27 11:50:12
 */
/**
 * Hibernate自定义类型处理器，用于实现Java对象与数据库JSON字符串之间的相互转换
 *
 * 该类允许将任意Java对象以JSON格式存储到数据库中，并在查询时自动将JSON字符串
 * 反序列化为Java对象，提供了一种无缝集成JSON数据到关系型数据库的方式
 *
 * 功能特点：
 * 1. 支持将Java对象序列化为JSON字符串
 * 2. 支持将JSON字符串反序列化为Java对象
 * 3. 处理空值情况，包括null值的读写
 * 4. 实现对象的深度复制，确保数据隔离
 * 5. 支持对象的序列化和反序列化，用于Hibernate的缓存机制
 *
 * 使用方法：
 * 在实体类的JSON字段上添加注解：
 * @Type(type = "org.thingsboard.server.dao.model.JsonStringType")
 * @Column(name = "json_column")
 * private Object jsonData;
 */
public class JsonStringType implements UserType{
    /** 使用Jackson的ObjectMapper进行JSON处理 */
    private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.OBJECT_MAPPER;

    /**
     * 返回此类型映射的SQL类型
     * @return 表示VARCHAR的SQL类型数组
     */
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    /**
     * 返回该类型处理的Java类
     * @return Object类，表示可以处理任意类型
     */
    @Override
    public Class<?> returnedClass() {
        return Object.class;
    }

    /**
     * 判断两个对象是否相等
     * @param x 第一个对象
     * @param y 第二个对象
     * @return 如果相等返回true，否则返回false
     * @throws HibernateException 如果比较过程中发生错误
     */
    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    /**
     * 生成对象的哈希码
     * @param x 要生成哈希码的对象
     * @return 对象的哈希码
     * @throws HibernateException 如果生成过程中发生错误
     */
    @Override
    public int hashCode(Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    /**
     * 从结果集读取数据并反序列化为Java对象
     *
     * 处理流程：
     * 1. 从结果集中获取指定列的JSON字符串
     * 2. 如果字符串为空，直接返回null
     * 3. 使用Jackson的ObjectMapper将JSON字符串反序列化为Java对象
     *
     * @param rs 结果集
     * @param names 列名数组
     * @param session Hibernate会话
     * @param owner 拥有者对象
     * @return 反序列化后的Java对象
     * @throws HibernateException 如果反序列化失败
     * @throws SQLException 如果从结果集读取数据失败
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String json = rs.getString(names[0]);
        if (json == null) {
            return null;
        }
        try {
            // 将JSON字符串反序列化为Java对象
            return OBJECT_MAPPER.readValue(json, Object.class);
        } catch (Exception e) {
            throw new HibernateException("Failed to deserialize JSON: " + json, e);
        }
    }

    /**
     * 将Java对象序列化为JSON字符串并写入预编译语句
     *
     * 处理流程：
     * 1. 如果对象为空，将对应位置设为SQL NULL
     * 2. 使用Jackson的ObjectMapper将Java对象序列化为JSON字符串
     * 3. 将JSON字符串设置到预编译语句的指定位置
     *
     * @param st 预编译语句
     * @param value 要序列化的Java对象
     * @param index 参数索引
     * @param session Hibernate会话
     * @throws HibernateException 如果序列化失败
     * @throws SQLException 如果设置参数失败
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
            return;
        }
        try {
            // 将Java对象序列化为JSON字符串
            String json = OBJECT_MAPPER.writeValueAsString(value);
            st.setString(index, json);
        } catch (Exception e) {
            throw new HibernateException("Failed to serialize object to JSON: " + value, e);
        }
    }

    /**
     * 创建对象的深度副本
     *
     * 实现方式：
     * 1. 将对象序列化为JSON字符串
     * 2. 将JSON字符串反序列化为新对象
     *
     * 这样可以确保新对象与原对象在内存中是完全独立的
     *
     * @param value 要复制的对象
     * @return 深度复制后的新对象
     * @throws HibernateException 如果复制过程中发生错误
     */
    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        try {
            // 通过JSON序列化和反序列化实现深度复制
            String json = OBJECT_MAPPER.writeValueAsString(value);
            return OBJECT_MAPPER.readValue(json, Object.class);
        } catch (Exception e) {
            throw new HibernateException("Failed to deep copy object: " + value, e);
        }
    }

    /**
     * 判断类型是否可变
     * @return 始终返回true，表示该类型处理的对象是可变的
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /**
     * 将对象转换为可序列化形式，用于Hibernate的二级缓存
     * @param value 要序列化的对象
     * @return 可序列化的对象
     * @throws HibernateException 如果序列化失败
     */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    /**
     * 从可序列化形式恢复对象，用于Hibernate的二级缓存
     * @param cached 缓存的可序列化对象
     * @param owner 拥有者对象
     * @return 恢复后的对象
     * @throws HibernateException 如果反序列化失败
     */
    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    /**
     * 使用原始对象替换目标对象
     * @param original 原始对象
     * @param target 目标对象
     * @param owner 拥有者对象
     * @return 替换后的对象
     * @throws HibernateException 如果替换过程中发生错误
     */
    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }


}
