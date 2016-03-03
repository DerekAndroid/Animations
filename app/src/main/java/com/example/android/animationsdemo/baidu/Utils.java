package com.example.android.animationsdemo.baidu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String TAG = "PushDemoActivity";
    public static final String RESPONSE_METHOD = "method";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_ERRCODE = "errcode";
    protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
    protected static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_MESSAGE = "message";

    public static String logStringCache = "";

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
    public static boolean hasBind(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String flag = sp.getString("bind_flag", "");
        if ("ok".equalsIgnoreCase(flag)) {
            return true;
        }
        return false;
    }

    public static void setBind(Context context, boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }

    public static List<String> getTagsList(String originalText) {
        if (originalText == null || originalText.equals("")) {
            return null;
        }
        List<String> tags = new ArrayList<String>();
        int indexOfComma = originalText.indexOf(',');
        String tag;
        while (indexOfComma != -1) {
            tag = originalText.substring(0, indexOfComma);
            tags.add(tag);

            originalText = originalText.substring(indexOfComma + 1);
            indexOfComma = originalText.indexOf(',');
        }

        tags.add(originalText);
        return tags;
    }

    public static String getLogText(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString("log_text", "");
    }

    public static void setLogText(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("log_text", text);
        editor.commit();
    }
    
    public static String sendGet(String url , String param)
	{  
		String result = "";
		BufferedReader in = null;
		try
		{
			String urlName = url;
			if( param != null && param != "" && param.length() > 0 )
				urlName += "?" + param;  

			System.out.println("====请求url=====>"+urlName);
			
			URL realUrl = new URL(urlName);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();

			// 设置通用的请求属性			
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setRequestProperty("accept", "*/*");  
			conn.setRequestProperty("connection", "Keep-Alive");  
			conn.setRequestProperty("user-agent",  
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
			// 建立实际的连接
			conn.connect(); 
			
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
			new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine())!= null)  
			{  
				result += line;  
			}  
		}
		catch(Exception e)
		{  
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}  
		// 使用finally块来关闭输入流
		finally
		{
			try
			{
				if (in != null)  
				{
					in.close();  
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();  
			}
		}
		return result;  
	}
    
    public static String sendPost(String url , String param)
    {  
        PrintWriter out = null;
        String result = "";
        BufferedReader in = null;
        try
        {
            String urlName = url;
            if( param != null && param != "" && param.length() > 0 )
                urlName += "?" + param;  

            System.out.println("====请求url=====>"+urlName);
            
            URL realUrl = new URL(urlName);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();

            // 设置通用的请求属性            
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent",  
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
           
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流 
            
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数  
            out.print(param);  
            // flush输出流的缓冲  
            out.flush();  
            // 建立实际的连接
           // conn.connect(); 
            
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
            new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine())!= null)  
            {  
                result += line;  
            }  
        }
        catch(Exception e)
        {  
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }  
        // 使用finally块来关闭输入流
        finally
        {
            try
            {
                if (out != null)  
                {
                    out.close();  
                }
                
                if (in != null)  
                {
                    in.close();  
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();  
            }
        }
        return result;  
    }
    
 // 新增三个方法，在需要获取当前userid和channelid的地方直接调用getUserId、getChannelId方法即可
    public static void setUC(Context context, String userid, String channelid) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("userid", userid);
        editor.putString("channelid", channelid);
        editor.commit();
    }

    public static String getUserId(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userid = sp.getString("userid", "");
        return userid;

    }

    public static String getChannelId(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String channelid = sp.getString("channelid", "");
        return channelid;

    }
}
