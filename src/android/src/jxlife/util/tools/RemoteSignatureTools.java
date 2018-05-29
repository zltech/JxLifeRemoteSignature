package jxlife.util.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import cn.org.bjca.anysign.android.R3.api.AnySignBuild;
import cn.org.bjca.anysign.android.R3.api.BioType;
import cn.org.bjca.anysign.android.R3.api.Interface.OnSignatureResultListener;
import cn.org.bjca.anysign.android.R3.api.Interface.RecordStatusListener;
import cn.org.bjca.anysign.android.R3.api.MassInputType;
import cn.org.bjca.anysign.android.R3.api.PhotoObj;
import cn.org.bjca.anysign.android.R3.api.SignRule;
import cn.org.bjca.anysign.android.R3.api.SignRule.KWRule;
import cn.org.bjca.anysign.android.R3.api.SignRule.KWRule.SigAlignMethod;
import cn.org.bjca.anysign.android.R3.api.SignRule.SignRuleType;
import cn.org.bjca.anysign.android.R3.api.SignatureAPI;
import cn.org.bjca.anysign.android.R3.api.SignatureObj;
import cn.org.bjca.anysign.android.R3.api.Signer;
import cn.org.bjca.anysign.android.R3.api.exceptions.ApiNotInitializedException;
import cn.org.bjca.anysign.android.R3.api.exceptions.BadFormatException;
import cn.org.bjca.anysign.android.R3.api.exceptions.ConfigNotFoundException;
import cn.org.bjca.anysign.android.R3.api.exceptions.DocumentNotSpecifiedException;
import cn.org.bjca.anysign.android.R3.api.exceptions.SignatureNotGeneratedException;
import cn.org.bjca.anysign.android.R3.api.exceptions.WrongContextIdException;
import jxlife.sigdto.SiganatureCordovaImageVo;
import jxlife.sigdto.SignImagesVo;
import jxlife.sigdto.SignatureBizSubmitDataDto;
import jxlife.sigdto.SignatureDeesignImageDto;
import jxlife.sigdto.SignatureEsignFlagDataDto;
import jxlife.sigdto.SignatureUploadImgDto;
import jxlife.sigdto.SignatureWrapDeeimgAndSBimg;
import jxlife.sigdto.cordova.CordovaImgsAndDivResponse;
import jxlife.sigdto.cordova.CordovaResponse;
import jxlife.sigdto.response.SignatureEsignObjectInfoResponseDto;
import jxlife.sigdto.response.SignatureSigRiskAndImgConfirmResponseDto;
import jxlife.signature.service.SignatureBizService;
import jxlife.signature.utils.JsonUtils;
import jxlife.signature.utils.LoadingDialog;
import jxlife.signature.utils.LoadingDialogUtils;
import jxlife.signature.utils.NetWorkCallBack;
import jxlife.signature.utils.Utils;
import jxlife.util.AppConfig;
import jxlife.util.ImageUtils;

public class RemoteSignatureTools {
	
	private static Activity ctx;
	private SignatureAPI api;
	private SignatureAPI api2;
	private Map<String,String> template_seria_map = new HashMap<String,String>();
	private static RemoteSignatureTools instance = null;
	private String template_serial;
	private static LoadingDialog dialog = null;
	private String userName = "";
	public static CallbackContext callBack;
	private String sigType = "";
	private String businessType = "";
	private String divId = "";
	private String relaId = "";
	private String imageType = "PNG";
	private static Map<String,Map<String,SignatureWrapDeeimgAndSBimg>>sigNatureDeesignImageMap;
	private static Map<String,SignatureWrapDeeimgAndSBimg> singleWrapDeeimgAndSBimg;
	private static Map<String,SignatureEsignObjectInfoResponseDto> deesignImageDtoMap;
	private CordovaResponse cordovaResponse = new CordovaResponse();
	private static Map<String,SignatureEsignFlagDataDto> esignKeyWordsMap = null;
	
