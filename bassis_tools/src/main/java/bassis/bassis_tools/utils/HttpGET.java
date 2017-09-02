package bassis.bassis_tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
public class HttpGET {
     public static String  sendHttpGet(String url,String coding) throws IOException{
     	URL localURL = null;
 		try {
 			localURL = new URL(url);
 		} catch (MalformedURLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         URLConnection connection = localURL.openConnection();
         HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
         
         httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
         httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C)");
         
         InputStream inputStream = null;
         InputStreamReader inputStreamReader = null;
         BufferedReader reader = null;
         StringBuffer resultBuffer = new StringBuffer();
         String tempLine = null;
         
         if (httpURLConnection.getResponseCode() >= 300) {
             try {
 				throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
         }
         try {
             inputStream = httpURLConnection.getInputStream();
             inputStreamReader = new InputStreamReader(inputStream, null==coding?"utf-8":coding);
             reader = new BufferedReader(inputStreamReader);
             
             while ((tempLine = reader.readLine()) != null) {
                 resultBuffer.append(tempLine);
             }
         } finally {
             if (reader != null) {
                 reader.close();
             }
             if (inputStreamReader != null) {
                 inputStreamReader.close();
             }
             if (inputStream != null) {
                 inputStream.close();
             }
         }
         return resultBuffer.toString();
     
     }
}
