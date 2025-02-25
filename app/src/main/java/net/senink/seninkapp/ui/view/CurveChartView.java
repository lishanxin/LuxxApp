package net.senink.seninkapp.ui.view;

import java.util.LinkedList;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 曲线图
 */
public class CurveChartView extends View {
	/** 背景色 */
	private int backColor = Color.BLACK;
	/** 格线色 */
	private int gridColor = 0xFF339933;
	/** 曲线颜色 curve color */
	private int[] curveColor = null;
	/** 缺省曲线颜色 */
	private int defaCurveColor = Color.YELLOW;

	private int textColor = 0xff000000;

	private int popupColor = 0xaa33ff33;

	private int selectLineColor = 0xaaaaaaaa;
	/** 最小水平线的数目 */
	private int minHarizonLineCount = 3;

	private int maxHarizonLineCount = 10;
	/** 是否显示选择时的数据 */
	private boolean showTips = true;

	private float minValue = 0;

	private float maxValue = 0;

	private int maxCeil = 0;

	private int minCeil = 0;

	private DataQueue[] dataList = null;

	private int gridPaddingLeft = 3;

	private int gridPaddingRight = 3;

	private int gridPaddingTop = 3;

	private int gridPaddingBottom = 3;

	private int gridStart = gridPaddingLeft;

	private int pointGapDef = 6;

	private float pointGap = pointGapDef;
	/** 是否为静态曲线 curve is static */
	private boolean isStatic = true;

	private float currentMaxValue;

	private float[][] staticData = null;

	private float[] pointX = null;

	private int selectPoint = -1;

	private PointF lastPoint = null;

	private boolean sizeHaveSet = false;
	/** 是否要显示刻度 */
	private boolean showCalibration = false;
	/** 刻度是否显示在左边 */
	private boolean onLeft = false;

	private boolean setScope = false;

	private Context context = null;

	public CurveChartView(Context context) {
		super(context);
		init(context);
	}

