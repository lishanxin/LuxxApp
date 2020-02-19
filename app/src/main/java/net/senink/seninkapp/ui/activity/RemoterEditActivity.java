package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinRemoter;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISLightRemoter;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SortUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.RemoterListView;
import net.senink.seninkapp.ui.view.RemoterListView.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于修改遥控器的设备
 * 
 * @author zhaojunfeng
 * @date 2015-08-28
 */
public class RemoterEditActivity extends BaseActivity implements
		View.OnClickListener{
	private final static String TAG = "RemoterEditActivity";
	protected static final long REQUEST_TIME = 2000;
	// 加载框显示的最长时间
	private final static int SHOW_MAX_TIME = 30000;
	//设置键失败
	private static final int MSG_SET_KEYINFOR_FAILED = 100;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	// 加载框布局
	private RelativeLayout loadingLayout;
	// 蓝牙未连接提示的取消按钮
	private ImageView ivLoading;
	// 加载动画
	private AnimationDrawable anima;
	// 组列表
	private RemoterListView listView;
	// 适配器
	private RemoterInforAdapter adapter;
	// 遥控器基类
	private PISXinRemoter infor;
	// 键
	private int key = -1;
	// 备选中的设备名称
	private String deviceName;
	// 命令加载框
	// private ProgressDialog dialog = null;
	private PISManager manager = null;
	private List<PISBase> infors = new ArrayList<>();
	private LayoutInflater inflater;
	// 蓝牙管理器
	private MeshController controller;
	// 获取分组信息的类
	private PISMCSManager mcm;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SET_KEYINFOR_FAILED:
				mHandler.removeMessages(MSG_SET_KEYINFOR_FAILED);
				hideLoadingDialog();
				ToastUtils.showToast(RemoterEditActivity.this,
						R.string.setting_failed);
				break;
			case MessageModel.MSG_GET_DEVICES:
				if (manager != null) {
					mcm = manager.getMCSObject();
				}
				if (null == infors) {
					infors = new ArrayList<>();
				}

				ArrayList<Integer> pistypes = new ArrayList<Integer>();

				pistypes.add(PISConstantDefine.PIS_MAJOR_LIGHT | (PISConstantDefine.PIS_LIGHT_COLOR<<8));
				pistypes.add(PISConstantDefine.PIS_MAJOR_LIGHT | (PISConstantDefine.PIS_LIGHT_LIGHT<<8));
				pistypes.add(PISConstantDefine.PIS_MAJOR_MULTIMEDIA | (PISConstantDefine.PIS_MULTIMEDIA_COLOR<<8));

				List<PISBase> srvs;
				for (Integer i : pistypes){
					srvs = manager.PIServicesWithQuery(i, PISManager.EnumServicesQueryBaseonType);
					if (srvs != null && srvs.size() > 0){
						infors.addAll(srvs);
					}
					srvs = manager.PIGroupsWithQuery(i, PISManager.EnumGroupsQueryBaseonType);
					if (srvs != null && srvs.size() > 0)
						infors.addAll(srvs);
				}
				adapter.notifyDataSetChanged();
				LogUtils.i(TAG,
						"addDevice()========MessageModel.MSG_GET_DEVICES=========");
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remoter_edit);
		manager = PISManager.getInstance();
		inflater = LayoutInflater.from(this);
		controller = MeshController.getInstance(this);
		initView();
		setData();
		setListener();
		mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
	}

	/**
	 * 显示加载动画
	 */
	private void showLoadingDialog() {
		mHandler.removeMessages(MSG_SET_KEYINFOR_FAILED);
		if (null == anima) {
			anima = (AnimationDrawable) ivLoading.getBackground();
		}
		anima.start();
		loadingLayout.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageDelayed(MSG_SET_KEYINFOR_FAILED, SHOW_MAX_TIME);
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

	/**
	 * 获取传值
	 */
	private void setData() {
		mcm = manager.getMCSObject();
		Intent itent = getIntent();
		if (itent == null)
			throw new NullPointerException("Intent is null");
		String strKey = itent.getStringExtra(MessageModel.PISBASE_KEYSTR);
		key = itent.getIntExtra("keyvalue", -1);
		if (strKey == null || key == -1)
			return;
		infor = (PISXinRemoter)PISManager.getInstance().getPISObject(strKey);
		if (infor == null)
			return;

		if (null == adapter) {
			adapter = new RemoterInforAdapter();
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onStartRefresh() {
				manager.DiscoverAll();
			}

			@Override
			public void onRefresh() {
				infors.clear();
				mHandler.sendEmptyMessageDelayed(MessageModel.MSG_GET_DEVICES,
						REQUEST_TIME);
			}
		});
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		loadingLayout = (RelativeLayout) findViewById(R.id.remoter_edit_layout);
		ivLoading = (ImageView) findViewById(R.id.remoter_edit_loading);
		listView = (RemoterListView) findViewById(R.id.remoter_edit_list);
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.setting);
		backBtn.setVisibility(View.VISIBLE);
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
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
//			if (infor != null && infor.currentKeyInfor != null) {
//				infor.currentKeyInfor.name = deviceName;
//				infor.keyInfors.put(key, infor.currentKeyInfor);
//				PISManager.cacheMap.put(infor.getPISKeyString(), infor);
//			}
			RemoterEditActivity.this.finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
		super.onDestroy();
	}

	public class RemoterInforAdapter extends BaseAdapter {

		public RemoterInforAdapter() {

		}

		@Override
		public int getCount() {
			return infors.size();
		}

		@Override
		public Object getItem(int position) {
			return infors.get(position);
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
				convertView = inflater
						.inflate(R.layout.remoter_edit_item, null);
				holder = new ViewHolder();
				holder.tvName = (Button) convertView
						.findViewById(R.id.remoter_edit_item_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			PISBase base = infors.get(position);
			if (base == null)
				return convertView;

			holder.tvName.setText(null == base.getName() ? "" : base.getName());

//			Drawable drawable;
//			if (base.ServiceType == PISBase.SERVICE_TYPE_GROUP) {
//				drawable = getResources().getDrawable(R.drawable.remoter_group);
//			} else {
//				drawable = getResources().getDrawable(R.drawable.remoter_light);
//			}
//			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//					drawable.getIntrinsicHeight());
			int resourceId = 0;
			PISDevice dev;
			if (base.ServiceType != PISBase.SERVICE_TYPE_GROUP){
				dev = base.getDeviceObject();
				if (dev != null) {
					resourceId = ProductClassifyInfo.getThumbResourceId(dev.getClassString());
				} else {
					resourceId = R.drawable.pro_default_selector;
				}
			}else {
				//找到对应的Service，并利用其DEVICE找到classid
				List<PISBase> srvs = PISManager.getInstance().PIServicesWithQuery(
						base.getIntegerType(), PISManager.EnumServicesQueryBaseonType);
				if(srvs == null || srvs.size() == 0)
					dev = null;
				else
					dev = srvs.get(0).getDeviceObject();
				if (dev != null) {
					resourceId = ProductClassifyInfo.getGroupThumbResourceId(dev.getClassString());
				} else {
					resourceId = R.drawable.grp_default_selector;
				}
			}
			Drawable drawable;
			try {
				drawable = getResources().getDrawable(resourceId);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				if (base.getStatus() == PISBase.SERVICE_STATUS_ONLINE)
					setGrayOnBackgroud(drawable, 1);
				else
					setGrayOnBackgroud(drawable, 0);
				holder.tvName.setCompoundDrawables(drawable, null, null, null);
			}catch (Exception e){
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}


			holder.tvName.setTag(base);
			holder.tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (infor != null && key >= 0) {
						try {
							PISBase mBase = (PISBase) v.getTag();
							if (infor.getStatus() != PISBase.SERVICE_STATUS_ONLINE){
								ToastUtils.showToast(RemoterEditActivity.this,
										R.string.devicelist_offline_tip);
								return;
							}
							PipaRequest req = infor.commitKeyInfo(key, mBase.getPanId(),
									mBase.getShortAddr(),
									mBase.getServiceId(), 0, null);
							req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {

								@Override
								public void onRequestStart(PipaRequest req) {
									showLoadingDialog();
								}

								@Override
								public void onRequestResult(PipaRequest req) {
									hideLoadingDialog();
									if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
										Intent intent = new Intent();
										intent.putExtra("keyvalue", key);
										RemoterEditActivity.this.setResult(RESULT_OK, intent);
										RemoterEditActivity.this.finish();
										overridePendingTransition(R.anim.anim_in_from_left,
												R.anim.anim_out_to_right);
									}else{
										ToastUtils.showToast(RemoterEditActivity.this,
												R.string.setting_failed);									}
								}
							});
							infor.request(req);
						} catch (Exception e) {
							PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
						}
					}
				}
			});
			return convertView;
		}

		public class ViewHolder {
			public Button tvName;
		}
	}
	private void setGrayOnBackgroud(Drawable iv, int value) {
		ColorMatrix matrix = new ColorMatrix();
		if (value == 0) {
			matrix.setSaturation(0);
		}
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		iv.setColorFilter(filter);
	}
}