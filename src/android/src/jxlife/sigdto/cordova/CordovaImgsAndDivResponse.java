package jxlife.sigdto.cordova;

public class CordovaImgsAndDivResponse {
	private String subdivid;
	private String localPath;
	private String imgBase64;
	private String imageKind;
	private String remotePath;
	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getImageKind() {
		return imageKind;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public void setImageKind(String imageKind) {
		this.imageKind = imageKind;
	}
	public String getSubdivid() {
		return subdivid;
	}
	public void setSubdivid(String subdivid) {
		this.subdivid = subdivid;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getImgBase64() {
		return imgBase64;
	}
	public void setImgBase64(String imgBase64) {
		this.imgBase64 = imgBase64;
	}
}
