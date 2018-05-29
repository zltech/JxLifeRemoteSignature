/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package afinal.http;

import android.app.Application;
import android.os.SystemClock;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;

import afinal.core.AsyncTask;
import afinal.http.entityhandler.EntityCallBack;
import afinal.http.entityhandler.FileEntityHandler;
import afinal.http.entityhandler.StringEntityHandler;
import jxlife.signature.utils.LoadingDialog;
import jxlife.signature.utils.Utils;
import jxlife.util.AppException;
import jxlife.util.MyHttpClient;
import jxlife.util.TimeOutException;
import jxlife.util.UpdateAppCallBackListener;


public class  HttpHandler  <T> extends AsyncTask<Object, Object, Object> implements EntityCallBack {

	private final AbstractHttpClient client;
	private final HttpContext context;
	
	private final StringEntityHandler mStrEntityHandler = new StringEntityHandler();
	private final FileEntityHandler mFileEntityHandler = new FileEntityHandler();
	
	private final AjaxCallBack<T> callback;

	private int executionCount = 0;
	private String targetUrl = null; //下载的路径
	private boolean isResume = false; //是否断点续传
	private String charset;
	private long l_incFileSize;
	private long eachCount;
	private LoadingDialog dialog;
	private long bean_incSize;
	private UpdateAppCallBackListener ac_callBack;

	public HttpHandler(AbstractHttpClient client, HttpContext context, AjaxCallBack<T> callback, String charset) {
		this.client = client;
		this.context = context;
		this.callback = callback;
		this.charset = charset;
	}

	public HttpHandler(AbstractHttpClient client, HttpContext context, AjaxCallBack<T> callback, String charset, long l_incFileSize, long eachCount, LoadingDialog dialog, long bean_incSize, UpdateAppCallBackListener ac_callBack) {
		this.client = client;
		this.context = context;
		this.callback = callback;
		this.charset = charset;
		this.l_incFileSize=l_incFileSize;
		this.eachCount=eachCount;
		this.dialog=dialog;
		this.bean_incSize=bean_incSize;
		this.ac_callBack=ac_callBack;
	}
	public void shutdown(){
		if(client!=null){
			ClientConnectionManager connect = client.getConnectionManager();
			if(connect!=null){
				connect.shutdown();
			}
		}
	}

	private void makeRequestWithRetries(HttpUriRequest request) throws IOException {
		if(isResume && targetUrl!= null){
			File downloadFile = new File(targetUrl);
			long fileLen = 0;
			if(downloadFile.isFile() && downloadFile.exists()){
				fileLen = downloadFile.length();
			}
			if(fileLen > 0)
				request.setHeader("RANGE", "bytes="+fileLen+"-");
		}
		
			try {
				if (!isCancelled()) {
					//modified by liuxinglong 20170818
//					HttpResponse response = client.execute(request, context);
					HttpEntity entity = MyHttpClient.execute4HttpEntity(null, client, request);
					
					Object responseBody = null;
					if (entity != null) {
						time = SystemClock.uptimeMillis();
						if(targetUrl!=null){
							responseBody = mFileEntityHandler.handleEntity(entity,this,targetUrl,isResume);
						}
						else{
							responseBody = mStrEntityHandler.handleEntity(entity,this,charset);
							Utils.saveStringToSDCard("响应内容：" + responseBody.toString(), "request_response.xyz");
						}
					}
					publishProgress(UPDATE_SUCCESS,responseBody);
				}
			} catch (TimeOutException e) {
	        	publishProgress(UPDATE_FAILURE,e,0,e.getMessage());
			} catch (AppException e) {
				publishProgress(UPDATE_FAILURE,e,0,e.getMessage());
			} catch (Exception e) {  
				String errormsg= MyHttpClient.saveException(null, e, null);
				e.printStackTrace();
				publishProgress(UPDATE_FAILURE,e,0,errormsg);
			} catch (Error e) {  
				String errormsg= MyHttpClient.saveException(null, e, null);
				e.printStackTrace();
				publishProgress(UPDATE_FAILURE,e,0,errormsg);
			}
	}

	@Override
	protected Object doInBackground(Object... params) {
		if(params!=null && params.length == 3){
			targetUrl = String.valueOf(params[1]);
			isResume = (Boolean) params[2];
		}
		try {
			publishProgress(UPDATE_START); // 开始
			makeRequestWithRetries((HttpUriRequest)params[0]);
			
		} catch (IOException e) {
			publishProgress(UPDATE_FAILURE,e,0,e.getMessage()); // 结束
		}

		return null;
	}

