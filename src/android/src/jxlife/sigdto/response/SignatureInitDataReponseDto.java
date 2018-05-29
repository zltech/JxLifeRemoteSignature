package jxlife.sigdto.response;

import java.util.Map;

public class SignatureInitDataReponseDto {
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
	private Map<String,SignatureEsignObjectInfoResponseDto> esignObjectInfoMap;
;
	public Map<String, SignatureEsignObjectInfoResponseDto> getEsignObjectInfoMap() {
		return esignObjectInfoMap;
	}
	public void setEsignObjectInfoMap(
			Map<String, SignatureEsignObjectInfoResponseDto> esignObjectInfoMap) {
		this.esignObjectInfoMap = esignObjectInfoMap;
	}
	public String getTemplateHtml() {
		return templateHtml;
	}
	public void setTemplateHtml(String templateHtml) {
		this.templateHtml = templateHtml;
	}

	
}
