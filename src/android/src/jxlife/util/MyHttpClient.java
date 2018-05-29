package jxlife.util;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import jxlife.signature.utils.Utils;


public class MyHttpClient {
	
	
	/**
	 * 发送错误日志给服务端专用，防止无限循环
	 * @param client
	 * @param request
	 * @return
	 */
	public static String execute4Log(Context act, final HttpClient client , final HttpUriRequest request){
		String requestUUID = UUID.randomUUID().toString();
		request.addHeader("uuid", requestUUID);
		CallBack cb = new CallBack() {
			public HttpResponse doCall() throws ClientProtocolException,
					IOException {
				return (HttpResponse) client.execute(request);
			}
		};
		String response = (String) handlerResponse(act,cb,"1",false);
		Utils.saveStringToSDCard(">>>>>>>>>> UUID: "+requestUUID+"\n>>>>>>>>>> response: "+response, "request_response.xyz");
		return response;
	}
	
	public static String execute(Context act, final HttpClient client , final HttpUriRequest request){
		String requestUUID = UUID.randomUUID().toString();
		request.addHeader("uuid", requestUUID);
		CallBack cb = new CallBack() {
			public HttpResponse doCall() throws ClientProtocolException,
					IOException {
				return (HttpResponse) client.execute(request);
			}
		};
		String response = (String) handlerResponse(act,cb,"1",true);
		Utils.saveStringToSDCard(">>>>>>>>>> UUID: "+requestUUID+"\n>>>>>>>>>> response: "+response, "request_response.xyz");
		return response;
	}
	
	public static HttpEntity execute4HttpEntity(Context act, final HttpClient client , final HttpUriRequest request){
		request.addHeader("uuid", UUID.randomUUID().toString());
		CallBack cb = new CallBack() {
			public HttpResponse doCall() throws ClientProtocolException,
					IOException {
				return (HttpResponse) client.execute(request);
			}
		};
		return (HttpEntity)handlerResponse(act,cb,"2",true);
	}
	
	public static HttpResponse execute4HttpResponse(Context act, final HttpClient client , final HttpUriRequest request){
		request.addHeader("uuid", UUID.randomUUID().toString());
		CallBack cb = new CallBack() {
			public HttpResponse doCall() throws ClientProtocolException,
					IOException {
				return (HttpResponse) client.execute(request);
			}
		};
		return (HttpResponse)handlerResponse(act,cb,"3",true);
	}
	
	


	interface CallBack {
		public HttpResponse doCall() throws ClientProtocolException,
				IOException;
	}

