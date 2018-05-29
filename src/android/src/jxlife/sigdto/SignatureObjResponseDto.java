package jxlife.sigdto;

public class SignatureObjResponseDto {
	private String divId;
	private String objectName;
	private String phone;
	private String isTodo;
	private String objectSex;
	private String expired;
	private String agentName;
	private String source;
	private String shortUrl;

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getExpired() {
		return expired;
	}
	public void setExpired(String expired) {
		this.expired = expired;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getDivId() {
		return divId;
	}
	public String getObjectSex() {
		return objectSex;
	}
	public void setObjectSex(String objectSex) {
		this.objectSex = objectSex;
	}
	public void setDivId(String divId) {
		this.divId = divId;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getIsTodo() {
		return isTodo;
	}
	public void setIsTodo(String isTodo) {
		this.isTodo = isTodo;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
}