	private final static int UPDATE_START = 1;
	private final static int UPDATE_LOADING = 2;
	private final static int UPDATE_FAILURE = 3;
	private final static int UPDATE_SUCCESS = 4;

	@SuppressWarnings("unchecked")
	@Override
	protected void onProgressUpdate(Object... values) {
		int update = Integer.valueOf(String.valueOf(values[0]));
		switch (update) {
		case UPDATE_START:
			if(callback!=null)
				callback.onStart();
			break;
		case UPDATE_LOADING:
			if(callback!=null)
				callback.onLoading(Long.valueOf(String.valueOf(values[1])),Long.valueOf(String.valueOf(values[2])));
			break;
		case UPDATE_FAILURE:
			if(callback!=null)
				callback.onFailure((Throwable)values[1],(Integer)values[2],(String)values[3]);
			break;
		case UPDATE_SUCCESS:
			if(callback!=null)
				callback.onSuccess((T)values[1]);
			break;
		default:
			break;
		}
		super.onProgressUpdate(values);
	}
	
	public boolean isStop() {
		return mFileEntityHandler.isStop();
	}


	/**
	 * @param stop 停止下载任务
	 */
	public void stop() {
		mFileEntityHandler.setStop(true);
	} 
	//modified by liuxinglong 20170818
	private void handleResponse(HttpEntity entity) {
//		StatusLine status = response.getStatusLine();
//		Utils.saveStringToSDCard("响应码：" + status.getStatusCode(), "request_response.xyz");
//		if (status.getStatusCode() >= 300) {
//			String errorMsg = "response status error code:"+status.getStatusCode();
//			if(status.getStatusCode() == 416 && isResume){
//				errorMsg += " \n maybe you have download complete.";
//			}
//			try {
//				HttpEntity entity = response.getEntity();
//				Object responseBody = null;
//				if (entity != null) {
//					time = SystemClock.uptimeMillis();
//					if(targetUrl!=null){
//							responseBody = mFileEntityHandler.handleEntity(entity,this,targetUrl,isResume);
//					}
//					else{
//						responseBody = mStrEntityHandler.handleEntity(entity,this,charset);
//						Utils.saveStringToSDCard("响应内容：" + responseBody.toString(), "request_response.xyz");
//					}
//				}
//				if(responseBody!=null){					
//					errorMsg=responseBody.toString();
//				}
//			} catch (Exception e) {
//				errorMsg="获取服务器异常信息失败";
//			}
//			
//			publishProgress(UPDATE_FAILURE,new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()),status.getStatusCode() ,errorMsg);
//		} else {
			try {
//				HttpEntity entity = response.getEntity();
				Object responseBody = null;
				if (entity != null) {
					time = SystemClock.uptimeMillis();
					if(targetUrl!=null){
						responseBody = mFileEntityHandler.handleEntity(entity,this,targetUrl,isResume);
					}
					else{
						responseBody = mStrEntityHandler.handleEntity(entity,this,charset);
						Utils.saveStringToSDCard("响应内容：" + responseBody.toString(), "request_response.xyz");
					}
				}
				publishProgress(UPDATE_SUCCESS,responseBody);
				
			} catch (TimeOutException e) {
				//Log.e("HttpClient.post", "调用服务端接口失败", exception);
	        	e.printStackTrace();  
	        	publishProgress(UPDATE_FAILURE,e,0,e.getMessage());
			} catch (AppException e) {
				//Log.e("HttpClient.post", "调用服务端接口失败", exception);
				e.printStackTrace();  
				publishProgress(UPDATE_FAILURE,e,0,e.getMessage());
			} catch (Exception e) {  
				Application a = new Application();
				String errormsg= MyHttpClient.saveException(a.getApplicationContext(), e, null);
				e.printStackTrace();
				publishProgress(UPDATE_FAILURE,e,0,errormsg);
//	        } catch (IOException e) {
//				publishProgress(UPDATE_FAILURE,e,0,e.getMessage());
			}
			
//		}
	}
	
	
	private long time;
	@Override
	public void callBack(long count, long current,boolean mustNoticeUI) {
		if(callback!=null && callback.isProgress()){
			if(mustNoticeUI){
				publishProgress(UPDATE_LOADING,count,current);
			}else{
				long thisTime = SystemClock.uptimeMillis();
				if(thisTime - time >= callback.getRate()){
					time = thisTime ;
					publishProgress(UPDATE_LOADING,count,current);
				}
			}
		}
	}
	

}