	private RemoteSignatureTools(){
		
	}
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				dialog = LoadingDialog.createDialog(ctx);
				dialog.setMessage("正在处理图片...");
				dialog.show();
				break;
			case 1:
				//影像上传->抄写风险语提示
				SignatureUploadImgDto dto = new SignatureUploadImgDto();
				dto.setBusinesstype("WPE_LEFORM");
				dto.setEsignformid(UUID.randomUUID().toString());
				dto.setFiletype("image/png");
				
				List<SignImagesVo> fileList = new ArrayList<SignImagesVo>();
				Set<Entry<String, SignatureWrapDeeimgAndSBimg>> set = singleWrapDeeimgAndSBimg.entrySet();
				if(set!=null&&set.size()>0){
					for(Entry<String,SignatureWrapDeeimgAndSBimg> entry: set){
						SignatureWrapDeeimgAndSBimg vo = entry.getValue();
						SignImagesVo images = vo.getSignImagesVo();
						fileList.add(images);
					}
				}
				LoadingDialogUtils.dismissLoadingDialog(ctx,"");
				try {
					LoadingDialogUtils.showLoadingDialog(ctx, "影像上传中...");
					SignatureBizService.uploadBizImg(dto,fileList, new NetWorkCallBack() {
						
						@Override
						public void callSuccess(String s) {
							if(s!=null){
								//{"msgCode":"000000","msg":null,"data":"{\"1499935049241.jpg\":\"D:\\\\image\\\\mobile\\\\image\\\\png\\\\2017\\\\07\\\\13\\\\WPE_LEFORM\\\\aa177e9d-feb6-4659-85e9-82f7849a165e\\\\82cdcbd8-7af3-4b70-ad52-5ed7773ce72e.jpg\"}"}
								try {
									final Map<String,CordovaImgsAndDivResponse> cordovaImgMap = new HashMap<String,CordovaImgsAndDivResponse>();//定义一个传送到前端的影像的列表的数据
									JSONObject data = new JSONObject(s);
									SignatureBizSubmitDataDto dto = new SignatureBizSubmitDataDto();
									dto.setBusinessType(businessType);
									dto.setDivId(divId);
									dto.setEncryptData(getPacketData());
									dto.setRelaId(relaId);
									dto.setResource("1");
									dto.setSignKind("2");
									dto.setWoNo(template_seria_map.get(divId));//(UUID.randomUUID().toString());
									Set<Entry<String, SignatureWrapDeeimgAndSBimg>> set = singleWrapDeeimgAndSBimg.entrySet();
									List<SignImagesVo> list = new ArrayList<SignImagesVo>();
									if(set!=null&&set.size()>0){
										for(Entry<String,SignatureWrapDeeimgAndSBimg> entry: set){
											SignatureWrapDeeimgAndSBimg vo = entry.getValue();
											SignImagesVo images = vo.getSignImagesVo();
											String certiName = images.getCertName();

											//add by chenqing
											//如承保前撤件，银行账户变更，只上传一张图片，不管顺序，都会有二个图片对象待上传，非空判断。
											if(null == certiName || "".equals(certiName)|| certiName.length()<1){
												continue;
											}

											String rePath = data.getString(certiName);
											images.setRemotePath(rePath);
											vo.setSignImagesVo(images);
											
											SignatureDeesignImageDto signatureDeesignImageDto = vo.getSignatureDeesignImageDto();
											signatureDeesignImageDto.setOriginalPath(images.getLocalPath());
											signatureDeesignImageDto.setImagePath(images.getRemotePath());
											vo.setSignatureDeesignImageDto(signatureDeesignImageDto);
											
											CordovaImgsAndDivResponse cordovaImgsAndDivResponse  = new CordovaImgsAndDivResponse();
											cordovaImgsAndDivResponse.setLocalPath(images.getLocalPath());
											cordovaImgsAndDivResponse.setSubdivid(images.getSubdivid());
											cordovaImgsAndDivResponse.setImageKind(images.getImagekind());
											cordovaImgMap.put(images.getSubdivid(), cordovaImgsAndDivResponse);
											singleWrapDeeimgAndSBimg.put(entry.getKey(), vo);
											list.add(images);
										}
									}
									
									dto.setSignImages(list);
									//将相关数据提交到服务器，再做一次请求
									LoadingDialogUtils.dismissLoadingDialog(ctx, "");
									LoadingDialogUtils.showLoadingDialog(ctx, "数据上传中...");
									SignatureBizService.submitBizDataAboutSignature(dto, new NetWorkCallBack() {
										
										@Override
										public void callSuccess(String s) {
											SignatureSigRiskAndImgConfirmResponseDto signatureSigRiskAndImgConfirmResponseDto = JsonUtils.fromJson(s, SignatureSigRiskAndImgConfirmResponseDto.class);
											String handleDate = signatureSigRiskAndImgConfirmResponseDto.getHandleDate();//投保日期
											String reminder = signatureSigRiskAndImgConfirmResponseDto.getReminder();//温馨提示
											Map bigMap = new HashMap();
											CordovaResponse response = new CordovaResponse();
											response.setSuccess(true);
											for(Entry<String,CordovaImgsAndDivResponse> e: cordovaImgMap.entrySet()){
												CordovaImgsAndDivResponse cordovaImgsAndDivResponse  = e.getValue();
												String base64 = decodeImage2Base64((cordovaImgsAndDivResponse.getLocalPath()));
												cordovaImgsAndDivResponse.setImgBase64(base64);
												cordovaImgMap.put(e.getKey(), cordovaImgsAndDivResponse);
											}
											bigMap.put("source", "签名来源于本地");
											bigMap.put("handleDate", handleDate);
											bigMap.put("reminder", reminder);
											bigMap.put("cordovaImgMap", cordovaImgMap);
											response.setData(JsonUtils.toJsonObj(bigMap));
											LoadingDialogUtils.dismissLoadingDialog(ctx, "");
											callBack.success(JsonUtils.toJsonObj(response));//回调将影像放到页面中去
										}
										
										@Override
										public void callFailure(String s) {
											CordovaResponse response = new CordovaResponse();
											response.setSuccess(false);
											response.setData(s);
											LoadingDialogUtils.dismissLoadingDialog(ctx, "");
											callBack.error(JsonUtils.toJsonObj(response));
										}

										@Override
										public void callSuccessByMsg(String s) {
											CordovaResponse response = new CordovaResponse();
											response.setSuccess(false);
											response.setData(s);
											LoadingDialogUtils.dismissLoadingDialog(ctx, "");
											callBack.error(JsonUtils.toJsonObj(response));
											
										}
									}, ctx);
								} catch (JSONException e) {
									LoadingDialogUtils.dismissLoadingDialog(ctx, "");
									e.printStackTrace();
								}
							}
						}
						
						@Override
						public void callFailure(String s) {
							CordovaResponse response = new CordovaResponse();
							response.setSuccess(false);
							response.setData(s);
							LoadingDialogUtils.dismissLoadingDialog(ctx, "");
							callBack.error(JsonUtils.toJsonObj(response));
						}

						@Override
						public void callSuccessByMsg(String s) {
							CordovaResponse response = new CordovaResponse();
							response.setData(s);
							LoadingDialogUtils.dismissLoadingDialog(ctx, "");
							response.setSuccess(false);
							callBack.success(JsonUtils
									.toJsonObj(response));
							
						}
					});
				} catch (FileNotFoundException e) {
					CordovaResponse response = new CordovaResponse();
					response.setData("影像文件不存在，烦请重新签名");
					LoadingDialogUtils.dismissLoadingDialog(ctx, "");
					response.setSuccess(false);
					callBack.success(JsonUtils
							.toJsonObj(response));
					e.printStackTrace();
				}
				break;
			case 3:
				LoadingDialogUtils.dismissLoadingDialog(ctx,"");
				sign(0);
				break;
			case 4:
				//针对于抄写风险提示做的特殊处理
				Map<String,CordovaImgsAndDivResponse> cordovaImgMap = new HashMap<String,CordovaImgsAndDivResponse>();
				SignImagesVo vo = (SignImagesVo) msg.obj;
				if(vo!=null){
					String subDivid = vo.getSubdivid();
					String localPath = vo.getLocalPath();
					CordovaResponse response = new CordovaResponse();
					if(localPath!=null&&new File(localPath).exists()){
						CordovaImgsAndDivResponse cordovaImgsAndDivResponse  = new CordovaImgsAndDivResponse();
						String base64 = decodeImage2Base64((localPath));
						cordovaImgsAndDivResponse.setImgBase64(base64);
						cordovaImgsAndDivResponse.setLocalPath(localPath);
						cordovaImgsAndDivResponse.setImgBase64(decodeImage2Base64(localPath));
						cordovaImgMap.put(subDivid,cordovaImgsAndDivResponse);
						response.setSuccess(true);
						response.setData(JsonUtils.toJsonObj(cordovaImgMap));
						
						api2 = api;
						
						LoadingDialogUtils.dismissLoadingDialog(ctx,"");
						callBack.success(JsonUtils.toJsonObj(response));
					}else{
						response.setSuccess(false);
						response.setData("签字没有成功，请重试");
						LoadingDialogUtils.dismissLoadingDialog(ctx,"");
						callBack.error(JsonUtils.toJsonObj(response));
					}
				}
				break;
			}
		}
	};
	
