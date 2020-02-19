package net.senink.seninkapp.ui.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.os.Environment;

public class SDCardUtils {
	public static final String PATH = "/sdcard/senink/information.txt";

	public static Bitmap pointBitSize(Bitmap bmp, int w, int h) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		float scaleHeight = h / (height * 1.0f);
		float scaleWidht = w / (width * 1.0f);
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix,
				true);
		return newbm;
	}
	
	//生成圆角图片
	public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
	    try {
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	                bitmap.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);                
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
	                bitmap.getHeight());       
	        final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
	                bitmap.getHeight()));
	        final float roundPx = 14;
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(Color.BLACK);       
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));            
	  
	        final Rect src = new Rect(0, 0, bitmap.getWidth(),
	                bitmap.getHeight());
	          
	        canvas.drawBitmap(bitmap, src, rect, paint);   
	        return output;
	    } catch (Exception e) {        
	        return bitmap;
	    }
	}
	public static Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
		Bitmap output=null;
		try {
			Bitmap scaledSrcBmp;
			int diameter = radius * 2;

			// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
			int bmpWidth = bmp.getWidth();
			int bmpHeight = bmp.getHeight();
			int squareWidth = 0, squareHeight = 0;
			int x = 0, y = 0;
			Bitmap squareBitmap;
			if (bmpHeight > bmpWidth) {// 高大于宽
				squareWidth = squareHeight = bmpWidth;
				x = 0;
				y = (bmpHeight - bmpWidth) / 2;
				// 截取正方形图片
				squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
						squareHeight);
			} else if (bmpHeight < bmpWidth) {// 宽大于高
				squareWidth = squareHeight = bmpHeight;
				x = (bmpWidth - bmpHeight) / 2;
				y = 0;
				squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
						squareHeight);
			} else {
				squareBitmap = bmp;
			}

			if (squareBitmap.getWidth() != diameter
					|| squareBitmap.getHeight() != diameter) {
				scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
						diameter, true);

			} else {
				scaledSrcBmp = squareBitmap;
			}
			output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
					scaledSrcBmp.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			Paint paint = new Paint();
			Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
					scaledSrcBmp.getHeight());

			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setDither(true);
			canvas.drawARGB(0, 0, 0, 0);
			canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
					scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
					paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
			// bitmap回收(recycle导致在布局文件XML看不到效果)
			 bmp.recycle();
			 squareBitmap.recycle();
			 scaledSrcBmp.recycle();
			bmp = null;
			squareBitmap = null;
			scaledSrcBmp = null;
			System.gc();
		} catch (Throwable e) {
			e.printStackTrace();
			try {
				System.gc();
				Thread.sleep(2000);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return output;
	}
	
	public static Bitmap getRoundRectBitmap(Bitmap bmp, float radius) {
		Bitmap output=null;
		try {
			// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
			int bmpWidth = bmp.getWidth();
			int bmpHeight = bmp.getHeight();
			int squareWidth = 0, squareHeight = 0;
			int x = 0, y = 0;
			Bitmap squareBitmap;
			if (bmpHeight > bmpWidth) {// 高大于宽
				squareWidth = squareHeight = bmpWidth;
				x = 0;
				y = (bmpHeight - bmpWidth) / 2;
				// 截取正方形图片
				squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
						squareHeight);
			} else if (bmpHeight < bmpWidth) {// 宽大于高
				squareWidth = squareHeight = bmpHeight;
				x = (bmpWidth - bmpHeight) / 2;
				y = 0;
				squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
						squareHeight);
			} else {
				squareBitmap = bmp;
			}
			output = Bitmap.createBitmap(squareBitmap.getWidth(),
					squareBitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			Paint paint = new Paint();
			Rect rect = new Rect(0, 0, squareBitmap.getWidth(),
					squareBitmap.getHeight());
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setDither(true);
			canvas.drawARGB(0, 0, 0, 0);
			RectF rtf = new RectF(0, 0, squareBitmap.getWidth(),
					squareBitmap.getHeight());
			canvas.drawRoundRect(rtf, radius, radius, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(squareBitmap, rect, rect, paint);
			// bitmap回收(recycle导致在布局文件XML看不到效果)
			 bmp.recycle();
			 squareBitmap.recycle();
			bmp = null;
			squareBitmap = null;
			System.gc();
		} catch (Throwable e) {
			e.printStackTrace();
			try {
				System.gc();
				Thread.sleep(2000);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return output;
	}
	/*
	 * 判断sdcard是否被挂载
	 */
	public static boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
