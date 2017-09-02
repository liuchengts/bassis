package bassis_tools;

import bassis.bassis_tools.utils.HttpGET;

public class Test {
	public static void main(String[] args) {
//			Concurrent.test(1000,"wap_taoke_main",Test.class );
		wap_taoke_main();
	}

	public static void wap_taoke_main() {
		try {
			String json=HttpGET.sendHttpGet("https://www.modaolc.com/taoke/wap/Relay.json?itemId=545182216039&type=detailImg", null);
			String sta="images";
			String end="]";
//			json=json.replace(, newChar)
//			json=json.substring(json.indexOf(sta)+sta.length()+5,json.lastIndexOf(end));
			System.out.println(json);
		} catch (Exception e) {
			// XXX: handle exception
			e.printStackTrace();
		}
		
	}
}
