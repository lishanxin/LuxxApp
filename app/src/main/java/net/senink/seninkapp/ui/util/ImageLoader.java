package net.senink.seninkapp.ui.util;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * 用于下载图片
 * 
 * @author zhaojunfeng
 * 
 */
public class ImageLoader {

	private static ImageLoader instance;
	private LruCache<String, Bitmap> mLruCache;
	private List<String> classIds;
	private OnResultListener resultListener;
	
	public static ImageLoader getInstance() {
		if (null == instance) {
			instance = new ImageLoader();
		}
		return instance;
	}

	private ImageLoader() {
		init();
	}

	private void init() {
		classIds = new ArrayList<String>();
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	/**
	 * 加载图片
	 * @param iv
	 * @param path
	 * @param resId
	 */
	public void loadImage(ImageView iv,String path,String classId,int resId){
		if (!TextUtils.isEmpty(path)) {
			if (mLruCache.get(path) != null) {
				classIds.remove(classId);
				iv.setImageBitmap(mLruCache.get(path));
			} else {
				if (!classIds.contains(classId)) {
					DownloadTask task = new DownloadTask(iv,classId, resId);
					classIds.add(classId);
					task.execute(path);
				}
			}
		}else{
			iv.setImageResource(resId);
		}
	}
	
	/**
	 * 结束
	 */
	public void stop() {
		mLruCache.evictAll();
		classIds.clear();
	}
	
	public Bitmap getBitmap(String classId){
		return mLruCache.get(classId);
	}

	private class DownloadTask extends AsyncTask<String, Long, Bitmap>{

		private ImageView iv;
		private int resId;
		private String classId;
		public DownloadTask(ImageView iv,String classId,int resId){
			this.iv = iv;
			this.resId = resId;
			this.classId = classId;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			String path = params[0];
			Bitmap bm = PictureUtils.downloadPicture(path);
			return bm;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			classIds.remove(classId);
			if (null == result) {
				iv.setImageResource(resId);
			} else {
				synchronized (resultListener) {
					resultListener.onResult(classId, result);
				}
				mLruCache.put(classId, result);
				iv.setImageBitmap(result);
			}
		}
	}
	
	public void setOnResultListener(OnResultListener listener){
		this.resultListener = listener;
	}

	public interface OnResultListener{
		public void onResult(String classId,Bitmap bm);
	}
}
