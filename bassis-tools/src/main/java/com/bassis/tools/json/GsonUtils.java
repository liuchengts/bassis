package com.bassis.tools.json;

import java.lang.reflect.Type;
import java.util.Map;

import com.bassis.tools.exception.CustomException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * gson工具包
 */
public class GsonUtils {
    private static final long serialVersionUID = 1L;
    static GsonBuilder builder = new GsonBuilder();
    static com.google.gson.Gson gson = builder.create();
    static String error_str = GsonUtils.class.getName() + "转换异常";

    public static GsonBuilder getBuilder() {
        return builder;
    }

    public static void setBuilder(GsonBuilder builder) {
        GsonUtils.builder = builder;
    }

    public static Gson getGson() {
        return gson;
    }

    public static void setGson(Gson gson) {
        GsonUtils.gson = gson;
    }

    public static String getError_str() {
        return error_str;
    }

    public static void setError_str(String error_str) {
        GsonUtils.error_str = error_str;
    }

    /**
     * json字符串转换为对象
     *
     * @param json json字符串
     * @param type 对象类型
     * @return 返回对象
     */
    public static Object jsonToObject(String json, Class<?> type) {
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }

    /**
     * json字符串转换为对象
     *
     * @param json    json字符串
     * @param typeOfT 对象类型  通常是一个 new TypeToken<List<CLASS>>(){}.getType()
     * @return 返回对象
     */
    public static Object jsonToObject(String json, Type typeOfT) {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }

    /**
     * 对象转换为json字符串
     *
     * @param obj json字符串
     * @return 返回json字符串
     */
    public static String objectToJson(Object obj) {
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }

    /***
     * map对象转换为json字符串
     * @param map map对象
     * @return 返回json字符串
     */
    public static String maptoJsonstr(Map map) {
        try {
            return gson.toJson(map);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }

    /***
     * map对象转换为对象
     * @param map map对象
     * @param type 对象类型
     * @return 返回对象
     */
    public static Object jsonToObject(Map map, Class<?> type) {
        try {
            return gson.fromJson(maptoJsonstr(map), type);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }

    /**
     * 给一个需要@Expose 注解的字段 才转换的对象
     *
     * @param obj 要转换的对象
     * @return 返回转换后的json字符串
     */
    public static String exposeObjectToJson(Object obj) {
        try {
            GsonBuilder _builder = new GsonBuilder();
            // 不转换没有 @Expose 注解的字段
            _builder.excludeFieldsWithoutExposeAnnotation();
            return _builder.create().toJson(obj);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }
}
