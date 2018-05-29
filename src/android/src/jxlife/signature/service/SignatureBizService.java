package jxlife.signature.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.entity.StringEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import afinal.http.AjaxCallBack;
import afinal.http.AjaxParams;
import jxlife.channel.FinalHttp;
import jxlife.sigdto.SignImagesVo;
import jxlife.sigdto.SignatureBizSubmitDataDto;
import jxlife.sigdto.SignatureCommitDataDto;
import jxlife.sigdto.SignatureDelDataDto;
import jxlife.sigdto.SignatureDelSigPropDto;
import jxlife.sigdto.SignatureInitDataDto;
import jxlife.sigdto.SignatureRequestEntity;
import jxlife.sigdto.SignatureUploadImgDto;
import jxlife.sigdto.response.SignatureResponseBody;
import jxlife.signature.utils.JsonUtils;
import jxlife.signature.utils.NetWorkCallBack;
import jxlife.util.tools.SignatureConstants;

public class SignatureBizService {
	public static NetWorkCallBack callBack;
	public static Context context;
	private static SignatureBizService instance = null;

	private SignatureBizService(NetWorkCallBack callback, Context ctx) {
		callBack = callback;
		context = ctx;
	}

	public static SignatureBizService getInstance(NetWorkCallBack callback,
                                                  Context ctx) {
		if (instance == null) {
			instance = new SignatureBizService(callback, ctx);
		}else{
			callBack = callback;
			context = ctx;
		}
		return instance;
	}

	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
					SignatureResponseBody body = JsonUtils.fromJson(msg.obj.toString(),SignatureResponseBody.class);
					String msgCode = body.getMsgCode();
					if(msgCode!=null&&msgCode.equals("000000")){
						String data = body.getData();
						callBack.callSuccess(data);
					}else if(msgCode!=null&&msgCode.equals("JX000001")){
						SignatureDelSigPropDto signatureDelSigPropDto = new SignatureDelSigPropDto();
						signatureDelSigPropDto.setMsgCode(body.getMsgCode());
						signatureDelSigPropDto.setMsg(body.getMsg());
						String data = JsonUtils.toJsonObj(signatureDelSigPropDto);
						callBack.callSuccess(data);
					}else if(msgCode!=null&&msgCode.equals("JX000002")){
						SignatureDelSigPropDto signatureDelSigPropDto = new SignatureDelSigPropDto();
						signatureDelSigPropDto.setMsgCode(body.getMsgCode());
						String data = JsonUtils.toJsonObj(signatureDelSigPropDto);
						callBack.callSuccess(data);
					}else if(msgCode!=null&&msgCode.equals("JX000003")){
						SignatureDelSigPropDto signatureDelSigPropDto = new SignatureDelSigPropDto();
						signatureDelSigPropDto.setMsgCode(body.getMsgCode());
						signatureDelSigPropDto.setMsg(body.getMsg());
						String data = JsonUtils.toJsonObj(signatureDelSigPropDto);
						callBack.callFailure(data);
					}else if(msgCode!=null&&msgCode.equals("JX000004")){
						SignatureDelSigPropDto signatureDelSigPropDto = new SignatureDelSigPropDto();
						signatureDelSigPropDto.setMsgCode(body.getMsgCode());
						signatureDelSigPropDto.setMsg(body.getMsg());
						String data = JsonUtils.toJsonObj(signatureDelSigPropDto);
						callBack.callSuccess(data);
					}else if(msgCode!=null&&!msgCode.equals("000000")){
						String notiMsg =body.getMsg();
						callBack.callSuccessByMsg(notiMsg);
					}else{
						String notiMsg =body.getMsg();
						callBack.callFailure(notiMsg);
					}
				break;
			case 1:
				callBack.callFailure(msg.obj.toString());
			}
		}
	};

	// 获取模板数据
	public static void fetchBizTemplateData(final SignatureInitDataDto dto, final NetWorkCallBack callback, final Context ctx) {
		Runnable rb = new Runnable() {
			
			@Override
			public void run() {
				try {
					// 加载网络请求，获取远程文件资源并显示
					getInstance(callback,ctx);
					FinalHttp http = new FinalHttp();
					SignatureRequestEntity entity = new SignatureRequestEntity();
					entity.setBeanId("EsignTemplateAction");
					entity.setOperation("queryPadTemplate");
					entity.setData(JsonUtils.toJsonObj(dto));
					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json", new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							if(t!=null){
								Message msg = Message.obtain();
								msg.what = 0;
								msg.obj = t.toString();
								mHandler.sendMessage(msg);
							}
						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Message msg = Message.obtain();
							msg.what = 1;
							msg.obj = strMsg;
							mHandler.sendMessage(msg);
						}
					});
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		mHandler.post(rb);
	}
	
	//上传影像数据
	public static void uploadBizImg(final SignatureUploadImgDto dto, final List<SignImagesVo> fileList, final NetWorkCallBack callBack) throws FileNotFoundException{
		// 加载网络请求，获取远程文件资源并显示
		Runnable rb = new Runnable() {
			
			@Override
			public void run() {
				getInstance(callBack,context);
				
				FinalHttp http = new FinalHttp();
				AjaxParams params = new AjaxParams();
				params.put("filetype", dto.getFiletype());
				params.put("businesstype", dto.getBusinesstype());
				params.put("esignformid", dto.getEsignformid());
				params.put("initType","commonFile");
				List<File> files = new ArrayList<File>();
				for(SignImagesVo vo:fileList){
					if(!TextUtils.isEmpty(vo.getLocalPath())){
						files.add(new File(vo.getLocalPath()));
					}
				}
				try {
					params.put("files", files);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				http.post(SignatureConstants.SIGNATURE_REMOTE_UPLOAD_URL, params,  new AjaxCallBack<Object>() {

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						if(t!=null){
							Message msg = Message.obtain();
							msg.what = 0;
							msg.obj = t.toString();
							mHandler.sendMessage(msg);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						Message msg = Message.obtain();
						msg.what = 1;
						msg.obj = strMsg;
						mHandler.sendMessage(msg);
					}
				});
			}
		};
		mHandler.post(rb);
	}
	
	public static void submitBizDataAboutSignature(SignatureBizSubmitDataDto dto, NetWorkCallBack callback, Context ctx){
		try {
			getInstance(callback,ctx);
			FinalHttp http = new FinalHttp();
			SignatureRequestEntity entity = new SignatureRequestEntity();
			entity.setBeanId("EsignImageAction");
			entity.setOperation("saveImageInfoByPlug");
			entity.setData(JsonUtils.toJsonObj(dto));
			Log.w("json=======",JsonUtils.toJsonObj(entity));
			StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
			http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json", new AjaxCallBack<Object>() {

				@Override
				public void onSuccess(Object t) {
					super.onSuccess(t);
					if(t!=null){
						Message msg = Message.obtain();
						msg.what = 0;
						msg.obj = t.toString();
						mHandler.sendMessage(msg);
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo,
						String strMsg) {
					Message msg = Message.obtain();
					msg.what = 1;
					msg.obj = strMsg;
					mHandler.sendMessage(msg);
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void delRemoteSigImgs(final SignatureDelDataDto dto, final NetWorkCallBack callback, final Context ctx){
		Runnable rb = new Runnable() {
			@Override
			public void run() {
				try {
					getInstance(callback,ctx);
					FinalHttp http = new FinalHttp();
					SignatureRequestEntity entity = new SignatureRequestEntity();
					entity.setBeanId("EsignImageAction");
					entity.setOperation("deleteFormDoc");
					entity.setData(JsonUtils.toJsonObj(dto));
					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json", new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							if(t!=null){
								Message msg = Message.obtain();
								msg.what = 0;
								msg.obj = t.toString();
								mHandler.sendMessage(msg);
							}
						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Message msg = Message.obtain();
							msg.what = 1;
							msg.obj = strMsg;
							mHandler.sendMessage(msg);
						}
					});
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		};
		mHandler.post(rb);
	}
	
	public static void commitSigTask(final SignatureCommitDataDto dto, final NetWorkCallBack callback, final Context ctx){
		Runnable rb = new Runnable() {
			
			@Override
			public void run() {
				try {
					getInstance(callback,ctx);
					FinalHttp http = new FinalHttp();
					SignatureRequestEntity entity = new SignatureRequestEntity();
					entity.setBeanId("EsigntodoAction");
					entity.setOperation("completeSign");
					entity.setData(JsonUtils.toJsonObj(dto));
					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json", new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							if(t!=null){
								Message msg = Message.obtain();
								msg.what = 0;
								msg.obj = t.toString();
								mHandler.sendMessage(msg);
							}
						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Message msg = Message.obtain();
							msg.what = 1;
							msg.obj = strMsg;
							mHandler.sendMessage(msg);
						}
					});
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		};
		mHandler.post(rb);
	}
	
//	//获取群发短信的签名对象
//	public static void querySignObjectList(final SignatureObjDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					getInstance(callback,ctx);
//					FinalHttp http = new FinalHttp();
//					SignatureRequestEntity entity = new SignatureRequestEntity();
//					entity.setBeanId("EsignObjectAction");
//					entity.setOperation("querySignObjectList");
//					entity.setData(JsonUtils.toJsonObj(dto));
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json", new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							super.onSuccess(t);
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo,
//								String strMsg) {
//							super.onFailure(t, errorNo, strMsg);
//							Message msg = Message.obtain();
//							msg.what = 1;
//							msg.obj = strMsg;
//							mHandler.sendMessage(msg);
//						}
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}
	
	//生成电子签名的任务
