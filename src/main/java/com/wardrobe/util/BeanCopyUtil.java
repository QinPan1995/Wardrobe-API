package com.wardrobe.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：luke
 * @date ：Created in 2025/5/12 15:33
 * @description：对象拷贝
 * @modified By：
 */


public class BeanCopyUtil {

    /**
     * 将源对象的属性值拷贝到目标对象中，包括所有父类的属性。
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            System.err.println("Source and target objects must not be null");
            return;
        }

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        while (sourceClass != null && targetClass != null) {
            Field[] sourceFields = sourceClass.getDeclaredFields();
            Field[] targetFields = targetClass.getDeclaredFields();

            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true); // 允许访问私有字段
                for (Field targetField : targetFields) {
                    if (sourceField.getName().equals(targetField.getName()) &&
                            sourceField.getType().equals(targetField.getType())) {
                        targetField.setAccessible(true); // 允许访问私有字段
                        try {
                            targetField.set(target, sourceField.get(source)); // 拷贝属性值
                        } catch (IllegalAccessException e) {
                            System.err.println("Failed to copy field: " + sourceField.getName() + " - " + e.getMessage());
                        }
                    }
                }
            }

            // 处理父类
            sourceClass = sourceClass.getSuperclass();
            targetClass = targetClass.getSuperclass();
        }
    }

    /**
     * 将源对象的属性值拷贝到一个新实例化的目标对象中，包括所有父类的属性。
     *
     * @param source 源对象
     * @param targetClass 目标对象的类类型
     * @param <T> 目标对象类型
     * @return 新实例化并赋值后的目标对象，或 null 如果实例化失败
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null || targetClass == null) {
            System.err.println("Source and target class must not be null");
            return null;
        }

        try {
            T target = targetClass.getDeclaredConstructor().newInstance(); // 使用新方法实例化目标对象
            copyProperties(source, target); // 调用已有的拷贝逻辑
            return target;
        } catch (Exception e) {
            System.err.println("Failed to create target object: " + e.getMessage());
            return null;
        }
    }

    /**
     * 将多个源对象的属性值拷贝到多个新实例化的目标对象中，包括所有父类的属性。
     *
     * @param sources 源对象列表
     * @param targetClass 目标对象的类类型
     * @param <T> 目标对象类型
     * @return 拷贝后的目标对象列表
     */
    public static <T> List<T> copyProperties(List<?> sources, Class<T> targetClass) {
        if (sources == null || targetClass == null) {
            System.err.println("Sources and target class must not be null");
            return new ArrayList<>();
        }

        List<T> targets = new ArrayList<>();
        for (Object source : sources) {
            T target = copyProperties(source, targetClass); // 调用单对象拷贝方法
            if (target != null) {
                targets.add(target);
            } else {
                System.err.println("Failed to copy properties for one of the source objects.");
            }
        }
        return targets;
    }
}
