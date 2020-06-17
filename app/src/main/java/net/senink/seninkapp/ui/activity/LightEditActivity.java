package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.GeneralDeviceModel;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.LightGroupEditAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISLightLED;
//import com.senink.seninkapp.core.PISLightRGB;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISMarquee;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;

import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.telink.api.TelinkGroupApiManager;
import net.senink.seninkapp.telink.model.TelinkOperation;
import net.senink.seninkapp.telink.model.TelinkBase;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase.Mode;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase.OnRefreshListener;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 用于修改等分组和某分组下的设备
 * 
 * @author zhaojunfeng
 * @date 2015-07-17
 */
public class LightEditActivity extends BaseActivity implements
		View.OnClickListener{
	// 绑定失败消息
	private final static int MSG_BIND_FAILED = 100;
	// 点击item时发送消息
	public final static int MSG_ITEM_CLICK = 101;
	private final static String TAG = "LightEditActivity";
	// 标题名称
	private TextView tvTitle;
	private ImageView ivTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	// 组列表
	private PullToRefreshListView mRefreshListView;
	private ListView listView;

	private ImageView ivLoading;
	private RelativeLayout loadingLayout;
	private AnimationDrawable anima;
	// 适配器
	private LightGroupEditAdapter adapter;
	// 传递过来的pisbase对象
	private PISBase infor;

	private boolean isTelink = false;
	private boolean isTelinkGroup = false;
	private int telinkAddress = 0;
	private DeviceInfo telinkDeviceinfo;
	private Group telinkGroup;
	private Group.BOUND_TYPE bound_type = Group.BOUND_TYPE.NONE;
	// 点击TELINK item时发送消息
	public final static int MSG_TELINK_ITEM_CLICK = 0x1011;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageModel.MSG_GET_DEVICES:
				break;
			case MSG_BIND_FAILED:
				break;
			case MSG_ITEM_CLICK:
//				showLoadingDialog();
				if (msg.obj != null) {
					if(isTelinkGroup && telinkGroup != null){
						telinkGroup.type = Group.BOUND_TYPE.PIS_GROUP;
						MyApplication.getInstance().getMesh().saveOrUpdate(LightEditActivity.this);
					}
					PISBase obj = PISManager.getInstance().getPISObject(((PISBase)msg.obj).getPISKeyString());
					PISBase base = null;
					PipaRequest req = null;
					if (infor.ServiceType == PISBase.SERVICE_TYPE_GROUP){
						base = obj;
						req = base.addToGroup(infor);
					}else{
						base = infor;
						req = base.addToGroup(obj);
						TelinkGroupApiManager.getInstance().deleteTelinkGroupByPisKey(obj.getPISKeyString());
					}
					req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
						@Override
						public void onRequestStart(PipaRequest req) {
							showLoadingDialog();
						}

						@Override
						public void onRequestResult(PipaRequest req) {
							hideLoadingDialog();
							if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
								Intent aintent = new Intent(LightEditActivity.this, LightSettingActivity.class);
								LightEditActivity.this.setResult(RESULT_OK, aintent); //这理有2个参数(int resultCode, Intent intent)
								finish();
							}else{
								ToastUtils.showToast(LightEditActivity.this,
										R.string.lightedit_add_failed);
							}

						}
					});
					req.NeedAck = true;
					base.request(req);
					EventBus.getDefault().post(new TelinkOperation(TelinkOperation.REFRESH_GROUP_DATA));
				}
				break;
			case MSG_TELINK_ITEM_CLICK:
				if(msg.obj != null){
					showLoadingDialog();
					TelinkBase telinkBase = (TelinkBase) msg.obj;
					if(telinkBase.isDevice()){
						if(telinkGroup != null){
							TelinkGroupApiManager.getInstance().addDeviceToGroup(telinkGroup.address, telinkBase.getDevice().meshAddress);
							setTelinkGroup(telinkGroup);
						}else {
							hideLoadingDialog();
						}
					}else{
						if(telinkDeviceinfo != null){
							TelinkGroupApiManager.getInstance().addDeviceToGroup(telinkBase.getGroup().address, telinkDeviceinfo.meshAddress);
							setTelinkGroup(telinkBase.getGroup());
						}else {
							hideLoadingDialog();
						}
					}
					EventBus.getDefault().post(new TelinkOperation(TelinkOperation.REFRESH_GROUP_DATA));
				}
				break;
			case 1000:
				mRefreshListView.onRefreshComplete();
				break;
			case 15:
				removeMessages(15);
				ToastUtils.showToast(LightEditActivity.this,
						R.string.candle_only);
				break;
			case 16:
				removeMessages(16);
				ToastUtils.showToast(LightEditActivity.this,
						R.string.colorlight_only);
				break;

			}
		}
	};

	@Subscribe
	public void reSetTelinkGroupData(TelinkOperation opr){
		if(opr.getOpr() == TelinkOperation.DEVICE_BIND_OR_UNBIND_GROUP_SUCCEED){
			hideLoadingDialog();
			Intent aintent = new Intent(LightEditActivity.this, LightSettingActivity.class);
			LightEditActivity.this.setResult(RESULT_OK, aintent); //这理有2个参数(int resultCode, Intent intent)
			finish();
		}else if(opr.getOpr() == TelinkOperation.DEVICE_BIND_OR_UNBIND_GROUP_FAIL){
			hideLoadingDialog();
			ToastUtils.showToast(LightEditActivity.this,
					R.string.lightedit_add_failed);
		}
	}
	private void setTelinkGroup(Group telinkGroup) {
		if(telinkGroup.type != Group.BOUND_TYPE.TELINK_GROUP){
			infor = null;
			String key = telinkGroup.PISKeyString;
			telinkGroup.type = Group.BOUND_TYPE.TELINK_GROUP;
			telinkGroup.PISKeyString = "";
			TelinkGroupApiManager.getInstance().deletePISGroup(key, new PipaRequest.OnPipaRequestStatusListener() {
				@Override
				public void onRequestStart(PipaRequest req) {
					Log.e(TAG, "pis group start delete");
				}

				@Override
				public void onRequestResult(PipaRequest req) {
					Log.e(TAG, "pis group start delete");
				}
			});
			MyApplication.getInstance().getMesh().saveOrUpdate(LightEditActivity.this);
			updateGroupList(null);
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lightedit);

		EventBus.getDefault().register(this);
		initView();
		setData();
		setListener();
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		try{
			Bundle bundle = getIntent().getExtras();
			String key = null;

			if(bundle != null){
				isTelink = bundle.getBoolean(TelinkApiManager.IS_TELINK_KEY);
				isTelinkGroup = bundle.getBoolean(TelinkApiManager.IS_TELINK_GROUP_KEY);
				telinkAddress = bundle.getInt(TelinkApiManager.TELINK_ADDRESS);
				if(!isTelinkGroup && isTelink){
					telinkDeviceinfo = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(telinkAddress);
				}else if(isTelinkGroup){
					telinkGroup = MyApplication.getInstance().getMesh().getGroupByAddress(telinkAddress);
					if(telinkGroup != null){
						bound_type = telinkGroup.type;
						if(bound_type == Group.BOUND_TYPE.NONE){
							key = telinkGroup.PISKeyString;
						}
					}
				}
			}

			if(telinkGroup == null){
				key = getIntent().getStringExtra(MessageModel.PISBASE_KEYSTR);
			}
			if (key == null)
				return;
			infor = PISManager.getInstance().getPISObject(key);

		}catch (Exception e){
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (loadingLayout.getVisibility() == View.VISIBLE) {
			if (ev.getAction() == MotionEvent.BUTTON_BACK) {
				hideLoadingDialog();
			}
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}


	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		mRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						mHandler.sendEmptyMessageDelayed(1000, 1000);
						if (infor.ServiceType != PISBase.SERVICE_TYPE_GROUP){
							//更新所有的分组信息
							PISMCSManager mcs = PISManager.getInstance().getMCSObject();
							if (mcs != null){
								PipaRequest req = mcs.getGroupInfos();
								req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
									@Override
									public void onRequestStart(PipaRequest req) {

									}

									@Override
									public void onRequestResult(PipaRequest req) {
										if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
											try{
												int query = infor.getT1() | ((infor.getT2() & 0xFF) << 8);
												List<PISBase> allGroups =
														PISManager.getInstance().PIGroupsWithQuery(query, PISManager.EnumGroupsQueryBaseonType);
												updateGroupList(allGroups);
											}catch (Exception e){
												PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
											}
										}

									}
								});
								req.NeedAck = true;
								mcs.request(req);
							}
						}else{
							PISManager.getInstance().DiscoverAll();
						}
					}
				});
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		ivTitle = (ImageView) findViewById(R.id.title_logo_center);
		backBtn = (Button) findViewById(R.id.title_back);
		ivLoading = (ImageView) findViewById(R.id.lightedit_loading);
		loadingLayout = (RelativeLayout) findViewById(R.id.lightedit_loading_layout);
		mRefreshListView = (PullToRefreshListView) findViewById(R.id.lightedit_grouplist);
		mRefreshListView.setMode(Mode.PULL_FROM_START);
		listView = mRefreshListView.getRefreshableView();
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		//tvTitle.setText(R.string.add);
		tvTitle.setVisibility(View.GONE);
		ivTitle.setVisibility(View.VISIBLE);

		backBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示加载动画
	 */
	private void showLoadingDialog() {
		mHandler.removeMessages(MSG_BIND_FAILED);
		if (null == anima) {
			anima = (AnimationDrawable) ivLoading.getBackground();
		}
		anima.start();
		loadingLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示加载动画
	 */
	private void hideLoadingDialog() {
		if (anima != null) {
			anima.stop();
		}
		loadingLayout.setVisibility(View.GONE);
		mHandler.removeMessages(MSG_BIND_FAILED);
	}

	public void updateGroupList(List<PISBase> objects){
//		if (objects == null || objects.size() == 0)
//			return;
		List<GeneralDeviceModel> filterList = new ArrayList<>();
		List<PISBase> srvs = new ArrayList<>();
		if(infor != null){
			srvs = infor.getGroupObjects();
		}
		if(telinkGroup != null){
			if(bound_type == Group.BOUND_TYPE.NONE){
				addPisDeviceToFilter(objects, filterList);
				addTelinkDeviceToFilter(telinkGroup, filterList);
			}else if(bound_type == Group.BOUND_TYPE.TELINK_GROUP){
				addTelinkDeviceToFilter(telinkGroup, filterList);
			}else{
				addPisDeviceToFilter(objects, filterList);
			}
		}else if (telinkDeviceinfo != null){
			addTelinkGroupToFilter(telinkDeviceinfo, filterList);
		}else if(infor != null){
			addPisDeviceToFilter(objects, filterList);
		}

		PISBase tmp = null;
		if ( srvs.size() > 0 )
		{
			tmp = srvs.get(0);

			if (null == adapter) {
				adapter = new LightGroupEditAdapter(
						LightEditActivity.this, filterList, tmp.getT2(),
						mHandler);
				listView.setAdapter(adapter);
			}else
				adapter.setList(filterList);
		}
		else
		{
			if (null == adapter) {
				adapter = new LightGroupEditAdapter(
						LightEditActivity.this, filterList, 0,
						mHandler);
				listView.setAdapter(adapter);
			}else
				adapter.setList(filterList);

		}

		adapter.notifyDataSetChanged();
	}

	private void addTelinkGroupToFilter(DeviceInfo telinkDeviceinfo, List<GeneralDeviceModel> filterList) {
		List<Group> alreadyGroup = TelinkGroupApiManager.getInstance().getGroupsWithDevice(telinkDeviceinfo.meshAddress);
		List<Group> allGroups = MyApplication.getInstance().getMesh().groups;
		outer:
		for (Group groupInfo : allGroups) {
			if(groupInfo.type == Group.BOUND_TYPE.PIS_GROUP){
				continue;
			}
			for (Group group : alreadyGroup) {
				if(groupInfo.address == group.address){
					continue outer;
				}
			}
			filterList.add(new GeneralDeviceModel(new TelinkBase(groupInfo)));
		}
	}

	private void addTelinkDeviceToFilter(Group telinkGroup, List<GeneralDeviceModel> filterList) {
		List<DeviceInfo> addAlready = TelinkGroupApiManager.getInstance().getDevicesInGroup(telinkGroup.address);
		List<DeviceInfo> allDevice = MyApplication.getInstance().getMesh().devices;
		outer:
		for (DeviceInfo deviceInfo : allDevice) {
			for (DeviceInfo info : addAlready) {
				if(deviceInfo.meshAddress == info.meshAddress){
					continue outer;
				}
			}
			filterList.add(new GeneralDeviceModel(new TelinkBase(deviceInfo)));
		}
	}

	private void addPisDeviceToFilter(List<PISBase> objects, List<GeneralDeviceModel> filterList){
		if(infor != null && objects != null){
			List<PISBase> srvs = infor.getGroupObjects();
			for (PISBase srv : objects) {
				if (!srvs.contains(srv)){
					filterList.add(new GeneralDeviceModel(srv));
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			LightEditActivity.this.finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		List<PISBase> allObjects = null;
		if(infor != null){
			Class query = infor.getClass(); // infor.getT1() | ((infor.getT2() & 0xFF) << 8);
			if (infor.ServiceType == PISBase.SERVICE_TYPE_GROUP){
				allObjects =
						PISManager.getInstance().PIServicesWithQuery(query, PISManager.EnumServicesQueryBaseonClass);
			}else{
				allObjects =
						PISManager.getInstance().PIGroupsWithQuery(query, PISManager.EnumGroupsQueryBaseonClass);
			}
		}

		updateGroupList(allObjects);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
		mHandler.removeMessages(MSG_BIND_FAILED);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

}