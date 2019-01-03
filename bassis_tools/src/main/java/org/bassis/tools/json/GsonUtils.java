package org.bassis.tools.json;

import java.util.Map;

import org.bassis.tools.exception.CustomException;
import com.google.gson.GsonBuilder;

/**
 * gson工具包
 */
public class GsonUtils {
    private static final long serialVersionUID = 1L;
    static GsonBuilder builder = new GsonBuilder();
    static com.google.gson.Gson gson = builder.create();
    static String error_str = GsonUtils.class.getName() + "转换异常";

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
