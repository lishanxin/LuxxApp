package net.senink.seninkapp.ui.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.activity.MarqueeActivity;
import net.senink.seninkapp.ui.activity.TimerListActivity;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.smart.SmartMainActivity.SmartListAdater.Holder;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;
import net.senink.seninkapp.ui.view.TopNavArea;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

public class SmartMainActivity extends BaseActivity {
	private TopNavArea topNavArea;
	private View backBt, rightBt;
	private ListView mListView;
	private PullToRefreshListView refreshListView;
	public final int COMPLETE = 1000;
	public final int REFRESH = 2000;
	boolean isRefresh = false;
	boolean isDelSucc = false;
	SmartListAdater adater;
	Map<String, Bitmap> iconMap = new HashMap<String, Bitmap>();
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COMPLETE:
				refreshListView.onRefreshComplete();
				sendEmptyMessage(REFRESH);
				break;
			case REFRESH:
				isRefresh = true;
//				List<PISBase> list = makeSmartItem();
//				if (adater == null) {
//					adater = new SmartListAdater(SmartMainActivity.this,
//							list);
//					mListView.setAdapter(adater);
//				} else {
//					adater.addData(list);
//				}
				isRefresh = false;
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smart_main_activity);
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		PISManager.getInstance().setOnPISChangedListener(this);
//		PISManager.getInstance().setOnPipaRequestStatusListener(this);
		refreshOperate(1);
	}

	long preTime = 0;

	private void refreshOperate(int i) {
		// Log.w("jj", "ii==>" + i);
		if (!isRefresh && System.currentTimeMillis() - preTime >= 1000
				|| isDelSucc) {
			// Log.e("jj", "ii==>" + i);
			isDelSucc = false;
			preTime = System.currentTimeMillis();
			handler.removeMessages(REFRESH);
			handler.sendEmptyMessageDelayed(REFRESH, 0);

		}
	}

	private void initView() {
		topNavArea = (TopNavArea) findViewById(R.id.icon_search_top);
		topNavArea.setTitle(R.string.smart_title);
		backBt = findViewById(R.id.back);
		backBt.setVisibility(View.GONE);
		rightBt = findViewById(R.id.title_btn);
		rightBt.setVisibility(View.GONE);
		refreshListView = (PullToRefreshListView) findViewById(R.id.smart_list);
		refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		mListView = refreshListView.getRefreshableView();
		refreshListView
		.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(
					PullToRefreshBase<ListView> refreshView) {
				PISManager.getInstance().DiscoverAll();
				handler.sendEmptyMessageDelayed(COMPLETE, 2000);
			}

		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				try {
					Holder holder = (Holder) view.getTag();
//					PISSmartCell smartItem = (PISSmartCell) holder.icon
//							.getTag();
//					Intent intent = new Intent();
//					if (smartItem != null) {
//						if (PISSmartCell.GUID_PARENT_CONTROL.equals(smartItem
//								.getGUID())) {
//							intent.setClass(SmartMainActivity.this,
//									ParentsControl.class);
//							intent.putExtra(HomeActivity.VALUE_KEY,
//									smartItem.getPISKeyString());
//						} else if (PISSmartCell.GUID_FLOW_CONTROL
//								.equals(smartItem.getGUID())) {
//							intent.setClass(SmartMainActivity.this,
//									LiuLiangControlDeviceActivity.class);
//							intent.putExtra(HomeActivity.VALUE_KEY,
//									smartItem.getPISKeyString());
//						} else if (PISSmartCell.GUID_BANDWIDTH_CONTROL
//								.equals(smartItem.getGUID())) {
//							intent.setClass(SmartMainActivity.this,
//									KuanDaiControlDeviceActivity.class);
//							intent.putExtra(HomeActivity.VALUE_KEY,
//									smartItem.getPISKeyString());
//						} else if (PISSmartCell.GUID_VIDEO_SPEEDUP
//								.equals(smartItem.getGUID())) {
//						} else if (PISSmartCell.GUID_GAME_SPEEDUP
//								.equals(smartItem.getGUID())) {
//						} else if (PISSmartCell.GUID_AD_INTERCEPT
//								.equals(smartItem.getGUID())) {
//						} else if (PISSmartCell.GUID_TIMER_GWCONFIG
//								.equals(smartItem.getGUID())
//								|| PISSmartCell.GUID_TIMER_SWITCH
//										.equals(smartItem.getGUID())) {
//							intent.setClass(SmartMainActivity.this,
//									TimerListActivity.class);
//							PISManager.cacheMap.put(
//									smartItem.getPISKeyString(), smartItem);
//							intent.putExtra(MessageModel.ACTIVITY_VALUE,
//									smartItem.getPISKeyString());
//						} else if (PISSmartCell.GUID_MARQUEE_LIGHT
//								.equals(smartItem.getGUID())) {
//							intent.setClass(SmartMainActivity.this,
//									MarqueeActivity.class);
//							PISManager.cacheMap.put(
//									smartItem.getPISKeyString(), smartItem);
//							intent.putExtra(MessageModel.ACTIVITY_VALUE,
//									smartItem.getPISKeyString());
//						}
//						startActivity(intent);
//						overridePendingTransition(R.anim.anim_in_from_right,
//								R.anim.anim_out_to_left);
//					} else {
//						intent.setClass(SmartMainActivity.this,
//								AddIntallSmartActivity.class);
//						intent.setClass(SmartMainActivity.this,
//								AddIntallSmartActivity.class);
//						if (netWorkConnected()) {
//							startActivity(intent);
//							overridePendingTransition(
//									R.anim.anim_in_from_right,
//									R.anim.anim_out_to_left);
//						} else {
//							showErrorDialogInfo(getString(R.string.net_link_alert));
//						}
//					}
				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
			}

		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int arg2, long arg3) {
				Holder holder = (Holder) view.getTag();
//				PISSmartCell smartItem = (PISSmartCell) holder.icon.getTag();
//				deleteAlert(PISManager.getInstance()
//						.getRunningOnPisDeviceObject(smartItem), smartItem
//						.getInstanceID());
				return true;
			}

		});
	}

	public class SmartListAdater extends BaseAdapter {
		@SuppressLint("UseSparseArrays")
		private Map<Integer, List<PISBase>> map = new HashMap<Integer, List<PISBase>>();

		public SmartListAdater(Context context, List<PISBase> smartItems) {
//			List<PISBase> newBase = new ArrayList<PISBase>();
//			newBase.addAll(smartItems);
//			PISBase smartCell = new PISBase();
//			newBase.add(smartCell);
//			int N = newBase.size();
//			int rows = N % 3 == 0 ? N / 3 : N / 3 + 1;
//			for (int i = 0; i < rows; i++) {
//				if ((3 * i + 3) <= N) {
//					map.put(i, newBase.subList(3 * i, 3 * i + 3));
//				} else if ((3 * i + 3) > N) {
//					map.put(i, newBase.subList(3 * i, N));
//				}
//			}
		}

		public void addData(List<PISBase> smartItems) {
			map.clear();
			List<PISBase> newBase = new ArrayList<PISBase>();
			newBase.addAll(smartItems);
//			PISBase smartCell = new PISBase();
//			newBase.add(smartCell);
//			int N = newBase.size();
//			int rows = N % 3 == 0 ? N / 3 : N / 3 + 1;
//			for (int i = 0; i < rows; i++) {
//				if ((3 * i + 3) <= N) {
//					map.put(i, newBase.subList(3 * i, 3 * i + 3));
//				} else if ((3 * i + 3) > N) {
//					map.put(i, newBase.subList(3 * i, N));
//				}
//			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return map.size();
		}

		@Override
		public List<PISBase> getItem(int position) {
			List<PISBase> bases = null;
			try {
				bases = map.get(position);
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
			return bases;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView1, ViewGroup parent) {
			LinearLayout layout = null;
			if (convertView1 == null) {
				layout = (LinearLayout) LayoutInflater.from(
						SmartMainActivity.this).inflate(
						R.layout.smart_list_container, null);
				convertView1 = layout;
			} else {
				layout = (LinearLayout) convertView1;
			}
			layout.removeAllViews();
			List<PISBase> bases = getItem(position);
			for (int i = 0; i < bases.size(); i++) {
				Holder holder = new Holder();
				PISBase pisBase = bases.get(i);
				View convertView = LayoutInflater.from(SmartMainActivity.this)
						.inflate(R.layout.smart_list_item, null);
				holder.switchCk = (CheckBox) convertView
						.findViewById(R.id.smart_switch);
				Log.i("test", "pisBase.mStatus==>" + pisBase.getStatus());
				if (pisBase.getStatus() == PISBase.SERVICE_STATUS_OFFLINE) {
					convertView.setAlpha(0.3f);
					holder.switchCk.setEnabled(false);
				} else {
					convertView.setAlpha(1f);
					holder.switchCk.setEnabled(true);
				}

				layout.addView(convertView);
				setViewClick(convertView);
				setViewLongClick(convertView);
				holder.nameTv = (TextView) convertView.findViewById(R.id.name);

				holder.switchCk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						HttpUtils.networkStateusOK(SmartMainActivity.this);
						CheckBox switchCk = (CheckBox) v;
//						PISSmartCell smartItem = (PISSmartCell) v.getTag();
//						smartItem.switchState = switchCk.isChecked();
//						if (!smartItem.switchState) {
//							smartItem.stop();
//						} else {
//							smartItem.start();
//						}
					}
				});
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);

				convertView.setTag(pisBase);
