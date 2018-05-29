package jxlife.util.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONObject;

import afinal.http.AjaxCallBack;
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
import jxlife.signature.utils.LoadingDialog;
import jxlife.signature.utils.Utils;
import jxlife.util.AppConfig;
import jxlife.util.ImageUtils;


/**
 * 签名工具
 * 
 * @author lee
 * @date 2015-9-1 下午3:39:18
 * 
 */
public class SignatureTools {

	private static SignatureTools instance;

	private SignatureTools() {
	}

	private static Activity context;
	private SignatureAPI api;
	private AjaxCallBack<String> callBack;
	private String userName;
	private LoadingDialog dialog = null;
	private String template_serial;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				dialog = LoadingDialog.createDialog(context);
				dialog.setMessage("正在处理图片...");
				dialog.show();
				break;
			case 1:
				dialog.dismiss();
				break;

			}
		}

	};

	public String getTemplate_serial() {
		return template_serial;
	}

	public void setTemplate_serial(String template_serial) {
		this.template_serial = template_serial;
	}

	public static SignatureTools getInstance(Activity ctx) {
		context = ctx;
		if (instance == null) {
			instance = new SignatureTools();
		}
		return instance;
	}

	/**
	 * 初始化签名
	 */
	public boolean init(String username, String data) {
		this.userName = username;
		boolean isSuccess = true;
		AnySignBuild.Default_Cert_Signing_Alg = "SM2";
		
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(dm.density != 1)
		{
			// 华为8寸pad  设置为150显示不完整
			AnySignBuild.DEFAULT_GRID_SIGN_GRID_WIDTH = 145;
			AnySignBuild.DEFAULT_GRID_SIGN_GRID_HEIGHT = 145;
		}

		try {
			// 释放资源
			if (api != null)
				api.finalizeAPI();
			// 初始化信手书API,参数1：上下文对象，参数2
			// xssData配置文件名称，填NULL即默认配置文件，参数3：业务渠道号，参数4：模板序列号
			api = new SignatureAPI(context, null, "30731174");
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
			template_serial = Utils.getUUID();
			api.setTemplate(12, new byte[] { 1 }, template_serial, "1130");

		} catch (Exception e1) {
			isSuccess = false;
			e1.printStackTrace();
		}
		SignRule rule;

		try {
			JSONObject obj = new JSONObject(data);
			JSONArray array = obj.getJSONArray("bars");
			for (int i = 0, size = array.length(); i < size; i++) {
				JSONObject bean = array.getJSONObject(i);
				String type = bean.getString("signType");
				// 抄写风险栏
				if ("2".equals(type)) {
					// 配置30签名对象
					try {
						// 获取签名定位规则实例
						rule = SignRule.getInstance(SignRuleType.TYPE_KEY_WORD);
						//rule.setServerConfigRule("1123");
						rule.setKWRule(new KWRule(bean.getString("positionKeyWords"), SigAlignMethod.overlap, 0, 1));// 设置关键字信息
						//rule.setKWRule(new KWRule("test", SigAlignMethod.to_right_of_keyword, 0, 1));// 设置关键字信息
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
				} else {

					// 配置23签名对象
					// 获取签名定位规则实例，采用关键字的方式
					rule = SignRule.getInstance(SignRuleType.TYPE_KEY_WORD);
					// 使用关键字方式定位签名图片位置
					// 参数1：keyWord - 关键字
					// 参数2：alignMethod - 签名图片相对于关键字分布方法
					// at_right_bottom_corner_of_keyword
					// 签字图片左上角和关键字右下角重合，可能额外附加偏移量
					// below_keyword 签字图片位于关键字正下方，中心线对齐
					// overlap 签字图片和关键字矩形重心重合
					// to_right_of_keyword 签字图片位于关键字正右方，中心线对齐
					// 参数3：offset - 按照alignMethod定位后，额外赋予的偏移量，单位dip
					rule.setKWRule(new KWRule(bean.getString("positionKeyWords"), SigAlignMethod.below_keyword, 0, 1));// 设置关键字信息
					// 配置一个签名对象：23
					// 参数1：表示签名对象，取值为[20,30)、[200,300)
					// 参数2：上面创建的签名规则
					// 参数3：签名框顶部的标题
					// 参数4：标题中如果有需要突出显示的字，该参数代表突显字的起始索引，从0开始
					// 参数5：标题中如果有需要突出显示的字，该参数代表突显字的结束索引

					SignatureObj obj_23 = new SignatureObj(bean.getInt("sortNo") + 20, rule, bean.getString("signLabelText"), 0, bean.getString("signLabelText").length());
					obj_23.IsTSS = false;
					obj_23.nessesary = true;// 是否为必签项
					// 此签名对象签名人信息 参数1：姓名，参数2：唯一id,身份证
					obj_23.Signer = new Signer(bean.getString("signObject"), "111");
					// 22签名对象注册到接口中
					api.addSignatureObj(obj_23);
				}
			}
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
				handler.sendEmptyMessage(0);
				Bitmap newbitmap = ImageUtils.dealImage(signature, userName,false);
				String path = AppConfig.getImagePath(context) + "/" + System.currentTimeMillis() + ".jpg";
				if (ImageUtils.savePicToSdcard(newbitmap, path, true,"PNG")) {
					handler.sendEmptyMessage(1);
					callBack.onSuccess(path);
				}
				handler.sendEmptyMessage(1);
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
				// TODO Auto-generated method stub
//				Log.e("test", "length:" + ((byte[]) arg1).length);
				handler.sendEmptyMessage(0);
				final byte[] bit = (byte[]) arg1;
				Bitmap bitmap = BitmapFactory.decodeByteArray(bit, 0, bit.length);
				Bitmap newbitmap = ImageUtils.dealImage(bitmap, userName,false);
				String path = AppConfig.getImagePath(context.getBaseContext()) + "/" + System.currentTimeMillis() + ".jpg";
				if (ImageUtils.savePicToSdcard(newbitmap, path, true,"PNG")) {
					handler.sendEmptyMessage(1);
					callBack.onSuccess(path);
				}
				handler.sendEmptyMessage(1);
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
	public void takePhotoEvidence(int sortNo, AjaxCallBack<String> call) {
		callBack = call;
		if (api != null) {
			try {
				api.takePictureEvidence(sortNo + 20, null, 0, BioType.PHOTO_SIGNER_IDENTITY_CARD_FRONT, false);
			} catch (BadFormatException e) {
				e.printStackTrace();
				callBack.onFailure(null, 0, "异常");
			} catch (WrongContextIdException e) {
				e.printStackTrace();
				callBack.onFailure(null, 0, "异常");
			} catch (ApiNotInitializedException e) {
				e.printStackTrace();
				callBack.onFailure(null, 0, "异常");
			} catch (DocumentNotSpecifiedException e) {
				e.printStackTrace();
				callBack.onFailure(null, 0, "异常");
			}
		} else {
			callBack.onFailure(null, 0, "请初始化！");
		}

	}

	public void sign(int index, AjaxCallBack<String> call) {
		this.callBack = call;
		try {
			// 弹出签名框签名
			api.showInputDialog(index + 20);
		} catch (ConfigNotFoundException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		} catch (BadFormatException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		} catch (ApiNotInitializedException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		} catch (WrongContextIdException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		} catch (DocumentNotSpecifiedException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		}

	}

	public void copySign(AjaxCallBack<String> call) {
		this.callBack = call;
		try {
			api.showInputDialog(30);
		} catch (ConfigNotFoundException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		} catch (BadFormatException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		} catch (ApiNotInitializedException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		} catch (WrongContextIdException e) {
			callBack.onFailure(null, 0, "异常");
			e.printStackTrace();
		} catch (DocumentNotSpecifiedException e) {
			callBack.onFailure(null, 0, "异常");
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
}
