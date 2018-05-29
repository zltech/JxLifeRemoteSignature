package jxlife.sigdto;

import java.io.Serializable;

public class SignatureRequestEntity implements Serializable{
	/**
	 * 通用传输对象
	 */
	private static final long serialVersionUID = 1L;
	private String beanId;
	private String operation;
	private String data;
	public String getBeanId() {
		return beanId;
	}
	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

}
