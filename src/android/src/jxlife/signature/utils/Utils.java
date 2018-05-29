package jxlife.signature.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import afinal.http.AjaxCallBack;

/**
 * 工具类
 *
 * @author lee
 */
public class Utils {
    /**
     * 数据库路径  初始化在 Application中
     */
    public static String DB_PATH;
    public static String DEVICE_ID;
    public static final String LOGIN_SUCCESS = "cn.com.jxlife.loginsuccess";
    public static String ENTER_TYPE_MAIN = "MAIN";
    public static String ENTER_TYPE_LEFORM = "LEFORM";
    public static String ENTER_TYPE_RECEIPT = "RECEIPT";
    public static String ENTER_TYPE_CLAIM = "CLAIM";
    public static String ENTER_TYPE_PRSV = "prsv";
    public static String ENTER_TYPE_LTRT = "ltrt";
    /**
     * 进入方式：主程序MAIN、电子投保单LEFORM、回执RECEIPT   图标进入要自己设置各自的值
     */
    public static String JX_ENTER_TYPE;
    private static Toast toast;

    /**
     * 统一处理请求头，填充字段：设备号、经度、纬度、用户名、密码
     *
     * @param beanId    action 类名
     * @param opreation 方法名
     */
//    public static RequestEntity getRequestEntity(Context context, String beanId, String opreation) {
//        RequestEntity entity = new RequestEntity();
//        //entity.setSystem("1");
//        entity.setBeanId(beanId);
//        entity.setOpreation(opreation);
//        entity.setDeviceId(Utils.getDeviceId(context));
//        if (Utils.gi == null || TextUtils.isEmpty(Utils.gi.Operator) || TextUtils.isEmpty(Utils.gi.psw)) {
//            entity.setUsercCode(SharedPreferencesHelper.getUserName(context));
//            entity.setPsw(SharedPreferencesHelper.getPassword(context));
//        } else {
//            entity.setUsercCode(Utils.gi.Operator);
//            entity.setPsw(Utils.gi.psw);
//        }
//        AppApplication app = (AppApplication) context.getApplicationContext();
//        String[] loc = new String[]{"1", "2"};//app.getLocation();
//        if (loc != null) {
//            entity.setLongitude(loc[0]);
//            entity.setLatitude(loc[1]);
//        } else {
//            entity.setLongitude("-1");
//            entity.setLatitude("-1");
//        }
//        return entity;
//    }

