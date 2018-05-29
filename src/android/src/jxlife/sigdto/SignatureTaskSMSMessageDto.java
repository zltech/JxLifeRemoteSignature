package jxlife.sigdto;

import java.util.Map;

public class SignatureTaskSMSMessageDto {
	private String messageCode;
	private String tel;
	private Map mapContent;
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Map getMapContent() {
		return mapContent;
	}
	public void setMapContent(Map mapContent) {
		this.mapContent = mapContent;
	}

}
