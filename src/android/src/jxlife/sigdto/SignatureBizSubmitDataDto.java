package jxlife.sigdto;

import java.util.List;

public class SignatureBizSubmitDataDto {
	private String businessType;
	private String encryptData;
	private String woNo;
	private String signKind;
	private String resource;
	private List<SignImagesVo> signImages;
	private String relaId;
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getEncryptData() {
		return encryptData;
	}
	public void setEncryptData(String encryptData) {
		this.encryptData = encryptData;
	}
	public String getWoNo() {
		return woNo;
	}
	public void setWoNo(String woNo) {
		this.woNo = woNo;
	}
	public String getSignKind() {
		return signKind;
	}
	public void setSignKind(String signKind) {
		this.signKind = signKind;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public List<SignImagesVo> getSignImages() {
		return signImages;
	}
	public void setSignImages(List<SignImagesVo> signImages) {
		this.signImages = signImages;
	}
	public String getRelaId() {
		return relaId;
	}
	public void setRelaId(String relaId) {
		this.relaId = relaId;
	}
	public String getDivId() {
		return divId;
	}
	public void setDivId(String divId) {
		this.divId = divId;
	}
	private String divId;

}
