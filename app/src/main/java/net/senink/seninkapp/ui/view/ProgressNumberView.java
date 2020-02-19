package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 显示进度条进度值的组件
 * @author zhaojunfeng
 */
public class ProgressNumberView extends View {
	
	//进度值
	private int progress = 0;
	//x坐标
	private float x = 0;
	//字体大小
	private final static float textszie= 30.0f;
	//画笔
	private Paint mPaint = null;
	//高
	private int height = 0;
	//宽
	private int width = 0;
	//偏移量
	private int offset = 30;
    public ProgressNumberView(Context context) {
		super(context);
		setPaint();
	}

	
    
    public ProgressNumberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaint();
	}
    
    public ProgressNumberView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setPaint();
	}
    
    /**
     *设置画笔
     */
    private void setPaint() {
		mPaint = new Paint();
		mPaint.setColor(0xFFFFA800);
		mPaint.setAntiAlias(true);
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(textszie);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (null == mPaint) {
			setPaint();
		}
		if (width == 0 || height == 0) {
			width = getWidth() - offset;
			height = getHeight();
		}
		if (progress >= 0 && x > 6 ) {
			canvas.drawText(""+progress, x, 4*height/5, mPaint);
		}
		super.onDraw(canvas);
		
	}
	/**
	 * 刷新数据
	 * @param progress
	 * @param max
	 */
	public void updateView(int progress,int max){
		this.progress = progress;
		if (progress <= 10) {
			if (progress > 0) {
				this.x = width * 10 / max + 11;
			}else{
				this.x = width * 10 / max;
			}
		}else if(progress >= 200){
			this.x = width * progress / max - 20;
		}else if(progress >= 100){
			this.x = width * progress / max - 16;
		}else if(progress > 60){
			this.x = width * progress / max + 2;
		}else if(progress > 10){
			this.x = width * progress / max + 5;
		}else{
			this.x = width * progress / max;
		}
		invalidate();
	}
}