//				if (pisBase != null && pisBase instanceof PISSmartCell) {
//					PISSmartCell smartItem = (PISSmartCell) pisBase;
//					holder.switchCk.setTag(smartItem);
//					holder.switchCk.setChecked(smartItem.switchState);
//					holder.nameTv.setText(smartItem.getTitle());
//					String GUID = smartItem.getGUID();
//					int resId = -1;
//					if (GUID != null) {
//						if (PISSmartCell.GUID_PARENT_CONTROL.equals(GUID)) {
//							resId = R.drawable.icon_zhineng01;
//						} else if (PISSmartCell.GUID_FLOW_CONTROL.equals(GUID)) {
//							resId = R.drawable.icon_zhineng02;
//						} else if (PISSmartCell.GUID_BANDWIDTH_CONTROL
//								.equals(GUID)) {
//							resId = R.drawable.icon_zhineng03;
//						} else if (PISSmartCell.GUID_VIDEO_SPEEDUP.equals(GUID)) {
//							resId = R.drawable.icon_zhineng04;
//						} else if (PISSmartCell.GUID_GAME_SPEEDUP.equals(GUID)) {
//							resId = R.drawable.icon_zhineng05;
//						} else if (PISSmartCell.GUID_AD_INTERCEPT.equals(GUID)) {
//							resId = R.drawable.icon_zhineng06;
//						} else if (PISSmartCell.GUID_MARQUEE_LIGHT.equals(GUID)) {
//							resId = R.drawable.icon_timer_marquee;
//						} else if (PISSmartCell.GUID_COLOR_LIGHT.equals(GUID)) {
//							resId = R.drawable.icon_timer_colorlight;
//						} else if (PISSmartCell.GUID_TIMER_GWCONFIG
//								.equals(GUID)
//								|| PISSmartCell.GUID_TIMER_SWITCH.equals(GUID)) {
//							resId = R.drawable.icon_timer_timer;
//						} else {
//							resId = R.drawable.ic_launcher;
//						}
//
//					} else {
//						resId = R.drawable.icon_zhineng07;
//					}
//					holder.switchCk.setVisibility(View.VISIBLE);
//					try {
//						Bitmap bit = iconMap.get(smartItem.getGUID());
//						if (bit == null ) {
//							bit = HttpUtils.getImage(convertView.getContext(),
//									smartItem.getGUID(), null);
//							if (bit != null) {
//								int radius = (bit.getWidth() < bit.getHeight() ? bit
//										.getWidth() : bit.getHeight()) / 2;
//								bit = SDCardUtils.getCroppedRoundBitmap(bit,
//										radius);
//								iconMap.put(smartItem.getGUID(), bit);
//							}
//						}
//
//						if (bit != null) {
//							holder.icon.setImageBitmap(bit);
//						} else {
//							holder.icon.setImageResource(resId);
//						}
//						holder.icon.setTag(pisBase);
//						setListener(holder.icon);
//					} catch (Exception e) {
//						net.senink.seninkapp
//					}
//
//					holder.icon.setTag(smartItem);
//				} else {
//					holder.switchCk.setVisibility(View.GONE);
//					holder.icon.setImageResource(R.drawable.icon_zhineng07);
//					holder.nameTv.setText(getResources().getString(
//							R.string.add_lebal));
//				}
			}

			return convertView1;
		}

		private void setListener(ImageView icon) {
			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v.getTag() != null && v.getTag() instanceof PISBase) {
						PISBase base = (PISBase) v.getTag();
//						if (base.mStatus != PISConstantDefine.SERVICE_STATUS_ONLINE) {
//							return;
//						}
//						if (base.mT1 == PISConstantDefine.PIS_SMART_CELL_T1
//								&& base.mT2 == PISConstantDefine.PIS_SMART_CELL_T2) {
//							PISSmartCell cell = (PISSmartCell) base;
//							PISManager.cacheMap.put(base.getPISKeyString(),
//									cell);
//							if (PISSmartCell.GUID_TIMER_GWCONFIG.equals(cell
//									.getGUID())
//									|| PISSmartCell.GUID_TIMER_SWITCH
//											.equals(cell.getGUID())) {
//								Intent intent = new Intent(
//										SmartMainActivity.this,
//										TimerListActivity.class);
//								intent.putExtra(MessageModel.ACTIVITY_VALUE,
//										cell.getPISKeyString());
//								startActivity(intent);
//								overridePendingTransition(
//										R.anim.anim_in_from_right,
//										R.anim.anim_out_to_left);
//							} else if (PISSmartCell.GUID_MARQUEE_LIGHT
//									.equals(cell.getGUID())) {
//								Intent intent = new Intent(SmartMainActivity.this,
//										MarqueeActivity.class);
//								PISManager.cacheMap.put(cell.getPISKeyString(),
//										cell);
//								intent.putExtra(MessageModel.ACTIVITY_VALUE,
//										cell.getPISKeyString());
//								startActivity(intent);
//								overridePendingTransition(
//										R.anim.anim_in_from_right,
//										R.anim.anim_out_to_left);
//							}
//						}
					}
				}
			});
		}

		public void setViewClick(View convertView) {
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
//					if (view.getTag() instanceof PISSmartCell) {
//						try {
//							PISSmartCell smartItem = (PISSmartCell) view
//									.getTag();
//							Intent intent = new Intent();
//							if (smartItem != null) {
//
//								if (PISSmartCell.GUID_PARENT_CONTROL
//										.equals(smartItem.getGUID())) {
//									intent.setClass(SmartMainActivity.this,
//											ParentsControl.class);
//									intent.putExtra(HomeActivity.VALUE_KEY,
//											smartItem.getPISKeyString());
//								} else if (PISSmartCell.GUID_FLOW_CONTROL
//										.equals(smartItem.getGUID())) {
//									intent.setClass(SmartMainActivity.this,
//											LiuLiangControlDeviceActivity.class);
//									intent.putExtra(HomeActivity.VALUE_KEY,
//											smartItem.getPISKeyString());
//								} else if (PISSmartCell.GUID_BANDWIDTH_CONTROL
//										.equals(smartItem.getGUID())) {
//									intent.setClass(SmartMainActivity.this,
//											KuanDaiControlDeviceActivity.class);
//									intent.putExtra(HomeActivity.VALUE_KEY,
//											smartItem.getPISKeyString());
//								} else if (PISSmartCell.GUID_VIDEO_SPEEDUP
//										.equals(smartItem.getGUID())) {
//								} else if (PISSmartCell.GUID_GAME_SPEEDUP
//										.equals(smartItem.getGUID())) {
//								} else if (PISSmartCell.GUID_AD_INTERCEPT
//										.equals(smartItem.getGUID())) {
//								}
//								startActivity(intent);
//							} else {
//								intent.setClass(SmartMainActivity.this,
//										AddIntallSmartActivity.class);
//								if (netWorkConnected()) {
//									startActivity(intent);
//								} else {
//									showErrorDialogInfo(getString(R.string.net_link_alert));
//								}
//							}
//						} catch (Exception e) {
//							net.senink.seninkapp
//						}
//
//					} else {
//						Intent intent = new Intent();
//						intent.setClass(SmartMainActivity.this,
//								AddIntallSmartActivity.class);
//						if (netWorkConnected()) {
//							startActivity(intent);
//						} else {
//							showErrorDialogInfo(getString(R.string.net_link_alert));
//						}
//					}

				}
			});
		}

		private void setViewLongClick(View convertView) {
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View view) {
//					if (view.getTag() instanceof PISSmartCell) {
//						PISSmartCell smartItem = (PISSmartCell) view.getTag();
//						if (PISManager.getInstance().isRunningOnPisMcmObject(
//								smartItem)) {
//							deleteAlert(PISManager.getInstance()
//									.getPisMcmObject(), smartItem
//									.getInstanceID());
//						} else {
//							deleteAlert(PISManager.getInstance()
//									.getRunningOnPisDeviceObject(smartItem),
//									smartItem.getInstanceID());
//						}
//					}

					return true;
				}
			});
		}

		class Holder {
			TextView nameTv;
			CheckBox switchCk;
			ImageView icon;
		}

	}

