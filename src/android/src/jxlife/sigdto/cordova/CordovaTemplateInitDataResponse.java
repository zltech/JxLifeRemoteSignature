package jxlife.sigdto.cordova;

import java.util.Map;

import jxlife.sigdto.SignatureSateAndImgsVo;

public class CordovaTemplateInitDataResponse {
	private String templateHtml;
	private String agentMobile;
	private String handleDate;
	private String signStatus;
	private String reminder;
	public String getReminder() {
		return reminder;
	}
	public void setReminder(String reminder) {
		this.reminder = reminder;
	}
	public String getSignStatus() {
		return signStatus;
	}
	public void setSignStatus(String signStatus) {
		this.signStatus = signStatus;
	}
	public String getHandleDate() {
		return handleDate;
	}
	public void setHandleDate(String handleDate) {
		this.handleDate = handleDate;
	}
	public String getAgentMobile() {
		return agentMobile;
	}
	public void setAgentMobile(String agentMobile) {
		this.agentMobile = agentMobile;
	}
	private Map<String,SignatureSateAndImgsVo> signatureSateAndImgsMap;//{leformd:[]}
	public String getTemplateHtml() {
		return templateHtml;
	}
	public void setTemplateHtml(String templateHtml) {
		this.templateHtml = templateHtml;
	}
	public Map<String, SignatureSateAndImgsVo> getSignatureSateAndImgsMap() {
		return signatureSateAndImgsMap;
	}
	public void setSignatureSateAndImgsMap(
			Map<String, SignatureSateAndImgsVo> signatureSateAndImgsMap) {
		this.signatureSateAndImgsMap = signatureSateAndImgsMap;
	}
}