//	public static void generateSignatureTask(final SignatureGenerateTaskDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("EsigntodoAction");
//				entity.setOperation("createTodoInfo");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json",new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo, String strMsg) {
//							if(strMsg!=null){
//								Message msg = Message.obtain();
//								msg.what = 1;
//								msg.obj = strMsg;
//								mHandler.sendMessage(msg);
//							}
//						}
//
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}
	//获取语音验证码
//	public static void getBroadcastSMSCode(final SMSRequestDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("SmsAction");
//				entity.setOperation("broadcastSmsCode");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json",new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo, String strMsg) {
//							if(strMsg!=null){
//								Message msg = Message.obtain();
//								msg.what = 1;
//								msg.obj = strMsg;
//								mHandler.sendMessage(msg);
//							}
//						}
//
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}
	
	//获取短信验证码
//	public static void getSmsCode(final SMSRequestDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("SmsAction");
//				entity.setOperation("sendSmsCode");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json",new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo, String strMsg) {
//							if(strMsg!=null){
//								Message msg = Message.obtain();
//								msg.what = 1;
//								msg.obj = strMsg;
//								mHandler.sendMessage(msg);
//							}
//						}
//
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}
	
//	public static void fetchWXQcodeContent(final SignatureWXQcodeDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("QRCodeAction");
//				entity.setOperation("createQRCode");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json",new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo, String strMsg) {
//							if(strMsg!=null){
//								Message msg = Message.obtain();
//								msg.what = 1;
//								msg.obj = strMsg;
//								mHandler.sendMessage(msg);
//							}
//						}
//
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}
	
	//获取微信转发的相关话术内容
