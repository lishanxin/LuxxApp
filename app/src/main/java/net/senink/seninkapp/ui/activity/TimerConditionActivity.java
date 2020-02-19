package net.senink.seninkapp.ui.activity;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.util.StringUtils;
import net.senink.seninkapp.ui.view.calendarview.Day;
import net.senink.seninkapp.ui.view.calendarview.ExtendedCalendarView;
import net.senink.seninkapp.ui.view.calendarview.MonthView;
import net.senink.seninkapp.ui.view.calendarview.OnDayClickListener;

/**
 * 智能控制之定时器重复条件设置界面
 * 
 * @author zhaojunfeng
 * @date 2016-01-05
 */
public class TimerConditionActivity extends BaseActivity implements
		View.OnClickListener {
	public final static int MSG_REFRESH_MONTH_YEAR = 1;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	private RadioGroup mRadioGroup;
	private RadioButton dayBtn, weekBtn, monthBtn, yearBtn;
	private CheckBox cbSunday, cbMonday, cbTuesday, cbWednesday, cbThursday,
			cbFriday, cbSaturday;
	private LinearLayout weekLayout;
	//开关按钮
	private CheckBox cbSwitch;
	// 星期字符串
	private String week = "xxxxxxx";
	// 日期字符串
	private String date = "xxxxxxxx";
	// 年的日历组件
	private ExtendedCalendarView yearCalendarView;
	// 月的日历组件
	private MonthView monthView;
    //是否新建任务
	private boolean isNew;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_MONTH_YEAR:
				if (msg.arg1 >= 0) {
					monthView.setCurrentItem(msg.arg1);
				} else {
					yearCalendarView.resetView();
				}
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timercondition);
		setData();
		initView();
		setViews();
		setListener();
	}

	/**
	 * 为组件赋值
	 */
	private void setViews() {
		if (isNew || (!isNew && !TextUtils.isEmpty(date) && !date.startsWith("xxxx"))) {
			cbSwitch.setChecked(false);
			setEnable(false);
			setContent(3);
			yearCalendarView.setYears(false);
		} else if ((!TextUtils.isEmpty(date) && date.equals("xxxxxxxx"))
				&& (!TextUtils.isEmpty(week) && week.equals("xxxxxxx"))) {
			cbSwitch.setChecked(true);
			dayBtn.setChecked(true);
		}else {
			if (!TextUtils.isEmpty(date) && !date.equals("xxxxxxxx")) {
				if (date.startsWith("xxxxxx")) {
					monthBtn.setChecked(true);
					setContent(2);
					String day = date.substring(6, 8);
					if (TextUtils.isDigitsOnly(day)) {
						Message msg = mHandler.obtainMessage(MSG_REFRESH_MONTH_YEAR);
						msg.arg1 = Integer.parseInt(day) - 1;
						mHandler.sendMessageDelayed(msg, 400);
					}
					cbSwitch.setChecked(true);
					setEnable(true);
				} else if (date.startsWith("xxxx")) {
					yearBtn.setChecked(true);
					setContent(3);
					String month = date.substring(4, 8);
					if (TextUtils.isDigitsOnly(month)) {
						int moth = Integer.parseInt(month.substring(0, 2));
						int day = Integer.parseInt(month.substring(2, 4));
						Calendar cal = Calendar.getInstance();
						int year = cal.get(Calendar.YEAR);
						Message msg = mHandler.obtainMessage(MSG_REFRESH_MONTH_YEAR);
						msg.arg1 = - 1;
						yearCalendarView.setCurrentItem(year, moth, day);
						mHandler.sendMessageDelayed(msg, 400);
					}
					cbSwitch.setChecked(true);
					setEnable(true);
				}
			} else if (!TextUtils.isEmpty(week) && !week.equals("xxxxxxx")) {
				cbSwitch.setChecked(true);
				setEnable(true);
				setContent(1);
				weekBtn.setChecked(true);
				if (week.contains("0")) {
					cbSunday.setChecked(true);
				} else {
					cbSunday.setChecked(false);
				}
				if (week.contains("1")) {
					cbMonday.setChecked(true);
				} else {
					cbMonday.setChecked(false);
				}
				if (week.contains("2")) {
					cbTuesday.setChecked(true);
				} else {
					cbTuesday.setChecked(false);
				}
				if (week.contains("3")) {
					cbWednesday.setChecked(true);
				} else {
					cbWednesday.setChecked(false);
				}
				if (week.contains("4")) {
					cbThursday.setChecked(true);
				} else {
					cbThursday.setChecked(false);
				}
				if (week.contains("5")) {
					cbFriday.setChecked(true);
				} else {
					cbFriday.setChecked(false);
				}
				if (week.contains("6")) {
					cbSaturday.setChecked(true);
				} else {
					cbSaturday.setChecked(false);
				}
			}
		}
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (getIntent() != null) {
			Intent intent = getIntent();
			String str = intent.getStringExtra("date");
			if (!TextUtils.isEmpty(str)) {
				if (str.length() >= 8) {
					date = str.substring(0, 8);
				}
				if (str.length() >= 15) {
					week = str.substring(8, 15);
				}
				isNew = false;
			}else{
				isNew = true;
			}
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		cbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setEnable(isChecked);
				if (!isChecked) {
					setContent(3);
				}
				yearCalendarView.setYears(isChecked);
			}
		});
		monthView.setOnDayClickListener(new OnDayClickListener() {

			@Override
			public void onDayClicked(AdapterView<?> adapter, View view,
					int position, long id, Day day) {
				refreshWeek();
				date = "xxxxxx" + StringUtils.getString(day.getDay());
				week = "xxxxxxx";
				yearCalendarView.clearFocusOnChild();
			}
		});
		yearCalendarView.setOnDayClickListener(new OnDayClickListener() {

			@Override
			public void onDayClicked(AdapterView<?> adapter, View view,
					int position, long id, Day day) {
				refreshWeek();
				if (cbSwitch.isChecked()) {
					date = "xxxx";
				}else{
					date = "" + day.getYear();
				}
				date = date + StringUtils.getString(day.getMonth() + 1)
						+ StringUtils.getString(day.getDay());
				week = "xxxxxxx";
				monthView.clearFocusOnChild();
			}
		});
		cbWednesday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							week = week.substring(0, 3) + "3"
									+ week.substring(4);
							date = "xxxxxxxx";
							refreshMothAndYear();
						} else {
							week = week.substring(0, 3) + "x"
									+ week.substring(4);
						}
					}
				});
		cbTuesday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							week = week.substring(0, 2) + "2"
									+ week.substring(3);
							date = "xxxxxxxx";
							refreshMothAndYear();
						} else {
							week = week.substring(0, 2) + "x"
									+ week.substring(3);
						}
					}
				});
		cbThursday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							week = week.substring(0, 4) + "4"
									+ week.substring(5);
							date = "xxxxxxxx";
							refreshMothAndYear();
						} else {
							week = week.substring(0, 4) + "x"
									+ week.substring(5);
						}
					}
				});
		cbSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					week = "0" + week.substring(1);
					date = "xxxxxxxx";
					refreshMothAndYear();
				} else {
					week = "x" + week.substring(1);
				}
			}
		});
		cbSaturday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							week = week.substring(0, 6) + "6";
							date = "xxxxxxxx";
							refreshMothAndYear();
						} else {
							week = week.substring(0, 6) + "x";
						}
					}
				});
		cbMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					week = week.substring(0, 1) + "1" + week.substring(2);
					date = "xxxxxxxx";
					refreshMothAndYear();
				} else {
					week = week.substring(0, 1) + "x" + week.substring(2);
				}
			}
		});
		cbFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					week = week.substring(0, 5) + "5" + week.substring(6);
					date = "xxxxxxxx";
					refreshMothAndYear();
				} else {
					week = week.substring(0, 5) + "x" + week.substring(6);
				}
			}
		});
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == dayBtn.getId()) {
					setContent(0);
					refreshWeek();
					date = "xxxxxxxx";
					week = "xxxxxxx";
				} else if (checkedId == weekBtn.getId()) {
					setContent(1);
				} else if (checkedId == monthBtn.getId()) {
					setContent(2);
				} else if (checkedId == yearBtn.getId()) {
					setContent(3);
				}
			}
		});
	}

	private void refreshMothAndYear() {
		monthView.clearFocusOnChild();
		yearCalendarView.clearFocusOnChild();
	}

	private void refreshWeek() {
		cbFriday.setChecked(false);
		cbMonday.setChecked(false);
		cbSaturday.setChecked(false);
		cbSunday.setChecked(false);
		cbThursday.setChecked(false);
		cbWednesday.setChecked(false);
		cbTuesday.setChecked(false);
	}

	/**
	 * 初始化时间组件
	 */

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		mRadioGroup = (RadioGroup) findViewById(R.id.timercondition_time_layout);
		dayBtn = (RadioButton) findViewById(R.id.timercondition_day);
		weekBtn = (RadioButton) findViewById(R.id.timercondition_week);
		monthBtn = (RadioButton) findViewById(R.id.timercondition_month);
		yearBtn = (RadioButton) findViewById(R.id.timercondition_year);
		cbMonday = (CheckBox) findViewById(R.id.timercondition_monday);
		cbSwitch = (CheckBox) findViewById(R.id.timercondition_switch);
		cbTuesday = (CheckBox) findViewById(R.id.timercondition_tuesday);
		cbWednesday = (CheckBox) findViewById(R.id.timercondition_wednesday);
		cbThursday = (CheckBox) findViewById(R.id.timercondition_thursday);
		cbFriday = (CheckBox) findViewById(R.id.timercondition_friday);
		cbSaturday = (CheckBox) findViewById(R.id.timercondition_saturday);
		cbSunday = (CheckBox) findViewById(R.id.timercondition_sunday);
		weekLayout = (LinearLayout) findViewById(R.id.timercondition_week_layout);
		monthView = (MonthView) findViewById(R.id.timercondition_monthCalendar);
		yearCalendarView = (ExtendedCalendarView) findViewById(R.id.timercondition_yearCalendar);
		yearCalendarView.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置年月周日四个组件是否可用
	 */
	private void setEnable(boolean enabled){
		if (!enabled) {
			dayBtn.setChecked(false);
			weekBtn.setChecked(false);
			monthBtn.setChecked(false);
			yearBtn.setChecked(false);
		}
		dayBtn.setEnabled(enabled);
		weekBtn.setEnabled(enabled);
		monthBtn.setEnabled(enabled);
		yearBtn.setEnabled(enabled);
	}
	/**
	 * 更改内容
	 * 
	 * @param index
	 *            0:day 1:week 2:month 3:year
	 */
	private void setContent(int index) {
		if (index == 0) {
			yearCalendarView.setVisibility(View.GONE);
			monthView.setVisibility(View.GONE);
			weekLayout.setVisibility(View.GONE);
		} else if (index == 1) {
			weekLayout.setVisibility(View.VISIBLE);
			yearCalendarView.setVisibility(View.GONE);
			monthView.setVisibility(View.GONE);
		} else if (index == 2) {
			monthView.setVisibility(View.VISIBLE);
			monthView.refreshCalendar();
			yearCalendarView.setVisibility(View.GONE);
			weekLayout.setVisibility(View.GONE);
		} else if (index == 3) {
			yearCalendarView.setVisibility(View.VISIBLE);
			yearCalendarView.refreshCalendar();
			monthView.setVisibility(View.GONE);
			weekLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.repeat);
		backBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			Intent it = new Intent();
			week = week.trim();
			date = date.trim();
			it.putExtra("repeat", date + week);
			setResult(Activity.RESULT_OK, it);
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}
}