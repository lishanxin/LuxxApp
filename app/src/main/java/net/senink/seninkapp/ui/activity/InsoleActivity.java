package net.senink.seninkapp.ui.activity;


import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISInsoleGroup;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISXinInsole;
//import com.senink.seninkapp.core.PISXinInsole.InsoleState;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.HalfCircleButton;
import net.senink.seninkapp.ui.view.HalfCircleButton.OnItemClickListener;

/**
 * 智能穿戴详情界面
 * 
 * @author zhaojunfeng
 * @date 2016-01-30
 * 
 */
public class InsoleActivity extends BaseActivity implements
		OnClickListener {
	private final static int MSG_REFRESH_VIEW = 1;
	private PISManager manager;
//	private PISXinInsole mLeftInsole;
//	private PISXinInsole mRightInsole;
//	private PISInsoleGroup group;
	// 标题名称
	private TextView tvTitle;
	// 步数
	private TextView tvSteps;
	// 左右电量图标
	private ImageView ivEleLeft, ivEleRight;
	// 是否加热
	private ImageView ivHeatLeft, ivHeatRight;
	// 左右边温度值
	private TextView tvTempOnLeft, tvTempOnRight;
	// 三种功能按钮
	private Button timerBtn, tempBtn;
	private CheckBox heatBtn;
	// 增加或者减少按钮
	private ImageButton addBtn, reduceBtn;
	// 需要设置的温度
	private TextView tvTemperature;
	// 返回按钮
	private Button backBtn;
	// 标题布局
	private View titleLayout;
	// 半圆形按钮
	private HalfCircleButton mHalfBtn;
	//组id
	private short groupId;
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MSG_REFRESH_VIEW:
				setView();
				break;

			default:
				break;
			}
    	}
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insole);
		manager = PISManager.getInstance();
		initView();
		setData();
		setListener();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void setListener() {
		backBtn.setOnClickListener(this);
		tvSteps.setOnClickListener(this);
		timerBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		reduceBtn.setOnClickListener(this);
		heatBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (group != null) {
//					group.setStatus(isChecked);
//				}
			}
		});
		mHalfBtn.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mHalfBtn.setAlias((int) event.getX(), (int) event.getY());
					return true;
				}
				return false;
			}
		});
		mHalfBtn.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(int index) {
				setModeBtn(index);
			}
		});
	}

	/**
	 * 更新组件信息
	 */
	private void setView() {
		int steps = 0;
		int ele = 0;
		boolean isHeated = false;
		int temperature = 0;
//		if (mLeftInsole != null && mLeftInsole.getState() != null) {
//			InsoleState state = mLeftInsole.getState();
//			steps = state.currentSteps;
//			ele = state.dumpEnergy;
//			isHeated = state.isHeating;
//			temperature = state.currentTemp;
//		}
		setHeatOnLeft(isHeated);
		setEnergyOnLeft(ele);
		tvTempOnLeft.setText(getString(R.string.insole_temperature_value, temperature));
//		if (mRightInsole != null && mRightInsole.getState() != null) {
//			InsoleState state = mRightInsole.getState();
//			steps = state.currentSteps + steps;
//			ele = state.dumpEnergy;
//			isHeated = state.isHeating;
//			temperature = state.currentTemp;
//		} else {
//			ele = 0;
//			isHeated = false;
//			temperature = 0;
//		}
		tvTempOnRight.setText(getString(R.string.insole_temperature_value, temperature));
		setEnergyOnRight(ele);
		tvSteps.setText(steps+"");
		setTemperature(temperature);
	}
	
	private void setEnergyOnLeft(int ele) {
		if (100 == ele) {
			ivEleLeft.setImageResource(R.drawable.icon_ele_3);
		} else if(100 / 3 >= ele && 200 / 3 <= ele){
			ivEleLeft.setImageResource(R.drawable.icon_ele_2);
		}else{
			ivEleLeft.setImageResource(R.drawable.icon_ele_1);
		}
	}

	private void setEnergyOnRight(int ele) {
		if (100 == ele) {
			ivEleRight.setImageResource(R.drawable.icon_ele_3);
		} else if(100 / 3 >= ele && 200 / 3 <= ele){
			ivEleRight.setImageResource(R.drawable.icon_ele_2);
		}else{
			ivEleRight.setImageResource(R.drawable.icon_ele_1);
		}
	}
	/**
	 * 是否加热（用于替换图片）
	 * @param isHeated
	 */
	private void setHeatOnLeft(boolean isHeated) {
		
	}
	/**
	 * 是否加热（用于替换图片）
	 * @param isHeated
	 */
	private void setHeatOnRight(boolean isHeated) {
		
	}
	/**
	 * 切换模式 0:恒温 1:手动 2：智能
	 * 
	 * @param index
	 */
	private void setModeBtn(int index) {
		if (index == 0) {
			heatBtn.setEnabled(false);
			tempBtn.setEnabled(true);
			addBtn.setEnabled(true);
			reduceBtn.setEnabled(true);
//			if (group != null) {
//				group.setMode(true);
//			}
		} else if (index == 1) {
			heatBtn.setEnabled(true);
			tempBtn.setEnabled(true);
			addBtn.setEnabled(true);
			reduceBtn.setEnabled(true);
//			if (group != null) {
//				group.setMode(false);
//			}
		} else {
			heatBtn.setEnabled(false);
			tempBtn.setEnabled(false);
			addBtn.setEnabled(false);
			reduceBtn.setEnabled(false);
//			if (group != null) {
//				group.setMode(true);
//			}
		}
	}

	/**
	 * 设置目标温度值
	 * 
	 * @param value
	 */
	private void setTemperature(int value) {
		String str = getString(R.string.insole_temperature_value, value);
		tvTemperature.setText(str);
	}

	/**
	 * 设置目标温度值
	 * 
	 * @param isAdded
	 */
	private void setTemperature(boolean isAdded) {
		String content = tvTemperature.getText().toString();
		int value = 0;
		if (!TextUtils.isEmpty(content) && content.length() > 1) {
			value = Integer
					.parseInt(content.substring(0, content.length() - 1));
		}
		if (isAdded) {
			if (value < 50) {
				value++;
			}
		} else {
			if (value > 20) {
				value--;
			}
		}
//		if (group != null) {
//			int high = value + 5;
//			int low = value -5;
//			if (high > 50) {
//				high = 50;
//			}
//			if (low < 20) {
//				low = 20;
//			}
//			group.setTemperature((byte)low, (byte)high);
//		}
		setTemperature(value);
	}

	/**
	 * 获取其他界面跳转的传值
	 */
	private void setData() {
		groupId = getIntent().getShortExtra("groupid", (short)-1);
		if (manager != null) {
//			group = manager.getInsoleGroup(groupId);
//			if (group.infors != null && group.infors.size() > 0) {
//				setLeftAndRightData();
//			}else{
//				PISMCSManager mcm = manager.getPisMcmObject();
//				if (mcm != null) {
//					mcm.getServiceGroup(groupId);
//				}
//			}
		}
	}

	/**
	 * 设置左右脚的信息
	 */
	private void setLeftAndRightData() {
//		List<Object[]> list = group.infors;
//		int size = list.size();
//		if (size > 2) {
//			size = 0;
//		}
//		for (int i = 0; i < size; i++) {
//			Object[] objs = list.get(i);
//			if (i == 0) {
//				mLeftInsole = (PISXinInsole)manager.getPISBase((String)objs[0], (Short)objs[1]);
//				if (mLeftInsole != null) {
//					group.leftInsole = mLeftInsole;
//					mLeftInsole.getStatus();
//				}
//			} else {
//				mRightInsole = (PISXinInsole)manager.getPISBase((String)objs[0], (Short)objs[1]);
//				if (mRightInsole != null) {
//					group.rightInsole = mRightInsole;
//					mRightInsole.getStatus();
//				}
//			}
//		}
//		if (null == mLeftInsole || null == mRightInsole) {
//			ArrayList<PISBase> insoles = manager.getPisXinInsoleList();
//			if (insoles != null && insoles.size() > 0) {
//				size = insoles.size();
//				for (PISBase base:insoles) {
//					if (null == base.groupIds || base.groupIds.length == 0) {
//						base.getGroupsOnDevice();
//					}
//				}
//			}
//		}
		mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		titleLayout = findViewById(R.id.insole_title);
		tvSteps = (TextView) findViewById(R.id.insole_steps);
		ivEleLeft = (ImageView) findViewById(R.id.insole_left_ele);
		ivEleRight = (ImageView) findViewById(R.id.insole_right_ele);
		ivHeatLeft = (ImageView) findViewById(R.id.insole_left_heat);
		ivHeatRight = (ImageView) findViewById(R.id.insole_right_heat);
		tvTempOnLeft = (TextView) findViewById(R.id.insole_temperature_left);
		tvTempOnRight = (TextView) findViewById(R.id.insole_temperature_right);
		timerBtn = (Button) findViewById(R.id.insole_mode_timer);
		tempBtn = (Button) findViewById(R.id.insole_mode_temperature);
		heatBtn = (CheckBox) findViewById(R.id.insole_mode_heat);
		addBtn = (ImageButton) findViewById(R.id.insole_add);
		reduceBtn = (ImageButton) findViewById(R.id.insole_reduce);
		tvTemperature = (TextView) findViewById(R.id.insole_temperature_value);
		mHalfBtn = (HalfCircleButton) findViewById(R.id.insole_halfbtn);
		setTitle();
	}

	/**
	 * 设置标题相关信息
	 */
	private void setTitle() {
		tvTitle.setText(R.string.insole_title);
		backBtn.setVisibility(View.VISIBLE);
		titleLayout.setBackgroundColor(getResources().getColor(
				R.color.insole_title_bgcolor));
	}

	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPipaRequestStatusListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		manager.setOnPipaRequestStatusListener(null);
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if ((mLeftInsole != null && pis.getPISKeyString().equals(mLeftInsole.getPISKeyString()))
//				|| (mRightInsole != null && pis.getPISKeyString().equals(mRightInsole.getPISKeyString()))) {
//			if (reqType == PISXinInsole.PIS_CMD_INSOLE_MODE_SET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					ToastUtils.showToast(this, R.string.insole_setmode_success);
//				} else {
//					ToastUtils.showToast(this, R.string.insole_setmode_failed);
//				}
//			}else if(reqType == PISXinInsole.PIS_CMD_INSOLE_TEMP_SET){
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					ToastUtils.showToast(this, R.string.insole_set_temperature_success);
//				} else {
//					ToastUtils.showToast(this, R.string.insole_set_temperature_failed);
//				}
//			}else if(reqType == PISXinInsole.PIS_CMD_INSOLE_HEAT_ONOFF){
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					if (heatBtn.isChecked()) {
//						ToastUtils.showToast(this, R.string.insole_heating_open);
//					} else {
//						ToastUtils.showToast(this, R.string.insole_heating_close);
//					}
//				} else {
//					if (heatBtn.isChecked()) {
//						ToastUtils.showToast(this, R.string.insole_heating_open_failed);
//						heatBtn.setChecked(false);
//					} else {
//						heatBtn.setChecked(true);
//						ToastUtils.showToast(this, R.string.insole_heating_close_failed);
//					}
//				}
//			}else if(reqType == PISXinInsole.PIS_CMD_INSOLE_GET_STATE){
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
//				}
//			}
//		}else if(pis.mT1 == (byte) PISConstantDefine.PIS_INSOLE_T1
//				&& pis.mT2 == (byte) PISConstantDefine.PIS_INSOLE_T2){
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE
//					&& reqType == PISBase.PIS_CMD_GROUP_GET) {
//				if (pis.groupIds != null && pis.groupIds.length > 0) {
//					short[] ids = pis.groupIds;
//					if (ids[0] == groupId) {
//						if (null == mLeftInsole) {
//							mLeftInsole = (PISXinInsole) pis;
//							group.leftInsole = mLeftInsole;
//							mLeftInsole.getStatus();
//						} else if (null == mRightInsole) {
//							mRightInsole = (PISXinInsole) pis;
//							group.rightInsole = mRightInsole;
//							mRightInsole.getStatus();
//						}
//						mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
//					}
//				}
//			}
//		}else if(pis.mT1 == PISConstantDefine.PIS_MCM_T1
//				&& pis.mT2 == PISConstantDefine.PIS_MCM_T2){
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				if (reqType == PISMCSManager.PIS_CMD_MCM_GET_GROUP_SRV) {
//					group = manager.getInsoleGroup(groupId);
//					if (group.infors != null && group.infors.size() > 0) {
//						setLeftAndRightData();
//					}
//				}
//			}
//		}
//	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.insole_steps:
//			intent = new Intent(this, InsoleStepActivity.class);
//			if (group != null) {
//				manager.addDeviceGroup(group);
//				intent.putExtra("groupid",groupId);
//			}
			break;
		case R.id.insole_mode_timer:
//			intent = new Intent(this, InsoleTimerListActivity.class);
//			if (group != null) {
//				manager.addDeviceGroup(group);
//				intent.putExtra("groupid",groupId);
//			}
			break;
		case R.id.insole_add:
			setTemperature(true);
			break;
		case R.id.insole_reduce:
			setTemperature(false);
			break;
		default:
			break;
		}
		if (intent != null) {
			startActivity(intent);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
		}
	}
}
