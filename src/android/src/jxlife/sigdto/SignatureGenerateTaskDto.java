package jxlife.sigdto;

import java.util.List;

public class SignatureGenerateTaskDto {
	private String relaId;
	private String businessType;
	private String handleDate;
	public String getHandleDate() {
		return handleDate;
	}
	public void setHandleDate(String handleDate) {
		this.handleDate = handleDate;
	}
	private List<SignatureObjResponseDto> esignObjectList;
	//private String messageCode;
	//private Map<String,String> mapContent;
	public String getRelaId() {
		return relaId;
	}
	public void setRelaId(String relaId) {
		this.relaId = relaId;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public List<SignatureObjResponseDto> getEsignObjectList() {
		return esignObjectList;
	}
	public void setEsignObjectList(List<SignatureObjResponseDto> esignObjectList) {
		this.esignObjectList = esignObjectList;
	}
	/*public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public Map getMapContent() {
		return mapContent;
	}
	public void setMapContent(Map mapContent) {
		this.mapContent = mapContent;
	}*/

}
