package jxlife.cordova.plugins.wpesignatures;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxlife.sigdto.EsignTodoDto;
import jxlife.sigdto.SiganatureCordovaImageVo;
import jxlife.sigdto.SignImagesVo;
import jxlife.sigdto.SignatureCommitDataDto;
import jxlife.sigdto.SignatureDeesignImageDto;
import jxlife.sigdto.SignatureDelDataDto;
import jxlife.sigdto.SignatureDelSigPropDto;
import jxlife.sigdto.SignatureEsignFlagDataDto;
import jxlife.sigdto.SignatureGenerateTaskDto;
import jxlife.sigdto.SignatureInitDataDto;
import jxlife.sigdto.SignatureObjResponseDto;
import jxlife.sigdto.SignatureSateAndImgsVo;
import jxlife.sigdto.SignatureWrapDeeimgAndSBimg;
import jxlife.sigdto.cordova.CordovaActivityRequest;
import jxlife.sigdto.cordova.CordovaImgsAndDivResponse;
import jxlife.sigdto.cordova.CordovaResponse;
import jxlife.sigdto.cordova.CordovaTemplateInitDataResponse;
import jxlife.sigdto.response.SignatureEsignObjectInfoResponseDto;
import jxlife.sigdto.response.SignatureEsignToDoResponseDto;
import jxlife.sigdto.response.SignatureInitDataReponseDto;
import jxlife.signature.service.SignatureBizService;
import jxlife.signature.utils.JsonUtils;
import jxlife.signature.utils.LoadingDialogUtils;
import jxlife.signature.utils.NetWorkCallBack;
import jxlife.util.tools.RemoteSignatureTools;


public class JxLifeRemoteSignature extends CordovaPlugin {

