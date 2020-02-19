package net.senink.seninkapp.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import net.senink.seninkapp.R;

/**
 * 进度条上方水滴形状和颜色背景的自定义组件
 * 
 * @author zhaojunfeng
 * @date 2016-01-28
 * 
 */
public class WaterDrop extends View {

	private static final int[] FROM_COLOR = new int[] { 49, 179, 110 };
	private static final int THRESHOLD = 3;
	private Context mContext;
	// 颜色框图片
	private Bitmap bgBitmap;
	// 水滴形状的图片
	private Bitmap thumbDrawable;
	// 距离顶部的间隔
	private float paddingTop;
	// 距离底部的间隔
	private float paddingBottom;
	// 距离左边的间隔
	private float paddingLeft;
	// 距离右边的间隔
	private float paddingRight;
	// 颜色框与水滴形状图片的间隔
	private float drawablePadding;
	// 颜色框图片的高度
	private float drawableHeight = 14;
	// 自定义图形的宽度
	private float width;
	// 自定义图形的高度
	private float height;
	// 当前的进度
	private int progress;
	// 当前对应的颜色
	private int currentColor;
	// 图标是否可见
	private boolean isVisiable;
	// 进度条最大值
	private int max;
	// 偏移量
	private float offset = 7;
	// 画笔
	private Paint mPaint;
	public WaterDrop(Context context) {
		super(context);
		init(context, null);
	}

