package net.senink.seninkapp.ui.smart;

import java.util.Calendar;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISFamilyControl;
//import com.senink.seninkapp.core.PISFamilyControl.TimePeriodInfo;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.view.timerpicker.widget.NumericWheelAdapter;
import net.senink.seninkapp.ui.view.timerpicker.widget.OnWheelChangedListener;
import net.senink.seninkapp.ui.view.timerpicker.widget.OnWheelScrollListener;
import net.senink.seninkapp.ui.view.timerpicker.widget.WheelView;

public class AddTimer extends BaseActivity implements OnClickListener {
	private View backbt, customTimePicker;
	private TextView startTime, endingTime;
	private TextView two, three, four, five, six, seven;
	private CheckBox one;
	private boolean curTimeSet = false;

	// Time changed flag
	private boolean timeChanged = false;

	//
	private boolean timeScrolled = false;
	private View root;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_timer_layout);
		initView();
	}

	private void initView() {
		backbt = findViewById(R.id.back);
		startTime = (TextView) findViewById(R.id.start_time_edit);
		endingTime = (TextView) findViewById(R.id.ending_time_edit);
		customTimePicker = findViewById(R.id.custom_time_picker);
		one = (CheckBox) findViewById(R.id.one);
		two = (TextView) findViewById(R.id.two);
		three = (TextView) findViewById(R.id.three);
		four = (TextView) findViewById(R.id.four);
		five = (TextView) findViewById(R.id.five);
		six = (TextView) findViewById(R.id.six);
		seven = (TextView) findViewById(R.id.seven);

		backbt.setOnClickListener(this);
		startTime.setOnClickListener(this);
		endingTime.setOnClickListener(this);
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);
		five.setOnClickListener(this);
		six.setOnClickListener(this);
		seven.setOnClickListener(this);
		root = findViewById(R.id.root);
		root.setOnClickListener(this);
		findViewById(R.id.ok).setOnClickListener(this);

		initTimePicker();
		initData();
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(R.string.saving));
	}

//	PISFamilyControl familyControl;
	PISManager pm;
//	PISSmartCell smartItem;
//	TimePeriodInfo timeInfo;

	@SuppressWarnings("deprecation")
	private void initData() {
		pm = PISManager.getInstance();
//		smartItem = (PISSmartCell) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
//		if (smartItem.getSmartCellChildObject() instanceof PISFamilyControl) {
//			familyControl = (PISFamilyControl) smartItem
//					.getSmartCellChildObject();
//			if (ParentsControl.curClickPosition != -1) {
//				timeInfo = familyControl.getTimePeriodList().get(
//						ParentsControl.curClickPosition);
//				LinearLayout week_group = (LinearLayout) findViewById(R.id.week_group);
//				for (int i = 0; i < week_group.getChildCount(); i++) {
//					View child = week_group.getChildAt(i);
//					if (timeInfo.getRepeatDay().contains(String.valueOf(i))) {
//						child.setBackgroundColor(Color.GREEN);
//					} else {
//						child.setBackgroundDrawable(null);
//					}
//
//				}
//				startTime.setText(timeInfo.getStartTime());
//				endingTime.setText(timeInfo.getEndTime());
//			}
//
//		}
	}

	@SuppressWarnings("deprecation")
	private void refreshWeek(View view) {
		Log.i("xj", "bb==>"+view.getBackground());
		if (view.getBackground()==null) {
			view.setBackgroundColor(Color.GREEN);
		} else {
			view.setBackgroundDrawable(null);
		}
	}

	private void initTimePicker() {
		final WheelView hours = (WheelView) findViewById(R.id.hour);
		hours.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
		hours.setCyclic(false);

		final WheelView mins = (WheelView) findViewById(R.id.mins);
		mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		mins.setCyclic(false);

		Calendar c = Calendar.getInstance();
		int curHours = c.get(Calendar.HOUR_OF_DAY);
		int curMinutes = c.get(Calendar.MINUTE);

		String hour = curHours < 10 ? (String.valueOf(0) + curHours) : String
				.valueOf(curHours);
		String min = curMinutes < 10 ? (String.valueOf(0) + curMinutes)
				: String.valueOf(curMinutes);

		startTime.setText(hour + ":" + min);
		endingTime.setText(hour + ":" + min);

		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);

		// add listeners
		addChangingListener(mins, "min");
		addChangingListener(hours, "hour");

		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!timeScrolled) {
					timeChanged = true;
					// added the final time in here liuhongjie
					String hour = hours.getCurrentItem() < 10 ? (String
							.valueOf(0) + hours.getCurrentItem()) : String
							.valueOf(hours.getCurrentItem());
					String min = mins.getCurrentItem() < 10 ? (String
							.valueOf(0) + mins.getCurrentItem()) : String
							.valueOf(mins.getCurrentItem());
					if (curTimeSet) {
						startTime.setText(hour + ":" + min);
					} else {
						endingTime.setText(hour + ":" + min);
					}
					timeChanged = false;
				}
			}
		};

		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				timeScrolled = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				timeScrolled = false;
				timeChanged = true;
				// added the final time in here liuhongjie
				String hour = hours.getCurrentItem() < 10 ? (String.valueOf(0) + hours
						.getCurrentItem()) : String.valueOf(hours
						.getCurrentItem());
				String min = mins.getCurrentItem() < 10 ? (String.valueOf(0) + mins
						.getCurrentItem()) : String.valueOf(mins
						.getCurrentItem());
				if (curTimeSet) {
					startTime.setText(hour + ":" + min);
				} else {
					endingTime.setText(hour + ":" + min);
				}
				timeChanged = false;
			}
		};

		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * 
	 * @param wheel
	 *            the wheel
	 * @param label
	 *            the wheel label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
	}

