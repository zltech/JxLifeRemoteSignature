package jxlife.signature.utils;

import android.app.Activity;

public class LoadingDialogUtils {
	private static LoadingDialog dialog;
	
	public  static  void showLoadingDialog(final Activity act, final String message)
	{
		if(dialog != null)
		{
			if(dialog.isShowing())
				dialog.dismiss();
			dialog = null;
		}
			//Utils.showToast(act, "请检查程序，是否有没关闭的loading dialog");		}
		if(!act.isFinishing()){
			dialog = LoadingDialog.createDialog(act);
			dialog.setMessage(message);
			dialog.setCancelable(false);
			dialog.show();
		}
		
	}
	
	/**
	 * 关闭加载框
	 * 
	 * @param act
	 * @param message
	 * @param callBack
	 */
	public static void dismissLoadingDialog(final Activity act, final String message)
	{
		if(dialog != null)
		{
			if(dialog.isShowing())
				dialog.dismiss();
			dialog = null;
		}
		else
		{
			//Utils.showToast(act, "请检查程序，loading dialog 已经不存在，为什么还会调用dismiss");		}
		}
	}
	
}
