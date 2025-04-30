package com.wardrobe.enums;

import java.util.Arrays;
import java.util.List;

public enum SeasonEnum {
    SPRING("春", "spring", 0),
    SUMMER("夏", "summer", 1),
    AUTUMN("秋", "autumn", 2),
    WINTER("冬", "winter", 3);

    private final String chineseName;
    private final String englishName;
    private final int order;

    SeasonEnum(String chineseName, String englishName, int order) {
        this.chineseName = chineseName;
        this.englishName = englishName;
        this.order = order;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public int getOrder() {
        return order;
    }

    /**
     * 根据中文标签获取对应的枚举值
     */
    public static SeasonEnum fromChinese(String chineseName) {
        for (SeasonEnum season : values()) {
            if (season.getChineseName().equals(chineseName)) {
                return season;
            }
        }
        throw new IllegalArgumentException("未知的中文季节: " + chineseName);
    }

    /**
     * 根据英文标签获取对应的枚举值（不区分大小写）
     */
    public static SeasonEnum fromEnglish(String englishName) {
        for (SeasonEnum season : values()) {
            if (season.getEnglishName().equalsIgnoreCase(englishName)) {
                return season;
            }
        }
        throw new IllegalArgumentException("未知的英文季节: " + englishName);
    }

    /**
     * 判断是否是合法的中文季节名称
     */
    public static boolean isValidChinese(String chineseName) {
        for (SeasonEnum season : values()) {
            if (season.getChineseName().equals(chineseName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是合法的英文季节名称（不区分大小写）
     */
    public static boolean isValidEnglish(String englishName) {
        for (SeasonEnum season : values()) {
            if (season.getEnglishName().equalsIgnoreCase(englishName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有中文季节名称列表
     */
    public static List<String> getAllChineseNames() {
        return Arrays.stream(values())
                .map(SeasonEnum::getChineseName)
                .toList();
    }

    /**
     * 获取所有英文季节名称列表
     */
    public static List<String> getAllEnglishNames() {
        return Arrays.stream(values())
                .map(SeasonEnum::getEnglishName)
                .toList();
    }

    /**
     * 获取某个季节的英文名（根据中文名）
     */
    public static String getEnglishFromChinese(String chineseName) {
        return fromChinese(chineseName).getEnglishName();
    }

    /**
     * 获取某个季节的中文名（根据英文名）
     */
    public static String getChineseFromEnglish(String englishName) {
        return fromEnglish(englishName).getChineseName();
    }

}