package net.senink.seninkapp.ui.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * 曲线分布图
 * 
 * @author zhaojunfeng
 * @date 2016-02-03
 * 
 */
public class CurveProfileView extends View {
	// 最大步数
	private final static int MAX = 30000;
	// 自定义图形的宽度
	private float width;
	// 自定义图形的高度
	private float height;
	// 圆环画笔
	private Paint circlePaint;
	// 实心圆画笔
	private Paint mCirclePaint;
	// 直线的画笔
	private Paint linePaint;
	// 区域画笔
	private Paint tranglePaint;
	// 曲线画笔
	private Paint curvelinePaint;
	// 日期画笔
	private Paint timePaint;
	// 年：3  月：2  日： 1   天: 0 指示标记
	private int index;
	// 最大步数
	private short maxStep;
	// 顶部间隔
	private final static int MARGINTOP = 30;
	// 左右间隔
	private final static int MARGIN = 100;
	// y等分间隔
	private int y_distance = 0;
	// y轴对应的刻度与步数之间的比例
	private double y_scale = 0.0f;
	// 步数
	private ArrayList<Short> steps;
	// 水平方向被均分多少等份
	private int count;
	// 圆环半径
	private final static int RADIUS = 10;
	//日期转换
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
	private List<Point> mPoints;

	public CurveProfileView(Context context) {
		super(context);
		init(context, null);
	}

	public CurveProfileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CurveProfileView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		setCirclePaint();
		setLinePaint();
		setTranglePaint();
		setCurveLinePaint();
		setTimePaint();
		steps = new ArrayList<Short>();
		mPoints = new ArrayList<Point>();
	}

