package jxlife.signature.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


/**
 * 自定义加载对话框
 *
 * @author lee
 * @date 2015-8-12 下午4:33:13
 */
public class LoadingDialog extends Dialog {

    private static LoadingDialog dialog;
    private static Activity act;
    public static TextView textView;
    public String[] message;
    public int remindTimes = 0;
    private int s = 0;

    private LoadingDialog(Activity context) {
        super(context);
    }

    private LoadingDialog(Activity context, int theme) {
        super(context, theme);
    }

    public static LoadingDialog createDialog(Activity context) {
        if (dialog != null) {
            //Utils.showToast(context, "dialog!!!");
            if (dialog.isShowing())
                dialog.dismiss();
            dialog = null;
        }
        if (!context.isFinishing()) {
            act = context;
            dialog = new LoadingDialog(context, MResource.getIdByName(context, "style", "progress_dialog"));

            LayoutInflater inflater = act.getLayoutInflater();
            View view = inflater.inflate(MResource.getIdByName(context, "layout", "loading_dialog"), null);
            textView = (TextView) view.findViewById(MResource.getIdByName(context, "id", "tv_tips"));
            // TODO: 2018/1/3
            // 为什么用资源id就不行？？？？？？
            dialog.setContentView(view);

            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            dialog.setCancelable(false);
        }
        return dialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (dialog == null) {
            return;
        }
    }

    public LoadingDialog setMessage(String msg) {
        if (dialog != null) {
            TextView message = (TextView) dialog
                    .findViewById(MResource.getIdByName(LoadingDialog.act, "id", "loading_dialog_tv"));
            if (message != null) {
                if (msg != null) {
                    message.setText(msg);
                } else {
                    message.setText("");
                }
            }
            return dialog;
        }
        return dialog;
    }

    @Override
    public void show() {
        try {
            if (dialog == null || act == null) return;
            if (act.isFinishing()) return;

            if (isShowing())
                dialog.dismiss();
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
            dialog = null;
        }


    }

    @Override
    public void dismiss() {
        try {
            if (act != null && !act.isFinishing()) {
                super.dismiss();

            }
            dialog = null;
            act = null;
            handle.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMsg(String[] msg, int remindTimes) {
        this.s = 0;
        this.message = msg;
        this.remindTimes = remindTimes;
        handle.post(runnable);
    }

    private Handler handle = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                int k = (int) ((Math.random() * message.length));
                if (s % remindTimes == 0 && !"".equals(message[k])) {
                    LoadingDialog.textView.setText("小提示：" + message[k]);
                }
                handle.postDelayed(this, 1000);
                s++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
 