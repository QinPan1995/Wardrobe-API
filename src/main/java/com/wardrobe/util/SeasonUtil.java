package com.wardrobe.util;

import com.wardrobe.enums.SeasonEnum;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author ：luke
 * @date ：Created in 2025/4/30 11:32
 * @description：
 * @modified By：
 */


public class SeasonUtil {

    /**
     * 使用SeasonEnum枚举对季节列表进行排序
     */
    public static void sortSeasons(List<String> seasons) {
        if (seasons == null || seasons.isEmpty()) return;

        seasons.sort(Comparator.comparingInt(s -> {
            try {
                return SeasonEnum.fromChinese(s).getOrder();
            } catch (IllegalArgumentException e) {
                // 如果遇到不支持的季节名，可以抛出异常或者将其放在最后
                // 这里选择将不支持的季节名视为最大值
                return Integer.MAX_VALUE;
            }
        }));
    }

    /**
     * 将季节列表转换为以逗号分隔的文本
     * @param seasons
     * @return
     */
    public static String seasonsToString(List<String> seasons) {
        if (seasons == null || seasons.isEmpty()) return null;
        return String.join(",", seasons);
    }

    /**
     * 将逗号分隔的文本转换为季节列表
     * @param seasons
     * @return
     */
    public static List<String> stringToSeasons(String seasons) {
        if (seasons == null || seasons.isEmpty()) return null;
        return Arrays.asList(seasons.split(","));
    }
}
