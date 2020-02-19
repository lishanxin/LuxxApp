package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinCenter;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISBlueLinker;
//import com.senink.seninkapp.core.PISBlueLinker.PISWifiInfor;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.BlueLinkerListView;

/**
 * 用于修改等分组和某分组下的设备
 * 
 * @author zhaojunfeng
 * @date 2015-08-25
 */
public class WifiActivity extends BaseActivity implements View.OnClickListener {
	private final static String TAG = "WifiActivity";
	// 界面跳转时的请求码
	public final static int REQUEST_CODE = 2000;
	// 更换网络时，message发送超时的消息
	public final static int MSG_TIMEOUT = 120000;
	// // 加载框
	// private ImageView iv;
	// // 动画
	// private AnimationDrawable anima;
	// // 加载框的布局
	// private RelativeLayout animaLayout;
	// 标题名称
	private TextView tvTitle;
	// 网络连接图标
	private ImageView ivIcon;
	// 标题的中的返回按钮
	private Button backBtn;
	// wifi列表
	private BlueLinkerListView listView;
	// wifi名称
	private TextView tvWifiName;
	// ip地址
	private TextView tvIPAddr;
	// 网关ip
	private TextView tvIPGateway;
	// 子网掩码
	private TextView tvSubMask;
	// 其他网关的按钮
	private Button otherBtn;
	// 适配器
//	private WifiInforAdapter adapter;
	// wifi列表
//	private List<PISWifiInfor> wifiInfors;
	// 网关类
	private PISXinCenter linker;
	// wifi信号最大值
	private final static int MAX_SINGAL = 5;

