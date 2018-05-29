package jxlife.sigdto;

import java.io.Serializable;

public class SignatureDelSigPropDto implements Serializable{
	
	private static final long serialVersionUID = 6902471099664909454L;
	
	private String msgCode;
	private String msg;
	private String divId;
	public String getDivId() {
		return divId;
	}
	public void setDivId(String divId) {
		this.divId = divId;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
