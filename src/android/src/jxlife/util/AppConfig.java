package jxlife.util;

import android.content.Context;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * App 相关配置：服务器地址、文件目录等等
 * 
 * @author lee
 * 
 */
public class AppConfig extends Properties {
	private static final long serialVersionUID = 1L;
	/** 服务器地址 */
	private static final String SERVER_URL = "SERVER_URL";
	/** 数据库存放路径 */
	private static final String DB_PATH = "DB_PATH";
	/** 影像文件存放路径 */
	private static final String IMAGE_PATH = "IMAGE_PATH";
	private static AppConfig config;
	/**服务器的根目录地址/
	 */
	private static final String SERVER_ROOT_URL = "SERVER_ROOT_URL";
	
	private static final String GETNOTICE="getNotice";
	private AppConfig() {
	}

	private static AppConfig getInstence(Context context) {
		if (config == null) {
			config = new AppConfig();
			try {
				InputStream in = context.getAssets().open("config.properties");
				config.load(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return config;
	}
	
	public static String getServerRootUrl(Context context){
		return getInstence(context).getProperty(SERVER_ROOT_URL);
	}

	public static String getGetnotice(Context context) {
		return getInstence(context).getProperty(GETNOTICE);
	}

	/**
	 * 获得服务器地址
	 *
	 * @param Context
	 * @return 服务器地址
	 * @author lee
	 */
	public static String getServerUrl(Context context) {
		D.e(getInstence(context).getProperty(SERVER_URL));
		//modified by liuxinglong20170810 接口统一加时间戳参数
		return getInstence(context).getProperty(SERVER_URL)+"?dateTime="+new Random().toString()+"_"+new Random().toString();
	}
	/**
	 * 获得数据库路径
	 * 
	 */
	public static String getDBPath(Context context) {
		String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
		return sd + getInstence(context).get(DB_PATH);
	}
	
	/**
	 * 获得影像文件存放路径 
	 */
	public static  String getImagePath(Context context)
	{
		String sd = "/data/data/com.jxlife.dat/files/jx";
//		String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
		return sd;
	}
}
