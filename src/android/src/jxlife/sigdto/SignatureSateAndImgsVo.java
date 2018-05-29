package jxlife.sigdto;

import java.util.Map;

public class SignatureSateAndImgsVo {
	private String isSiged;//是否签字
	private String source;//来源
	private String copyContent;
	private String isRemoted;
	private Map<String,String> divList;
	public String getCopyContent() {
		return copyContent;
	}
	public String getIsRemoted() {
		return isRemoted;
	}
	public void setIsRemoted(String isRemoted) {
		this.isRemoted = isRemoted;
	}
	public void setCopyContent(String copyContent) {
		this.copyContent = copyContent;
	}
	public Map<String, String> getDivList() {
		return divList;
	}
	public void setDivList(Map<String, String> divList) {
		this.divList = divList;
	}
	public String getIsSiged() {
		return isSiged;
	}
	public void setIsSiged(String isSiged) {
		this.isSiged = isSiged;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

}
