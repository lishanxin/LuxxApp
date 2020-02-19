package net.senink.seninkapp.ui.view.calendarview;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.text.format.Time;
import android.widget.BaseAdapter;

/**
 * 日历自定义组件的基类
 * @author zhaojunfeng
 * @date 2016-01-05
 */

public class Day{
	
	int startDay;
	int monthEndDay;
	int day;
	int year;
	int month;
	Context context;
	BaseAdapter adapter;
	int week;
	Day(Context context,int day, int year, int month){
		this.day = day;
		this.year = year;
		this.month = month;
		this.context = context;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day);
		int end = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(year, month, end);
		week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		TimeZone tz = TimeZone.getDefault();
		monthEndDay = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	}
	
//	public long getStartTime(){
//		return startTime;
//	}
//	
//	public long getEndTime(){
//		return endTime;
//	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public void setDay(int day){
		this.day = day;
	}
	
	public int getDay(){
		return day;
	}
	
	public int getWeek(){
		return week;
	}
	/**
	 * Set the start day
	 * 
	 * @param startDay
	 */
	public void setStartDay(int startDay){
		this.startDay = startDay;
	}
	
	public int getStartDay(){
		return startDay;
	}
	
	public void setAdaper(BaseAdapter adpater){
		this.adapter = adpater;
	}
}