	private ProgressDialog dialog;
	// 当前查询wifi中第n个信息
	private int currentIndex = 0;
	private PISManager manager;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 更新wifi列表
			case MessageModel.MSG_WIFIS_UPDATE:
				// hideLoadingDialog();
//				hideProgressDialog();
//				if (null == adapter) {
//					adapter = new WifiInforAdapter(WifiActivity.this);
//					listView.setAdapter(adapter);
//				} else {
//					adapter.notifyDataSetChanged();
//				}
				break;
			case MessageModel.MSG_WIFI_TIMEOUT:
				// hideLoadingDialog();
				hideProgressDialog();
				ToastUtils.showToast(WifiActivity.this, R.string.wifi_input_failed);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi);
		manager = PISManager.getInstance();
		initView();
		setData();
		setView();
		setListener();
	}

	/**
	 * 显示加载框
	 */
	// private void showLoadingDialog() {
	// if (null == anima) {
	// anima = (AnimationDrawable) iv.getBackground();
	// }
	// anima.start();
	// animaLayout.setVisibility(View.VISIBLE);
	// }

	private void showProgressDialog(int resId) {
		// dialog = Utils.createProgressDialog(WifiActivity.this, resId);
		// dialog.show();
	}

	/**
	 * 加载框是否显示
	 * 
	 * @return
	 */
	// private boolean isLoading() {
	// boolean isShown = false;
	// if (animaLayout.getVisibility() == View.VISIBLE) {
	// isShown = true;
	// }
	// return isShown;
	// }

	@Override
	public void onBackPressed() {
		// if (dialog.isShowing()) {
		// hideLoadingDialog();
		// hideProgressDialog();
		// } else {
		backBtn.performClick();
		// }
	}

	/**
	 * 隐藏加载框
	 */
	// private void hideLoadingDialog() {
	// if (anima != null) {
	// anima.stop();
	// animaLayout.setVisibility(View.GONE);
	// }
	// }

	private void setView() {
		if (linker != null) {
			setWifiName();
//			if (linker.mGateWayInfor != null) {
//				setGateWayViews();
//			}
		}
	}

	/**
	 * 设置网络连接的名称和图标
	 */
	private void setWifiName() {
//		if (linker.netWorkType == 0) {
//			tvWifiName.setText(R.string.wire);
//			ivIcon.setBackgroundResource(R.drawable.wire_connect);
//		} else {
//			tvWifiName.setText(null == linker.SSID ? "" : linker.SSID);
//			ivIcon.setBackgroundResource(R.drawable.wifi_3);
//		}
	}

	private void setGateWayViews() {
		String ip = "";
		String gatewayIp = "";
		String submask = "";
//		if (linker != null && linker.mGateWayInfor != null) {
//			if (linker.mGateWayInfor.iPAddress != null) {
//				ip = WifiActivity.this.getString(R.string.wifi_net_ip,
//						linker.mGateWayInfor.iPAddress);
//			} else {
//				ip = WifiActivity.this.getString(R.string.wifi_net_ip, "");
//			}
//			if (linker.mGateWayInfor.gatewayAddress != null) {
//				gatewayIp = WifiActivity.this.getString(
//						R.string.wifi_gateway_ip,
//						linker.mGateWayInfor.gatewayAddress);
//			} else {
//				gatewayIp = WifiActivity.this.getString(
//						R.string.wifi_gateway_ip, "");
//			}
//			if (linker.mGateWayInfor.subnetMask != null) {
//				submask = WifiActivity.this.getString(R.string.wifi_submask,
//						linker.mGateWayInfor.subnetMask);
//			} else {
//				submask = WifiActivity.this
//						.getString(R.string.wifi_submask, "");
//			}
//		}
		tvIPAddr.setText(ip);
		tvIPGateway.setText(gatewayIp);
		tvSubMask.setText(submask);
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (getIntent() != null
				&& getIntent().getStringExtra(MessageModel.ACTIVITY_VALUE) != null) {
			String value = getIntent().getStringExtra(
					MessageModel.ACTIVITY_VALUE);
//			PISBase base = PISManager.cacheMap.get(value);
//			if (base != null && base instanceof PISBlueLinker) {
//				linker = (PISXinCenter) base;
//				refresh(false);
//				// showLoadingDialog();
//				showProgressDialog(R.string.wifi_loading_data);
//			}
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		otherBtn.setOnClickListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				if (linker != null && wifiInfors != null) {
//					Intent intent = new Intent(WifiActivity.this,
//							WifiConnectActivity.class);
//					PISManager.cacheMap.put(linker.getPISKeyString(), linker);
//					intent.putExtra(MessageModel.ACTIVITY_VALUE,
//							linker.getPISKeyString());
//					intent.putExtra("ssid", wifiInfors.get(position).wifiName);
//					WifiActivity.this.startActivityForResult(intent,
//							REQUEST_CODE);
//					WifiActivity.this.overridePendingTransition(
//							R.anim.anim_in_from_right, R.anim.anim_out_to_left);
//				}
			}

		});
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		tvIPAddr = (TextView) findViewById(R.id.wifi_net_ip);
		tvIPGateway = (TextView) findViewById(R.id.wifi_gateway_ip);
		tvWifiName = (TextView) findViewById(R.id.wifi_name);
		tvSubMask = (TextView) findViewById(R.id.wifi_submask);
		otherBtn = (Button) findViewById(R.id.wifi_other_network);
		ivIcon = (ImageView) findViewById(R.id.wifi_icon);
		listView = (BlueLinkerListView) findViewById(R.id.wifi_listview);
		// animaLayout = (RelativeLayout)
		// findViewById(R.id.wifi_loading_layout);
		// iv = (ImageView) findViewById(R.id.wifi_loading_anima);
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.wifi);
		backBtn.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i(TAG, "======onActivityResult=====start===requestCode = "
				+ requestCode + ",resultCode = " + resultCode);
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			boolean isReseted = data.getBooleanExtra(MessageModel.WIFI_REBOOT,
					false);
			LogUtils.i(TAG, "======onActivityResult========isReseted = "
					+ isReseted);
			if (isReseted) {
				showProgressDialog(R.string.wifi_loading_data);
				// showLoadingDialog();
				refresh(true);
				// mHandler.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// if (isLoading()) {
				// LogUtils.i(TAG, "======Runnable======== true");
				// mHandler.sendEmptyMessage(MessageModel.MSG_WIFI_TIMEOUT);
				// }
				// LogUtils.i(TAG, "======Runnable======== end");
				// }
				// }, MSG_TIMEOUT);
			}
		}
		LogUtils.i(TAG, "======onActivityResult===========");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			WifiActivity.this.finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.wifi_other_network:
			if (linker != null) {
				Intent intent = new Intent(WifiActivity.this,
						WifiOtherConnectActivity.class);
//				PISManager.cacheMap.put(linker.getPISKeyString(), linker);
				intent.putExtra(MessageModel.ACTIVITY_VALUE,
						linker.getPISKeyString());
				WifiActivity.this.startActivityForResult(intent, REQUEST_CODE);
				WifiActivity.this.overridePendingTransition(
						R.anim.anim_in_from_right, R.anim.anim_out_to_left);
			}
			break;
		}
	}

	/**
	 * 刷新界面信息（包含wifi信息，网关信息）
	 */
	private void refresh(boolean reset) {
		if (linker != null) {
//			linker.getAPInfo(false);
//			linker.getApCount(true);
//			// linker.getWifis(0,false);
//			if (reset) {
//				linker.getNetWorkType(false);
//			}
		}
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
	protected void onDestroy() {
		removeAllMessages();
		super.onDestroy();
	}

	/**
	 * 移除所有的消息
	 */
	private void removeAllMessages() {
		mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
		mHandler.removeMessages(MessageModel.MSG_WIFIS_UPDATE);
		mHandler.removeMessages(MessageModel.MSG_WIFI_TIMEOUT);
	}
//
//	public class WifiInforAdapter extends BaseAdapter {
//
//		private LayoutInflater inflater;
//
//		public WifiInforAdapter(Context context) {
//			this.inflater = LayoutInflater.from(context);
//		}
//
//		@Override
//		public int getCount() {
//			return wifiInfors.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return wifiInfors.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@SuppressLint("InflateParams")
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder = null;
//			if (null == convertView) {
//				holder = new ViewHolder();
//				convertView = inflater.inflate(R.layout.wifi_item, null);
//				holder.ivSignal = (ImageView) convertView
//						.findViewById(R.id.wifi_item_signal);
//				holder.tvName = (TextView) convertView
//						.findViewById(R.id.wifi_item_name);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			PISWifiInfor infor = wifiInfors.get(position);
//			if (infor != null) {
//				holder.tvName.setText(infor.wifiName == null ? ""
//						: infor.wifiName);
//				setBackground(infor.signal, holder.ivSignal);
//			}
//			return convertView;
//		}
//
//		private void setBackground(short signal, ImageView iv) {
//			if (signal > MAX_SINGAL * 2 / 3) {
//				iv.setBackgroundResource(R.drawable.wifi_3);
//			} else if (signal > MAX_SINGAL * 1 / 3) {
//				iv.setBackgroundResource(R.drawable.wifi_2);
//			} else if (signal < MAX_SINGAL * 1 / 3 && signal > 0) {
//				iv.setBackgroundResource(R.drawable.wifi_1);
//			} else {
//				iv.setBackgroundResource(R.drawable.wifi_0);
//			}
//		}
//
//		private class ViewHolder {
//			public TextView tvName;
//			public ImageView ivSignal;
//		}
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		if (netConnStatus == PISConstantDefine.PIPA_NET_STATUS_DISCONNECTED) {
//			// hideLoadingDialog();
//			hideProgressDialog();
//			ToastUtils.showToast(WifiActivity.this, R.string.disconnect);
//		}
//	}

	private void hideProgressDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

//	@Override
//	public void onFirmwareInfoChange() {
//
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//
//	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (reqType == PISBlueLinker.PIS_CMD_AP_COUNT_GET) {
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				currentIndex = 0;
//				if (linker.apCount > 0) {
//					linker.getWifis(currentIndex, false);
//				}
//			}
//		} else if (reqType == PISBlueLinker.PIS_CMD_AP_NAME_GET) {
//			// 获取wifi信息成功
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				if (linker != null) {
//					currentIndex++;
//					if (linker.apCount > currentIndex) {
//						linker.getWifis(currentIndex, false);
//					} else {
//						currentIndex = linker.apCount - 1;
//					}
//					wifiInfors = linker.apInfors;
//					if (null == wifiInfors) {
//						wifiInfors = new ArrayList<PISBlueLinker.PISWifiInfor>();
//					}
//					mHandler.sendEmptyMessage(MessageModel.MSG_WIFIS_UPDATE);
//				}
//			}
//		}else if(reqType == PISBlueLinker.PIS_CMD_AP_INFO_GET){
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				setGateWayViews();
//			}
//		}else if(reqType == PISBlueLinker.PIS_CMD_GW_NETWORK_TYPE_GET){
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				setWifiName();
//			}
//		}
//	}
}