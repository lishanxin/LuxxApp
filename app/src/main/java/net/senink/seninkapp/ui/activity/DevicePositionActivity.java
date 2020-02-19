package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.struct.PipaServiceInfo;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.LocationName;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.view.DeviceListView;

/**
 * 设备位置的设置
 * 
 * @author zhaojunfeng
 * @date 2015-07-28
 */
public class DevicePositionActivity extends BaseActivity implements
		View.OnClickListener {
	// 加载框显示的最长时间
	private final static int SHOW_MAX_TIME = 60000;
	private static final int MSG_MODIFY_FAILED = 100;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	// 列表
	private DeviceListView listView;
	private ImageView ivLoading;
	private RelativeLayout loadingLayout;
	private AnimationDrawable anima;
	private PISBase infor;

	private PIServiceInfo serviceInfor;

	private PositionAdapter adapter;

	private LayoutInflater inflater;
	private MeshController controller;
	private PISManager manager;
	private MeshController.onFeedbackListener listener = new MeshController.onFeedbackListener() {

		@Override
		public void onNetSecurity() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDisconnected() {

		}

		@Override
		public void onConnected() {
		
		}
	};
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageModel.MSG_GET_LOCATIONS_SUCCESS:
//				adapter.setList(PISManager.getInstance().locations);
				adapter.notifyDataSetChanged();
				break;
			case MSG_MODIFY_FAILED:
				hideLoadingDialog();
				Toast.makeText(DevicePositionActivity.this,
						R.string.deviceposition_setting_failed,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deviceposition);
		inflater = LayoutInflater.from(this);
		controller = MeshController.getInstance(this);
		manager = PISManager.getInstance();
		initView();
		setData();
		setView();
		setListener();
	}

	/**
	 * 给组件赋值
	 */
	private void setView() {
//		adapter = new PositionAdapter(PISManager.locations);
		listView.setAdapter(adapter);
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (getIntent() != null
				&& getIntent().getStringExtra(MessageModel.ACTIVITY_VALUE) != null) {
			String key = getIntent()
					.getStringExtra(MessageModel.ACTIVITY_VALUE);
			if (key != null) {
//				infor = PISManager.cacheMap.get(key);
//				if (infor != null) {
//					serviceInfor = new PipaServiceInfo(infor);
//					LogUtils.i("Ryan",
//							"DevicePositionActivity-> setData():position = "
//									+ infor.mLocation);
//				}
			}
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (infor != null && serviceInfor != null) {
					if (position == 1) {

					} else {
						showLoadingDialog();
//						serviceInfor.mLocation = (byte) ((position - 2) & 0xff);
//						infor.setPISInfo(serviceInfor, true);
					}
				}
			}
		});
		listView.setonRefreshListener(new DeviceListView.OnRefreshListener() {

			@Override
			public void onStartRefresh() {

			}

			@Override
			public void onRefresh() {
				getLocations();
			}
		});
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		listView = (DeviceListView) findViewById(R.id.deviceposition_listview);
		ivLoading = (ImageView) findViewById(R.id.deviceposition_loading);
		loadingLayout = (RelativeLayout) findViewById(R.id.deviceposition_loading_layout);
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.position);
		backBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			DevicePositionActivity.this.finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (loadingLayout.getVisibility() == View.VISIBLE) {
			hideLoadingDialog();
		} else {
			backBtn.performClick();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (PISManager.locations == null || PISManager.locations.size() == 0) {
//			getLocations();
//		}
//		controller.setonFeedbackListener(listener);
//		manager.setOnPipaRequestStatusListener(this);
	}

	/**
	 * 显示加载动画
	 */
	private void showLoadingDialog() {
		mHandler.removeMessages(MSG_MODIFY_FAILED);
		if (null == anima) {
			anima = (AnimationDrawable) ivLoading.getBackground();
		}
		anima.start();
		loadingLayout.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageDelayed(MSG_MODIFY_FAILED,SHOW_MAX_TIME);
	}

	/**
	 * 显示加载动画
	 */
	private void hideLoadingDialog() {
		if (anima != null) {
			anima.stop();
		}
		loadingLayout.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		controller.setonFeedbackListener(null);
//		manager.setOnPipaRequestStatusListener(null);
	}

	/**
	 * 获取位置
	 */
	private void getLocations() {

		new Thread() {
			@Override
			public void run() {
//				PISManager.locations = HttpUtils
//						.getLocationAndSave(DevicePositionActivity.this);
//				mHandler.sendEmptyMessage(MessageModel.MSG_GET_LOCATIONS_SUCCESS);
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
		super.onDestroy();
	}

	private class PositionAdapter extends BaseAdapter {
		private List<LocationName> list;

		public PositionAdapter(List<LocationName> locations) {
			setList(locations);
		}

		private void setList(List<LocationName> locations) {
			LocationName location = new LocationName();
			location.name = DevicePositionActivity.this
					.getString(R.string.custom);
			if (null == locations) {
				list = new ArrayList<LocationName>();
				list.add(location);
			} else {
				list = new ArrayList<LocationName>(locations);
				list.add(0, location);
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
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
				convertView = inflater.inflate(R.layout.deviceposition_item,
						null);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.deviceposition_item_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			LocationName location = list.get(position);
			if (location != null && location.name != null) {
				holder.tvName.setText(location.name);
			} else {
				holder.tvName.setText("");
			}
			return convertView;
		}

		private class ViewHolder {
			public TextView tvName;
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
//		if (reqType == PISBase.PIS_CMD_SRVINFO_SET) {
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				mHandler.removeMessages(MSG_MODIFY_FAILED);
//				hideLoadingDialog();
//				Intent it = new Intent(MessageModel.ACTION_CHANGE_POSITION);
//				it.putExtra("position", infor.mLocation);
//				DevicePositionActivity.this.sendBroadcast(it);
//				PISManager.cacheMap.put(infor.getPISKeyString(), infor);
//				backBtn.performClick();
//			} else {
//				mHandler.removeMessages(MSG_MODIFY_FAILED);
//				mHandler.sendEmptyMessage(MSG_MODIFY_FAILED);
//			}
//		}
//	}
}