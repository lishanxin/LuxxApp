package net.senink.seninkapp.ui.view.calendarview;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;


/**
 * 日历自定义组件
 * @author zhaojunfeng
 * @date 2016-01-05
 */
public class MonthView extends RelativeLayout implements
		OnItemClickListener{

	private Context context;
	private OnDayClickListener dayListener;
	private GridView calendar;
	private MonthAdapter mAdapter;
	private Calendar cal;
    private int currentPos;
	public MonthView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public MonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public MonthView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	@SuppressLint("InflateParams")
	private void init() {
		cal = Calendar.getInstance();
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.bottomMargin = 20;
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		calendar = new GridView(context);
		calendar.setLayoutParams(params);
		calendar.setVerticalSpacing(4);
		calendar.setHorizontalSpacing(4);
		calendar.setSelector(new ColorDrawable(Color.TRANSPARENT));
		calendar.setNumColumns(7);
		calendar.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		calendar.setDrawSelectorOnTop(true);

		mAdapter = new MonthAdapter(context, cal);
		calendar.setAdapter(mAdapter);
		addView(calendar);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (dayListener != null) {
			currentPos = arg2;
			Day d = (Day) mAdapter.getItem(arg2);
			if (d.getDay() != 0) {
				dayListener.onDayClicked(arg0, arg1, arg2, arg3, d);
			}
		}
	}
	
	/**
	 * 设置当前的时间
	 * @param index
	 */
	public void setCurrentItem(int index){
		View view = calendar.getChildAt(index);
		if (view != null) {
			currentPos = index;
			calendar.performItemClick(view, index, 0);
		}
	}
	
	public void clearFocusOnChild(){
//		View view = calendar.getFocusedChild();
//		if (view != null) {
//			view.clearFocus();
//		}
//		view = calendar.getChildAt(calendar.getCheckedItemPosition());
//		if (view != null) {
//			view.clearFocus();
//		}
//		calendar.clearFocus();
//		calendar.setActivated(false);
//		calendar.refreshDrawableState();
//		mAdapter.notifyDataSetChanged();
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

	/**
	 * Refreshes the month
	 */
	public void refreshCalendar() {
		mAdapter.refreshDays();
		mAdapter.notifyDataSetChanged();
	}
}
