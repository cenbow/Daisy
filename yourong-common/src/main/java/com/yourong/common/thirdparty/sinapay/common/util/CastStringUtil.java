package com.yourong.common.thirdparty.sinapay.common.util;

public class CastStringUtil {

    public static final String UNDER_LINE = "_";

    /**
     * 驼峰转下划线
     * @param name
     * @return
     */
    public static String underLineName(String name) {
        StringBuilder result = new StringBuilder();

        if (name != null && name.length() > 0) {
            result.append(name.substring(0, 1).toLowerCase());
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append(UNDER_LINE);
                }
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }
}
