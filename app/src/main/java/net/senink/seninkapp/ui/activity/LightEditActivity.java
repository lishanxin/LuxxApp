package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
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

import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase.Mode;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase.OnRefreshListener;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

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
					PISBase obj = PISManager.getInstance().getPISObject(((PISBase)msg.obj).getPISKeyString());
					PISBase base = null;
					PipaRequest req = null;
					if (infor.ServiceType == PISBase.SERVICE_TYPE_GROUP){
						base = obj;
						req = base.addToGroup(infor);
					}else{
						base = infor;
						req = base.addToGroup(obj);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lightedit);


		initView();
		setData();
		setListener();
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		try{
			String key = getIntent().getStringExtra(MessageModel.PISBASE_KEYSTR);
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
		if (objects == null || objects.size() == 0)
			return;
		ArrayList<PISBase> filterList = new ArrayList<>();
		List<PISBase> srvs = infor.getGroupObjects();
		if (srvs == null || srvs.size() == 0){
			filterList.addAll(objects);
		}else {
			for (PISBase srv : objects) {
				if (!srvs.contains(srv))
					filterList.add(srv);
			}
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
		Class query = infor.getClass(); // infor.getT1() | ((infor.getT2() & 0xFF) << 8);
		if (infor.ServiceType == PISBase.SERVICE_TYPE_GROUP){
			allObjects =
					PISManager.getInstance().PIServicesWithQuery(query, PISManager.EnumServicesQueryBaseonClass);
		}else{
			allObjects =
					PISManager.getInstance().PIGroupsWithQuery(query, PISManager.EnumGroupsQueryBaseonClass);
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
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

}