package bassis.bassis_tools.string;

public class StringUtils {
	/**
	 * 判断字符串是否为null或为“”
	 * 
	 * @param str
	 * @return true表示是 不为空返回false
	 */
	public static boolean isEmptyString(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 判断字符串除去首尾空格后的长度
	 * 
	 * @param str
	 * @return
	 */
	public static int getlength(String str) {
		if (isEmptyString(str))
			return 0;

		return str.trim().length();
	}

	/**
	 * 替换字符串中的oldChar值为newChar值
	 * 
	 * @param str
	 * @param oldChar
	 * @param newChar
	 * @return
	 */
	public static String replace(String str, String oldChar, String newChar) {
		if (isEmptyString(str))
			return str;

		return str.replace(oldChar, newChar);
	}

	/**
	 * 检索字符串中是否出现了特定字符 存在返回字符的位置 不存在返回-1
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static int indexOf(String str, String c) {
		if (isEmptyString(str))
			return -1;

		int index = str.indexOf(c);
		if (index >= 0)
			return index;

		return -1;
	}

	/**
	 * 检索字符串最后一次出现特定字符的位置 存在特定字符返回字符最后出现的位置 不存在返回-1
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static int lastIndexOf(String str, String c) {
		if (isEmptyString(str))
			return -1;

		int index = str.lastIndexOf(c);
		if (index >= 0)
			return index;

		return -1;
	}

	/**
	 * 检查字符串前缀
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static Boolean startsWith(String str, String c) {
		if (isEmptyString(str))
			return null;

		return str.startsWith(c);
	}

	/**
	 * 检查字符串后缀
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static Boolean endsWith(String str, String c) {
		if (isEmptyString(str))
			return null;

		return str.endsWith(c);
	}

	/**
	 * 获得指定特殊字符最后一次出现位置后的字符串
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static String endslastIndexOf_str(String str, String c) {
		if (isEmptyString(str))
			return null;

		str = str.substring(lastIndexOf(str, c) + 1, str.length());
		return str;
	}
	/**
	 * 获得指定特殊字符最后一次出现位置后的字符串
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static String endsIndexOf_str(String str, String c) {
		if (isEmptyString(str))
			return null;

		str = str.substring(indexOf(str, c) + 1, str.length());
		return str;
	}
	/**
	 * 获得指定特殊字符第一次出现位置前的字符串
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static String startsIndexOf_str(String str, String c) {
		if (isEmptyString(str))
			return null;

		str = str.substring(0,indexOf(str, c));
		return str;
	}
	/**
	 * 获得指定特殊字符最后一次出现位置前的字符串
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static String startslastIndexOf_str(String str, String c) {
		if (isEmptyString(str))
			return null;

		str = str.substring(0,lastIndexOf(str, c));
		return str;
	}
	//
	/**
	 * 根据特定字符串分割为数组 并且获得某个字符串前一个分割元素 例 str=org.modao.controllers.Test   c=Test   f=.
	 * 结果为 controllers.Test
	 * 
	 * @param str 需要处理的字符串
	 * @param c 定位字符串
	 * @param f 分割符
	 * @return
	 */
	public static String subStringCustom(String str, String c, String f) {
		if (isEmptyString(str))
			return null;

		if (isEmptyString(c))
			return str;

		try {
			int index = 0;
			if(".".equals(f)){
				f="å";
				str=str.replace(".", f);
			}
			String[] arr = str.split(f);
			for (int i = 0; i < arr.length; i++) {
				if (indexOf(arr[i], c) >= 0){
					index = i;
					break;
				}
			}
			if (index == 0)
				return null;

			str = arr[index - 1];
		} catch (Exception e) {
			// TODO: handle exception
		}

		return str;
	}
	/**
	 * 获得指定个数分隔符前的字符
	 * 
	 * @param str
	 * @param c 分隔符
	 * @param v 个数
	 * @return
	 */
	public static String indexOf_str(String str, String c,int v) {
		if (isEmptyString(str) || str.length()<v){
			return null;
		}
		if(v==0){
			return str;
		}
		int index=0;
		if(v>1){
			for(int i=0;i<v;i++){
				index=str.indexOf(c, index+1);
			}
		}else{
			index=indexOf(str, c);
		}
		
		str = str.substring(0,index);
		return str;
	}
	/**
	 * 获得指定个数分隔符后的字符
	 * 
	 * @param str
	 * @param c 分隔符
	 * @param v 个数
	 * @return
	 */
	public static String indexOf_str_end(String str, String c,int v) {
		if (isEmptyString(str) || str.length()<v){
			return null;
		}
		if(v==0){
			return str;
		}
		int index=0;
		if(v>1){
			for(int i=0;i<v;i++){
				index=str.indexOf(c, index)+c.length();
			}
		}else{
			index=indexOf(str, c);
		}
		
		str = str.substring(index,str.length());
		return str;
	}
	public static void main(String[] args) {
		String str="ddsdade2232fredad2232eredsadada";
		System.out.println(dcover_str(3));
	}
	private static String dcover_str(int c){
        String str="000000";
          return str.substring(str.length()-c);
    }  
}
