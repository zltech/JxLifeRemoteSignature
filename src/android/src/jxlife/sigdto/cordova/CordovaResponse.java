package jxlife.sigdto.cordova;

public class CordovaResponse {
	private boolean isSuccess;
	private String data;

	public String getData() {
		return data;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public void setData(String data) {
		this.data = data;
	}
}