    /**
     * 获得设备串号
     */
    public static String getDeviceId(Context context) {
        if (TextUtils.isEmpty(DEVICE_ID)) {
            DEVICE_ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return DEVICE_ID;

    }

    /**
     * Toast提示
     *
     * @param context
     * @param content 提示信息
     * @author lee
     */
    public static void showToast(Context context, String content) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            } else {
                toast.setText(content);
            }
            toast.show();
        } catch (Exception e) {
            toast = null;
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * 格式化时间
     *
     * @param date    时间
     * @param pattern 格式 （yyyy.MM.dd HH:mm）
     * @return String 格式时间
     * @author lee
     * @author lee
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDateAll(String date, String pattern) {
        try {
            SimpleDateFormat format = getDateFormat(pattern);
            Date dates = new Date();
            dates.setTime(Long.parseLong(date));
            String d = format.format(dates);
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentDate(String pattern) {
        try {
            SimpleDateFormat format = getDateFormat(pattern);
            String d = format.format(new Date());
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得SimpleDateFormat
     *
     * @param pattern
     * @return SimpleDateFormat;
     * @author lee
     */
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat getDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * dialog 提示
     *
     * @param context
     * @param title      标题
     * @param message    提示
     * @param cancelable 是否可取消
     * @param callback   回调函数
     */
//    public static void showDialog(Context context, String title, String message, boolean cancelable, final CallBackListener callback) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setTitle(title);
//        dialog.setCancelable(cancelable);
//        dialog.setMessage(message);
//        dialog.setPositiveButton("确定", new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                callback.onSuccess();
//            }
//        });
//        dialog.setNegativeButton("取消", new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                callback.onFailure(null);
//            }
//        });
//        try {
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    /**
     * @param context
     * @param title
     * @param message
     * @param posBtnText
     * @param negBtnText
     * @param cancelable
     * @param callback
     */
//    public static void showDialog(Context context, String title, String message, String posBtnText, String negBtnText, boolean cancelable, final CallBackListener callback) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setTitle(title);
//        dialog.setCancelable(cancelable);
//        dialog.setMessage(message);
//        dialog.setPositiveButton(posBtnText, new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                callback.onSuccess();
//            }
//        });
//        dialog.setNegativeButton(negBtnText, new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                callback.onFailure(null);
//            }
//        });
//        try {
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * dialog 提示,有三个按钮
     *
     * @param context
     * @param title      标题
     * @param message    提示
     * @param posBtnText 第一个按钮内容
     * @param neuBtnText 第二个按钮内容
     * @param negBtnText 第三个按钮内容
     * @param cancelable
     * @param callback
     */
//    public static void showDialog(Context context, String title, String message, String posBtnText, String neuBtnText, String negBtnText, boolean cancelable, final CallBackListener callback) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setTitle(title);
//        dialog.setCancelable(cancelable);
//        dialog.setMessage(message);
//        //第一个按钮
//        dialog.setPositiveButton(posBtnText, new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                callback.onSuccess();
//            }
//        });
//
//        //中间的按钮
//        dialog.setNeutralButton(neuBtnText, new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                callback.onFailure(null);
//            }
//        });
//
//        //第三个按钮
//        dialog.setNegativeButton(negBtnText, new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //callback.onFailure(null);
//            }
//        });
//
//        try {
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * dialog 提示 ,只有一个确定按钮
     *
     * @param context
     * @param title      标题
     * @param message    提示
     * @param cancelable 是否可取消
     * @param callback   回调函数
     */
//    public static void showOneDialog(Context context, String title, String message, boolean cancelable, final CallBackListener callback) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setTitle(title);
//        dialog.setCancelable(cancelable);
//        dialog.setMessage(message);
//        dialog.setPositiveButton("确定", new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                callback.onSuccess();
//            }
//        });
//        try {
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * coyp 文件
     *
     * @param input   输入流
     * @param newFile 文件
     * @author lee
     */
    public static void copyFile(InputStream input, File newFile)
            throws Exception {
        OutputStream output = new FileOutputStream(newFile);
        byte[] buffer = new byte[1024];
        int i = 0;
        while ((i = input.read(buffer)) != -1) {
            output.write(buffer, 0, i);
        }
        output.flush();
        output.close();
        input.close();
    }

	/*public static int getSing(Context context)
    {
		try {
		    final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    for (final CellInfo info : tm.getAllCellInfo()) {
		    	
		        if (info instanceof CellInfoGsm) {
		            final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
		            return gsm.getAsuLevel();
		        } else if (info instanceof CellInfoCdma) {
		            final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
		            return cdma.getAsuLevel();
		        } else if (info instanceof CellInfoLte) {
		            final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
		           	return lte.getLevel();
		        } else {
		            throw new Exception("Unknown type of cell signal!");
		        }
		    }
		} catch (Exception e) {
		    D.e("");
		}
		return 0;
	}*/

    /**
     * 预览图片
     *
     * @param context
     * @param path    本地路径或者是下载地址URL
     * @param isURL   是否是下载地址URL
     */
    public static void previewImage(Context context, String path, boolean isURL) {
//        Intent i = new Intent(context, ImageActivity.class);
//        i.putExtra("isURL", isURL);
//        i.putExtra("path", path);
//        context.startActivity(i);
    }

    /**
     * 通过异或方式解密图片
     *
     * @param picPath 图片路径及名字
     * @return
     */

    public static Bitmap decrypt(String picPath) {
        Bitmap pic;
        try {
            // 去读加密后的图片，并解密，解密后在ImageView里面显示图片
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(picPath));
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = bis.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            bis.close();
            // 加密的字节数组
            byte[] content = out.toByteArray();
            // 定义一个存放解密的字节的字节数组
            byte[] content2 = new byte[content.length];
            for (int i = 0; i < content.length; i++) {
                // 异或解密
                content2[i] = (byte) (content[i] ^ 123);
            }
            // 通过BitmapFactory把字节数组转成Bitmap对象；
            pic = BitmapFactory.decodeByteArray(content2, 0, content2.length);
            return pic;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException E) {
            E.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过异或方式加密图片 （支持加密其他格式文件）
     *
     * @param picPath 文件路径及名字
     */
    public static void encryptPic(String picPath) {
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(picPath));
            List<Integer> list = new ArrayList<Integer>();// 定义集合<字节>用来存储数据
            int len; // 定义变量，用来存储数据
            while ((len = bis.read()) != -1)
                // 循环读取，直到读取到末尾为止
                list.add(len ^ 123);// 从文件中逐个字节读取数据，异或密码，存入集合
            bis.close(); // 关闭输入流

            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(picPath));
            for (Integer i : list)
                // 遍历集合，将所有数据写回文件
                bos.write(i);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过异或方式加密图片 （支持加密其他格式文件）
     *
     * @param picPath 文件路径及名字
     */
    public static void encryptBitmap(String picPath) {
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(picPath));
            List<Integer> list = new ArrayList<Integer>();// 定义集合<字节>用来存储数据
            int len; // 定义变量，用来存储数据
            while ((len = bis.read()) != -1)
                // 循环读取，直到读取到末尾为止
                list.add(len ^ 123);// 从文件中逐个字节读取数据，异或密码，存入集合
            bis.close(); // 关闭输入流

            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(picPath));
            for (Integer i : list)
                // 遍历集合，将所有数据写回文件
                bos.write(i);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理返回信息，适用于点击确定重试，点击取消返回上一页
     *
     * @param context
     * @param title    dialog 标题
     * @param message  dialog 提示内容
     * @param callBack 回调方法
     */
//    public static void relodOrBack(Context context, String title, String message, final AjaxCallBack<String> callBack) {
//        Utils.showDialog(context, title, message, false,
//                new CallBackListener() {
//
//                    @Override
//                    public void onSuccess() {
//                        super.onSuccess();
//                        callBack.onFailure(null, 0, "reload");
//                    }
//
//                    @Override
//                    public void onFailure(String error) {
//                        super.onFailure(error);
//                        callBack.onFailure(null, 0, "back");
//                    }
//
//                });
//    }

    /**
     * 处理返回信息，适用于检测到版本需要强制更新，点击确定直接退出程序
     *
     * @param context
     * @param title    dialog 标题
     * @param message  dialog 提示内容
     */
//    public static void needRestart(Context context, String title, String message) {
//        showDialog(context, title, message, "确定", null, false, new CallBackListener() {
//            @Override
//            public void onSuccess() {
//                super.onSuccess();
//                exitApp();
//            }
//
//            @Override
//            public void onFailure(String error) {
//                super.onFailure(error);
//                exitApp();
//            }
//        });
//    }

    /**
     * 列表对话框
     *
     * @param context
     * @param title    标题
     * @param items    数据
     * @param listener
     */
    public static void showItemDialog(Context context, String title, String[] items, boolean cancelable, final OnClickListener listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setItems(items, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int position) {
                listener.onClick(arg0, position);
            }
        });
        dialog.show();
    }

//    /**
//     * 无网络情况弹出对话框
//     *
//     * @param context
//     * @param callBack
//     */
//    public static void showNoNetWork(Context context, final CallBackListener callBack) {
//        Utils.showOneDialog(context, "网络提示", "请重新加载", false, callBack);
//    }

    public static void byteToFile(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 计算百分比（用于下载显示百分比）
     *
     * @param currrent 已下载
     * @param total    总大小
     * @return string 百分比0.00%
     */
//    public static String getPercent(long current, long total) {
//        String result = "";//接受百分比的值
//        long tempresult = current / total;
//        DecimalFormat df1 = new DecimalFormat("0.0%");    //##.00%   百分比格式，后面不足2位的用0补齐
//        result = df1.format(tempresult);
//        return result;
//    }

    /**
     * 生成UUID
     *
     * @return string uuid
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 退出程序
     */
    public static void exitApp() {
        System.exit(0);
    }

    /**
     * 判断是否在前台运行
     *
     * @param context
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
        if (taskInfos.size() > 0
                && TextUtils.equals(context.getPackageName(),
                taskInfos.get(0).topActivity.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     * String 保存到sd
     *
     * @param data
     * @param path
     * @return
     */
    public static boolean saveStringToSDCard(String data, String path) {
        //if (!D.allowE) return false;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = df.format(new Date());
        data = currentTime + ":" + data + "\n";
        BufferedWriter bw = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(),
                    path);
            //第二个参数意义是说是否以append方式添加内容
            bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(data);
            bw.flush();
            bw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            return false;
        }
    }

    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
//    public static boolean isServiceRunning(Context mContext, String className) {
//        boolean isRunning = false;
//        ActivityManager activityManager = (ActivityManager) mContext
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
//                .getRunningServices(100);
//        if (!(serviceList.size() > 0)) {
//            return false;
//        }
//        for (int i = 0, size = serviceList.size(); i < size; i++) {
//
//            if (serviceList.get(i).service.getClassName().equals(className) == true) {
//                isRunning = true;
//                D.e(serviceList.get(i).service.getClassName());
//                break;
//            }
//        }
//        return isRunning;
//    }

}
