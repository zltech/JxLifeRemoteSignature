package jxlife.sigdto;

public class EsignTodoDto {
	private String divId;
	private String source;
	private String expired;
	private String agentName;
	private String shortUrl;

	public String getDivId() {
		return divId;
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
	public void setDivId(String divId) {
		this.divId = divId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

}
