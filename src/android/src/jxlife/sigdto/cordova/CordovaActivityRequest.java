package jxlife.sigdto.cordova;

import java.util.Map;

public class CordovaActivityRequest {
	private String activity;//native activity
	private String pkg;//包名
	private Map<String,String> bizData;//相关传递的业务数据
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public Map<String, String> getBizData() {
		return bizData;
	}
	public void setBizData(Map<String, String> bizData) {
		this.bizData = bizData;
	}

}
