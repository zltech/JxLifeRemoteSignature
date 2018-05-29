package jxlife.sigdto;

import java.io.Serializable;

public class SignatureCommitDataDto implements Serializable {
	/**
	 *完成签名的data
	 */
	private static final long serialVersionUID = 6564642861666918636L;
	private String relaId;
	private String businessType;
	private String smsCode;
	private String agentMobile;
	private String smsBusinessType;
	private String smstype;
	private String ruleFun;

	public String getRuleFun() {
		return ruleFun;
	}
	public void setRuleFun(String ruleFun) {
		this.ruleFun = ruleFun;
	}
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
	public String getAgentMobile() {
		return agentMobile;
	}
	public void setAgentMobile(String agentMobile) {
		this.agentMobile = agentMobile;
	}
	public String getSmsBusinessType() {
		return smsBusinessType;
	}
	public void setSmsBusinessType(String smsBusinessType) {
		this.smsBusinessType = smsBusinessType;
	}
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
	public String getSmstype() {
		return smstype;
	}
	public void setSmstype(String smstype) {
		this.smstype = smstype;
	}

}
