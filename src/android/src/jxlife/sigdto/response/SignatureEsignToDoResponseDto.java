package jxlife.sigdto.response;

import java.util.List;

import jxlife.sigdto.EsignTodoDto;

public class SignatureEsignToDoResponseDto {
	private String relaId;
	private String businessType;
	private String reminder;
	
	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}

	public String getRelaId() {
		return relaId;
	}

	public void setRelaId(String relaId) {
		this.relaId = relaId;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	private String handleDate;
	public String getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(String handleDate) {
		this.handleDate = handleDate;
	}

	private List<EsignTodoDto> esignTodoInfoList;

	public List<EsignTodoDto> getEsignTodoInfoList() {
		return esignTodoInfoList;
	}

	public void setEsignTodoInfoList(List<EsignTodoDto> esignTodoInfoList) {
		this.esignTodoInfoList = esignTodoInfoList;
	}
}
