package xyz.eevee.coffee.util;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class DataTransformUtil {
    public static int transformToInt(Object obj) throws ClassCastException {
        return (int) transformToDouble(obj);
    }

    public static double transformToDouble(Object obj) throws ClassCastException {
        return Double.valueOf(obj.toString());
    }

    public static boolean transformToBoolean(Object obj) throws ClassCastException {
        return Boolean.valueOf(obj.toString());
    }

    public static List<String> transformToStringList(Object obj) throws ClassCastException {
        String str = (String) obj;
        return ImmutableList.copyOf(str.substring(1).substring(str.length()).split(","));
    }
}