	private CordovaInterface m_cordova;
	private CordovaWebView m_webview;
	public CallbackContext m_callback;
	private Map<String, SignatureEsignObjectInfoResponseDto> esignObjectInfoMap;
	private SiganatureCordovaImageVo siganatureCordovaImageVo = new SiganatureCordovaImageVo();
	private Map<String,SignatureEsignFlagDataDto> esignKeyWordsMap = new HashMap<String,SignatureEsignFlagDataDto>();
	private SignatureGenerateTaskDto m_signatureGenerateTaskDto = null;
	private SignatureEsignObjectInfoResponseDto signatureEsignObjectInfoResponseDto;
	private String mSignStatus;
	
    
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		this.m_cordova = cordova;
		this.m_webview = webView;
	}

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		m_callback = callbackContext;
		// 本地签名
		if ("sigRisk".equals(action)) {// 抄写风险提示
			sigRisk(args);
		} else if ("sigName".equals(action)) {// 单个对象签名
			sigName(args);
		} else if ("onlySign".equals(action)) {//只签名不拍照
			onlySign(args);
		} else if ("delSig".equals(action)) {// 删除签名接口
			delSig(args);
		} else if ("submitSig".equals(action)) {// 完成签名接口
			submitSig(args);
		}  else if ("fetchTemplate".equals(action)) {// 获取模板数据，以插件的形式，从h5页面发起请求
			fetchTemplate(args);
		}
		return true;
	}

	// 获取模板数据
	private void fetchTemplate(final JSONArray args) {
				try {
					if (args != null) {
						JSONObject json = args.getJSONObject(0);
						String businessType = json.getString("businessType");
						String relaID = json.getString("relaId");
						SignatureInitDataDto dto = new SignatureInitDataDto();
						dto.setBusinessType(businessType);
						dto.setRelaId(relaID);
						SignatureBizService.fetchBizTemplateData(dto,
								new NetWorkCallBack() {

									@Override
									public void callSuccess(String s) {
										CordovaResponse cordovaResponse = new CordovaResponse();
										if (s != null) {
											SignatureInitDataReponseDto dto = JsonUtils.fromJson(s,SignatureInitDataReponseDto.class);
											String mobileNo = dto.getAgentMobile();//投保人手机号码
											String templateHtml = dto.getTemplateHtml();
											String handleDate = dto.getHandleDate();//投保时间
											String signStatus = dto.getSignStatus();
											String reminder = dto.getReminder();//温馨提示
											esignObjectInfoMap = dto.getEsignObjectInfoMap();// 相关影像的条目

											mSignStatus=signStatus;//获取签名的状态
											CordovaTemplateInitDataResponse responseCordova = new CordovaTemplateInitDataResponse();
											responseCordova.setAgentMobile(mobileNo);// 投保人手机号码
											responseCordova.setTemplateHtml(templateHtml);
											responseCordova.setHandleDate(handleDate);
											responseCordova.setSignStatus(signStatus);
											responseCordova.setReminder(reminder);
											Map<String, SignatureSateAndImgsVo> divListMap = new HashMap<String, SignatureSateAndImgsVo>();

											// 做一个大的数据处理，将后台收到的data转化为前台，供给所需
											Map<String, Map<String, SignatureWrapDeeimgAndSBimg>> bigMap = new HashMap<String, Map<String, SignatureWrapDeeimgAndSBimg>>();
											if (esignObjectInfoMap != null) {
												for (Entry<String, SignatureEsignObjectInfoResponseDto> e : esignObjectInfoMap.entrySet()) {
													String key = e.getKey();
													// Map<String,Map<String,SignatureDeesignImageDto>>
													signatureEsignObjectInfoResponseDto = e.getValue();
													//signatureEsignObjectInfoResponseDto.setIsCopyRiskWarn("0");
													signatureEsignObjectInfoResponseDto.setIsCopyRisk("0");
													List<SignatureDeesignImageDto> list = signatureEsignObjectInfoResponseDto.getDeesignImageList();
													Map<String, SignatureWrapDeeimgAndSBimg> deeMap = new HashMap<String, SignatureWrapDeeimgAndSBimg>();

													// 返回给h5的相关内容
													SignatureSateAndImgsVo signatureSateAndImgsVo = new SignatureSateAndImgsVo();
													signatureSateAndImgsVo.setIsSiged(signatureEsignObjectInfoResponseDto.getIsImage());
													signatureSateAndImgsVo.setSource(signatureEsignObjectInfoResponseDto.getSource());
													signatureSateAndImgsVo.setIsRemoted(signatureEsignObjectInfoResponseDto.getIsRemoted());
													//签字的相关的标识
													String objectLabel = signatureEsignObjectInfoResponseDto.getObjectLabel();
													String copyWords = signatureEsignObjectInfoResponseDto.getCopyWords();												    
													SignatureEsignFlagDataDto signatureEsignFlagDataDto = new SignatureEsignFlagDataDto();
													signatureEsignFlagDataDto.setCopyWords(copyWords);
													signatureEsignFlagDataDto.setObjectLabel(objectLabel);
													esignKeyWordsMap.put(key, signatureEsignFlagDataDto);
													
													Map<String,String> divList = new HashMap<String,String>();

													for (SignatureDeesignImageDto imageDto : list) {
														SignatureWrapDeeimgAndSBimg wrapVO = new SignatureWrapDeeimgAndSBimg();//一个大的vo，用于整个生命周期
														String imageKind = imageDto.getImageKind();
														wrapVO.setSignatureDeesignImageDto(imageDto);
														SignImagesVo vo = new SignImagesVo();
														vo.setImagekind(imageDto.getImageKind());
														vo.setImagetype(imageDto.getImageType());
														vo.setSubdivid(imageDto.getDivId());
														vo.setLocalPath(imageDto.getOriginalPath());
														vo.setRemotePath(imageDto.getImagePath());
														wrapVO.setSignImagesVo(vo);
														deeMap.put(imageKind, wrapVO);
														divList.put(imageKind, imageDto.getDivId());
													}
													signatureSateAndImgsVo.setDivList(divList);
													divListMap.put(key,signatureSateAndImgsVo);
													bigMap.put(key, deeMap);
												}
												responseCordova.setSignatureSateAndImgsMap(divListMap);
												siganatureCordovaImageVo.setImageMap(bigMap);

												cordovaResponse.setData(JsonUtils.toJsonObj(responseCordova));
												cordovaResponse.setSuccess(true);
												m_callback.success(JsonUtils.toJsonObj(cordovaResponse));
											} else {
												cordovaResponse.setData("未获取到数据");
												cordovaResponse.setSuccess(false);
												m_callback.error(JsonUtils.toJsonObj(cordovaResponse));
											}
										} else {
											cordovaResponse.setData("程序发生异常");
											cordovaResponse.setSuccess(false);
											m_callback.error(JsonUtils.toJsonObj(cordovaResponse));
										}
									}

									@Override
									public void callFailure(String s) {
										CordovaResponse cordovaResponse = new CordovaResponse();
										cordovaResponse.setData(s);
										cordovaResponse.setSuccess(false);
										
										m_callback.error(JsonUtils.toJsonObj(cordovaResponse));
									}

									@Override
									public void callSuccessByMsg(String s) {
										CordovaResponse cordovaResponse = new CordovaResponse();
										cordovaResponse.setData(s);
										cordovaResponse.setSuccess(false);
										m_callback.error(JsonUtils.toJsonObj(cordovaResponse));
										
									}
								}, m_cordova.getActivity());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
	}

	// 完成任务
	private void submitSig(JSONArray args) {
		try {
			JSONObject json = args.getJSONObject(0);
			SignatureCommitDataDto dto = new SignatureCommitDataDto();
			dto.setBusinessType(json.getString("businessType"));
			dto.setRelaId(json.getString("relaId"));
			dto.setSmsBusinessType("2");
			dto.setAgentMobile(json.getString("agentMobile"));
			dto.setSmsCode(json.getString("smsCode"));
			dto.setSmstype(json.getString("smstype"));
			dto.setRuleFun(json.getString("ruleFun"));
			SignatureBizService.commitSigTask(dto, new NetWorkCallBack() {

				CordovaResponse resPonse = new CordovaResponse();

				@Override
				public void callSuccess(String s) {
					resPonse.setSuccess(true);
					resPonse.setData("提交成功");
					m_callback.success(JsonUtils.toJsonObj(resPonse));
				}

				@Override
				public void callFailure(String s) {
					if (s.indexOf("JX000003") != -1) {
						CordovaResponse resPonse = new CordovaResponse();
						resPonse.setSuccess(false);
						resPonse.setData(s);
						m_callback.error(JsonUtils.toJsonObj(resPonse));
					}else{
						resPonse.setSuccess(false);
						resPonse.setData(s);
						m_callback.error(JsonUtils.toJsonObj(resPonse));
					}
				}

				@Override
				public void callSuccessByMsg(String s) {
					resPonse.setData(s);
					resPonse.setSuccess(false);
					m_callback.error(JsonUtils
							.toJsonObj(resPonse));
					
				}
			}, m_cordova.getActivity());

		} catch (JSONException e) {
			CordovaResponse resPonse = new CordovaResponse();
			resPonse.setSuccess(false);
			resPonse.setData("程序发生异常，请联系管理员");
			m_callback.error(JsonUtils.toJsonObj(resPonse));
			e.printStackTrace();
		}
	}

	// 删除当前影像
	private void delSig(JSONArray args) {
		try {
			JSONObject json = args.getJSONObject(0);
			final SignatureDelDataDto dto = new SignatureDelDataDto();
			dto.setBusinessType(json.getString("businessType"));
			dto.setDivId(json.getString("divId"));
			dto.setRelaId(json.getString("relaId"));
			dto.setRuleFun(json.getString("ruleFun"));
			dto.setMsgCode(json.getString("msgCode"));
			SignatureBizService.delRemoteSigImgs(dto, new NetWorkCallBack() {

				@Override
				public void callSuccess(String s) {
					CordovaResponse response = new CordovaResponse();
					if(s != null){
						if(s.indexOf("JX000001") != -1){
							SignatureDelSigPropDto sdsDto = JsonUtils.fromJson(s, SignatureDelSigPropDto.class);
							response.setSuccess(false);
							sdsDto.setDivId(dto.getDivId());
							response.setData(JsonUtils.toJsonObj(sdsDto));
						}else if(s.indexOf("JX000002") != -1){
							SignatureDelSigPropDto sdsDto = JsonUtils.fromJson(s, SignatureDelSigPropDto.class);
							response.setSuccess(true);
							response.setData(sdsDto.getMsgCode());
						}else if(s.indexOf("JX000004") != -1){
							SignatureDelSigPropDto sdsDto = JsonUtils.fromJson(s, SignatureDelSigPropDto.class);
							response.setSuccess(false);
							sdsDto.setDivId(dto.getDivId());
							response.setData(JsonUtils.toJsonObj(sdsDto));
						}else{
							response.setSuccess(true);
							response.setData("");
						}
					}else{
						response.setSuccess(true);
						response.setData("");
					}
					
					//清空相关数据
					Map<String, Map<String, SignatureWrapDeeimgAndSBimg>> map = siganatureCordovaImageVo.getImageMap();
					Map<String, SignatureWrapDeeimgAndSBimg> signatureWrapDeeimgAndSBimgMap = map.get(dto.getDivId());
					for(Entry<String, SignatureWrapDeeimgAndSBimg> entry:signatureWrapDeeimgAndSBimgMap.entrySet()){
						entry.getValue().getSignImagesVo().setLocalPath("");
						entry.getValue().getSignImagesVo().setRemotePath("");
						entry.getValue().getSignatureDeesignImageDto().setImagePath("");
						entry.getValue().getSignatureDeesignImageDto().setOriginalPath("");
					}
					signatureEsignObjectInfoResponseDto.setIsCopyRisk("0");
					m_callback.success(JsonUtils.toJsonObj(response));
				}

				@Override
				public void callFailure(String s) {
					if (s.indexOf("JX000003") != -1) {
						CordovaResponse resPonse = new CordovaResponse();
						resPonse.setSuccess(false);
						resPonse.setData(s);
						m_callback.error(JsonUtils.toJsonObj(resPonse));
					}else{
						CordovaResponse response = new CordovaResponse();
						response.setSuccess(false);
						response.setData(s);
						m_callback.error(JsonUtils.toJsonObj(response));
					}
				}

				@Override
				public void callSuccessByMsg(String s) {
					CordovaResponse response = new CordovaResponse();
					response.setData(s);
					response.setSuccess(false);
					m_callback.error(JsonUtils
							.toJsonObj(response));
					
				}
			}, m_cordova.getActivity());
		} catch (JSONException e) {
			CordovaResponse response = new CordovaResponse();
			response.setData("程序发生异常");
			response.setSuccess(false);
			m_callback.error(JsonUtils
					.toJsonObj(response));
			e.printStackTrace();
		}
	}

	private void sigName(JSONArray args) {
		RemoteSignatureTools.getInstance(m_cordova.getActivity(), m_callback,
				siganatureCordovaImageVo,esignKeyWordsMap).takePhotoEvidence(0, args);
	}

	private void onlySign(JSONArray args) {
		RemoteSignatureTools.getInstance(m_cordova.getActivity(), m_callback,
				siganatureCordovaImageVo,esignKeyWordsMap).onlySign(0,args);
	}

	private void sigRisk(JSONArray args) {
		RemoteSignatureTools.getInstance(m_cordova.getActivity(), m_callback,
				siganatureCordovaImageVo,esignKeyWordsMap).copySign(args);
		//signatureEsignObjectInfoResponseDto.setIsCopyRiskWarn("1");
		signatureEsignObjectInfoResponseDto.setIsCopyRisk("1");
	}

}
