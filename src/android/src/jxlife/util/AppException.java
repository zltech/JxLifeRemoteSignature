package jxlife.util;
//应用自定义异常
public class AppException extends RuntimeException {

	public AppException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AppException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public AppException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public AppException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
