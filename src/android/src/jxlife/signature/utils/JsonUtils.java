package jxlife.signature.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jxlife.util.AESUtil;
import com.jxlife.util.RequestEntity;
import com.jxlife.util.ResponseEntity;

import org.json.JSONObject;

/** 
 * json 处理工具类
 *
 * @author	lee
 * @date	2015-8-12 下午2:08:45 
 * 
 */
public class JsonUtils {

	/**
	 * 统一获得Gson实例
	 *  
	 * @return Gson
	 * @author lee
	 */
	public static Gson getGson()
	{
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:sss").create();
		return gson;
	}
	
	/**
	 * 将请求实体类转换成JSON
	 * 
	 * @param entity
	 *            请求实体类
	 * @param data
	 *            业务实体类
	 * @return json
	 * @author lee
	 */
	public static String toJson(RequestEntity entity, Object data) {
		// 设置业务数据
		entity.setData(getGson().toJson(data));
		return toJson(entity);
	}

	/**
	 * 将请求实体类转换成json
	 * 
	 * @param entity
	 *            (业务数据已经放入)
	 * @return json
	 * @author lee
	 */
	public static String toJson(RequestEntity entity) {
		String json = toJsonObj(entity);
		//D.e(json);
		Utils.saveStringToSDCard(json, "request_response.xyz");
		// 加密
		json = AESUtil.encrypt(json);
		return json;
	}

	/**
	 * 类转换成json
	 *
	 * @param obj
	 *            类
	 * @author lee
	 */
	public static String toJsonObj(Object obj) {

		String json = getGson().toJson(obj);
		return json;
	}

	/**
	 * 解析服务器响应数据
	 *
	 * @param response
	 *            服务器返回数据
	 * @return ResponseEntity
	 * @author lee
	 */
	public static ResponseEntity fromJson(String response) {
		try {
			response = AESUtil.decrypt(response);
			Utils.saveStringToSDCard(response, "request_response.xyz");
			//D.largeLog(response);
			// 判断是否是json格式
			new JSONObject(response);
			ResponseEntity entity = fromJson(response, ResponseEntity.class);
			return entity;
		} catch (Exception e) {
			String errormsg="";//MyHttpClient.saveException(null, e, null);
			e.printStackTrace();
			ResponseEntity entity = new ResponseEntity();
			entity.setSuccess(false);
			entity.setMsg(errormsg);
			//D.e(response);
			Utils.saveStringToSDCard("~~~"+response, "request_response.xyz");
			return entity;
		}
		
		
	}

	/**
	 * 将json 处理成类(业务数据处理)
	 * 
	 * @param <T>
	 * @param data
	 *            业务数据
	 * @param t
	 *            业务类
	 * @return T 业务类
	 * @author lee
	 */
	public static <T> T fromJson(String data, Class<T> t) {
		T entity = getGson().fromJson(data, t);
		return entity;
	}

}
 