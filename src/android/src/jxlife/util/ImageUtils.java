package jxlife.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jxlife.signature.utils.Utils;

/** 
 * 影像处理
 *
 * @author	lee
 * @date	2015-9-7 下午5:23:15 
 * 
 */
public class ImageUtils {
	
	private static final String FIRST_LINE_WATERMARK = "仅供吉祥人寿投保使用";
	private static final String SECOND_LINE_WATERMARK = "与原件一致";
	private static final String THIRD_LINE_WATERMARK_01 = "核对人";
	private static final String THIRD_LINE_WATERMARK_03 = "核对日期";
	
	//根据960*720计算
		private static double calRate(int w,int h){
			double maxv=Math.max(w, h);
			double minv=Math.min(w, h);
			if(maxv*3>=minv*4){
				return 720.0/minv;
			}else{
				return 960.0/maxv;
			}
		}

	/**
	 * 图片尺寸处理 添加水印
	 */
	public static Bitmap dealImage(Bitmap bmp,String userName,boolean needWaterMark) {
		try {
			int height = bmp.getHeight();
			int width = bmp.getWidth();
			double rate = calRate(width,height);
			height = (int)(height*rate);
			width = (int)(width*rate); 

			// 建立画笔
			Paint photoPaint = new Paint();
			// 获取跟清晰的图像采样
			photoPaint.setDither(true);
			bmp = resizeImage(bmp, width, height);
			Bitmap newbm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(newbm);
			canvas.drawBitmap(bmp, 0, 0, null);
			if(needWaterMark){
				if (height > width) {// 竖屏960:720
					// 绘制第一行水印 
					drawWatermark(canvas, FIRST_LINE_WATERMARK, width-720+565, height-960+870, 15);
					// 绘制第二行水印
					drawWatermark(canvas, SECOND_LINE_WATERMARK, width-720+640, height-960+900, 15);
					// 绘制第三行水印
					drawWatermark(canvas, THIRD_LINE_WATERMARK_01, width-720+430, height-960+930, 15);
					drawWatermark(canvas, userName, width-720+480, height-960+930, 15);
					drawWatermark(canvas, THIRD_LINE_WATERMARK_03, width-720+540, height-960+930, 15);
					drawWatermark(canvas, Utils.getCurrentDate("yyyy年MM月dd日"), width-720+607, height-960+930, 15);
				} else {// 横屏720:960
					// 绘制第一行水印
					drawWatermark(canvas, FIRST_LINE_WATERMARK, width-960+805, height-720+630, 15);
					// 绘制第二行水印
					drawWatermark(canvas, SECOND_LINE_WATERMARK, width-960+880, height-720+660, 15);
					// 绘制第三行水印
					drawWatermark(canvas, THIRD_LINE_WATERMARK_01, width-960+670, height-720+690, 15);
					drawWatermark(canvas, userName, width-960+720, height-720+690, 15);
					drawWatermark(canvas, THIRD_LINE_WATERMARK_03, width-960+780, height-720+690, 15);
					drawWatermark(canvas, Utils.getCurrentDate("yyyy年MM月dd日"), width-960+847,	height-720+690, 15);

				}

			}
			canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
			canvas.restore();// 存储
			return newbm;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}

	/**
	 * 尺寸处理
	 */
	private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	private static void drawWatermark(Canvas canvas, String text, float x, float y,
			float size) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		paint.setTextSize(size);
		paint.setTypeface(Typeface.DEFAULT);
		paint.setColor(Color.WHITE);
		paint.setAlpha(40);
		canvas.drawText(text, x, y, paint);
	}
	
	/**
	 * 图片保存到SD卡，加密
	 */
	public static boolean savePicToSdcard(Bitmap bitmap, String path,boolean isEncry, String imageType) {
		if (bitmap == null) {
			return false;
		} else {

			File destFile = new File(path);
			boolean b = destFile.getParentFile().isDirectory();
			if(!b){
				File tem = new File(destFile.getParent());
				// tem.getParentFile().setWritable(true);
				tem.mkdirs();// 创建目录
			}
//			if (!destFile.exists()) {
//				try {
//					destFile.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//					return false;
//				}
//			}
			OutputStream os = null;
			try {
//				os = new FileOutputStream(destFile);
				os = new BufferedOutputStream(new FileOutputStream(destFile)); 
				if("PNG".equals(imageType)){
					bitmap.compress(CompressFormat.PNG, 80, os);
				} else{
					bitmap.compress(CompressFormat.JPEG, 80, os);
					
				}
				os.flush();
				os.close();
				
				if(isEncry)
				{
					// 加密
					Utils.encryptPic(path);
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				try {
					os.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	
//	public static void showPhotoView(String str,Activity ctx,String flag,SignatureQueryImageDto signatureQueryImageDto){
//		DisplayMetrics dm = new DisplayMetrics();
//		ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Intent i = new Intent(ctx,PhotoPreviewActivity.class);
//		if(flag.equals("base64")){
//			i.putExtra("signatureQueryImageDto", JsonUtils.toJsonObj(signatureQueryImageDto));
//			i.putExtra("tag", flag);
//			ctx.startActivity(i);
//		}else if(flag.equals("imageViewBase64")){
//			if(!TextUtils.isEmpty(str)){
//				Bitmap bitMap = resizeImage(decodeString2Bitmap(str),400,400);
//				LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//				//此处相当于布局文件中的Android:layout_gravity属性
//				lp.gravity = Gravity.CENTER;
//				LinearLayout layout=new LinearLayout(ctx);
//				layout.setOrientation(LinearLayout.VERTICAL);
//				layout.setBackgroundColor(Color.WHITE);
//				layout.setLayoutParams(lp);
//				 ImageView iv = new ImageView(ctx);
//				 iv.setImageBitmap(bitMap);
//				 layout.addView(iv);
//				 TextView tV=new TextView(ctx);
//				 tV.setText("扫描二维码转发签名链接给用户");
//				 tV.setGravity(Gravity.CENTER_HORIZONTAL);
//				 layout.addView(tV);
//				 tV.setTextSize(18);
//				new AlertDialog.Builder(ctx).setView(layout).show();
//			}
//		}else{
//			i.putExtra("photo", str);
//			i.putExtra("tag", flag);
//			ctx.startActivity(i);
//		}
//	}
	
	public static Bitmap decodeFilePath2Bitmap(String path){
		Bitmap bitMap = null;
		if(!TextUtils.isEmpty(path)){
			InputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(path);
				bitMap = BitmapFactory.decodeStream(fileInputStream);
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
		return bitMap;
	}
	
	public static Bitmap decodeString2Bitmap(String base64){
		Bitmap bitmap = null;
		try {
		    byte[]bitmapArray;
		    bitmapArray=Base64.decode(base64, Base64.DEFAULT);
		    bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		}catch(Exception e){
			e.printStackTrace();
		}
		return bitmap;
	}
}
 