	private static Object handlerResponse(Context act,CallBack callback,String type,boolean sendFlag) {
		int errorCode = 0;
		String errorMsg = "";
		String exceptionStackTrace = "";
		String flag="C";
		try {
			HttpResponse response = null;
			String rsData ="";
			if("1".equals(type)){
				// 请求响应码
				response = callback.doCall();
				StatusLine status = response.getStatusLine();
				// 响应数据
				rsData = EntityUtils.toString(response.getEntity(), "UTF-8");
				errorCode = status.getStatusCode();
				if (errorCode == 200 && !isCustormError(rsData)) {
					// 响应码200正常响应
					// 是否异常结构？
					return  rsData;
				}
//				// 异常信息
//				errorMsg = parseError(rsData);
//				if (!(errorMsg != null && errorMsg.endsWith("-S]"))) {
//					errorMsg = "服务器处理失败(" + errorMsg + ")";
//				}
			}else{
				// 请求响应码
				response = callback.doCall();
				StatusLine status = response.getStatusLine();
				errorCode = status.getStatusCode();
				if (errorCode == 200 ) {
					// 响应码200正常响应
					// 是否异常结构？
					if("2".equals(type)){
						return  response.getEntity();
					}
					return  response;
				}
				rsData = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
			// 响应数据
			// 异常信息
			errorMsg =rsData;
			if(500==errorCode){
				if (errorMsg != null && errorMsg.endsWith("-S]")) {
					errorMsg = "后台服务异常，请联系IT运维人员处理，具体原因为："+errorMsg;
				} else {
					errorMsg = "后台服务异常，请联系IT运维人员处理，具体原因为：";
				}
			}else if(403==errorCode){
				errorMsg = "后台服务异常，请联系IT运维人员处理，具体原因为：";
			}else if(404==errorCode){
				errorMsg = "后台服务异常，请联系IT运维人员处理，具体原因为：";
			}else if(502==errorCode){
				errorMsg = "后台服务异常，请联系IT运维人员处理，具体原因为：";
			}else{
				errorMsg = "后台服务异常，请联系IT运维人员处理，具体原因为：";
			}
			
			flag="S";
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			errorCode = 600;
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			exceptionStackTrace = getStackTrace(e);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			errorCode = 601;
			exceptionStackTrace = getStackTrace(e);
		} catch (HttpHostConnectException e) {
			e.printStackTrace();
			errorCode = 600;
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			exceptionStackTrace = getStackTrace(e);
		} catch (SocketException e) {
			e.printStackTrace();
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			exceptionStackTrace = getStackTrace(e);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			errorCode = 601;
			exceptionStackTrace = getStackTrace(e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			errorMsg = "系统运行发生异常，请重新操作或重新登录系统，如果重新操作还不能解决，请联系IT运维人员处理，具体原因为：";
			errorCode = 700;
			exceptionStackTrace = getStackTrace(e);
		} catch (IOException e) {
			e.printStackTrace();
			errorMsg = "系统运行发生异常，请重新操作或重新登录系统，如果重新操作还不能解决，请联系IT运维人员处理，具体原因为：";
			errorCode = 700;
			exceptionStackTrace = getStackTrace(e);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = "系统运行发生异常，请重新操作或重新登录系统，如果重新操作还不能解决，请联系IT运维人员处理，具体原因为：";
			errorCode = 700;
			exceptionStackTrace = getStackTrace(e);
		} catch (Error e) {
			e.printStackTrace();
			errorMsg = "系统运行发生异常，请重新操作或重新登录系统，如果重新操作还不能解决，请联系IT运维人员处理，具体原因为：";
			errorCode = 701;
			exceptionStackTrace = getStackTrace(e);
		}

		// 发送日志信息
		String id = new Date().getTime() + "";
		String key = "[" + errorCode + "-" + id + "-"+flag+"]";
		if (!(errorMsg != null && errorMsg.endsWith("-S]"))) {
			errorMsg = errorMsg + key;
		}
		// 选择性发送服务端记录
		if (!(errorMsg.endsWith("-S]") || 600 == errorCode||601 == errorCode)&&sendFlag) {
			// 线程发送错误日志给服务端记录，并打印输出,为了规避死循环，需要处理发送日志请求失败不再发送。
			// todo ==> errorCode id key errorMsg exceptionStackTrace
			savelog(act, errorCode+"", id, flag, exceptionStackTrace, "");
		}

		if (600 == errorCode||601 == errorCode) {
			// 特殊处理超时异常
			throw new TimeOutException(errorMsg);
		}
		// 其他自定义异常统一抛出
		throw new AppException(errorMsg);
	}

	private static boolean isCustormError(String msg) {
		if (msg != null && msg.endsWith("-S]")) {
			return true;
		}
		return false;
	}

//	private static String parseError(String msg) {
////		if (msg != null && msg.endsWith("{Err}")) {
////			return msg.substring(0, msg.length() - 5);
////		}
//		return msg;
//	}

	/**
	 * 获取异常的堆栈信息
	 * 
	 * @param t
	 * @return
	 */
	private static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try
		{
			t.printStackTrace(pw);
			return sw.toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			pw.close();
		}
		return "";
	}
	
	public static String saveException(Context context, Throwable  e , String user){
		String errorCode = "700" ;
		String errorMsg = "系统运行发生异常，请重新操作或重新登录系统，如果重新操作还不能解决，请联系IT运维人员处理，具体原因为：";
		String id = new Date().getTime() + "";
		String key = "[" + errorCode + "-" + id + "-C]";
		try {
			throw new Exception(e);
		} catch (ConnectTimeoutException exception) {
			errorCode = "600";
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			key = "[" + errorCode + "-" + id + "-C]";
		} catch (SocketTimeoutException exception) {
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			errorCode = "601";
			key = "[" + errorCode + "-" + id + "-C]";
		} catch (HttpHostConnectException exception) {
			errorCode = "600";
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			key = "[" + errorCode + "-" + id + "-C]";
		} catch (SocketException exception) {
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			errorCode = "600";
			key = "[" + errorCode + "-" + id + "-C]";
		} catch (UnknownHostException exception) {
			errorMsg = "您的网络连接超时，请先检查网络是否可用，然后重试当前操作，如果依然出现本提示，请联系IT运维人员处理，具体原因为：";
			errorCode = "600";
			key = "[" + errorCode + "-" + id + "-C]";
		} catch (Exception exception) {
			String stackTrace = getStackTrace(e);
			errorCode = "700" ;
			errorMsg = "系统运行发生异常，请重新操作或重新登录系统，如果重新操作还不能解决，请联系IT运维人员处理，具体原因为：";
			key = "[" + errorCode + "-" + id + "-C]";
			savelog(context, errorCode, id,"C", stackTrace, null);
		}
		//保存错误日志到本地
		return errorMsg+key;
	}
	
	public static void savelog(final Context context, final String errorCode, final String id, 
			final String reqType, final String stackTrace, final String user){

	}

	private static String getStackTraceInfo(Thread td){
        StringBuffer sBuffer = new StringBuffer();
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for(StackTraceElement s : stackTrace){
            sBuffer.append("类名：" + s.getClassName() + "  ,  java文件名：" + s.getFileName() + ",  当前方法名字：" + s.getMethodName() + ""
                    + " , 当前代码是第几行：" + s.getLineNumber()+"\n");
        	System.out.println("类名：" + s.getClassName() + "  ,  java文件名：" + s.getFileName() + ",  当前方法名字：" + s.getMethodName() + ""
                    + " , 当前代码是第几行：" + s.getLineNumber() + ", " );
        }
        return sBuffer.toString();
    }
}
