package net.senink.seninkapp.ui.activity;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.MarqueeAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISMarquee;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PisDeviceGroup;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;
import net.senink.seninkapp.ui.view.listview.SwipeMenu;
import net.senink.seninkapp.ui.view.listview.SwipeMenuCreator;
import net.senink.seninkapp.ui.view.listview.SwipeMenuItem;
import net.senink.seninkapp.ui.view.listview.SwipeMenuListView;

/**
 * 跑马灯界面
 * 
 * @author zhaojunfeng
 * @date 2016-01-20
 */

public class MarqueeActivity extends BaseActivity implements
		android.view.View.OnClickListener {
	// 颜色项布局
	private RelativeLayout colorLayout;
	// 添加项布局
	private RelativeLayout addLayout;
	// 进度条
	private SeekBar speedBar;
	// 进度显示
	private TextView tvFast, tvMiddle, tvSlow;
	// 标题
	private TextView tvTitle;
	// 灯列表
	private SwipeMenuListView listView;
	// 颜色的模式
	private TextView tvRandom;
	// 组名
	private TextView tvGroupName;
	// 跑马灯开关
	private CheckBox cbSwitch;
	// 添加组布局
	private RelativeLayout addGroupLayout;
	// 最下面一根线
	private View lineView;
	// 返回按钮
	private Button backBtn;
	// 数据管理器
	private PISManager manager;
	// 跑马灯对象
//	private PISMarquee mPisMarquee;
	// 灯列表的适配器
	private MarqueeAdapter mAdapter;
	// 快中慢的默认字体颜色
	private int normalColor = 0;
	// 快中慢的选中字体颜色
	private int selectedColor = 0;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_marquee);
		manager = PISManager.getInstance();
		setData();
		initView();
		setView();
		setListener();
	}

	private void setView() {
//		if (mPisMarquee != null) {
//			if (mPisMarquee.getTime() == 0) {
//				mPisMarquee.getIntervalTime();
//			} else {
//				setProgressOnSeekBar();
//			}
//			if (mPisMarquee.getGroupId() != -1) {
//				setTextOnGroup();
//			} else {
//				mPisMarquee.getGroupOnLight();
//			}
//			cbSwitch.setChecked(mPisMarquee.getIsRun());
//			refreshListView(true);
//			setTextOnRadom();
//		}
	}

	private void setProgressOnSeekBar() {
		speedBar.setProgress(getTime());
		setTextColor(speedBar.getProgress());
	}

	private void setTextOnGroup() {
//		int groupid = mPisMarquee.getGroupId();
//		PisDeviceGroup group = manager.getSomeoneGroup((short) groupid);
//		if (group != null) {
//			tvGroupName.setText(group.mName == null ? "" : group.mName);
//		}
	}

	private int getTime() {
		int time = 0;
//		if (mPisMarquee != null) {
//			time = (int) (mPisMarquee.getTime() & 0xffff);
//		}
		return time;
	}

	private void setData() {
		if (!TextUtils.isEmpty(getIntent().getStringExtra(
				MessageModel.ACTIVITY_VALUE))) {
			String key = getIntent()
					.getStringExtra(MessageModel.ACTIVITY_VALUE);
//			PISBase base = PISManager.cacheMap.get(key);
//			if (base != null && base.mT1 == PISConstantDefine.PIS_SMART_CELL_T1
//					&& base.mT2 == PISConstantDefine.PIS_SMART_CELL_T2) {
//				mPisMarquee = (PISMarquee) base;
//			}
		}
		Resources res = getResources();
		normalColor = res.getColor(R.color.marquee_bar_textcolor_noraml);
		selectedColor = res.getColor(R.color.marquee_bar_textcolor_selected);
	}

	private void setListener() {
		addLayout.setOnClickListener(this);
		colorLayout.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		addGroupLayout.setOnClickListener(this);
		setListViewListener();
		speedBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
//				if (mPisMarquee != null) {
//					int progress = speedBar.getProgress();
//					mPisMarquee.setIntervalTime((byte) (progress & 0xff), true);
//				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setTextColor(progress);
			}
		});
		cbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
