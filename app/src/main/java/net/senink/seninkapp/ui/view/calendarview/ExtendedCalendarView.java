package net.senink.seninkapp.ui.view.calendarview;

import java.util.Calendar;
import net.senink.seninkapp.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * 日历自定义组件
 * 
 * @author zhaojunfeng
 * @date 2016-01-05
 */
public class ExtendedCalendarView extends RelativeLayout implements
		OnItemClickListener, OnClickListener {

	private Context context;
	private OnDayClickListener dayListener;
	private GridView calendar;
	private CalendarAdapter mAdapter;
	private Calendar cal;
	private TextView tvMonth, tvYear;
	private View base;
	private ImageView nextBtn, prevBtn;
	private int gestureType = 0;
	private final GestureDetector calendarGesture = new GestureDetector(
			context, new GestureListener());

	public static final int NO_GESTURE = 0;
	public static final int LEFT_RIGHT_GESTURE = 1;
	public static final int UP_DOWN_GESTURE = 2;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private boolean isOneYear = false;

	public ExtendedCalendarView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ExtendedCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public ExtendedCalendarView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	@SuppressLint("InflateParams")
	private void init() {
		cal = Calendar.getInstance();
		base = LayoutInflater.from(context).inflate(R.layout.day_of_week, null);
		prevBtn = (ImageView) base.findViewById(R.id.week_month_prev);
		nextBtn = (ImageView) base.findViewById(R.id.week_month_next);
		tvYear = (TextView) base.findViewById(R.id.week_title);
		tvMonth = (TextView) base.findViewById(R.id.week_title1);
		tvMonth.setText(getMonth());
		tvYear.setText(getYear());
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		setEnabledOnNextButton();
		setEnabledOnPreButton();
		addView(base);
		addGridView();
	}

	private void addGridView() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.BELOW, base.getId());
		calendar = new GridView(context);
		calendar.setLayoutParams(params);
		calendar.setVerticalSpacing(4);
		calendar.setHorizontalSpacing(4);
		calendar.setSelector(new ColorDrawable(Color.TRANSPARENT));
		calendar.setNumColumns(7);
		calendar.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		calendar.setDrawSelectorOnTop(true);
		mAdapter = new CalendarAdapter(context, cal);
		calendar.setAdapter(mAdapter);
		calendar.setBackgroundColor(0xfff5f5f5);
		calendar.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return calendarGesture.onTouchEvent(event);
			}
		});

		addView(calendar);
	}

	private String getMonth() {
		String month = "";
		if (cal.get(Calendar.MONTH) + 1 < 10) {
			month = "0";
		}
		month = month + (cal.get(Calendar.MONTH) + 1)
				+ context.getString(R.string.month);
		return month;
	}

	private String getYear() {
		String year = "";
		if (!isOneYear) {
			year = cal.get(Calendar.YEAR) + context.getString(R.string.year)
					+ "-";
		}
		return year;
	}

	/**
	 * 是否是一年
	 * 
	 * @param isOne
	 */
	public void setYears(boolean isOne) {
		this.isOneYear = isOne;
		if (isOne) {
			cal = Calendar.getInstance();
		}
		rebuildCalendar();
	}

	private class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			if (gestureType == LEFT_RIGHT_GESTURE) {
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					nextMonth();
					return true; // Right to left
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					previousMonth();
					return true; // Left to right
				}
			} else if (gestureType == UP_DOWN_GESTURE) {
				if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					nextMonth();
					return true; // Bottom to top
				} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					previousMonth();
					return true; // Top to bottom
				}
			}
			return false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (dayListener != null) {
			Day d = (Day) mAdapter.getItem(arg2);
			if (d.getDay() != 0) {
				dayListener.onDayClicked(arg0, arg1, arg2, arg3, d);
			}
		}
	}

	/**
	 * 
	 * @param listener
	 * 
	 *            Set a listener for when you press on a day in the month
	 */
	public void setOnDayClickListener(OnDayClickListener listener) {
		if (calendar != null) {
			dayListener = listener;
			calendar.setOnItemClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.equals(nextBtn)) {
			clickNextButton();
		} else if (v.equals(prevBtn)) {
			clickPreButton();
		}
	}

	private void clickPreButton() {
		prevBtn.requestFocus();
		previousMonth();
	}

	private void clickNextButton() {
		nextBtn.requestFocus();
		nextMonth();
	}

	public void setCurrentItem(int year, int month, int day) {
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		mAdapter.setCalendar(day);
		rebuildCalendar();
	}

	public void resetView() {
		int index = mAdapter.getCurrentIndex();
		View view = calendar.getChildAt(index);
		if (view != null) {
			calendar.performItemClick(view, index, 0);
		}
		calendar.setActivated(false);
		calendar.refreshDrawableState();
		mAdapter.notifyDataSetChanged();
	}

	public void clearFocusOnChild() {
		// View view = calendar.getFocusedChild();
		// if (view != null) {
		// view.clearFocus();
		// view .clearAnimation();
		// }
		// calendar.clearFocus();
		// mAdapter.notifyDataSetChanged();
	}

	private void previousMonth() {
		if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
			cal.set((cal.get(Calendar.YEAR) - 1),
					cal.getActualMaximum(Calendar.MONTH), 1);
		} else {
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		}
		rebuildCalendar();
	}

	private void nextMonth() {
		if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
			cal.set((cal.get(Calendar.YEAR) + 1),
					cal.getActualMinimum(Calendar.MONTH), 1);
		} else {
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		}
		rebuildCalendar();
	}

	private void rebuildCalendar() {
		if (tvMonth != null) {
			tvMonth.setText(getMonth());
			tvYear.setText(getYear());
			setEnabledOnPreButton();
			setEnabledOnNextButton();
			refreshCalendar();
		}
	}

	private void setEnabledOnNextButton() {
		if (isOneYear && cal.get(Calendar.MONTH) == 11) {
			nextBtn.setEnabled(false);
			nextBtn.setBackgroundResource(R.drawable.icon_calendar_right_unenabled);
		} else {
			nextBtn.setEnabled(true);
			nextBtn.setBackgroundResource(R.drawable.btn_arrow_right_selector);
		}
	}

	private void setEnabledOnPreButton() {
		Calendar mCalendar = Calendar.getInstance();
		if (isOneYear && cal.get(Calendar.MONTH) == 0) {
			prevBtn.setEnabled(false);
			prevBtn.setBackgroundResource(R.drawable.icon_calendar_left_unenabled);
		} else if (!isOneYear && mCalendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
				&& mCalendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
			prevBtn.setEnabled(false);
			prevBtn.setBackgroundResource(R.drawable.icon_calendar_left_unenabled);
		} else {
			prevBtn.setEnabled(true);
			prevBtn.setBackgroundResource(R.drawable.btn_arrow_left_selector);
		}
	}

	/**
	 * Refreshes the month
	 */
	public void refreshCalendar() {
		mAdapter.refreshDays(cal);
		mAdapter.notifyDataSetChanged();
		if (calendar.getCount() > mAdapter.getCount()) {
			int start = mAdapter.getCount();
			int end = calendar.getCount();
			for (int i = start; i < end; i++) {
				View view = calendar.getChildAt(i);
				view.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 
	 * @param color
	 * 
	 *            Sets the background color of the month bar
	 */
	public void setMonthTextBackgroundColor(int color) {
		base.setBackgroundColor(color);
	}

	@SuppressLint("NewApi")
	/**
	 * 
	 * @param drawable
	 * 
	 * Sets the background color of the month bar. Requires at least API level 16
	 */
	public void setMonthTextBackgroundDrawable(Drawable drawable) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			base.setBackground(drawable);
		}

	}

	/**
	 * 
	 * @param resource
	 * 
	 *            Sets the background color of the month bar
	 */
	public void setMonehtTextBackgroundResource(int resource) {
		base.setBackgroundResource(resource);
	}

	/**
	 * 
	 * @param recource
	 * 
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageResource(int recource) {
		prevBtn.setImageResource(recource);
	}

	/**
	 * 
	 * @param bitmap
	 * 
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageBitmap(Bitmap bitmap) {
		prevBtn.setImageBitmap(bitmap);
	}

	/**
	 * 
	 * @param drawable
	 * 
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageDrawable(Drawable drawable) {
		prevBtn.setImageDrawable(drawable);
	}

	/**
	 * 
	 * @param recource
	 * 
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageResource(int recource) {
		nextBtn.setImageResource(recource);
	}

	/**
	 * 
	 * @param bitmap
	 * 
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageBitmap(Bitmap bitmap) {
		nextBtn.setImageBitmap(bitmap);
	}

	/**
	 * 
	 * @param drawable
	 * 
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageDrawable(Drawable drawable) {
		nextBtn.setImageDrawable(drawable);
	}

	/**
	 * 
	 * @param gestureType
	 * 
	 *            Allow swiping the calendar left/right or up/down to change the
	 *            month.
	 * 
	 *            Default value no gesture
	 */
	public void setGesture(int gestureType) {
		this.gestureType = gestureType;
	}

	public void setVisiabilityOnTitle(boolean visiable) {
		if (visiable) {
			base.setVisibility(View.VISIBLE);
		} else {
			base.setVisibility(View.GONE);
		}
	}
}
