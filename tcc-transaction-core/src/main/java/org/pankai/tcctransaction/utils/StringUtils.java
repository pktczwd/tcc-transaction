package org.pankai.tcctransaction.utils;

/**
 * Created by pankai on 2016/11/13.
 */
public class StringUtils {

    public static boolean isNotEmpty(String value) {
        if (value == null) {
            return false;
        }
        if (value.equals("")) {
            return false;
        }
        return true;
    }
}
