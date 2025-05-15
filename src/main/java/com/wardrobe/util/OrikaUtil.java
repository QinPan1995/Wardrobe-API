package com.wardrobe.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author ：luke
 * @date ：Created in 2025/5/13 14:28
 * @description：
 * @modified By：
 */


public class OrikaUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        // 配置忽略未知字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 深拷贝 - 同类型对象
     *
     * @param source 源对象
     * @param clazz  目标对象的类型
     * @param <T>    对象类型
     * @return 深拷贝后的对象
     */
    public static <T> T deepCopy(T source, Class<T> clazz) {
        if (source == null) {
            return null;
        }

        try {
            String json = objectMapper.writeValueAsString(source);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("深拷贝失败", e);
        }
    }

    /**
     * 不同类型对象的拷贝
     *
     * @param source 源对象
     * @param targetClass 目标对象的类型
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return 转换后的目标对象
     */
    public static <S, T> T convert(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }

        try {
            // 将源对象序列化为 JSON，再反序列化为目标对象
            String json = objectMapper.writeValueAsString(source);
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("对象转换失败", e);
        }
    }
}
