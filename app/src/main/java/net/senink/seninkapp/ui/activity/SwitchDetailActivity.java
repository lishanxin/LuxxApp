package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinSwitch;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSwitch;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.setting.LocationListsActivity;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.CurveChartView;

public class SwitchDetailActivity extends BaseActivity implements
		OnClickListener, CurveChartView.OnGongLvChangeListener {
	// the message on drawing the picture
	private final static int MSG_DRAW_PICTURE = 1;
	private Button backbtn;
	private CurveChartView dc = null;
	int maxValueScope = 1000;
	int maxScope = 10;
	private PISManager pm;
	// the status of the switch
	private CheckBox switcher;
	// the status of the background light
	private CheckBox bgLightSwitcher;
	public static PISXinSwitch pisSwitch;
	private TextView address_info, name;
	private Button saveBtn;
//	public static String curLocation = null;
	TextView tvTitle, gong_lv;
	List<Float> floats = new ArrayList<Float>();
	// 是否是从定时器设置界面跳转过来
	private boolean isTimer = false;
	private short cmd;
	private byte[] cmdContent;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_DRAW_PICTURE:
				// float value = (float) (Math.random() * maxValueScope);
				float value = pisSwitch.getPower();
				int temp = getMaxValue(value);
				int temp1 = getMaxValue(getListMaxValue());
				temp = Math.max(Math.max(temp, temp1), 10);
				// if (maxScope != temp && temp >= 10) {
				// maxScope = temp;
				dc.setDataScope(0, temp);
				// }
				floats.add(value);
				if (floats.size() >= 60) {
					floats.remove(floats.size() - 1);
				}
				dc.appendData(value);
				sendEmptyMessageDelayed(MSG_DRAW_PICTURE, 1000);
				break;

			default:
				break;
			}
		};
	};

	private float getListMaxValue() {
		int N = floats.size();
		float temp = 0.0f;
		for (int i = 0; i < N; i++) {
			if (floats.get(i) > temp) {
				temp = floats.get(i);
			}
		}
		return temp;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.switch_detail);
			pm = PISManager.getInstance();
			pisSwitch = (PISXinSwitch) pm.getPISObject(getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			initView();
			setData();
			setListener();
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(SwitchDetailActivity.this, e);
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		/**订阅消息*/
		pisSwitch.subscribe(PISXinSwitch.PIS_MSG_KGTG_POWER, true);
		pisSwitch.subscribe(PISXinSwitch.PIS_MSG_KGTG_STATUS, true);

		saveBtn.setOnClickListener(this);
		switcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!isTimer) {
//					pisSwitch.setSwitchLcs(switcher.isChecked());
					PipaRequest req = pisSwitch.commitSwitchStatus(switcher.isChecked());
					pisSwitch.request(req);
				}

			}
		});
		bgLightSwitcher
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(final CompoundButton buttonView,
												 final boolean isChecked) {
						if (!isTimer) {
							if (pisSwitch != null) {
//								pisSwitch.setBackGroundSwitchLcs(isChecked,
//										true);
								PipaRequest req = pisSwitch.commitBackLightStatus(isChecked);
								req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
									@Override
									public void onRequestStart(PipaRequest req) {

									}

									@Override
									public void onRequestResult(PipaRequest req) {
										if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED)
											buttonView.setChecked(!isChecked);
									}
								});
								pisSwitch.request(req);
							}
						}
					}
				});
		backbtn.setOnClickListener(this);
		findViewById(R.id.address_area).setOnClickListener(this);
		findViewById(R.id.name_area).setOnClickListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		try {
			if (hasFocus) {
				for (int i = 0; i < 500; i++) {
					dc.appendData(-10f);
				}
			}
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			name.setText(pisSwitch.getName());
			handler.sendEmptyMessage(MSG_DRAW_PICTURE);
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(SwitchDetailActivity.this, e);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeMessages(MSG_DRAW_PICTURE);
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backbtn = (Button) findViewById(R.id.title_back);
		dc = (CurveChartView) findViewById(R.id.chart_view);
		switcher = (CheckBox) findViewById(R.id.registerd_device_switch);
		bgLightSwitcher = (CheckBox) findViewById(R.id.switch_detail_bglight_status);

		address_info = (TextView) findViewById(R.id.address_info);
		saveBtn = (Button) findViewById(R.id.title_finished);
		name = (TextView) findViewById(R.id.name);
		gong_lv = (TextView) findViewById(R.id.gong_lv);
		backbtn.setVisibility(View.VISIBLE);
		if (isTimer) {
			saveBtn.setBackgroundColor(Color.TRANSPARENT);
			saveBtn.setText(R.string.save);
			saveBtn.setVisibility(View.VISIBLE);
		}
		setCurveView();
	}

	/**
	 * 为对应的组件赋值
	 */
	@SuppressWarnings("static-access")
	private void setData() {
		switcher.setChecked(pisSwitch.getSwitchStatus());
		setSwitchName();

		PipaRequest req = pisSwitch.updateSwitchStatus();
		req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
			@Override
			public void onRequestStart(PipaRequest req) {

			}

			@Override
			public void onRequestResult(PipaRequest req) {
				if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
					switcher.setChecked(pisSwitch.getSwitchStatus());
				}
			}
		});
		pisSwitch.request(req);

		req = pisSwitch.updateBackLightStatus();
		req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
			@Override
			public void onRequestStart(PipaRequest req) {

			}

			@Override
			public void onRequestResult(PipaRequest req) {
				if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
					bgLightSwitcher.setChecked(pisSwitch.getSwitchStatus());
				}
			}
		});
		pisSwitch.request(req);

		req = pisSwitch.updatePower();
		pisSwitch.request(req);

	}

	private void setSwitchName() {
		name.setText(pisSwitch.getName());
		tvTitle.setText(pisSwitch.getName());
	}

	/**
	 * 设置曲线图
	 */
	private void setCurveView() {
		dc.setCurveStatic(false);
		dc.setCurveCount(1);
		dc.setCalibrationLeft(true);
		dc.setCurveColor(new int[] { 0xff26A425 });
		dc.setCalibrationOn(true);
		dc.setDataScope(0, 10);
		dc.setBackgroundColor(Color.WHITE);
		dc.setGridColor(Color.BLACK);
		dc.setLvChangeListener(this);
	}

	public static int toInt(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;

		for (int i = 0; i < bRefArr.length; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_back:
			if (isTimer) {
				if (cmdContent != null) {
					Intent it = new Intent();
					if (pisSwitch != null) {
						it.putExtra("mac", pisSwitch.getDeviceObject().getMacString());
						it.putExtra("servId", pisSwitch.getServiceId());
					}
					it.putExtra("cmd", cmd);
					it.putExtra("cmdContent", cmdContent);
					setResult(RESULT_OK, it);
				}
			} else {
//				pm.upgradePISSwitch(pisSwitch);
			}
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.address_area:
//			if (!isTimer) {
//				Intent intent = new Intent(this, LocationListsActivity.class);
//				intent.putExtra("tab", "home");
//				startActivity(intent);
//				overridePendingTransition(R.anim.anim_in_from_right,
//						R.anim.anim_out_to_left);
//			}
			break;
		case R.id.name_area:
			if (!isTimer) {
				Intent it = new Intent(this, ModifyNameActivity.class);
				it.putExtra(MessageModel.PISBASE_KEYSTR, pisSwitch.getPISKeyString());
				startActivity(it);
				overridePendingTransition(R.anim.anim_in_from_right,
						R.anim.anim_out_to_left);
			}
			break;
		case R.id.title_finished:
			save();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == Constant.REQUEST_CODE_SWITCH_MODIFYNAME) {
//				pisSwitch = (PISSwitch) PISManager.cacheMap.get(pisSwitch
//						.getPISKeyString());
				setSwitchName();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 保存定时器的命令内容和命令
	 */
	private void save() {
		cmdContent = new byte[1];
		if (switcher.isChecked()) {
			cmdContent[0] = (byte) (0x01 & 0x0f);
		} else {
			cmdContent[0] = (byte) (0x00 & 0x0f);
		}
//		cmd = (short) PISSwitch.PIS_CMD_LCS_SET;
		ToastUtils.showToast(this, R.string.scene_detail_save_success);
	}

	public int getMaxValue(float value) {
		if (value <= 10) {
			return 10;
		} else if (value > 10 && value <= 100) {
			if (value <= 50) {
				return 50;
			} else {
				return 100;
			}
		} else if (value > 100 && value <= 500) {
			return 500;
		} else if (value > 500) {
			int hundredValue = (int) value / 100;
			if (value % 100 != 0) {
				hundredValue++;
			}

			while (hundredValue % 5 != 0) {
				hundredValue++;
			}

			return hundredValue * 100;
		}

		return 10;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(MSG_DRAW_PICTURE);
		isTimer = false;
	}

	@Override
	public void onBackPressed() {
		backbtn.performClick();
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
//		if (pis != null
//				&& pis.getPISKeyString().equals(pisSwitch.getPISKeyString())) {
//			switch (pis.mPisType) {
//			case PISConstantDefine.PIS_TYPE_SWITCH_LIGHT_ENABLE:
//			case PISConstantDefine.PIS_TYPE_SWITCH_LIGHT_DISABLE:
//				if (pis instanceof PISSwitch) {
//					pisSwitch = (PISSwitch) pis;
//					pisSwitch.clickStateSwitch = pisSwitch.isOn();
////					if (result == PipaRequest.REQUEST_RESULT_ERROR) {
////						Toast.makeText(SwitchDetailActivity.this,
////								R.string.operator_error, Toast.LENGTH_SHORT)
////								.show();
////					} else if (result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
////						Toast.makeText(SwitchDetailActivity.this,
////								R.string.link_timeout, Toast.LENGTH_SHORT)
////								.show();
////					}
//				}
//				break;
//			case PISConstantDefine.PIS_TYPE_UNKNOW:
//
//				break;
//
//			default:
//				break;
//			}
//			if (reqType == PISSwitch.PIS_CMD_LCS_SET
//					|| reqType == PISSwitch.PIS_MSG_LCS_STATUS) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					switcher.setChecked(pisSwitch.isOn());
//				}
//			}else if(reqType == PISSwitch.PIS_CMD_BACKLIGHT_GET){
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					if (pis.getPISKeyString().equals(
//							pisSwitch.getPISKeyString())) {
//						pisSwitch.mOnOffOfBackground = ((PISSwitch)pis).mOnOffOfBackground;
//						bgLightSwitcher
//								.setChecked(pisSwitch.mOnOffOfBackground);
//					}
//				}
//			}
//		}
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
//	@SuppressWarnings("static-access")
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//		// TODO Auto-generated method stub
//		if (!isTimer && pis instanceof PISSwitch
//				&& pis.getPISKeyString().equals(pisSwitch.getPISKeyString())) {
//			pisSwitch = (PISSwitch) pis;
//			name.setText(pisSwitch.mName);
//			curLocation = SharePreferenceUtils
//					.getInstance(this)
//					.getLocationValue(
//							this,
//							String.valueOf(toInt(new byte[] { pisSwitch.mLocation })));
//			address_info.setText(curLocation);
//			pisSwitch.clickStateSwitch = pisSwitch.isOn();
//		}
//
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void gongLvChangeListener(String gongLvValue) {
		try {
			String tmp = getResources().getString(R.string.cur_gong_lv)
					+ gongLvValue + getResources().getString(R.string.wa);
			gong_lv.setText(tmp);
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}
}
