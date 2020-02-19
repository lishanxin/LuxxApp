package net.senink.seninkapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinCenter;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISBlueLinker;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISManager;
import net.senink.seninkapp.ui.activity.LightRGBDetailActivity;
import net.senink.seninkapp.ui.activity.WifiActivity;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.util.SortUtils;
import net.senink.seninkapp.ui.view.BlueLinkerListView;

/**
 * 蓝牙网关的界面
 * 
 * @author zhaojunfeng
 * @date 2015-08-24
 * 
 */
public class BlueLinkerFragment extends Fragment implements OnClickListener {

	public static final String TAG = "BlueLinkerFragment";

	private View mRootView;
	// 显示连接器的组件
	private BlueLinkerListView deviceListView;
	// 网关名称
	private TextView tvName;
	// 版本
	private TextView tvVersion;
	// mac地址
	private TextView tvMac;
	// 网络类型
	private TextView tvNetWorkType;
	// 网络名称
	private Button netWorkNameBtn;
	// 缓存服务开关
	private CheckBox cbBuffer;
	// 列表组件的适配器
	private BlueLinkerAdapter deviceAdapter;
	// 主页的activity
	private HomeActivity activity;
	// 最近一次刷新界面的时间
	private long operateTime = 0;
	private PISManager manager;
	private PISDevice pisDevice;
	private PISXinCenter linker;
	private SparseArray<PISDevice> devices;

	// 是否获取到版本和mac地址
	public BlueLinkerFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		manager = PISManager.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.fragment_bluelinker,
					container, false);
			initView();
			setView();
			updateView();
			setListener();
		}
		return mRootView;
	}

	public void setView() {
		if (System.currentTimeMillis() - operateTime > 40 * 1000) {
//			pisDevice = manager.getBlueBlinker();
//			pisDevice = manager.PIServicesWithQuery()
//			if (pisDevice != null && pisDevice.getPIServices() != null
//					&& pisDevice.getPIServices().size() > 0) {
//				PISBase base = pisDevice.getPIServices().get(0);
//				if (base instanceof PISBlueLinker) {
//					linker = (PISBlueLinker) base;
//					linker.getBufferStatus(false);
//					linker.getNetWorkType(false);
//					setWifiView();
//				}
//			} else {
//				setWifiView();
//			}
//			refreshViews();
		}
	}

	/**
	 * 刷新列表
	 * @param serviceId
	 */
	public void refreshViews(int serviceId){
//		if (pisDevice != null && serviceId == pisDevice.mServiceID) {
//			updateView();
//		} else if (deviceAdapter != null) {
//			deviceAdapter.notifyDataSetChanged();
//		}
	}
	
	/**
	 * 设置缓存的开关状态
	 */
	public void setStatus(){
//		if (linker != null) {
//			cbBuffer.setChecked(linker.mOnOff);
//		}
	}
	
	private void updateView() {
//		if (pisDevice != null) {
//			if (!TextUtils.isEmpty(pisDevice.getMacAddr())){
//				if (activity != null) {
//					String mac = pisDevice.getMacAddr();
//					String version = pisDevice.getFwVersion();
//					if (mac != null) {
//						mac = activity.getString(R.string.bluelinker_mac, mac);
//					} else {
//						mac = activity.getString(R.string.bluelinker_mac, "");
//					}
//					if (version != null) {
//						version = activity.getString(
//								R.string.bluelinker_version, version);
//					} else {
//						version = activity.getString(
//								R.string.bluelinker_version, "");
//					}
//					tvMac.setText(mac);
//					tvVersion.setText(version);
//				} else {
//					tvVersion.setText(R.string.version);
//					tvMac.setText(R.string.mac);
//				}
//			}
//		} else {
//			tvVersion.setText(R.string.version);
//			tvMac.setText(R.string.mac);
//		}
//		if (linker != null && linker.mName != null) {
//			tvName.setText(linker.mName);
//		} else {
//			tvName.setText(R.string.home_gateway);
//		}
	}

	private void initView() {
		tvMac = (TextView) mRootView.findViewById(R.id.bluelinker_macaddress);
		tvName = (TextView) mRootView.findViewById(R.id.bluelinker_name);
		tvNetWorkType = (TextView) mRootView
				.findViewById(R.id.bluelinker_network_tip);
		netWorkNameBtn = (Button) mRootView
				.findViewById(R.id.bluelinker_network_name);
		tvVersion = (TextView) mRootView.findViewById(R.id.bluelinker_version);
		deviceListView = (BlueLinkerListView) mRootView
				.findViewById(R.id.bluelinker_listview);
		cbBuffer = (CheckBox) mRootView.findViewById(R.id.bluelinker_buffer);
	}

	/**
	 * 设置列表的监听器
	 */
	private void setListener() {
		deviceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (activity != null && devices != null
						&& devices.valueAt(position) != null) {
					PISDevice dev = devices.valueAt(position);
					if (dev.getPIServices() != null
							&& dev.getPIServices().size() > 0) {
						PISBase pisBase = dev.getPIServices().get(0);
						if (pisBase != null && pisBase instanceof PISXinCenter) {
							Intent intent = new Intent(activity,
									LightRGBDetailActivity.class);
//							PISManager.cacheMap.put(pisBase.getPISKeyString(),
//									pisBase);
							intent.putExtra(MessageModel.ACTIVITY_VALUE,
									pisBase.getPISKeyString());
							activity.startActivity(intent);
							activity.overridePendingTransition(
									R.anim.anim_in_from_right,
									R.anim.anim_out_to_left);
						}
					}
				}
			}
		});
		netWorkNameBtn.setOnClickListener(this);
		cbBuffer.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