/*	public static class MyHandler extends Handler{
		WeakReference<Activity> softReference;

		public MyHandler(Activity activity) {
			super();
			this.softReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				dialog = LoadingDialog.createDialog(ctx);
				dialog.setMessage("正在处理图片...");
				dialog.show();
				break;
			case 1:
				//影像上传->抄写风险语提示
				String filePath = signatureUploadImage;
				SignatureUploadImgDto dto = new SignatureUploadImgDto();
				dto.setBusinesstype("WPE_LEFORM");
				dto.setEsignformid(UUID.randomUUID().toString());
				dto.setFiletype("image/png");
				try {
					SignatureBizService.uploadBizImg(dto,filePath, new NetWorkCallBack() {
						
						@Override
						public void callSuccess(String s) {
							if(s!=null){
								
							}
						}
						
						@Override
						public void callFailure(String s) {
							
						}
					});
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
*/
	
	public static RemoteSignatureTools getInstance(Activity context, CallbackContext callback, SiganatureCordovaImageVo siganatureCordovaImageVo, Map<String,SignatureEsignFlagDataDto> m_esignKeyWordsMap) {
		ctx = context;
		callBack = callback;
		sigNatureDeesignImageMap = siganatureCordovaImageVo.getImageMap();
		esignKeyWordsMap = m_esignKeyWordsMap;
		if (instance == null) {
			instance = new RemoteSignatureTools();
		}
		return instance;
	}
	
	/**
	 * 初始化签名
	 */
	public boolean init(String username, JSONArray args,String tag) {
		this.userName = username;
		boolean isSuccess = true;
		AnySignBuild.Default_Cert_Signing_Alg = "SM2";
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) ctx).getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(dm.density != 1)
		{
			// 华为8寸pad  设置为150显示不完整
			AnySignBuild.DEFAULT_GRID_SIGN_GRID_WIDTH = 145;
			AnySignBuild.DEFAULT_GRID_SIGN_GRID_HEIGHT = 145;
		}

		SignRule rule;
		
		try {
				JSONObject bean = args.getJSONObject(0);
				sigType = bean.getString("signType");
				businessType = bean.getString("businessType");
				divId = bean.getString("divId");
				relaId= bean.getString("relaId");
				singleWrapDeeimgAndSBimg = sigNatureDeesignImageMap.get(divId);
				int m_size = singleWrapDeeimgAndSBimg.size();
				SignatureEsignFlagDataDto signatureEsignFlagDataDto = esignKeyWordsMap.get(divId);
				try {
					// 释放资源
					if(m_size==3){
						if(tag!=null){
							if (api != null)
								api.finalizeAPI();
							
							if(api2!=null){
								api2.finalizeAPI();
								api2 = new SignatureAPI(ctx, null, "30731174");
							}else{
								api2 = new SignatureAPI(ctx, null, "30731174");
							}
							template_serial = Utils.getUUID();
							template_seria_map.put(divId, template_serial);
						}
							api = api2;
					}else{
						if (api != null)
							api.finalizeAPI();
						// 初始化信手书API,参数1：上下文对象，参数2
						// xssData配置文件名称，填NULL即默认配置文件，参数3：业务渠道号，参数4：模板序列号
						api = new SignatureAPI(ctx, null, "30731174");
						template_serial = Utils.getUUID();
						template_seria_map.put(divId, template_serial);
					}
					// 加载模板数据、业务单
					// resourceByte = this.getResources().openRawResource(R.raw.test);
					// bTemplate = new byte[resourceByte.available()];
					// resourceByte.read(bTemplate);
					// 配置此次签名对应的模板数据
					// 参数1：表示模板类型：10: XML格式，11：HTML格式，12：PDF格式，13：JSON格式
					// 参数2：表示模板数据byte数组类型
					// 参数3：template_serial 业务流水号
					// String jsonObject =
					// "{\"holder\":\"邱天宇\",\"policyCode\": \"105545023021235\"}";
					// bTemplate = jsonObject.getBytes("UTF-8");
					//template_seria_map.put(divId, template_serial);
					api.setTemplate(12, new byte[] { 1 }, template_seria_map.get(divId), "1130");

				} catch (Exception e1) {
					isSuccess = false;
					e1.printStackTrace();
				}
				
				// 抄写风险栏
				if ("2".equals(sigType)) {
					// 配置30签名对象
					try {
						// 获取签名定位规则实例
						rule = SignRule.getInstance(SignRuleType.TYPE_KEY_WORD);
						//rule.setServerConfigRule("1123");
						//rule.setKWRule(new SignRule.KWRule("", SignRule.KWRule.SigAlignMethod.to_right_of_keyword, 10, 1, 1));// 设置关键字信息
						rule.setKWRule(new KWRule(signatureEsignFlagDataDto.getCopyWords(), SigAlignMethod.overlap, 0, 1));// 设置关键字信息
						// 配置一个批示对象：30
						// 参数1：表示签名对象，取值为[30,40)、[300,400)
						// 参数2：上面创建的签名规则
						// 参数3：批注内容
						SignatureObj obj_30 = new SignatureObj(30, rule, bean.getString("transcriptContent"));
						obj_30.nessesary = true;// 是否为必签项
						obj_30.IsTSS = false;
						// 此签名对象签名人信息 参数1：姓名，参数2：唯一id,身份证
						//obj_30.Signer = new Signer(bean.getString("signObject"), "111");

						obj_30.mass_dlg_type = MassInputType.Normal;
						// 每行显示的字数
						obj_30.mass_words_in_single_line = 12;

						// 每个字的高度dip
						obj_30.mass_word_height = 75;
						// 每个字的宽度dip
						obj_30.mass_word_width = 75;
						// 30签名对象注册到接口中
						api.addSignatureObj(obj_30);
					} catch (Exception e) {
						e.printStackTrace();
						isSuccess = false;
					}
				} 
					// 配置23签名对象
					// 获取签名定位规则实例，采用关键字的方式
				SignRule ruleObj = SignRule.getInstance(SignRuleType.TYPE_KEY_WORD);
					// 使用关键字方式定位签名图片位置
					// 参数1：keyWord - 关键字
					// 参数2：alignMethod - 签名图片相对于关键字分布方法
					// at_right_bottom_corner_of_keyword
					// 签字图片左上角和关键字右下角重合，可能额外附加偏移量
					// below_keyword 签字图片位于关键字正下方，中心线对齐
					// overlap 签字图片和关键字矩形重心重合
					// to_right_of_keyword 签字图片位于关键字正右方，中心线对齐
					// 参数3：offset - 按照alignMethod定位后，额外赋予的偏移量，单位dip
					//rule.setKWRule(new SignRule.KWRule("", SignRule.KWRule.SigAlignMethod.to_right_of_keyword, 10, 1, 1));// 设置关键字信息
					ruleObj.setKWRule(new KWRule(signatureEsignFlagDataDto.getObjectLabel(), SigAlignMethod.below_keyword, 0, 1));// 设置关键字信息
//					ruleObj.setKWRule(new KWRule(bean.getString("positionKeyWords"), SigAlignMethod.below_keyword, 0, 1));
					// 配置一个签名对象：23
					// 参数1：表示签名对象，取值为[20,30)、[200,300)
					// 参数2：上面创建的签名规则
					// 参数3：签名框顶部的标题
					// 参数4：标题中如果有需要突出显示的字，该参数代表突显字的起始索引，从0开始
					// 参数5：标题中如果有需要突出显示的字，该参数代表突显字的结束索引
					String signLabelText=bean.getString("signLabelText");
					if("".equals(signLabelText)){
						signLabelText=signatureEsignFlagDataDto.getObjectLabel();
					}
//					SignatureObj obj_23 = new SignatureObj(bean.getInt("sortNo") + 20, ruleObj, bean.getString("signLabelText"), 0, bean.getString("signLabelText").length());
					SignatureObj obj_23 = new SignatureObj(bean.getInt("sortNo") + 20, ruleObj, signLabelText, 0, signLabelText.length());
					obj_23.IsTSS = false;
					obj_23.nessesary = true;// 是否为必签项
					// 此签名对象签名人信息 参数1：姓名，参数2：唯一id,身份证
					obj_23.Signer = new Signer(bean.getString("signObject"), "111");
					// 22签名对象注册到接口中
					api.addSignatureObj(obj_23);
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}

		try {
			// 配置一个拍照对象：50
			// 若启动照相功能，请在配置文件中添加相关权限配置及 <activity
			// android:name="cn.org.bjca.anysign.android.R2.api.CameraActivity"
			// android:screenOrientation="landscape" ></activity>
			// 参数1：对象id 取值为[50,60)、[500,600)
			// 参数2：表示是否将该图片的加密数据放到提交数据缓存中
			PhotoObj photoObj = new PhotoObj(50, true);
			photoObj.heightPx = 960;
			photoObj.widthPx = 720;
			photoObj.cancelable = true;
			// 设置可以从相册选取图片
			// photoObj.openFromGallary = true;
			// photoObj.applyConfigOnGalleryPic = false;
			// 设置黑白拍照
			// photoObj.effect = Effect.mono;
			// 50拍照对象注册到接口中
			api.addPhotoObj(photoObj);
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}

		// 将上述对信手书API的配置进行提交
		api.commit();

		api.setOnSignatureResultListener(new OnSignatureResultListener() {
			@Override
			public void onSignatureResult(int data_type, Bitmap signature) {
				//new MyHandler(ctx).sendEmptyMessage(0);
				LoadingDialogUtils.showLoadingDialog(ctx, "签名数据处理中...");
				Bitmap newbitmap = ImageUtils.dealImage(signature, userName,false);
				String path = AppConfig.getImagePath(ctx) + "/" + System.currentTimeMillis() + ".png";
				if (ImageUtils.savePicToSdcard(newbitmap, path, false,imageType)) {
					Message msg = Message.obtain();
					msg.what = 1;//签名
					msg.obj = sigType;
					if(singleWrapDeeimgAndSBimg.get(sigType)!=null){
						if(singleWrapDeeimgAndSBimg.get(sigType).getSignImagesVo()!=null){
							singleWrapDeeimgAndSBimg.get(sigType).getSignImagesVo().setLocalPath(path);
							if(!TextUtils.isEmpty(path)){
								String fileName = new File(path).getName();
								singleWrapDeeimgAndSBimg.get(sigType).getSignImagesVo().setCertName(fileName);
								if(!sigType.equals("2")){
									mHandler.sendMessage(msg);
								}else{
									msg.what = 4;
									SignImagesVo vo = singleWrapDeeimgAndSBimg.get(sigType).getSignImagesVo();
									msg.obj = vo;
									mHandler.sendMessage(msg);
								}
							}
						}
					}
				}
				// 签名图片

				// ImageView iv = new ImageView(MainActivity.this);
				// iv.setBackgroundColor(Color.WHITE);
				// iv.setImageBitmap(signature);
				// //
				// new
				// AlertDialog.Builder(MainActivity.this).setView(iv).show();
			}

			@Override
			public void onDialogCancel(int data_type) {

			}

			@Override
			public void onDialogDismiss(int data_type) {

			}

			@Override
			public void onBufferSaved(boolean saveRes) {

			}

			@Override
			public void onDataDeleted(int data_type, boolean deleteRes) {

			}

			@Override
			public void onLowMemory() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSignTrack(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

		});

		api.setRecordStatusListener(new RecordStatusListener() {

			@Override
			public void onStopRecording(int arg0) {

			}

			@Override
			public void onStartRecording(int arg0) {

			}

			@Override
			public void onGeoRequestTimeout(int arg0, int arg1) {

			}

			@Override
			public void onGeoProviderDisabled(int arg0, int arg1, String arg2) {

			}

			@Override
			public void onGeoObtained(int arg0, int arg1, Location arg2) {

			}

			// 事件回调方法
			@Override
			public void onDataSaved(int arg0, Object arg1, BioType arg2) {
				Looper.prepare();
				LoadingDialogUtils.showLoadingDialog(ctx, "影像处理中...");
				final byte[] bit = (byte[]) arg1;
				Bitmap bitmap = BitmapFactory.decodeByteArray(bit, 0, bit.length);
				Bitmap newbitmap = ImageUtils.dealImage(bitmap, userName,false);
				String path = AppConfig.getImagePath(ctx.getBaseContext()) + "/" + System.currentTimeMillis() + ".png";
				Message msg = Message.obtain();
				msg.what = 3;
				if (ImageUtils.savePicToSdcard(newbitmap, path, false,imageType)) {
					if(singleWrapDeeimgAndSBimg.get("3")!=null){
						if(singleWrapDeeimgAndSBimg.get("3").getSignImagesVo()!=null){
							singleWrapDeeimgAndSBimg.get("3").getSignImagesVo().setLocalPath(path);
							if(!TextUtils.isEmpty(path)){
								String fileName = new File(path).getName();
								singleWrapDeeimgAndSBimg.get("3").getSignImagesVo().setCertName(fileName);
								mHandler.sendMessage(msg);
							}
						}
					}
				}
				//new MyHandler(ctx).sendEmptyMessage(1);
				// 证据拍照图片

				/*
				 * MainActivity.this.runOnUiThread(new Runnable() {
				 * 
				 * @Override public void run() {
				 * 
				 * // byte[] bit = Base64.decode(str, Base64.NO_WRAP); final
				 * Bitmap bitmap = BitmapFactory.decodeByteArray(bit, 0,
				 * bit.length); if (bitmap != null) { ImageView iv = new
				 * ImageView(MainActivity.this);
				 * iv.setBackgroundColor(Color.WHITE);
				 * iv.setImageBitmap(bitmap); new
				 * AlertDialog.Builder(MainActivity.this) .setView(iv)
				 * .setPositiveButton("确定", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { // TODO Auto-generated method stub if
				 * (!bitmap.isRecycled()) { bitmap.recycle(); } } }) .show(); }
				 * 
				 * } });
				 */

				// String s = android.util.Base64.encodeToString((byte[]) arg1,
				// Base64.DEFAULT);

			}

			@Override
			public void onPermissionDenied(int arg0) {
				
			}

		});
		return isSuccess;
	}

	/**
	 * 证据拍照
	 */
	public void takePhotoEvidence(int sortNo,JSONArray args) {
		//做关于风险提示的判断
		if(judgeRiskIsWrited(args)){
			init("", args,null);
			if (api != null) {
				try {
					api.takePictureEvidence(sortNo + 20, null, 0, BioType.PHOTO_SIGNER_IDENTITY_CARD_FRONT, false);
				} catch (BadFormatException e) {
					e.printStackTrace();
					cordovaResponse.setSuccess(false);
					cordovaResponse.setData("不好意思，发生异常，烦请重试");
					callBack.error(JsonUtils.toJsonObj(cordovaResponse));
				} catch (WrongContextIdException e) {
					e.printStackTrace();
					cordovaResponse.setSuccess(false);
					cordovaResponse.setData("不好意思，发生异常，烦请重试");
					callBack.error(JsonUtils.toJsonObj(cordovaResponse));
				} catch (ApiNotInitializedException e) {
					e.printStackTrace();
					cordovaResponse.setSuccess(false);
					cordovaResponse.setData("不好意思，发生异常，烦请重试");
					callBack.error(JsonUtils.toJsonObj(cordovaResponse));
				} catch (DocumentNotSpecifiedException e) {
					e.printStackTrace();
					cordovaResponse.setSuccess(false);
					cordovaResponse.setData("不好意思，发生异常，烦请重试");
					callBack.error(JsonUtils.toJsonObj(cordovaResponse));
				}
			} else {
				cordovaResponse.setSuccess(false);
				cordovaResponse.setData("请先初始化");
				callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			}
		}else{
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("请投保人先完成风险提示语句抄录，再进行签名，谢谢！");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
		}
	}

	private boolean judgeRiskIsWrited(JSONArray args) {
		boolean bool = false;
		try {
			JSONObject json = args.getJSONObject(0);
			String divID = json.getString("divId");
			Map<String,SignatureWrapDeeimgAndSBimg> map = sigNatureDeesignImageMap.get(divID);
			SignatureWrapDeeimgAndSBimg imageVo = map.get("2");
			if(imageVo!=null){
				SignImagesVo vo = imageVo.getSignImagesVo();
				String path = vo.getLocalPath();
				if(!TextUtils.isEmpty(path)){
					bool = true;
				}
			}else{
				bool = true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bool;
	}

	public void sign(int index) {
		try {
			//init("", args);
			// 弹出签名框签名
			api.showInputDialog(index + 20);
		} catch (ConfigNotFoundException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (BadFormatException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (ApiNotInitializedException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (WrongContextIdException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (DocumentNotSpecifiedException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		}

	}


	public void onlySign(int index,JSONArray args) {
		try {
			// 弹出签名框初始化
			init("", args,null);
			// 弹出签名框签名
			api.showInputDialog(index + 20);
		} catch (ConfigNotFoundException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (BadFormatException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (ApiNotInitializedException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (WrongContextIdException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (DocumentNotSpecifiedException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		}

	}

	public void copySign(JSONArray args) {
		try {
			init("", args,"1");
			api.showInputDialog(30);
		} catch (ConfigNotFoundException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (BadFormatException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (ApiNotInitializedException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (WrongContextIdException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		} catch (DocumentNotSpecifiedException e) {
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
			e.printStackTrace();
		}
	}

	public String getPacketData() {
		if (api.isReadyToUpload()) {
			String data;
			try {
				data = (String) api.getUploadDataGram();
				return data;
			} catch (SignatureNotGeneratedException e) {
				e.printStackTrace();
			} catch (ConfigNotFoundException e) {
				e.printStackTrace();
			} catch (BadFormatException e) {
				e.printStackTrace();
			}// 生成上传信手书服务端报文
		}
		return null;
	}

	/**
	 * 结束，释放
	 */
	public void finish() {
		if (api != null)
			api.finalizeAPI();
		instance = null;
		template_serial = null;
	}
	
	public String decodeImage2Base64(String path){
		String ecrptData = "";
		if(!TextUtils.isEmpty(path)){
			Bitmap bitmap = BitmapFactory.decodeFile(path);
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    bitmap.compress(Bitmap.CompressFormat.PNG, 30, baos);
		    byte[] datas = baos.toByteArray();
		    ecrptData = android.util.Base64.encodeToString(datas, android.util.Base64.DEFAULT);
		}else{
			cordovaResponse.setSuccess(false);
			cordovaResponse.setData("不好意思，发生异常，烦请重试");
			callBack.error(JsonUtils.toJsonObj(cordovaResponse));
		}
		
		return ecrptData;
	}


}
