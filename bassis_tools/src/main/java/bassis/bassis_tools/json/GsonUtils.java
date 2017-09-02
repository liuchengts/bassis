package bassis.bassis_tools.json;

import java.util.Map;

import com.google.gson.GsonBuilder;
/**
 * gson
 *
 */
public class GsonUtils {
	static GsonBuilder builder=new GsonBuilder();
	static com.google.gson.Gson gson =builder.create();
	public static Object jsonToObject(String json,Class<?> type){
		return gson.fromJson(json, type);
	}
	public static String objectToJson(Object obj){
		return gson.toJson(obj);
	}
	public static String maptoJsonstr(Map map){
       return gson.toJson(map);
	}
	public static Object jsonToObject(Map map,Class<?> type){
		String json=maptoJsonstr(map);
		return gson.fromJson(json, type);
	}
	/**
	 * 给一个需要@Expose 注解的字段 才转换的对象
	 * @param obj
	 * @return
	 */
	public static String exposeObjectToJson(Object obj) {
		GsonBuilder _builder=new GsonBuilder();
		// 不转换没有 @Expose 注解的字段
		_builder.excludeFieldsWithoutExposeAnnotation();
		return _builder.create().toJson(obj);
	}
}