//				if (mPisMarquee != null) {
//					if (isChecked) {
//						mPisMarquee.start();
//					} else {
//						mPisMarquee.stop();
//					}
//				}
			}
		});
	}

	/**
	 * 设置快中慢的字体颜色
	 * 
	 * @param progress
	 */
	private void setTextColor(int progress) {
		if (progress < 255 / 3) {
			tvFast.setTextColor(selectedColor);
			tvMiddle.setTextColor(normalColor);
			tvSlow.setTextColor(normalColor);
		} else if (progress >= 255 / 3 && progress <= 255 * 2 / 3) {
			tvMiddle.setTextColor(selectedColor);
			tvFast.setTextColor(normalColor);
			tvSlow.setTextColor(normalColor);
		} else {
			tvFast.setTextColor(normalColor);
			tvMiddle.setTextColor(normalColor);
			tvSlow.setTextColor(selectedColor);
		}
	}

	private void setListViewListener() {

		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(0xFFFF3B30));
				deleteItem.setWidth(Utils.dpToPx(50, getResources()));
				deleteItem.setTitle(R.string.delete);
				deleteItem.setTitleSize(20);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
		listView.setMenuCreator(creator);
		listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
//					PISBase base = mAdapter.getItem(position);
//					if (base != null) {
//						mPisMarquee.deleteLight(base.index, true);
//					}
					break;
				}
				return false;
			}
		});

		// set SwipeListener
		listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
			}

			@Override
			public void onSwipeEnd(int position) {
			}
		});

	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		addLayout = (RelativeLayout) findViewById(R.id.marquee_add_layout);
		colorLayout = (RelativeLayout) findViewById(R.id.marquee_color_layout);
		speedBar = (SeekBar) findViewById(R.id.marquee_bar);
		tvFast = (TextView) findViewById(R.id.marquee_fast);
		tvMiddle = (TextView) findViewById(R.id.marquee_middle);
		tvSlow = (TextView) findViewById(R.id.marquee_slow);
		listView = (SwipeMenuListView) findViewById(R.id.marquee_lights);
		lineView = findViewById(R.id.marquee_line6);
		tvGroupName = (TextView) findViewById(R.id.marquee_groupname);
		tvRandom = (TextView) findViewById(R.id.marquee_random);
		cbSwitch = (CheckBox) findViewById(R.id.marquee_switch);
		addGroupLayout = (RelativeLayout) findViewById(R.id.marquee_addgroup_layout);
		setTitle();
	}

	private void setTitle() {
		tvTitle.setText(R.string.marquee);
		backBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 刷新灯列表
	 * @param isNeed
	 *    是否需要重新获取信息
	 */
	private void refreshListView(boolean isNeed) {
//		if (mPisMarquee != null) {
//			// if (null == mAdapter) {
//			mAdapter = new MarqueeAdapter(this, mPisMarquee.getAllLights());
//			listView.setAdapter(mAdapter);
//			// } else {
//			// mAdapter.setList(mPisMarquee.getAllLights());
//			// mAdapter.notifyDataSetChanged();
//			// }
//			if (mAdapter.getCount() > 0) {
//				lineView.setVisibility(View.VISIBLE);
//			} else {
//				lineView.setVisibility(View.GONE);
//			}
//			if (isNeed) {
//				mPisMarquee.getLights();
//			}
//		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
//			if (mPisMarquee != null) {
//				if (requestCode == Constant.REQUEST_CODE_MARQUEE_COLOR) {
//					mPisMarquee = (PISMarquee) PISManager.cacheMap
//							.get(mPisMarquee.getPISKeyString());
//					setTextOnRadom();
//					mPisMarquee.getSmartCellState();
//				} else if (requestCode == Constant.REQUEST_CODE_MARQUEE_LIGHT) {
//					mPisMarquee = (PISMarquee) PISManager.cacheMap
//							.get(mPisMarquee.getPISKeyString());
//					refreshListView(true);
//					mPisMarquee.getSmartCellState();
//				} else if (requestCode == Constant.REQUEST_CODE_MARQUEE_ADDGROUP) {
//					mPisMarquee = (PISMarquee) PISManager.cacheMap
//							.get(mPisMarquee.getPISKeyString());
//					setTextOnGroup();
//					mPisMarquee.getSmartCellState();
//				}
//				manager.upgradePISSmartCell(mPisMarquee);
//			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setTextOnRadom() {
//		if (mPisMarquee.getColor() != null) {
//			boolean random = mPisMarquee.getColor().isRandom;
//			if (random) {
//				tvRandom.setText(R.string.random);
//			} else {
//				tvRandom.setText(R.string.setting);
//			}
//		} else {
//			mPisMarquee.getLightColor();
//		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.marquee_addgroup_layout:
//			if (mPisMarquee != null) {
//				intent = new Intent(this, MarqueeLightActivity.class);
//				PISManager.cacheMap.put(mPisMarquee.getPISKeyString(),
//						mPisMarquee);
//				intent.putExtra("isGroup", true);
//				intent.putExtra(MessageModel.ACTIVITY_VALUE,
//						mPisMarquee.getPISKeyString());
//				startActivityForResult(intent,
//						Constant.REQUEST_CODE_MARQUEE_ADDGROUP);
//			}
			break;
		case R.id.marquee_add_layout:
//			if (mPisMarquee != null) {
//				intent = new Intent(this, MarqueeLightActivity.class);
//				PISManager.cacheMap.put(mPisMarquee.getPISKeyString(),
//						mPisMarquee);
//				intent.putExtra(MessageModel.ACTIVITY_VALUE,
//						mPisMarquee.getPISKeyString());
//				startActivityForResult(intent,
//						Constant.REQUEST_CODE_MARQUEE_LIGHT);
//			}
			break;

		case R.id.marquee_color_layout:
//			if (mPisMarquee != null) {
//				intent = new Intent(this, MarqueeColorActivity.class);
//				PISManager.cacheMap.put(mPisMarquee.getPISKeyString(),
//						mPisMarquee);
//				intent.putExtra(MessageModel.ACTIVITY_VALUE,
//						mPisMarquee.getPISKeyString());
//				startActivityForResult(intent,
//						Constant.REQUEST_CODE_MARQUEE_COLOR);
//			}
			break;
		}
		if (intent != null) {
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
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
//		if (pis.mT1 == PISConstantDefine.PIS_SMART_CELL_T1
//				&& pis.mT2 == PISConstantDefine.PIS_SMART_CELL_T2) {
//			if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_TIME_GET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					setProgressOnSeekBar();
//				}
//			} else if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_DEV_GET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					refreshListView(false);
//				}
//			} else if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_TIME_SET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					mPisMarquee.getSmartCellState();
//					ToastUtils.showToast(this, R.string.marquee_time_success);
//				} else {
//					ToastUtils.showToast(this, R.string.marquee_time_failed);
//					setProgressOnSeekBar();
//				}
//			} else if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_DEV_DEL) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					ToastUtils.showToast(this, R.string.del_smart_succ);
//					refreshListView(false);
//					mPisMarquee.getSmartCellState();
//				} else {
//					ToastUtils.showToast(this, R.string.del_smart_fail);
//				}
//			} else if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_GROUP_GET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					setTextOnGroup();
//				}
//			} else if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_COLOR_GET) {
//				setTextOnRadom();
//			} else if (reqType == PISMarquee.PIS_CMD_SMART_GET_STAT) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					cbSwitch.setChecked(mPisMarquee.getIsRun());
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
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		if (pis.mT1 == PISConstantDefine.PIS_BLUETOOTH_LIGHT_T1
//				&& pis.mT2 == PISConstantDefine.PIS_BLUETOOTH_LIGHT_T2) {
//			boolean exist = false;
//			if (mAdapter != null) {
//				List<PISBase> bases = mAdapter.getLights();
//				if (bases != null && bases.size() > 0) {
//					for (PISBase base : bases) {
//						if (pis.getPISKeyString()
//								.equals(base.getPISKeyString())
//								|| (!TextUtils.isEmpty(pis.macAddr)
//										&& !TextUtils.isEmpty(base.macAddr) && pis.macAddr
//											.equals(base.macAddr))) {
//							exist = true;
//							break;
//						}
//					}
//				}
//			}
//			if (mPisMarquee.isIn(pis.macAddr) && !exist) {
//				refreshListView(false);
//			}
//		}
//	}
}
