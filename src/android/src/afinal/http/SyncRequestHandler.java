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

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

import afinal.http.entityhandler.StringEntityHandler;
import jxlife.util.AppException;
import jxlife.util.MyHttpClient;
import jxlife.util.TimeOutException;

public class SyncRequestHandler {

	private final AbstractHttpClient client;
	private final HttpContext context;
	private final StringEntityHandler entityHandler = new StringEntityHandler();

	private int executionCount = 0;
	private String charset;

	public SyncRequestHandler(AbstractHttpClient client, HttpContext context, String charset) {
		this.client = client;
		this.context = context;
		this.charset = charset;
	}

	private Object makeRequestWithRetries(HttpUriRequest request) {
		
		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
//		while (retry) {
			try {
				//modified by liuxinglong 20170818
//				HttpResponse response = client.execute(request, context);
				HttpEntity entity = MyHttpClient.execute4HttpEntity(null, client, request);
				return entityHandler.handleEntity(entity,null,charset);
			} catch (TimeOutException e) {
				throw e;
			} catch (AppException e) {
				throw e;
			} catch (Exception e) {  
				String error = MyHttpClient.saveException(null, e, null);
				throw new AppException(error);
			} catch (Error e) {  
				String error = MyHttpClient.saveException(null, e, null);
				throw new AppException(error);
			}
//			} catch (TimeOutException e) {
//	        	e.printStackTrace();  
//				cause = new IOException(e.getMessage());
//	        	retry = retryHandler.retryRequest(cause, ++executionCount,context);
//			} catch (AppException e) {
//				e.printStackTrace();  
//				cause = new IOException(e.getMessage());
//				retry = retryHandler.retryRequest(cause, ++executionCount,context);
//			} catch (UnknownHostException e) {
//				cause = e;
//				retry = retryHandler.retryRequest(cause, ++executionCount,context);
//			} catch (IOException e) {
//				cause = e;
//				retry = retryHandler.retryRequest(cause, ++executionCount,context);
//			} catch (NullPointerException e) {
//				// HttpClient 4.0.x 之前的一个bug
//				// http://code.google.com/p/android/issues/detail?id=5255
//				cause = new IOException("NPE in HttpClient" + e.getMessage());
//				retry = retryHandler.retryRequest(cause, ++executionCount,context);
//			}catch (Exception e) {
//				cause = new IOException("Exception" + e.getMessage());
//				retry = retryHandler.retryRequest(cause, ++executionCount,context);
//			}
//		}
//		if(cause!=null)
//			throw cause;
//		else
//			throw new IOException("未知网络错误");
		
	}

	public Object sendRequest (HttpUriRequest... params) {
//		try {
			return makeRequestWithRetries(params[0]);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
	}

	
}