//	public static void fetchWXShareContent(final SignatureWXContentDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("SmsAction");
//				entity.setOperation("queryWXContent");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json",new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo, String strMsg) {
//							if(strMsg!=null){
//								Message msg = Message.obtain();
//								msg.what = 1;
//								msg.obj = strMsg;
//								mHandler.sendMessage(msg);
//							}
//						}
//
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}
	
	//专门远程获取头像所用
//	public static void getPhotoUnderSomePeople(final SignatureQueryImageDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("EsignImageAction");
//				entity.setOperation("querySignObjectImage");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json", new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							super.onSuccess(t);
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo,
//								String strMsg) {
//							super.onFailure(t, errorNo, strMsg);
//							Message msg = Message.obtain();
//							msg.what = 1;
//							msg.obj = strMsg;
//							mHandler.sendMessage(msg);
//						}
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}
	
	//获取当前条目影像的接口
//	public static void queryImagesUnderDivid(final SignatureRequestImagesDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					getInstance(callback,ctx);
//					FinalHttp http = new FinalHttp();
//					SignatureRequestEntity entity = new SignatureRequestEntity();
//					entity.setBeanId("EsignImageAction");
//					entity.setOperation("querySignObjectImageList");
//					entity.setData(JsonUtils.toJsonObj(dto));
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json", new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							super.onSuccess(t);
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo,
//								String strMsg) {
//							super.onFailure(t, errorNo, strMsg);
//							Message msg = Message.obtain();
//							msg.what = 1;
//							msg.obj = strMsg;
//							mHandler.sendMessage(msg);
//						}
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}
	
	//生成电子签名的任务
