package jxlife.sigdto;

import java.util.List;

public class SignatureTaskSMSDto {
	private List<SignatureTaskSMSMessageDto> messageInfoList;

	public List<SignatureTaskSMSMessageDto> getMessageInfoList() {
		return messageInfoList;
	}

	public void setMessageInfoList(List<SignatureTaskSMSMessageDto> messageInfoList) {
		this.messageInfoList = messageInfoList;
	}
}
