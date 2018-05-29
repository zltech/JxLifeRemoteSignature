package jxlife.signature.utils;

public interface NetWorkCallBack {
	public void callSuccess(String s);//调用成功
	public void callFailure(String s);//系统异常处理
	public void callSuccessByMsg(String s);//调用成功但是数据有问题
}	
