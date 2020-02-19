package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import net.senink.seninkapp.R;

/**
 * 自定义步骤图
 * @author zhaojunfeng
 * @date 2015-10-08
 *
 */
public class StepsView extends View {
   
	private Paint mPaint;
	// 组件高度
	private int height = 0;
	// 组件宽度
	private int width = 0;
	// 总共分成n等分
	private int num = 4;
	// 显示n等分是否改变
	private boolean isChanged = false;
	// 当前的阶段
	private int state = 0;
	// 是否失败
	private boolean isFailed = false;
	// 三种状态的圆形图标
	private Bitmap[] drawables = new Bitmap[3];
    //圆形图标的y轴坐标
	private int[] locations = null;
	//圆形图标的高度
	private int bmHeight = 0;
	//当前进度的颜色值
	private int currentColor = Color.WHITE;
	
	public StepsView(Context context) {
		super(context);
		init(context);
	}

	public StepsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public StepsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		Resources res = context.getResources();
		drawables[0] = BitmapFactory
				.decodeResource(res, R.drawable.circle_white);
		drawables[1] = BitmapFactory
				.decodeResource(res, R.drawable.circle_gray);
		drawables[2] = BitmapFactory.decodeResource(res, R.drawable.circle_red);
		bmHeight = drawables[0].getHeight();
		locations = new int[num];
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (height == 0 || width == 0 || isChanged) {
			setWidthAndHeight();
		}
		canvas.save();
        for (int i = 0; i < num; i++) {
			drawCircle(canvas, i, locations[i]);
			if (i > 0) {
				drawLine(canvas, i, locations[i-1]+bmHeight, locations[i]);
			}
		}
        canvas.restore();
	}

	/**
	 * 设置高宽
	 */
	private void setWidthAndHeight() {
		width = getWidth();
		height = getHeight();
	}

	/**
	 * 设置显示分成几等分
	 * 
	 * @param num
	 */
	public void setNumber(int num) {
		if (num == 0) {
			return;
		}
		this.num = num;
		locations = new int[num];
		isChanged = true;
	}
    
	/**
	 * 设置当前进行到哪一步
	 * 从0开始计算
	 * @param index
	 */
	public void setCurrentStep(int index){
		this.state = index;
	}
	
	/**
	 * 设置结果成功与失败
	 * @param success
	 */
	public void setResult(boolean success){
		if (success) {
			this.isFailed = false;
		} else {
            this.isFailed = true;
		}
	}
	/**
	 * 设置结果成功与失败
	 * @param index 
	 *     进行到了哪一步成功或者失败
	 * @param success
	 * 
	 */
	public void setResult(int index,boolean success){
		if (success) {
			this.isFailed = false;
		} else {
            this.isFailed = true;
		}
		this.state = index;
		invalidate();
	}
	/**
	 * 画默认等分的字符串
	 */
	private void drawLine(Canvas canvas, int index, int start, int end) {
		if (isFailed) {
			if (state >= index + 1 || index == 1) {
				mPaint.setColor(currentColor);
			} else {
				mPaint.setColor(Color.RED);
			}
		} else {
			if (state >= index || index == 1) {
				mPaint.setColor(currentColor);
			} else {
				mPaint.setColor(Color.GRAY);
			}
		}
		mPaint.setStrokeWidth(3.0f);
		canvas.drawLine(width / 2, start, width / 2, end, mPaint);
	}

	/**
	 * 画默认等分的圆形图标
	 */
	private void drawCircle(Canvas canvas, int index, int top) {
		Bitmap bm = null;
		if (index == 0 || (state == index && !isFailed) || state > index) {
			bm = drawables[0];
		}else if (state <= index && isFailed) {
			bm = drawables[2];
		}else{
			bm = drawables[1];
		}
		canvas.drawBitmap(bm, (width - bm.getWidth())/2, top, mPaint);
	}
	
	/**
	 *设置第n-1个和第n个之间的间隔 
	 */
	public void setLocation(int index,int location){
		if (index >= locations.length || index < 0) {
			return;
		}
	    locations[index] = location - bmHeight/2;
	}
	
	@Override
	public void setLayoutParams(LayoutParams params) {
		super.setLayoutParams(params);
		setWidthAndHeight();
	}
}
