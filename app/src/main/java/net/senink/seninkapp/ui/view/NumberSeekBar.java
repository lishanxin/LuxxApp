package net.senink.seninkapp.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import net.senink.seninkapp.R;

/**
 * 用于自定义进度条且进度下面显示数字
 * 
 * @author zhaojunfeng
 * @date 2015-07-22
 * 
 */
public class NumberSeekBar extends View {

	private Paint mPaint;
	// 进度条的背景
	private Bitmap bgBitmap;
	// 进度条的进度的背景
	private Bitmap progressBitmap;
	// thumb的图片
	private Bitmap thumbBitmap;
	// 高
	private int height = 0;
	// 宽
	private int width = 0;
	// 当前进度
	private int progress = 0;
	// 最大值
	private int max = 255;
	// thumb的高度
	public int heightOnThumb = 0;
	// thumb距离顶部的间隔
	private int marginTopOnThumb = 0;
	// progress距离顶部的间隔
	private int marginTopOnProgress = 0;
	// thumb距离顶部的间隔
	private int marginTopOnText = 0;
	// thumb底部与文字的间隔
	private final static int DISTANCE = 25;
	// 文字大小
	private final static int TEXTSIZE = 25;
	// 滑动时按下的坐标
	private float xStart = 0;
	// 监听器
	private onBarChangerListener listener;

	public NumberSeekBar(Context context) {
		super(context);
		setPaint();
		setBitmaps(context);
	}

	public NumberSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaint();
		setBitmaps(context);
	}

	public NumberSeekBar(Context context, AttributeSet attrs, int definedStyle) {
		super(context, attrs, definedStyle);
		setPaint();
		setBitmaps(context);
	}

	/**
	 * 设置图片
	 */
	private void setBitmaps(Context context) {
		bgBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.light_slider_grey);
		progressBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.light_slider_yellow);
		thumbBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.thumb_cycle);
		heightOnThumb = thumbBitmap.getHeight();
	}

	/**
	 * 设置画笔
	 */
	private void setPaint() {
		mPaint = new Paint();
		mPaint.setColor(0xffffa800);
		mPaint.setTextSize(TEXTSIZE);
		mPaint.setAntiAlias(true);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		setData();
		// 画进度条的背景
		Bitmap bitmap = Bitmap.createScaledBitmap(bgBitmap, width,
				bgBitmap.getHeight(), false);
		canvas.drawBitmap(bitmap, 0, marginTopOnProgress, mPaint);
		// 画进度条
		Bitmap progessBM = Bitmap.createScaledBitmap(progressBitmap, progress
				* width / max + 1, progressBitmap.getHeight(), false);
		canvas.drawBitmap(progessBM, 0, marginTopOnProgress, mPaint);
		// 画thumb
		canvas.drawBitmap(thumbBitmap,
				progress * (width - thumbBitmap.getWidth()) / max,
				marginTopOnThumb, mPaint);
		// 显示数字
		drawText(canvas);
		super.onDraw(canvas);
	}

	/**
	 * 画数字
	 * 
	 * @param canvas
	 */
	private void drawText(Canvas canvas) {
		if (progress < 10) {
			canvas.drawText("" + progress,
					progress * (width - thumbBitmap.getWidth()) / max
							+ getDimensOnText("" + progress) / 2,
					marginTopOnText, mPaint);
		} else if (progress >= 100 && progress < 250) {
			canvas.drawText("" + progress,
					progress * (width - thumbBitmap.getWidth()) / max
							- getDimensOnText("" + progress) / 3,
					marginTopOnText, mPaint);
		} else if (progress > 250) {
			canvas.drawText("" + progress,
					progress * (width - thumbBitmap.getWidth()) / max
							- getDimensOnText("" + progress) / 3,
					marginTopOnText, mPaint);
		} else {
			canvas.drawText("" + progress,
					progress * (width - thumbBitmap.getWidth()) / max,
					marginTopOnText, mPaint);
		}
	}

	/**
	 * 设置间隔和高宽
	 */
	private void setData() {
		if (height == 0 || width == 0) {
			setHeightAndWidth();
			setMarginTop();
		}
	}

	/**
	 * 获取字体所占的长度
	 * 
	 * @param text
	 * @return
	 */
	private float getDimensOnText(String text) {
		float len = 0;
		if (mPaint != null) {
			len = mPaint.measureText(text);
		}
		return len;
	}

	/**
	 * 设置高宽
	 */
	private void setHeightAndWidth() {
		height = getHeight();
		width = getWidth();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			break;
		case MotionEvent.ACTION_MOVE:
			if (xStart == 0) {
				xStart = event.getX();
			} else {
				if (event.getX() - xStart > 40) {
					progress += 5;
				} else if (event.getX() - xStart < -40) {
					progress -= 5;
				}
				if (progress >= max) {
					progress = max;
				} else if (progress < 0) {
					progress = 0;
				}
				invalidate();
				this.listener.onChange(progress);
			}
			break;
		case MotionEvent.ACTION_UP:
			xStart = 0;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 设置间隔
	 */
	private void setMarginTop() {
		if (heightOnThumb * 2 > height) {
			marginTopOnThumb = 0;
		} else {
			marginTopOnThumb = (height - heightOnThumb) / 2;
		}
		marginTopOnProgress = marginTopOnThumb
				+ (heightOnThumb - bgBitmap.getHeight()) / 2;
		marginTopOnText = marginTopOnThumb + heightOnThumb + DISTANCE;
	}

	/**
	 * 设置当前进度
	 */
	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
	}

	/**
	 * 设置最大值
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * 当前进度
	 * 
	 * @return
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * 进度条的最大值
	 */
	public int getMax() {
		return max;
	}

	/**
	 * 设置监听器
	 * 
	 */
	public void setOnBarChangeListener(onBarChangerListener listener) {
		this.listener = listener;
	}

	public interface onBarChangerListener {
		public void onChange(int progress);
	}
}
