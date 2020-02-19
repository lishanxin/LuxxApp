package net.senink.seninkapp.ui.view.calendarview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
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
 * 
 * @author zhaojunfeng
 * @date 2016-01-05
 */

public class CalendarAdapter extends BaseAdapter {

	static final int FIRST_DAY_OF_WEEK = 0;
	Context context;
	Calendar cal;
	public String[] days;
	private int currentDay;
	ArrayList<Day> dayList = new ArrayList<Day>();

	public CalendarAdapter(Context context, Calendar cal) {
		this.cal = cal;
		this.context = context;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		refreshDays(cal);
	}

	public void setCalendar(int index) {
		this.currentDay = index;
	}

	@Override
	public int getCount() {
		return dayList.size();
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

	public int getCurrentIndex() {
		int id = 0;
		if (getCount() > 0) {
			int count = getCount();
			for (int i = 0; i < count; i++) {
				Day day = dayList.get(i);
				if (day.getDay() == currentDay) {
					id = i;
					break;
				}
			}
		}
		return id;
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
		Calendar cal = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		Day d = dayList.get(position);
		if (d.getYear() == cal.get(Calendar.YEAR)
				&& d.getMonth() == cal.get(Calendar.MONTH)
				&& d.getDay() == cal.get(Calendar.DAY_OF_MONTH)) {
			today.setVisibility(View.VISIBLE);
		} else {
			today.setVisibility(View.GONE);
		}
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

	public void refreshDays(Calendar calendar) {
		cal = calendar;
		// clear items
		dayList.clear();
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstDay = (int) cal.get(Calendar.DAY_OF_WEEK);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		TimeZone tz = TimeZone.getDefault();
		days = null;
		// figure size of the array
		if (firstDay == 1) {
			days = new String[lastDay + (FIRST_DAY_OF_WEEK * 6)];
		} else {
			days = new String[lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1)];
		}

		int j = FIRST_DAY_OF_WEEK;

		// populate empty days before first real day
		if (firstDay > 1) {
			for (j = 0; j < (firstDay - FIRST_DAY_OF_WEEK); j++) {
				days[j] = "";
				Day d = new Day(context, 0, 0, 0);
				dayList.add(d);
			}
		} else {
			for (j = 0; j < (FIRST_DAY_OF_WEEK * 6); j++) {
				days[j] = "";
				Day d = new Day(context, 0, 0, 0);
				dayList.add(d);
			}
			j = FIRST_DAY_OF_WEEK * 6 + 1; // sunday => 1, monday => 7
		}

		// populate days
		int dayNumber = 1;

		if (j > 0 && dayList.size() > 0 && j != 1) {
			dayList.remove(j - 1);
		}

		for (int i = j - 1; i < days.length; i++) {
			Day d = new Day(context, dayNumber, year, month);

			Calendar cTemp = Calendar.getInstance();
			cTemp.set(year, month, dayNumber);
			int startDay = Time.getJulianDay(cTemp.getTimeInMillis(),
					TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cTemp
							.getTimeInMillis())));
			d.setStartDay(startDay);
			d.setAdaper(this);
			days[i] = "" + dayNumber;
			dayNumber++;
			dayList.add(d);
		}
	}

}
