package net.senink.seninkapp.ui.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.text.TextUtils;
import net.senink.seninkapp.R;

/**
 * 图片处理的工具类
 * 
 * @author zhaojunfeng
 * @date 2015-11-24
 * 
 */

public class PictureUtils {
	// 图片高度
	public static final int PICTURE_HEIGHT = 120;
	// 图片宽度
	public static final int PICTURE_WIDTH = 120;
    //保存图片的路径
	public static final String PICTURE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/senink/scene/";
	public static final String PICTURE_PATH1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/senink/picture/";
	/**
	 * 缩放图片
	 * 
	 * @param path
	 * @param scale
	 * @return
	 */
	public static Bitmap scalePicture(String path, int width, int height) {
		Bitmap bm = getBitmap(path);
		if (bm != null) {
			int bmWidth = bm.getWidth();
			int bmHeight = bm.getHeight();
			float scaleWidth = (float) width / bmWidth;
			float scaleHeight = (float) height / bmHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			bm = Bitmap.createBitmap(bm, 0, 0, bmWidth, bmHeight, matrix, true);
		}
		return bm;
	}

	public static Bitmap scalePicture(Bitmap bm, int width, int height) {
		if (bm != null) {
			int bmWidth = bm.getWidth();
			int bmHeight = bm.getHeight();
			float scaleWidth = (float) width / bmWidth;
			float scaleHeight = (float) height / bmHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			bm = Bitmap.createBitmap(bm, 0, 0, bmWidth, bmHeight, matrix, true);
		}
		return bm;
	}
	/**
	 * 
	 * @param path
	 *            本地图片路径
	 * @return
	 */
	public static Bitmap getBitmap(String path) {
		Bitmap bm = null;
		if (!TextUtils.isEmpty(path) && new File(path).exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bm = BitmapFactory.decodeFile(path, options);
			options.outHeight = PICTURE_HEIGHT;
			options.outWidth = PICTURE_WIDTH;
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(path, options);
		}
		return bm;
	}

	public static Bitmap getBitmap(String path,int height,int width) {
		Bitmap bm = null;
		if (!TextUtils.isEmpty(path) && new File(path).exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bm = BitmapFactory.decodeFile(path, options);
			options.outHeight = height;
			options.outWidth = width;
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(path, options);
		}
		return bm;
	}
	/**
	 * 转换为圆角图片
	 * 
	 * @param bm
	 * @return
	 */
	public static Bitmap getRoundCornerPiture(Bitmap bm) {
		if (bm != null) {
			int radius = (bm.getWidth() < bm.getHeight() ? bm.getWidth() : bm
					.getHeight()) / 2;
			bm = SDCardUtils.getCroppedRoundBitmap(bm, radius);
		}
		return bm;
	}

	/**
	 * 把图片保存到本地
	 */
	public static String saveBitmap(Bitmap bm, String picName) {
		String path = null;
		File f = new File(PICTURE_PATH+ picName);
		if (f.exists()) {
			f.delete();
		}
		f.getParentFile().mkdirs();
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			f = null;
			e.printStackTrace();
		} catch (IOException e) {
			f = null;
			e.printStackTrace();
		}
		if(f != null){
			path = f.getAbsolutePath();
		}
		return path;
	}
	
	public static String saveBitmap(Bitmap bm, String picName,String fileName) {
		String path = null;
		File f = new File(PICTURE_PATH1+ "/"+fileName+"/"+picName+".png");
		if (!f.exists()) {
			if (f.getTotalSpace() == 0) {
				f.delete();
			}
			f.getParentFile().mkdirs();
			try {
				FileOutputStream out = new FileOutputStream(f);
				bm.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				f = null;
				e.printStackTrace();
			} catch (IOException e) {
				if (f.exists()) {
					f.delete();
				}
				f = null;
				e.printStackTrace();
			}
		}
		if(f != null){
			path = f.getAbsolutePath();
		}
		return path;
	}
	
	/**
	 * 把bitmap转换为字节数组
	 * @param bmp
	 * @return
	 */
	
	public static byte[] getPicByte(Bitmap bmp) {
		byte[] byteArray = null;
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byteArray = stream.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteArray;
	}
	
	/**
	 * 根据默认名称获取对应的默认图片
	 * @param context
	 * @param pictureName
	 * @return
	 */
	public static Bitmap getBigDefaultBitmap(Context context,String pictureName){
		Bitmap bm = null;
		if (!TextUtils.isEmpty(pictureName)) {
			if (pictureName.equals("0")) {
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_big_scene1);
			} else if(pictureName.equals("1")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_big_scene2);
			}else if(pictureName.equals("2")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_big_scene3);
			}else if(pictureName.equals("3")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_big_scene4);
			}else if(pictureName.equals("4")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_big_scene5);
			}if (pictureName.equals("5")) {
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_rgb_scene1);
			} else if(pictureName.equals("6")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_rgb_scene2);
			}else if(pictureName.equals("7")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_rgb_scene3);
			}else if(pictureName.equals("8")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_rgb_scene4);
			}
		}
		if (null == bm) {
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_big_scene1);
		}
		return bm;
	}
	
	/**
	 * 根据默认名称获取对应的默认图片
	 * @param context
	 * @param pictureName
	 * @return
	 */
	public static Bitmap getSmallDefaultBitmap(Context context,String pictureName){
		Bitmap bm = null;
		if (!TextUtils.isEmpty(pictureName)) {
			if (pictureName.equals("0") || pictureName.equals("5")) {
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_scene1);
			} else if(pictureName.equals("1") || pictureName.equals("6")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_scene2);
			}else if(pictureName.equals("2") || pictureName.equals("7")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_scene3);
			}else if(pictureName.equals("3") || pictureName.equals("8")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_scene4);
			}else if(pictureName.equals("4")){
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_scene5);
			}
		}
		if (null == bm) {
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_default_big_scene1);
		}
		return bm;
	}
	
	/**
	 * 根据默认名称获取对应的默认图片
	 * @param context
	 * @param pictureName
	 * @return
	 */
	public static int getDefaultPicture(String pictureName){
		int resId = 0;
		if (!TextUtils.isEmpty(pictureName)) {
			if (pictureName.equals("0")) {
				resId =  R.drawable.icon_default_bg_scene1;
			} else if(pictureName.equals("1")){
				resId =  R.drawable.icon_default_bg_scene2;
			}else if(pictureName.equals("2")){
				resId =  R.drawable.icon_default_bg_scene3;
			}else if(pictureName.equals("3")){
				resId =  R.drawable.icon_default_bg_scene4;
			}else if(pictureName.equals("4")){
				resId =  R.drawable.icon_default_bg_scene5;
			}
		}
		if (resId == 0) {
			resId =  R.drawable.icon_default_bg_scene1;
		}
		return resId;
	}
	
	/**
	 * 下载图片
	 * @param path
	 * @return
	 */
    public static Bitmap downloadPicture(String path) {
        Bitmap bitmap = null;
        URL imageUrl = null;
        if (path == null || path.length() == 0) {
            return null;
        }
        try {
            imageUrl = new URL(path);
            URLConnection conn = imageUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            int length = conn.getContentLength();
            if (length != -1) {
                byte[] imgData = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) != -1) {
                    System.arraycopy(temp, 0, imgData, destPos, readLen);
                    destPos += readLen;
                }
                bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            }
        } catch (IOException e) {
           e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
