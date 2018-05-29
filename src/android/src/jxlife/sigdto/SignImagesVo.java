package jxlife.sigdto;

public class SignImagesVo {
	private String uuid;
	private String sortNo;
	private String certName;
	private String imagetype;
	private String status;
	private String remotePath;
	private String localPath;
	private String subdivid;
	private String imagekind;
	public String getUuid() {
		return uuid;
	}
	public String getImagekind() {
		return imagekind;
	}
	public void setImagekind(String imagekind) {
		this.imagekind = imagekind;
	}
	public String getSubdivid() {
		return subdivid;
	}
	public void setSubdivid(String subdivid) {
		this.subdivid = subdivid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getSortNo() {
		return sortNo;
	}
	public void setSortNo(String sortNo) {
		this.sortNo = sortNo;
	}
	public String getCertName() {
		return certName;
	}
	public void setCertName(String certName) {
		this.certName = certName;
	}

	public String getStatus() {
		return status;
	}
	public String getImagetype() {
		return imagetype;
	}
	public void setImagetype(String imagetype) {
		this.imagetype = imagetype;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

}
