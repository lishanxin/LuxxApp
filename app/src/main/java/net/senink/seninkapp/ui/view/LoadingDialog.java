package net.senink.seninkapp.ui.view;

import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.util.PictureUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 加载框
 * @author zhaojunfeng
 * @date 2015-12-08
 */

public class LoadingDialog extends RelativeLayout {

	//默认加载时间是一分钟
	private int loadingTime = 60000;
	private final static int MSG_HIDE_DIALOG = 10;
	private ImageView iv;
	private AnimationDrawable anima;
	private Handler mHandler;
	private Context context;
	public LoadingDialog(Context context) {
		super(context);
		init(context);
	}
	
	public LoadingDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public LoadingDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
       this.context = context;
//	   setBackgroundResource(R.drawable.icon_loading_bg);
	   setHandler(context);
	}

	private void addLoadingAnima(Context context) {
		iv = new ImageView(context);
		   iv.setBackgroundResource(R.drawable.anim_loading);
		   LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		   params.addRule(CENTER_IN_PARENT);
		   iv.setLayoutParams(params);
		   addView(iv);
	}
    
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		Paint mPaint = new Paint();
		mPaint.setStyle(Style.FILL);
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_loading_bg);
		bm = PictureUtils.scalePicture(bm, width, height);
		canvas.drawBitmap(bm, 0, 0, mPaint);
		addLoadingAnima(context);
	}
	
	
	private void setHandler(Context context) {
		mHandler = new Handler(context.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MSG_HIDE_DIALOG) {
					hideLoadingDialog();
				}
			}
		};
	}

	/**
	 * 显示加载框
	 */
	public void showLoadingDialog(){
		if (null == anima && iv != null) {
			anima = (AnimationDrawable) iv.getBackground();
		}
		if (anima != null) {
			anima.start();
		}
		setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageAtTime(MSG_HIDE_DIALOG, loadingTime);
	}
	
	/**
	 * 隐藏加载框
	 */
	public void hideLoadingDialog(){
		if (anima != null) {
			anima.stop();
		}
		setVisibility(View.GONE);
	}
	
	/**
	 * 设置加载时间
	 * @param time
	 */
	public void setLoadingTime(int time){
		if (time > 0) {
			loadingTime = time;
		}
	}
}
