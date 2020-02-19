package net.senink.seninkapp.ui.view.calendarview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.senink.seninkapp.R;

/**
 * 日历自定义组件的适配器
 * @author zhaojunfeng
 * @date 2016-01-05
 */

public class MonthAdapter extends BaseAdapter {

	static final int FIRST_DAY_OF_WEEK = 0;
	Context context;
	Calendar cal;
	public String[] days;
	// OnAddNewEventClick mAddEvent;

	ArrayList<Day> dayList = new ArrayList<Day>();

	public MonthAdapter(Context context, Calendar cal) {
		this.cal = cal;
		this.context = context;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		refreshDays();
	}

	@Override
	public int getCount() {
		return days.length;
	}

	@Override
	public Object getItem(int position) {
		return dayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public int getPrevMonth() {
		if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR - 1));
		} else {

		}
		int month = cal.get(Calendar.MONTH);
		if (month == 0) {
			return month = 11;
		}

		return month - 1;
	}

	public int getMonth() {
		return cal.get(Calendar.MONTH);
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.day_view, null);
		FrameLayout today = (FrameLayout) v.findViewById(R.id.today_frame);
		today.setVisibility(View.GONE);
		TextView dayTV = (TextView) v.findViewById(R.id.textView1);
		TextView dayTV1 = (TextView) v.findViewById(R.id.textView2);
		RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl);
		ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
		iv.setVisibility(View.VISIBLE);
		dayTV.setVisibility(View.VISIBLE);
		rl.setVisibility(View.VISIBLE);
		Day day = dayList.get(position);
		iv.setVisibility(View.INVISIBLE);
		if (day.getDay() == 0) {
			rl.setVisibility(View.GONE);
		} else {
			dayTV.setVisibility(View.VISIBLE);
			dayTV.setText(String.valueOf(day.getDay()));
			if (position % 7 == 0 || position % 7 == 6) {
				dayTV1.setVisibility(View.VISIBLE);
				dayTV.setVisibility(View.GONE);
				dayTV1.setText(String.valueOf(day.getDay()));
			} else {
				dayTV1.setVisibility(View.GONE);
				dayTV.setVisibility(View.VISIBLE);
				dayTV.setText(String.valueOf(day.getDay()));
			}
		}
		return v;
	}

	public void refreshDays() {
		dayList.clear();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		TimeZone tz = TimeZone.getDefault();
		days = new String[getCurrentMonthDay()];
		int dayNumber = 1;
		for (int i = 0; i < days.length; i++) {
			Day d = new Day(context, dayNumber, year, month);

			Calendar cTemp = Calendar.getInstance();
			cTemp.set(year, month, dayNumber);
			int startDay = Time.getJulianDay(cTemp.getTimeInMillis(),
					TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cTemp
							.getTimeInMillis())));
			d.setStartDay(startDay);
			days[i] = "" + dayNumber;
			dayNumber++;
			dayList.add(d);
		}
	}

	/**
	 * 获取当月的天数
	 * @return
	 */
	 public static int getCurrentMonthDay() {  
	        Calendar a = Calendar.getInstance();  
	        a.set(Calendar.DATE, 1);  
	        a.roll(Calendar.DATE, -1);  
	        int maxDate = a.get(Calendar.DATE);  
	        return maxDate;  
	} 
}