	public CurveChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CurveChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		gridPaddingLeft = toPixel(context, gridPaddingLeft);
		gridPaddingTop = toPixel(context, gridPaddingTop);
		gridPaddingRight = toPixel(context, gridPaddingRight);
		gridPaddingBottom = toPixel(context, gridPaddingBottom);
		pointGapDef = toPixel(context, pointGapDef);
	}

	/**
	 * 设置曲线是静态的还是动态的 默认为静态的
	 * 
	 * @param b
	 */
	public void setCurveStatic(boolean b) {
		isStatic = b;
	}

	/**
	 * 设置曲线个数 如果要画多个曲线，本方法必须是所有方法之前第一个调用的方法
	 * 
	 * @param c
	 */
	public void setCurveCount(int c) {
		if (isStatic) {
			staticData = new float[c][];
		} else {
			dataList = new DataQueue[c];
		}
		curveColor = new int[c];
		for (int i = 0; i < c; i++) {
			curveColor[i] = defaCurveColor;
		}
	}

	/**
	 * 设置数据的范围 当设置了范围后，只会显示在范围之内的数据
	 * 
	 * @param minv
	 *            最小的数据
	 * @param maxv
	 *            最大的数据
	 */
	public void setDataScope(int minv, int maxv) {
		setScope = true;
		minCeil = minv;
		maxCeil = maxv;
	}

	/**
	 * 设置刻度的显示位置 true在左边，false在右边
	 * 
	 * @param b
	 */
	public void setCalibrationLeft(boolean b) {
		onLeft = b;
	}

	/**
	 * 是否要显示刻度
	 * 
	 * @param b
	 */
	public void setCalibrationOn(boolean b) {
		showCalibration = b;
		gridPaddingTop = toPixel(context, 10);
		gridPaddingBottom = toPixel(context, isStatic ? 10 : 25);
	}

	/**
	 * 设置背景色 set back color
	 * 
	 * @param color
	 *            the back color to set
	 */
	public void setBackgroundColor(int color) {
		backColor = color;
	}

	/**
	 * 设置格线颜色 set grid line color
	 * 
	 * @param color
	 */
	public void setGridColor(int color) {
		gridColor = color;
	}

	/**
	 * 设置最大的水平线条数
	 * 
	 * @param c
	 */
	public void setMaxHarizonLineCount(int c) {
		maxHarizonLineCount = c;
	}

	/**
	 * 设置曲线颜色
	 * 
	 * @param index
	 *            第几个曲线，从0开始
	 * @param Color
	 */
	public void setCurveColor(int index, int Color) {
		if (curveColor == null || curveColor.length <= index)
			return;
		curveColor[index] = Color;
	}

	/**
	 * 设置曲线颜色
	 */
	public void setCurveColor(int[] color) {
		if (curveColor == null || color == null
				|| curveColor.length != color.length)
			return;
		curveColor = color;
	}

	/**
	 * 设置曲线颜色 当只有一条曲线时可使用本方法
	 * 
	 * @param color
	 */
	public void setCurveColor(int color) {
		if ((isStatic && curveColor == null)
				|| (isStatic == false && dataList == null)) {
			setCurveCount(1);
		}
		curveColor[0] = color;
	}

	/**
	 * 设置动态曲线可显示的最大数据个数
	 * 
	 * @param size
	 */
	public void setMaxCount(int size) {
		if (isStatic || dataList == null)
			return;
		for (int i = 0; i < dataList.length; i++) {
			if (dataList[i] == null)
				dataList[i] = new DataQueue();
			dataList[i].setSize(size);
		}
		pointGap = (getWidth() - getPaddingLeft() - getPaddingRight()) * 1.0f
				/ size * 1.0f;
	}

	/**
	 * 追加一个动态数据 如果只有一个曲线，可以采用本方法。
	 * 
	 * @param data
	 */
	public void appendData(float data) {
		if (isStatic)
			return;
		if (dataList == null) {
			setCurveCount(1);
		}
		if (sizeHaveSet == false) {
			int s = (getWidth() - getPaddingLeft() - getPaddingRight())
					/ pointGapDef;
			if (s > 1) {
				for (int i = 0; i < dataList.length; i++) {
					if (dataList[i] == null)
						dataList[i] = new DataQueue();
					dataList[i].setSize(s);
				}
				sizeHaveSet = true;
				pointGap = pointGapDef;
			}
		}
		if (dataList[0] == null)
			dataList[0] = new DataQueue();
		if (maxValue < data)
			maxValue = data;
		if (minValue > data)
			minValue = data;
		dataList[0].add(new Float(data));
		if (setScope == false) {
			maxCeil = (int) Math.ceil(maxValue);
			minCeil = (int) Math.floor(minValue);
		}
		currentMaxValue = maxValue - minValue;
		invalidate();
	}

	/**
	 * 追加一个动态数据 本方法只在动态曲线时有效
	 * 
	 * @param data
	 */
	public void appendData(float[] data) {
		if (data == null || isStatic || dataList == null
				|| dataList.length != data.length) {
			return;
		}
		if (sizeHaveSet == false) {
			int s = (getWidth() - getPaddingLeft() - getPaddingRight())
					/ pointGapDef;
			if (s > 1) {
				for (int i = 0; i < dataList.length; i++) {
					if (dataList[i] == null)
						dataList[i] = new DataQueue();
					dataList[i].setSize(s);
				}
				sizeHaveSet = true;
				pointGap = pointGapDef;
			}
		}
		for (int i = 0; i < data.length; i++) {
			if (maxValue < data[i])
				maxValue = data[i];
			if (minValue > data[i])
				minValue = data[i];
			if (dataList[i] == null)
				dataList[i] = new DataQueue();
			dataList[i].add(new Float(data[i]));
		}
		if (setScope == false) {
			maxCeil = (int) Math.ceil(maxValue);
			minCeil = (int) Math.floor(minValue);
		}
		currentMaxValue = maxValue - minValue;
		invalidate();
	}

	/**
	 * 设置曲线数据 本方法只在曲线为静态时有效
	 * 设置数据时，应该先设置第一个曲线的数据。如果第一个曲线的数据没有设置，而直接设置后面的曲线数据，本方法会自动将此作为第一个曲线的数据。
	 * 
	 * @param index
	 *            第几个曲线，从0开始
	 * @param data
	 */
	public void setData(int index, float[] data) {
		if (data == null || isStatic == false)
			return;
		if (staticData == null || staticData.length <= index)
			return;
		if (staticData[0] == null)
			index = 0;
		for (int i = 0; i < data.length; i++) {
			if (maxValue < data[i]) {
				maxValue = data[i];
			}
			if (minValue > data[i]) {
				minValue = data[i];
			}
		}
		staticData[index] = data;
		if (setScope == false) {
			maxCeil = (int) Math.ceil(maxValue);
			minCeil = (int) Math.floor(minValue);
		}
		currentMaxValue = maxValue - minValue;
		pointX = null;
	}

	/**
	 * 设置曲线数据 本方法只在曲线为静态时有效 本方法在只有一条曲线时有效，如果在本方法之前调用setCurveCount设置的曲线数目大于1个时无效
	 * 本方法不需要单独调用setCurveCount来设置曲线数目，会自动设置为1
	 * 
	 * @param data
	 */
	public void setData(float[] data) {
		if (data == null || isStatic == false)
			return;
		if (staticData != null && staticData.length > 1)
			return;

		if (staticData == null)
			setCurveCount(1);

		for (int i = 0; i < data.length; i++) {
			if (maxValue < data[i]) {
				maxValue = data[i];
			}
			if (minValue > data[i]) {
				minValue = data[i];
			}
		}
		staticData[0] = data;
		if (setScope == false) {
			maxCeil = (int) Math.ceil(maxValue);
			minCeil = (int) Math.floor(minValue);
		}
		currentMaxValue = maxValue - minValue;
		pointX = null;
	}

	protected void onDraw(Canvas canvas) {

		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();

		int height = getHeight() - paddingTop - paddingBottom;
		int width = getWidth() - paddingLeft - paddingRight;

		canvas.save();
		canvas.translate(paddingLeft, paddingTop);
		canvas.clipRect(0, 0, width, height);

		canvas.drawColor(backColor);

		Paint p = new Paint();
		float textw = 0;
		if (showCalibration) {
			Rect rect = new Rect();
			String txt = String.valueOf(maxCeil) + "88";
			p.getTextBounds(txt, 0, txt.length(), rect);
			textw = rect.width();
			txt = String.valueOf(minCeil) + "88";
			p.getTextBounds(txt, 0, txt.length(), rect);
			if (textw < rect.width())
				textw = rect.width();
			textw += 5;
		}

		float vsp = 1;

		int ceilValue = Math.abs(maxCeil - minCeil);
		if (ceilValue == 0)
			ceilValue = minHarizonLineCount;
		if (ceilValue > maxHarizonLineCount) {
			vsp = (ceilValue * 1.0f) / maxHarizonLineCount;
			ceilValue = maxHarizonLineCount;
		}

		int vheight = height - gridPaddingTop - gridPaddingBottom;
		int vwidth = (int) (width - gridPaddingLeft - gridPaddingRight - textw);
		if (isStatic) {
			if (staticData != null && staticData[0].length > 1)
				pointGap = (vwidth * 1.0f) / (staticData[0].length - 1);
			else
				pointGap = pointGapDef;
		} else {
			// pointGap = pointGapDef;
		}

		/** 水平格线距离 */
		float verticalSpace = (vheight * 1.0f) / (ceilValue * 1.0f);
		gridStart = gridPaddingLeft;
		// 格线
		Paint paint = new Paint();
		paint.setColor(textColor);
		paint.setTextSize(20);
		FontMetrics fm = paint.getFontMetrics();
		if (onLeft) {
			paint.setTextAlign(Paint.Align.RIGHT);
			gridStart += textw;
		}

		for (int i = 0; i <= ceilValue; i++) {
			p.setColor(gridColor);
			canvas.drawLine(gridStart, gridPaddingTop + i * verticalSpace,
					gridStart + vwidth, gridPaddingTop + i * verticalSpace, p);
			if (showCalibration) {
				if (i > 0 && i < ceilValue) {
					p.setColor(textColor);
					String cs = String.format("%.1f", maxCeil - i * vsp);
					if (cs.endsWith("0"))
						cs = cs.substring(0, cs.length() - 2);
					if (onLeft) {
						canvas.drawText(cs, gridStart,
								gridPaddingTop + i * verticalSpace - fm.ascent
										- toPixel(context, 5), paint);
					} else {
						canvas.drawText(cs, gridPaddingLeft + vwidth + 1,
								gridPaddingTop + i * verticalSpace - fm.ascent
										- toPixel(context, 5), paint);
					}
				}
			}
		}
		p.setColor(gridColor);
		canvas.drawLine(gridStart, gridPaddingTop, gridStart, gridPaddingTop
				+ vheight, p);
		canvas.drawLine(gridStart + vwidth, gridPaddingTop, gridStart + vwidth,
				gridPaddingTop + vheight, p);
		if (showCalibration) {
			if (onLeft) {
				canvas.drawText(String.valueOf(maxCeil), gridStart,
						gridPaddingTop - fm.ascent - toPixel(context, 5), paint);
				canvas.drawText(String.valueOf(minCeil), gridStart,
						gridPaddingTop + vheight + toPixel(context, 5), paint);
			} else {
				canvas.drawText(String.valueOf(maxCeil), gridPaddingLeft
						+ vwidth + 1,
						gridPaddingTop - fm.ascent - toPixel(context, 5), paint);
				canvas.drawText(String.valueOf(minCeil), gridPaddingLeft
						+ vwidth + 1,
						gridPaddingTop + vheight + toPixel(context, 5), paint);
			}
		}
		canvas.restore();
		if (isStatic)
			drawStaticCurve(canvas, vwidth, vheight, verticalSpace, vsp);
		else {
			drawDynamicCurve(canvas, vwidth, vheight, verticalSpace, vsp);
		}

	}

	private void drawDynamicCurve(Canvas canvas, int width, int height,
			float hsp, float hv) {

		if (dataList != null && dataList[0] != null) {
			canvas.save();
			canvas.translate(getPaddingLeft() + gridStart, getPaddingTop()
					+ gridPaddingTop);
			canvas.clipRect(0, 0, width, height + 1);
			Paint p = new Paint();
			p.setAntiAlias(true);
			p.setStrokeWidth(3);
			int s = dataList[0].getListSize();
			float mx = minCeil;
			float mi = maxCeil;
			for (int i = 0; i < dataList.length; i++) {
				if (dataList[i] == null)
					continue;
				p.setColor(curveColor[i]);
				boolean flag = false;
				float px = 0;
				float py = 0;
				float tmp = width - s * pointGap;
				if (tmp > 0) {
					for (int j = 0; j < s; j++) {
						float v = dataList[i].getData(j);
						if (mx < v)
							mx = v;
						if (mi > v)
							mi = v;
						float x = width - j * pointGap;
						float y = height - ((minCeil - v) * (-1.0f) * hsp) / hv;

						if (flag) {
							if (v >= 0)
								canvas.drawLine(px, py, x, y, p);
						} else {
							flag = true;
						}
						px = x;
						py = y;
					}
				} else {
					int k = 0;
					for (int j = s - 1; j >= 0; j--) {
						float v = dataList[i].getData(j);
						if (mx < v)
							mx = v;
						if (mi > v)
							mi = v;
						float x = k * pointGap;
						k++;
						if (x > width)
							break;
						float y = height - ((minCeil - v) * (-1.0f) * hsp) / hv;

						if (flag) {
							if (v >= 0)
								canvas.drawLine(px, py, x, y, p);
						} else {
							flag = true;
						}
						px = x;
						py = y;
					}
				}
			}
			if (maxValue > mx)
				maxValue = mx;
			if (minValue < mi)
				minValue = mi;
			canvas.restore();
			FontMetrics fm = p.getFontMetrics();
			float tw = 0;
			float ty = gridPaddingTop + height + toPixel(context, 15)
					- fm.ascent;
			float tx = gridPaddingLeft + toPixel(context, 30);
			p.setTextSize(25);
			for (int i = 0; i < dataList.length; i++) {
				if (dataList[i] == null)
					continue;
				float temp = dataList[i].getData(s - 1);
				if (temp >= 0) {
					String result = String.format("%.2f", temp);
					// String tmp =
					// getResources().getString(R.string.cur_gong_lv)
					// + result;
					if (lvChangeListener != null) {
						lvChangeListener.gongLvChangeListener(result);
					}
				}
				// String result = String.format("%.2f", temp);
				// // String tmp =
				// getResources().getString(R.string.cur_gong_lv)
				// // + result;
				// if (lvChangeListener != null) {
				// lvChangeListener.gongLvChangeListener(result);
				// }
				// Rect r = new Rect();
				// p.setColor(curveColor[i]);
				// p.getTextBounds(tmp, 0, tmp.length(), r);
				// tw += r.width();
				// canvas.drawText(tmp, tx, ty, p);
				// tx += tw + toPixel(context, 10);
			}
		}
	}

	public interface OnGongLvChangeListener {
		public void gongLvChangeListener(String gongLvValue);
	}

	private OnGongLvChangeListener lvChangeListener;

	public void setLvChangeListener(OnGongLvChangeListener lvChangeListener) {
		this.lvChangeListener = lvChangeListener;
	}

	private void drawStaticCurve(Canvas canvas, int width, int height,
			float hsp, float hv) {
		Paint p = new Paint();
		canvas.save();
		canvas.translate(getPaddingLeft() + gridStart, getPaddingTop()
				+ gridPaddingTop);
		canvas.clipRect(0, 0, width, height + 1);
		if (staticData != null && staticData[0] != null) {
			boolean po = false;
			int s = staticData[0].length;
			if (pointX == null) {
				pointX = new float[s];
				po = true;
			}

			for (int k = 0; k < staticData.length; k++) {
				p.setColor(curveColor[k]);
				boolean flag = false;

				float px = 0;
				float py = 0;
				if (staticData[k] == null)
					continue;
				for (int i = 0; i < s; i++) {
					float v = staticData[k][i];
					float x = (i * pointGap);
					float y = height - ((minCeil - v) * (-1.0f) * hsp) / hv;
					if (po)
						pointX[i] = x;

					if (flag) {
						canvas.drawLine(px, py, x, y, p);
					} else {
						flag = true;
					}
					px = x;
					py = y;
				}
			}
		}

		if (showTips && selectPoint != -1) {
			p.setColor(selectLineColor);
			canvas.drawLine(pointX[selectPoint], 0, pointX[selectPoint],
					height, p);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(5);
			paint.setColor(popupColor);
			FontMetrics fm = paint.getFontMetrics();
			int bd = 2;
			float fms = fm.descent - fm.ascent;
			float x = width / 2 - 20;
			float y = gridPaddingTop + 10;
			Rect bounds = new Rect();
			for (int i = 0; i < staticData.length; i++) {
				if (staticData[i] == null)
					continue;
				String txt = String.valueOf(staticData[i][selectPoint]);
				paint.getTextBounds(txt, 0, txt.length(), bounds);
				RectF rf = new RectF(x - bd, y - bd, x + bounds.width() + bd, y
						+ fms + bd);
				// canvas.drawRoundRect(rf, 3, 3, paint);
				p.setColor(curveColor[i]);
				canvas.drawText(txt, x, y - fm.ascent, p);
				y += fms + bd + 3;
			}
			selectPoint = -1;
		}
		canvas.restore();
	}

	private int getPointData(float x, float y) {
		if (pointX != null && pointX.length > 2) {
			float sp = pointX[1] - pointX[0];
			if (lastPoint != null) {
				if (lastPoint.x > x) {
					for (int i = (int) lastPoint.y; i >= 0; i--) {
						if (Math.abs(x - pointX[i]) <= sp) {
							lastPoint.x = x;
							lastPoint.y = i;
							return i;
						}
					}
				} else if (lastPoint.x < x) {
					for (int i = (int) lastPoint.y; i < pointX.length; i++) {
						if (Math.abs(x - pointX[i]) <= sp) {
							lastPoint.x = x;
							lastPoint.y = i;
							return i;
						}
					}
				} else {
					return (int) lastPoint.y;
				}
			} else {
				for (int i = 0; i < pointX.length; i++) {
					if (Math.abs(x - pointX[i]) <= sp) {
						lastPoint = new PointF();
						lastPoint.x = x;
						lastPoint.y = i;
						return i;
					}
				}
			}
			return -1;
		} else {
			return -1;
		}
	}

	private boolean isDown = false;

	public boolean onTouchEvent(MotionEvent event) {
		if (showTips == false || isStatic == false)
			return false;
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (isDown) {
				handler.removeMessages(100);
				return true;
			} else {
				isDown = true;
			}
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			float y = event.getY();
			selectPoint = getPointData(x, y);
			invalidate();
			isDown = false;
			return true;
		case MotionEvent.ACTION_UP:
			handler.sendEmptyMessageDelayed(100, 5000);
			isDown = false;
			break;
		case MotionEvent.ACTION_OUTSIDE:
			isDown = false;
			break;
		}
		return false;
	}

	private void reDraw() {
		invalidate();
	}

	private int toPixel(Context context, int dip) {
		Resources res = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dip,
				res.getDisplayMetrics());
		return (int) px;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			reDraw();
		}
	};

	private class DataQueue {
		private LinkedList<Float> list = new LinkedList<Float>();
		private int size = 1;

		public DataQueue() {
		}

		public DataQueue(int s) {
			size = s;
		}

		public void setSize(int s) {
			if (s <= 1)
				return;
			if (size > s && list.size() > s) {
				for (int i = 0; i < list.size() - s; i++) {
					list.poll();
				}
			}
			size = s;
		}

		public void add(Float f) {
			if (list.size() >= size) {
				list.poll();
			}
			list.add(f);
		}

		public Float getData(int index) {
			return list.get(index);
		}

		public int getListSize() {
			return list.size();
		}

		public void removeAll() {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					list.remove();
				}
			}
		}
	}
}
