package net.senink.seninkapp.ui.view;

import java.util.ArrayList;
import java.util.List;
import net.senink.seninkapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 半圆形按钮
 * 
 * @author zhaojunfeng
 * @date 2016-02-02
 */
public class HalfCircleButton extends View {

	private Paint mPaint = null;
	// 组件宽度
	private int width;
	// 组件高度
	private int height;
	// 左边距
	private int boarderOnLeft;
	// 右边距
	private int boarderOnTop = 0;
	// 左边斜线列表
	private List<Point> listOnLeft;
	// 右边斜线列表
	private List<Point> listOnRight;
	// 当前图片的下标
	private int index = 0;
	// 三幅图片
	private Bitmap[] bms = new Bitmap[3];
	// 斜线的颜色
	private final int lineColor = Color.rgb(204, 204, 204);
	// 色环颜色
	private final int circleColor = Color.rgb(243, 243, 243);
	// 红色色环颜色
//	private final int redColor = Color.rgb(255, 91, 91);
	// 字体颜色
	private final int fontColor = Color.rgb(102, 102, 102);
	// 监听器
	private OnItemClickListener listener;

	
	public HalfCircleButton(Context context) {
		super(context);
		init(context, null);
	}

	public HalfCircleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HalfCircleButton(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		mPaint = new Paint();
		listOnLeft = new ArrayList<Point>();
		listOnRight = new ArrayList<Point>();
		bms[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_halfcircle_1);
		bms[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_halfcircle_2);
		bms[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_halfcircle_3);
		setListOnLeft();
		setListOnRight();
	}

	/**
	 * 获取右边斜线坐标集合
	 */
	private void setListOnRight() {
		int width = bms[0].getWidth();
		int height = bms[0].getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (bms[0].getPixel(j, i) == lineColor && j > (width / 2)) {
					Point mPoint = new Point();
					mPoint.x = j;
					mPoint.y = i;
					listOnRight.add(mPoint);
				}
			}
		}
	}

	/**
	 * 获取右边斜线坐标集合
	 */
	private void setListOnLeft() {
		int width = bms[2].getWidth();
		int height = bms[2].getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (bms[2].getPixel(j, i) == lineColor && j < (width / 2)) {
					Point mPoint = new Point();
					mPoint.x = j;
					mPoint.y = i;
					listOnLeft.add(mPoint);
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setWidthAndHeight();
		canvas.drawBitmap(bms[index], boarderOnLeft, boarderOnTop, mPaint);
	}

	/**
	 * 设置高宽
	 */
	private void setWidthAndHeight() {
		if (height == 0 || width == 0) {
			height = getHeight();
			width = getWidth();
			if (height == 0) {
				height = bms[0].getHeight();
				width = bms[0].getWidth();
			}
			boarderOnLeft = (width - bms[0].getWidth()) / 2;
			boarderOnTop = height - bms[0].getHeight();
//			boarderOnTop = (height - bms[0].getHeight()) / 2;
		}
	}

	/**
	 * 设置坐标（根据坐标判断出点击区域）
	 * 
	 * @param x
	 * @param y
	 */
	public void setAlias(int x, int y) {
		x = x - boarderOnLeft;
		y = y - boarderOnTop;
		Point mPoint = new Point();
		mPoint.x = x;
		mPoint.y = y;
		boolean exist = false;
		int color = bms[index].getPixel(x, y);
		if (color == circleColor || color == fontColor) {
			for (Point point : listOnLeft) {
				if (point.compareTo(mPoint) > 0) {
					exist = true;
					index = 0;
					break;
				}
			}
			if (!exist) {
				for (Point point : listOnRight) {
					if (point.compareTo(mPoint) > 0) {
						exist = true;
						index = 1;
						break;
					} else if (mPoint.compareTo(point) > 0) {
						exist = true;
						index = 2;
						break;
					}
				}
			}
			if (exist) {
				invalidate();
				listener.onItemClick(index);
			}
		}
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		int width = getMeasuredWidth();
//		int height = getMeasuredHeight();
//		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
//			setMeasuredDimension(width, width);
//		} else {
//			setMeasuredDimension(width, height);
//		}
//	}
	/**
	 * index 0,1,2
	 * @param index
	 */
    public void setCurrentItem(int index){
    	if (index >= 0 && index < 3) {
			if (index != this.index) {
				this.index = index;
				invalidate();
				listener.onItemClick(index);
			}
		}
    }
	
    public int getCurrentItemId(){
    	return index;
    }
    
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public interface OnItemClickListener {
		public void onItemClick(int index);
	}

	private class Point implements Comparable<Point> {
		float x;
		float y;

		@Override
		public int compareTo(Point another) {
			int value = -1;
			if (x > another.x && y > another.y) {
				value = 1;
			} else if (x == another.x && y == another.y) {
				value = 0;
			}
			return value;
		}
	}
}