	public WaterDrop(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public WaterDrop(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		this.mContext = context;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		if (attrs != null) {
			TypedArray arr = mContext.obtainStyledAttributes(attrs,
					R.styleable.WaterDropAttr);
			int count = arr.getIndexCount();
			for (int i = 0; i < count; i++) {
				int itemId = arr.getIndex(i);
				int resId = -1;
				switch (itemId) {
//				case R.styleable.WaterDropAttr_background:
//					resId = arr.getResourceId(itemId, -1);
//					if (resId != -1) {
//						bgBitmap = BitmapFactory.decodeResource(getResources(),
//								resId);
//					} else {
//						bgBitmap = BitmapFactory.decodeResource(getResources(),
//								R.drawable.icon_marquee_seekbar_background);
//					}
//					break;
				case R.styleable.WaterDropAttr_thumb:
					resId = arr.getResourceId(itemId, -1);
					if (resId != -1) {
						thumbDrawable = BitmapFactory.decodeResource(
								getResources(), resId);
					} else {
						thumbDrawable = BitmapFactory.decodeResource(
								getResources(),
								R.drawable.icon_seebar_currentcolor);
					}
					break;
				case R.styleable.WaterDropAttr_drawPadding:
					drawablePadding = arr.getDimension(
							R.styleable.WaterDropAttr_drawPadding, 0);
					break;
				case R.styleable.WaterDropAttr_paddingTop:
					paddingTop = arr.getDimension(
							R.styleable.WaterDropAttr_paddingTop, 0);
					break;
				case R.styleable.WaterDropAttr_paddingBottom:
					paddingBottom = arr.getDimension(
							R.styleable.WaterDropAttr_paddingBottom, 0);
					break;
				case R.styleable.WaterDropAttr_paddingLeft:
					paddingLeft = arr.getDimension(
							R.styleable.WaterDropAttr_paddingLeft, 0);
					break;
				case R.styleable.WaterDropAttr_paddingRight:
					paddingRight = arr.getDimension(
							R.styleable.WaterDropAttr_paddingRight, 0);
					break;
//				case R.styleable.WaterDropAttr_width:
//					width = arr
//							.getDimension(R.styleable.WaterDropAttr_width, 0);
//					break;
//				case R.styleable.WaterDropAttr_height:
//					height = arr.getDimension(R.styleable.WaterDropAttr_height,
//							45);
//					break;
				case R.styleable.WaterDropAttr_bgHeight:
//					drawableHeight = arr.getDimension(
//							R.styleable.WaterDropAttr_bgHeight, 10);
					drawableHeight = 0;
					break;
				}
			}
			arr.recycle();
		}
	}

	@SuppressLint("NewApi")
	private void setWidthAndHeightOnBgDrawable() {
		// float value = height - drawablePadding - thumbDrawable.getHeight() -
		// paddingTop - paddingBottom;
		// if (value > 0 && value < drawableHeight) {
		// drawableHeight = value;
		// }
		// bgBitmap.setHeight((int)drawableHeight);
		// bgBitmap.setWidth((int)(width - paddingLeft - paddingRight));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setHeightAndWidth();
//		int thumbHeight = thumbDrawable.getHeight();
		int thumbWidth = thumbDrawable.getWidth();
		drawThumbPicture(canvas, thumbWidth);
//		drawBgPicture(canvas, thumbHeight);
	}

	/**
	 * 画颜色背景图片
	 * 
	 * @param canvas
	 * @param thumbHeight
	 */
	private void drawBgPicture(Canvas canvas, int thumbHeight) {
		float x = paddingLeft;
		float y = paddingTop + thumbHeight + drawablePadding;
		canvas.drawBitmap(getBitmapOnBackGround(canvas), x, y, mPaint);
	}

	/**
	 * 
	 * @param canvas
	 * @param thumbWidth
	 */
	private void drawThumbPicture(Canvas canvas, int thumbWidth) {
		if (isVisiable) {
			float x = 0;
			if (progress < width / 4) {
			   x = progress - (float)(thumbWidth * 0.42) ;
			} else if(progress < width / 2 && progress > width / 4){
	               x = progress - (float)(thumbWidth * 0.56) ;
			}else if(progress >= 3 * width / 4){
				x = progress - (float)(thumbWidth * 0.58) ;
			}else {
               x = progress - (float)(thumbWidth * 0.6) ;
			}
			float y = paddingTop;
			if (x < 0) {
				x = 0;
			} else if (x > width - thumbWidth) {
				x = width - thumbWidth;
			}
			canvas.drawBitmap(getBitmap(), x, y, mPaint);
		}
	}

	/**
	 * 设置高宽
	 */
	private void setHeightAndWidth() {
		if (width == 0) {
			width = getWidth();
		}
		if (height == 0) {
			height = getHeight();
		}
		setWidthAndHeightOnBgDrawable();
	}

	public int getColor(){
		return currentColor;
	}
	/**
	 * 设置当前的进度
	 * 
	 * @param progress
	 */
	public void setProgress(int progress, int max) {
		if (max >= progress && max > 0) {
			this.progress = (int)(width - paddingLeft - paddingRight)* progress / max;
			int x = bgBitmap.getWidth() * progress / max;
			if (x >= bgBitmap.getWidth()) {
				x = bgBitmap.getWidth() - 1;
			}
			if (progress > 3) {
				this.currentColor = bgBitmap.getPixel(x,bgBitmap.getHeight() / 2);
			}else {
				this.currentColor = Color.WHITE;
			}
			invalidate();
		}
	}

	public void setBitmap(Bitmap bm){
		if (bm != null) {
			bgBitmap = bm;
		}
	}
	
	/**
	 * 设置图标是否可见
	 * 
	 * @param isVisiable
	 */
	public void setVisibility(boolean isVisiable) {
		this.isVisiable = isVisiable;
		invalidate();
	}

	/**
	 * 获取当前进度
	 * 
	 * @return
	 */
	public int getProgress(int color, int max) {
		int progress = 0;
		if (max > 0) {
			int y = (int) height / 2;
			int count = bgBitmap.getWidth();
			for (int i = 0; i < count; i++) {
				int value = bgBitmap.getPixel(i, y);
				if (value == color) {
					progress = i;
				}
			}
			progress = progress / (count / max);
			this.max = max;
		}
		return progress;
	}

	/**
	 * 获取当前进度
	 * 
	 * @return
	 */
	public int getProgress() {
		int pro = 0;
		if (max > 0) {
			pro = progress / (bgBitmap.getWidth() / max);
		}
		return pro;
	}

	private Bitmap getBitmap() {
		// Need to copy to ensure that the bitmap is mutable.
		Bitmap bitmap = thumbDrawable.copy(Bitmap.Config.ARGB_8888, true);
		for (int x = 0; x < bitmap.getWidth(); x++)
			for (int y = 0; y < bitmap.getHeight(); y++)
				// if (match(bitmap.getPixel(x, y)))
				if (bitmap.getPixel(x, y) != Color.TRANSPARENT)
					bitmap.setPixel(x, y, currentColor);
		return bitmap;
	}

	private Bitmap getBitmapOnBackGround(Canvas canvas) {
		int bgHeight = (int) (height - drawablePadding
				- thumbDrawable.getHeight() - paddingTop - paddingBottom);
		int bgWidth = (int) (width - paddingLeft - paddingRight);
		if (bgHeight > drawableHeight) {
			bgHeight = (int) drawableHeight;
		}
		if (bgWidth > bgBitmap.getWidth()) {
			bgWidth = bgBitmap.getWidth();
		}
		Bitmap bm = Bitmap.createBitmap(bgBitmap, 0, 0, bgWidth, bgHeight);
		return bm;
	}

	private boolean match(int pixel) {
		// There may be a better way to match, but I wanted to do a comparison
		// ignoring
		// transparency, so I couldn't just do a direct integer compare.
		return Math.abs(Color.red(pixel) - FROM_COLOR[0]) < THRESHOLD
				&& Math.abs(Color.green(pixel) - FROM_COLOR[1]) < THRESHOLD
				&& Math.abs(Color.blue(pixel) - FROM_COLOR[2]) < THRESHOLD;
	}
}
