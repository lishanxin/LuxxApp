package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import net.senink.seninkapp.R;

/**
 * 自定义线
 * 
 * @author zhaojunfeng
 * @date 2016-02-03
 * 
 */
public class LineView extends View {
	// 颜色框图片
	private Bitmap bgBitmap;
	// 自定义图形的宽度
	private float width;
	// 自定义图形的高度
	private float height;
	// 画笔
	private Paint mPaint;
	//是否处于选中状态
	private boolean isChecked;
	
	public LineView(Context context) {
		super(context);
		init(context, null);
	}

	public LineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(4);
		mPaint.setColor(0xFFD3DBE6);
	    bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_insolestep_selectedline);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setHeightAndWidth();
		if (isChecked) {
			canvas.drawBitmap(bgBitmap, 0, height - bgBitmap.getHeight(), mPaint);
		}else{
			canvas.drawLine(0, height, width, height, mPaint);
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
		if (height == 0 || width == 0) {
			height = bgBitmap.getHeight();
			width = bgBitmap.getWidth();
		}
	}

	public void setCheckedOnLine(boolean isChecked){
		this.isChecked = isChecked;
		invalidate();
	}

}
