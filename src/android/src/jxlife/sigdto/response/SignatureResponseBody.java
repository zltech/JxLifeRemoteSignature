package jxlife.sigdto.response;

public class SignatureResponseBody {
	private String msgCode;
	private String msg;
	private String data;
	public String getMsgCode() {
		return msgCode;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
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
