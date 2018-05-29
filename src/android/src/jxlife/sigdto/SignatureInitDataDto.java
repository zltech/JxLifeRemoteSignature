package jxlife.sigdto;

import java.io.Serializable;

public class SignatureInitDataDto implements Serializable{
	/**
	 * 初始化模板数据获取
	 */
	private static final long serialVersionUID = 5025977217667638683L;
	private String businessType;
	private String relaId;
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getRelaId() {
		return relaId;
	}
	public void setRelaId(String relaId) {
		this.relaId = relaId;
	}
	

}
