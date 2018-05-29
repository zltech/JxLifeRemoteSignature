package jxlife.sigdto;

import java.io.Serializable;

public class SignatureDelDataDto implements Serializable{
	/**
	 * 删除影像的传输对象
	 */
	private static final long serialVersionUID = -2159522824882404961L;
	private String relaId;
	private String divId;
	private String businessType;
	private String ruleFun;
	private String msgCode;
	private String isCallBack;
	public String getRuleFun() {
		return ruleFun;
	}
	public void setRuleFun(String ruleFun) {
		this.ruleFun = ruleFun;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public String getRelaId() {
		return relaId;
	}
	public void setRelaId(String relaId) {
		this.relaId = relaId;
	}
	public String getDivId() {
		return divId;
	}
	public void setDivId(String divId) {
		this.divId = divId;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getIsCallBack() {
		return isCallBack;
	}
	public void setIsCallBack(String businessType) {
		this.isCallBack = isCallBack;
	}
}
