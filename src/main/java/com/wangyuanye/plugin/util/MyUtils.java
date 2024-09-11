package com.wangyuanye.plugin.util;

import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;

import java.awt.*;

/**
 * 国际化工具类
 *
 * @author wangyuanye
 * @since v1.0
 **/
public class MyUtils {
    public static Logger logger = new DefaultLogger("[MessagesUtil]");

    private static final String BUNDLE_MSG_PATH = "messages/messages"; // 不带扩展名的基础名称
    private static final String BUNDLE_DATA_PATH = "config_data";

    /**
     * 颜色转字符串
     *
     * @param color color
     * @return String, 如: "123,122,0"
     */
    public static String color2String(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return red + "," + green + "," + blue;
    }

    /**
     * String转color
     *
     * @param colorStr 颜色字符串,如: "123,122,0"
     * @return Color
     */
    @SuppressWarnings("ALL")
    public static Color string2Color(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) {
            return Color.GREEN;
        }
        String[] split = colorStr.split(",");
        int r = Integer.parseInt(split[0]);
        int g = Integer.parseInt(split[1]);
        int b = Integer.parseInt(split[2]);
        return new Color(r, g, b);
    }


    public static String genFilenameFromPath(String classPath) {
        String result = "";
        String[] split = classPath.split("\\.");
        for (int i = 0; i < split.length - 1; i++) {
            if (i == 0) {
                result = result.concat(split[i]);
            } else {
                result = result.concat(".").concat(split[i]);
            }
        }
        String replace = result.replace("/", "_");
        return replace + ".json";
    }
}