//	private void test() {
//		ArrayList<Short> data = new ArrayList<Short>();
//		data.add((short) 25);
//		data.add((short) 75);
//		data.add((short) 100);
//		setSteps(data, 0);
//	}

	/**
	 * 设置时间的画笔
	 */
	private void setTimePaint() {
		timePaint = new Paint();
		timePaint.setColor(0xff333333);
		timePaint.setTextSize(25);
		timePaint.setAntiAlias(true);
	}

	/**
	 * 设置曲线的画笔
	 */
	private void setCurveLinePaint() {
		curvelinePaint = new Paint();
		curvelinePaint.setColor(0xffde4e4f);
		curvelinePaint.setStrokeWidth(4);
		curvelinePaint.setAntiAlias(true);
	}

	/**
	 * 设置多边形的画笔
	 */
	private void setTranglePaint() {
		tranglePaint = new Paint();
		tranglePaint.setColor(0xffde4e4f);
		tranglePaint.setStyle(Paint.Style.FILL);
		tranglePaint.setAlpha(51);
		tranglePaint.setAntiAlias(true);
	}

	/**
	 * 设置分割线的画笔
	 */
	private void setLinePaint() {
		linePaint = new Paint();
		linePaint.setColor(0xffffc2bf);
		linePaint.setAlpha(102);
		linePaint.setTextSize(28);
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(3);
	}

	/**
	 * 设置圆环形的画笔
	 */
	private void setCirclePaint() {
		circlePaint = new Paint();
		circlePaint.setStrokeWidth(3.0f);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setColor(0xffcd3739);
		circlePaint.setAntiAlias(true);

		mCirclePaint = new Paint();
		mCirclePaint.setStyle(Paint.Style.FILL);
		mCirclePaint.setColor(Color.rgb(255, 98, 106));
		mCirclePaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setHeightAndWidth();
		drawLineText(canvas);
		setPoints();
		drawCurveLine(canvas);
		drawTrangle(canvas);
		drawCircle(canvas);
		drawTime(canvas);
	}

	/**
	 * 把步数列表转换为点的列表
	 */
	private void setPoints() {
		mPoints.clear();
		if (count > 0) {
			float distance = 0;
			if (index == 0) {
				distance = (width - 2 * MARGIN) / 23;
			}
			for (int i = 0; i < count; i++) {
				Point mPoint = new Point((int) (MARGIN + i * distance),
						(int) (MARGINTOP + 2 * y_distance - steps.get(i)
								* y_scale));
				mPoints.add(mPoint);
			}
		}
	}

	/**
	 * 画时间字符串
	 * 
	 * @param canvas
	 */
	private void drawTime(Canvas canvas) {
		float textWidth = 0;
		float distance = 0;
		if (index == 0) {
			canvas.drawText("0点", MARGIN, height - y_distance / 2, timePaint);
			textWidth = timePaint.measureText("12点");
			canvas.drawText("12点", width / 2 - textWidth / 2, height
					- y_distance / 2, timePaint);
			textWidth = timePaint.measureText("24点");
			canvas.drawText("24点", width - MARGIN - textWidth, height
					- y_distance / 2, timePaint);
		} else if (index == 1) {
			distance = (width - MARGIN) / 6;
            setWeekText(canvas,distance);
		} else if (index == 2) {
			distance = (width - MARGIN) / 29;
			 setMonthText(canvas,distance);
		}else if (index == 3) {
			distance = (width - MARGIN) / 11;
			for (int i = 0; i < 12; i++) {
				textWidth = timePaint.measureText("" + (i + 1));
				canvas.drawText("" + (i + 1), MARGIN + i * distance - 
						textWidth / 2, height - y_distance / 2, timePaint);
			}
		}
	}

	/**
	 * 设置月的时间
	 * @param canvas
	 * @param distance
	 */
	private void setMonthText(Canvas canvas, float distance) {
		long time = System.currentTimeMillis();
		for (int i = 29; i < 30; i--) {
			time = time - 3600*24*(i - 29);
			float textWidth = timePaint.measureText(sdf.format(time));
			if (i % 6 == 5 || i % 6 == 0){
			  canvas.drawText(sdf.format(time), MARGIN + i * distance - 
					textWidth / 2, height - y_distance / 2, timePaint);
			}
		}
	}

	/**
	 * 设置按周文字
	 */
	private void setWeekText(Canvas canvas,float distance) {
		long time = System.currentTimeMillis();
		for (int i = 6; i < 7; i--) {
			time = time - 3600*24*(i - 6);
			float textWidth = timePaint.measureText(sdf.format(time));
			if (i == 6) {
				canvas.drawText("今日", MARGIN + i * distance - 
						textWidth / 2, height - y_distance / 2, timePaint);
			}else{
			canvas.drawText(sdf.format(time), MARGIN + i * distance - 
					textWidth / 2, height - y_distance / 2, timePaint);
			}
		}
	}

	/**
	 * 画圈
	 * 
	 * @param canvas
	 */
	private void drawCircle(Canvas canvas) {
		if (mPoints.size() > 0) {
			int size = mPoints.size();
			for (int i = 0; i < size; i++) {
				Point mPoint = mPoints.get(i);
				canvas.drawCircle(mPoint.x, mPoint.y - RADIUS, RADIUS,
						mCirclePaint);
				canvas.drawCircle(mPoint.x, mPoint.y - RADIUS, RADIUS,
						circlePaint);

			}
		}
	}

	/**
	 * 画曲线
	 * 
	 * @param canvas
	 */
	private void drawCurveLine(Canvas canvas) {
		if (mPoints.size() > 1) {
			int size = mPoints.size();
			for (int i = 1; i < size; i++) {
				Point lastPoint = mPoints.get(i - 1);
				Point mPoint = mPoints.get(i);
				canvas.drawLine(lastPoint.x, lastPoint.y, mPoint.x, mPoint.y,
						curvelinePaint);
			}
		}
	}

	/**
	 * 画多边形
	 * 
	 * @param canvas
	 */
	private void drawTrangle(Canvas canvas) {
		if (mPoints.size() > 1) {
			int size = mPoints.size();
			Path mPath = new Path();
			for (int i = 0; i < size; i++) {
				Point mPoint = mPoints.get(i);
				if (i == 0) {
					mPath.moveTo(mPoint.x, height);
				}
				mPath.lineTo(mPoint.x, mPoint.y);
				if (i == size - 1) {
					mPath.lineTo(mPoint.x, height);
				}
			}
			mPath.close();
			canvas.drawPath(mPath, tranglePaint);
		}
	}

	/**
	 * 画水平线和水平线右边的文字
	 */
	private void drawLineText(Canvas canvas) {
		for (int i = 0; i < 3; i++) {
			canvas.drawLine(MARGIN, MARGINTOP + i * y_distance, width - MARGIN,
					MARGINTOP + i * y_distance, linePaint);
			if (i == 0) {
				canvas.drawText(maxStep + "", width - MARGIN + 5, MARGINTOP + i
						* y_distance + 7, linePaint);
			} else if (i == 1) {
				canvas.drawText(maxStep / 2 + "", width - MARGIN + 5, MARGINTOP
						+ i * y_distance + 7, linePaint);
			} else if (i == 2) {
				canvas.drawText("0", width - MARGIN + 5, MARGINTOP + i
						* y_distance + 7, linePaint);
			}
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
		y_distance = (int) (height - MARGINTOP) / 3;
	}

	/**
	 * 设置步数
	 * 
	 * @param steps
	 */
	public void setSteps(ArrayList<Short> steps) {
		if (null == steps) {
			steps = new ArrayList<Short>();
		} else {
			int size = steps.size();
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					maxStep = steps.get(i);
				} else {
					if (maxStep < steps.get(i)) {
						maxStep = steps.get(i);
					}
				}
			}
		}
		if (maxStep == 0) {
			maxStep = MAX;
		}
		if (this.steps != null) {
			this.steps.clear();
		}
		this.steps = steps;
		count = this.steps.size();
		setScale();
		invalidate();
	}

	/**
	 * 设置x轴和y轴的尺寸比例
	 */
	private void setScale() {
		if (index == 0) {
			y_scale = (2 * y_distance) / maxStep;
		} else if (index == 1) {
			y_scale = height / maxStep;
		}

	}

	/**
	 * 设置步数
	 * 
	 * @param steps
	 *            步数
	 * @param index
	 *            指示标记 年：3   月：2  周：1  日：0
	 */
	public void setSteps(ArrayList<Short> steps, int index) {
		this.index = index;
		setSteps(steps);
	}

	/**
	 * 释放资源
	 */
	public void clear() {
		if (steps != null) {
			steps.clear();
			steps = null;
		}
		if (mPoints != null) {
			mPoints.clear();
			mPoints = null;
		}
		linePaint = null;
		circlePaint = null;
		curvelinePaint = null;
		timePaint = null;
		tranglePaint = null;
	}
}