//	public List<PISBase> makeSmartItem() {
//		return PISManager.getInstance().getPisSmartCellList();
//	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//		refreshOperate(2);
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis == null) {
//			return;
//		}
//		switch (pis.mPisType) {
//		case PISConstantDefine.PIS_TYPE_SMART_CELL:
//			if ((PISSmartCell.PIS_CMD_SMART_STOP == reqType || PISSmartCell.PIS_CMD_SMART_START == reqType)
//					&& pis instanceof PISSmartCell) {
//				PISSmartCell pc = (PISSmartCell) pis;
//				pc.switchState = pc.getIsRun();
//				if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					Toast.makeText(SmartMainActivity.this,
//							R.string.link_timeout, Toast.LENGTH_SHORT).show();
//				}
//			}
//			break;
//		default:
//			break;
//		}
//
//		if (pis instanceof PISSmartCell) {
//			switch (reqType) {
//			case PISSmartCell.PIS_CMD_SMART_GET_STAT:
//			case PISSmartCell.PIS_CMD_SMART_STOP:
//			case PISSmartCell.PIS_CMD_SMART_START:
//				refreshOperate(3);
//				break;
//			}
//		} else if (pis instanceof PISDevice) {
//			switch (reqType) {
//			case PISDevice.PIS_CMD_DEVICE_INS_SMART:
//				break;
//			case PISDevice.PIS_CMD_DEVICE_UNS_SMART:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {// 成功
//					Toast.makeText(SmartMainActivity.this,
//							R.string.del_smart_succ, Toast.LENGTH_SHORT).show();
//					isDelSucc = true;
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					Toast.makeText(SmartMainActivity.this,
//							R.string.del_smart_fail, Toast.LENGTH_SHORT).show();
//				}
//				break;
//			}
//			refreshOperate(4);
//		} else if (pis instanceof PISMCSManager) {
//			switch (reqType) {
//			case PISMCSManager.PIS_CMD_DEVICE_INS_SMART:
//				break;
//			case PISMCSManager.PIS_CMD_DEVICE_UNS_SMART:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {// 成功
//					Toast.makeText(SmartMainActivity.this,
//							R.string.del_smart_succ, Toast.LENGTH_SHORT).show();
//					isDelSucc = true;
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					Toast.makeText(SmartMainActivity.this,
//							R.string.del_smart_fail, Toast.LENGTH_SHORT).show();
//				}
//				break;
//			}
//			refreshOperate(5);
//		}
//
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		refreshOperate(6);
//
//	}
//
//	@Override
//	public void onFirmwareInfoChange() {
//		refreshOperate(7);
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//
//		if (pis instanceof PISSmartCell) {
//			PISSmartCell pc = (PISSmartCell) pis;
//			pc.switchState = pc.getIsRun();
//		}
//
//		refreshOperate(8);
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//		refreshOperate(9);
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		refreshOperate(10);
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(REFRESH);
		handler.removeMessages(COMPLETE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeMessages(REFRESH);
		handler.removeMessages(COMPLETE);
//		PISManager.getInstance().setOnPISChangedListener(null);
//		PISManager.getInstance().setOnPipaRequestStatusListener(null);
	}

	@Override
	protected void onStop() {
		super.onStop();
		iconMap.clear();
	}

	private void deleteAlert(final PISBase obj, final String instanceId) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getResources().getString(R.string.del_smart_alert));
		builder.setTitle(getResources().getString(R.string.del_smart));
		builder.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// obj.uninstallSmartCell(instanceId);
						Log.i("xj", "instanceId==>" + instanceId + " obj==>"
								+ obj);
//						if (obj instanceof PISDevice) {
//							Log.i("xj", "111instanceId==>" + instanceId);
//							PISDevice pd = ((PISDevice) obj);
//							pd.uninstallSmartCell(instanceId);
//						} else if (obj instanceof PISMCSManager) {
//							Log.i("xj", "2222instanceId==>" + instanceId);
//							((PISMCSManager) obj)
//									.uninstallSmartCell(instanceId);
//						}
					}
				});

		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}

	private void showErrorDialogInfo(final String info) {
		AlertDialog.Builder scanBuilder = new AlertDialog.Builder(
				SmartMainActivity.this);
		scanBuilder.setTitle(R.string.operator_error);
		scanBuilder.setMessage(info);
		scanBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		scanBuilder.show();
	}

	private boolean netWorkConnected() {
		boolean netStateus = false;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) SmartMainActivity.this
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isAvailable() && networkInfo.isConnected()) {
					netStateus = true;
				}
			}
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
		return netStateus;
	}

	class LoadIcon implements Runnable {
		private String iconUrl;
		private ImageView imageView;
		Bitmap bit;
		String iconName;

		public LoadIcon(ImageView imageView, String iconName, String iconUrl) {
			this.iconUrl = iconUrl;
			this.imageView = imageView;
			this.iconName = iconName;
		}

		@Override
		public void run() {
			try {
				bit = HttpUtils.getImage(SmartMainActivity.this, iconName,
						iconUrl);
				int radius = (bit.getWidth() < bit.getHeight() ? bit.getWidth()
						: bit.getHeight()) / 2;
				bit = SDCardUtils.getCroppedRoundBitmap(bit, radius);
				handler.post(new Runnable() {

					@Override
					public void run() {
						imageView.setImageBitmap(bit);
						imageView.setContentDescription(iconName);
					}
				});
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
		}

	}
}