//	public static void sendSmsRemind(final SignatureTaskSMSDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("SmsAction");
//				entity.setOperation("sendSmsRemind");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json",new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo, String strMsg) {
//							if(strMsg!=null){
//								Message msg = Message.obtain();
//								msg.what = 1;
//								msg.obj = strMsg;
//								mHandler.sendMessage(msg);
//							}
//						}
//
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}

	//取消电子签字任务
//	public static void signApplyCancel(final SignApplyCancelDto dto, final NetWorkCallBack callback, final Context ctx){
//
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("EsignformAction");
//				entity.setOperation("signApplyCancel");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json",new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo, String strMsg) {
//							if(strMsg!=null){
//								Message msg = Message.obtain();
//								msg.what = 1;
//								msg.obj = strMsg;
//								mHandler.sendMessage(msg);
//							}
//						}
//
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//
//	}
	//取消电子签字任务
//	public static void signApplyCancel_S16B1(final SignApplyCancelDto dto, final NetWorkCallBack callback, final Context ctx){
//
//		Runnable rb = new Runnable() {
//
//			@Override
//			public void run() {
//				getInstance(callback,ctx);
//				FinalHttp http = new FinalHttp();
//				SignatureRequestEntity entity = new SignatureRequestEntity();
//				entity.setBeanId("EsignformAction");
//				entity.setOperation("signApplyCancel");
//				entity.setData(JsonUtils.toJsonObj(dto));
//				try {
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json",new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo, String strMsg) {
//							if(strMsg!=null){
//								Message msg = Message.obtain();
//								msg.what = 1;
//								msg.obj = strMsg;
//								mHandler.sendMessage(msg);
//							}
//						}
//
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//
//	}
	/**发生jx00003异常时，重新发起签名*/
//	public static void reApplySign(final SignatureSignReApplyDataDto dto, final NetWorkCallBack callback, final Context ctx){
//		Runnable rb = new Runnable() {
//			@Override
//			public void run() {
//				try {
//					getInstance(callback,ctx);
//					FinalHttp http = new FinalHttp();
//					SignatureRequestEntity entity = new SignatureRequestEntity();
//					entity.setBeanId("EsignformAction");
//					entity.setOperation("signApplyAnew");
//					entity.setData(JsonUtils.toJsonObj(dto));
//					StringEntity requestBody = new StringEntity(JsonUtils.toJsonObj(entity),"utf-8");
//					http.post(SignatureConstants.SIGNATURE_REMOTE_URL, requestBody, "application/json", new AjaxCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object t) {
//							super.onSuccess(t);
//							if(t!=null){
//								Message msg = Message.obtain();
//								msg.what = 0;
//								msg.obj = t.toString();
//								mHandler.sendMessage(msg);
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable t, int errorNo,
//								String strMsg) {
//							super.onFailure(t, errorNo, strMsg);
//							Message msg = Message.obtain();
//							msg.what = 1;
//							msg.obj = strMsg;
//							mHandler.sendMessage(msg);
//						}
//					});
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		mHandler.post(rb);
//	}


}
