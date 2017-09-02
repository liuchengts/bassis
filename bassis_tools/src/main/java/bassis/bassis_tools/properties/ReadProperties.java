package bassis.bassis_tools.properties;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ReadProperties extends Properties{
	private static ReadProperties properties;
	public static ReadProperties getInit() {
		if(null==properties){
			properties=new ReadProperties();
		}
		return properties;
	}
	public void setProperties(ReadProperties properties) {
		this.properties = properties;
	}

	private static  Map<String, String> map=null;
	private static  Map<String, String> map_name=null;
	private static  Map<String, String> map_flesh=null;
	private ListEnumerationAdapter<Object> keyList = new ListEnumerationAdapter<Object>();
	static{
		if(map==null){
			map=new HashMap<String, String>();
		}
		if(map_name==null){
			map_name=new HashMap<String, String>();
		}
		if(map_flesh==null){
			map_flesh=new HashMap<String, String>();
		}
	}
	public ReadProperties(){
	}
	/******
	 * 常规map读取  结果会以最后一次读取的为准  不处理重复情况
	 * 如 dataSource.driverClassName
	 * @param key
	 * @return
	 */
	public static String getProperties(String key){
		return map.get(key);
	}
	/******
	 *map方式读取 key需带上文件名 如 db2.dataSource.driverClassName
	 * @param key
	 * @return
	 */
	public static String getPropertiesName(String key){
		return map_name.get(key);
	}
	/******
	 * map读取 如果key在所有的文件中唯一，按常规方式读取 如 dataSource.driverClassName
	 *       如果key在所有文件中不唯一，需带上文件名 如 db2.dataSource.driverClassName
	 * @param key
	 * @return
	 */
	public static String getPropertiesFlesh(String key){
		return map_flesh.get(key);
	}
	/**
    * 从指定路径加载信息到Properties,同一个文件中出现相同key的  以最后一个key值为准
    * @param path
    */
   public void  read(String path) {
      try {
        // InputStream is = new FileInputStream(path);
    	 InputStream is = this.getClass().getResourceAsStream(path);
         this.load(is);
      } catch (FileNotFoundException e) {
         throw new RuntimeException("指定文件不存在！",e);
      } catch (IOException e) {
         e.printStackTrace();
      }
      try {
    	  String key="",value="";
    	  String str="properties";
   	      String name=path.substring(1, path.length()-str.length());
    	  mapProperties(key,value);
          mapPropertiesName(key,value,name);
          mapPropertiesFlesh(key,value,name);
	  } catch (Exception e) {
			// TODO: handle exception
		  throw new RuntimeException("文件解析错误！",e);
	  }
   }
   /**********
    * 将Properties文件的对象全部put到map
    * 检查map中是否有相同的map，第一次读取的不需要带上文件名 后面重复的key需要带上文件名
    * 如 db2.dataSource.driverClassName
    * @param p
    */
   private  void mapPropertiesFlesh(String key,String value,String name){
	   for(int i=0;i<this.size();i++){
		   key=(String) this.keyList.get(i);
		   value=this.getProperty(key);
		   if(map_flesh.containsKey(key)){
			   //发现相同的key
			   key=name+key;
		   }
		   map_flesh.put(key,value);
       }
   }
   /**********
    * 将Properties文件的对象全部put到map
    * 将所有map对象的key都带上文件名
    * 如 db2.dataSource.driverClassName
    * @param p
    */
   private  void mapPropertiesName(String key,String value,String name){
	   for(int i=0;i<this.size();i++){
		   key=(String) this.keyList.get(i);
		   value=this.getProperty(key);
		   key=name+key;
		   map_name.put(key,value);
       }
   }
   /**********
    * 将Properties文件的对象全部put到map
    * 检查map中是否有相同的map，覆盖掉，默认保留最后一次读取的mapkey
    * @param p
    */
   private  void mapProperties(String key,String value){
	   for(int i=0;i<this.size();i++){
		   key=(String) this.keyList.get(i);
		   value=this.getProperty(key);
		   map.put(key,value);
       }
   }
   /**
    * 重写put方法，按照property的存入顺序保存key到keyList，遇到重复的后者将覆盖前者。
    */
   @Override
   public synchronized Object put(Object key, Object value) {
      if (keyList.contains(key)) {
         keyList.remove(key);
      }
      keyList.add(key);
      return super.put(key, value);
   }
  
   /**
    * 获取Properties中key的有序集合
    * @return
    */
   public List<Object> getKeyList() {
      return keyList;
   }
  
   /**
    * 保存Properties到指定文件，默认使用UTF-8编码
    * @param path 指定文件路径
    */
   public void store(String path) {
      this.store(path, "UTF-8");
   }
  
   /**
    * 保存Properties到指定文件，并指定对应存放编码
    * @param path 指定路径
    * @param charset 文件编码
    */
   public void store(String path, String charset) {
      if (path != null && !"".equals(path)) {
         try {
            OutputStream os = new FileOutputStream(path);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, charset));
            this.store(bw, null);
            bw.close();
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         throw new RuntimeException("存储路径不能为空!");
      }
   }
 
   /**
    * 重写keys方法
    */
   @Override
   public synchronized Enumeration<Object> keys() {
      keyList.reset();
      return keyList;
   }
   
   /**
    * ArrayList到Enumeration的适配器
    */
    private static class ListEnumerationAdapter<T> extends ArrayList<T> implements Enumeration<T> {
      private static final long serialVersionUID = 1L;
      private int index = 0;
     
      public boolean hasMoreElements() {
         return index < this.size();
      }
 
      public T nextElement() {
         if (this.hasMoreElements()) {
            return this.get(index++);
         }
         return null;
      }
     
      /**
       * 重置index的值为0，使得Enumeration可以继续从头开始遍历
       */
      public void reset() {
         this.index = 0;
      }
    }
    /**
     * 验证key参数存在并且不为null
     * 如果是多个key  必须key都验证通过才返回true
     * @param keys
     * @return
     */
  public static boolean verification(String[] keys) {
	  if(null==properties || null==keys ||keys.length<=0)
		  return false;
	  
	  for(String s:keys){
		  if(false==properties.containsKey(s)){
			  return false;
		  }else if( null==properties.getProperty(s)){
			  return false;
		  }
	  }
	return true;
  }
  /**
   * 验证key参数存在并且不为null
   * @param key
   * @return
   */
  public static boolean verification(String key) {
	  if(null==properties || null==key ||key.trim().length()<=0)
		  return false;
	  
	  if (!properties.containsKey(key)){
			return false;
	  }else if( null==properties.getProperty(key)){
		  return false;
	  }
	  return true;
  }
}