//	TimePeriodInfo periodInfo;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.start_time_edit:
			curTimeSet = true;
			customTimePicker.setVisibility(View.VISIBLE);
			break;
		case R.id.ending_time_edit:
			curTimeSet = false;
			customTimePicker.setVisibility(View.VISIBLE);
			break;
		case R.id.one:
		case R.id.two:
		case R.id.three:
		case R.id.four:
		case R.id.five:
		case R.id.six:
		case R.id.seven:
			refreshWeek(view);
			break;
		case R.id.root:
			customTimePicker.setVisibility(View.GONE);
			break;
		case R.id.ok:
			LinearLayout week_group = (LinearLayout) findViewById(R.id.week_group);
			String repeatDay = "";
			for (int i = 0; i < week_group.getChildCount(); i++) {
				View child = week_group.getChildAt(i);
				if (child.getBackground() != null) {
					repeatDay += i;
				}
			}
//			periodInfo = new TimePeriodInfo();
//			if (repeatDay.length()>0) {
//				periodInfo.setRepeatDay(repeatDay);
//			}
//			Log.i("hxj", "start==>"+String.valueOf(startTime.getText()).trim());
//			Log.i("hxj", "end==>"+String.valueOf(endingTime.getText()).trim());
//			periodInfo.setStartTime(String.valueOf(startTime.getText()).trim());
//			periodInfo.setEndTime(String.valueOf(endingTime.getText()).trim());
//			progressDialog.show();
//			familyControl.requestAddTimePeriod(periodInfo);
			break;
		default:
			break;
		}

	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//
//		if (pis == null) {
//			return;
//		}
//		if (pis instanceof PISSmartCell) {
//			switch (pis.mPisType) {
//			case PISConstantDefine.PIS_TYPE_DEVICE:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_UPDATE:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_MCM:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_GW_CONFIG:
//				break;
//			case PISConstantDefine.PIS_TYPE_SMART_CELL:
//				switch (reqType) {
//				case PISFamilyControl.PIS_CMD_SMART_ADD_TIME_PERIOD:
//					if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//						if (ParentsControl.curClickPosition != -1) {
//							familyControl.requestDelTimePeriod(timeInfo);
//						} else {
//							try {
//								progressDialog.dismiss();
//								finish();
//							} catch (Exception e) {
//								net.senink.seninkapp
//							}
//						}
//					} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//							|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//						try {
//							progressDialog.dismiss();
//						} catch (Exception e) {
//							net.senink.seninkapp
//						}
//					}
//					break;
//				case PISFamilyControl.PIS_CMD_SMART_DEL_TIME_PERIOD:
//					if (result == PipaRequest.REQUEST_RESULT_ERROR
//							|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//							|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//						try {
//							progressDialog.dismiss();
//							if (familyControl != null) {
//								familyControl.getInfo();
//							}
//
//						} catch (Exception e) {
//							net.senink.seninkapp
//						}
//						finish();
//					}
//					break;
//				}
//				break;
//			case PISConstantDefine.PIS_TYPE_SWITCH_LIGHT_ENABLE:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_SWITCH_LIGHT_DISABLE:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_UNKNOW:
//
//				break;
//			default:
//				break;
//			}
//		}
//
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onFirmwareInfoChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		// TODO Auto-generated method stub
//
//	}

}
