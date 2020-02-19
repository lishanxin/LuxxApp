package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆圈的自定义组件
 * 
 * @author zhaojunfeng
 * @date 2015-07-14
 * 
 */

public class CircleView extends View {
	// 画笔
	private Paint mPaint = null;
	// 颜色值
	private int color = 0;
	// 组件高度
	private int height = 0;
	// 组件的宽度
	private int width = 0;
	// 半径
	private int radius = 0;

	// 构造方法
	public CircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setPaint();
	}

	// 构造方法
	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaint();
	}

	// 构造方法
	public CircleView(Context context) {
		super(context);
		setPaint();
	}

	/**
	 * 设置画笔
	 */
	private void setPaint() {
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		this.color = Color.RED;
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (null == mPaint) {
			setPaint();
		}
		if (width == 0 || height == 0) {
			width = getWidth();
			height = getHeight();
			radius = width / 2;
		}
		canvas.drawCircle(radius, radius, radius, mPaint);
		super.onDraw(canvas);
	}

	// @SuppressLint("ClickableViewAccessibility")
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// if (event.getAction() == MotionEvent.ACTION_UP) {
	// if (listener != null) {
	// this.listener.onClick();
	// }
	// return true;
	// }
	// return super.onTouchEvent(event);
	// }
	/**
	 * 改变画笔的颜色
	 * 
	 * @param color
	 */
	public void changeColor(int color) {
		if (mPaint != null) {
			mPaint.setColor(color);
		}
		this.color = color;
		invalidate();
	}

	/**
	 * 设置圆圈半径
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * 获取颜色值
	 */
	public int getColor() {
		return color;
	}


	public interface OnViewClickListener {
		public void onClick();
	}
}