//				if (linker != null) {
//					linker.setBufferStatus(isChecked, true);
//				}
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.activity = (HomeActivity) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DeviceController callback interface.");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void refreshViews() {
		if (linker != null) {
//			if (!PISManager.cacheMap.containsKey(linker.getPISKeyString())) {
//				PISManager.cacheMap.put(linker.getPISKeyString(), linker);
//			} else {
//				linker = (PISBlueLinker) PISManager.cacheMap.get(linker
//						.getPISKeyString());
//				setWifiView();
//			}
		}
//		devices = SortUtils.sortLightsOnBlueLinker(manager
//				.getPisDeviceList());
		if (null == deviceAdapter && activity != null) {
			deviceAdapter = new BlueLinkerAdapter(activity);
			deviceListView.setAdapter(deviceAdapter);
		} else if (null != deviceAdapter) {
			deviceAdapter.notifyDataSetChanged();
		}
	}

	public void setWifiView() {
		if (linker != null) {
			if (linker.getApLinkType() == 0) {
				tvNetWorkType.setText(R.string.network);
				netWorkNameBtn.setText(R.string.wire);
			} else {
				tvNetWorkType.setText(R.string.wifi);
				netWorkNameBtn.setText(linker.getSsid() == null ? "" : linker.getSsid());
			}
		} else {
			tvNetWorkType.setText(R.string.network);
			netWorkNameBtn.setText(R.string.wire);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bluelinker_network_name:
			if (linker != null) {
				Intent intent = new Intent(activity, WifiActivity.class);
//				PISManager.cacheMap.put(linker.getPISKeyString(), linker);
				intent.putExtra(MessageModel.ACTIVITY_VALUE,
						linker.getPISKeyString());
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.anim_in_from_right,
						R.anim.anim_out_to_left);
			}
			break;

		default:
			break;
		}
	}

	public class BlueLinkerAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public BlueLinkerAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return devices.size();
		}

		@Override
		public Object getItem(int position) {
			return devices.valueAt(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.bluelinker_item, null);
				holder.tvMacAddr = (TextView) convertView
						.findViewById(R.id.bluelinker_item_macaddress);
				holder.tvVersion = (TextView) convertView
						.findViewById(R.id.bluelinker_item_version);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.bluelinker_item_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			PISDevice device = devices.valueAt(position);
			if (device != null && device.getPIServices() != null) {
				PISBase base = device.getPIServices().get(0);
				holder.tvName.setText(base.getName() == null ? "" : base.getName());
				String version = "";
				String mac = "";
				if (null != device.getMacString()) {
					mac = activity.getString(R.string.bluelinker_mac,
							device.getMacString());
				} else {
					mac = activity.getString(R.string.bluelinker_mac, "");
				}
				if (null != device.getVersionString()) {
					version = activity.getString(R.string.bluelinker_version,
							device.getVersionString());
				} else {
					version = activity.getString(R.string.bluelinker_version,
							"");
				}
				holder.tvVersion.setText(version);
				holder.tvMacAddr.setText(mac);
			}
			return convertView;
		}

		private class ViewHolder {
			public TextView tvName;
			public TextView tvVersion;
			public TextView tvMacAddr;
		}
	}
}
