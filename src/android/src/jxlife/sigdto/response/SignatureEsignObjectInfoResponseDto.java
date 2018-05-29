package jxlife.sigdto.response;

import java.util.List;

import jxlife.sigdto.SignatureDeesignImageDto;

public class SignatureEsignObjectInfoResponseDto {
	private String isCopyRiskWarn;
	private String isImage;
	private String isRemoted;
	private String objectLabel;
	private String copyWords;

	private String isCopyRisk;
	
	public String getIsCopyRisk() {
		return isCopyRisk;
	}
	public void setIsCopyRisk(String isCopyRisk) {
		this.isCopyRisk = isCopyRisk;
	}
	
	public String getObjectLabel() {
		return objectLabel;
	}
	public void setObjectLabel(String objectLabel) {
		this.objectLabel = objectLabel;
	}
	public String getCopyWords() {
		return copyWords;
	}
	public void setCopyWords(String copyWords) {
		this.copyWords = copyWords;
	}
	public String getIsRemoted() {
		return isRemoted;
	}
	public void setIsRemoted(String isRemoted) {
		this.isRemoted = isRemoted;
	}
	public String getIsImage() {
		return isImage;
	}
	public void setIsImage(String isImage) {
		this.isImage = isImage;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	private String source;

	private List<SignatureDeesignImageDto> deesignImageList;
	public String getIsCopyRiskWarn() {
		return isCopyRiskWarn;
	}
	public void setIsCopyRiskWarn(String isCopyRiskWarn) {
		this.isCopyRiskWarn = isCopyRiskWarn;
	}
	public List<SignatureDeesignImageDto> getDeesignImageList() {
		return deesignImageList;
	}
	public void setDeesignImageList(List<SignatureDeesignImageDto> deesignImageList) {
		this.deesignImageList = deesignImageList;
	}
	
	private String copyContent;
	public String getCopyContent() {
		return copyContent;
	}
	public void setCopyContent(String copyContent) {
		this.copyContent = copyContent;
	}

}
