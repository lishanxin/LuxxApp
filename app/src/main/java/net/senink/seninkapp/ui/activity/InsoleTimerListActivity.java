package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import net.senink.seninkapp.adapter.InsoleTimerListAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISXinInsole;
//import com.senink.seninkapp.core.PISXinInsole.TimeAction;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

/**
 * 智能控制之网关定时器和开关定时器控制界面
 * 
 * @author zhaojunfeng
 * @date 2015-12-31
 */
public class InsoleTimerListActivity extends BaseActivity implements
		View.OnClickListener{
	private final static String TAG = "TimerListActivity";
	// 超时消息
	private final static int MSG_TIMEOUT = 10;
	// 删除成功
	private final static int MSG_DELETE_SUCCESS = 11;
	// 删除失败
	private final static int MSG_DELETE_FAILED = 12;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	//标题栏的布局
	private View titleLayout;
	// 添加按钮
	private ImageButton addBtn;
	// 组列表
	private ListView listView;
	// 适配器
//	private InsoleTimerListAdapter adapter;
	private PISManager manager = null;
	// 智能鞋垫对象
//	private PISXinInsole infor;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_TIMEOUT:
				removeMessages(MSG_TIMEOUT);
				break;
			case MessageModel.MSG_GET_DEVICES:
				removeMessages(MSG_TIMEOUT);
				refreshTimers();
				break;
			case MSG_DELETE_FAILED:
				removeMessages(MSG_TIMEOUT);
				ToastUtils.showToast(InsoleTimerListActivity.this,
						R.string.del_smart_fail);
				break;
			case MSG_DELETE_SUCCESS:
				refreshTimers();
				removeMessages(MSG_TIMEOUT);
				ToastUtils.showToast(InsoleTimerListActivity.this,
						R.string.del_smart_succ);
				sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insoletimerlist);
		manager = PISManager.getInstance();
		initView();
		setData();
		setListener();
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (getIntent() != null) {
			Intent intent = getIntent();
			String key = intent.getStringExtra(MessageModel.ACTIVITY_VALUE);
			try {
//				infor = (PISXinInsole) PISManager.cacheMap.get(key);
//				mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
//				if (infor != null) {
//					infor.getTaskInforOnTimer();
//				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 更新列表信息
	 */
	private void refreshTimers() {
//		SparseArray<TimeAction> list = null;
//		if (infor != null) {
//			list = infor.getTasks();
//		}
//		adapter = new InsoleTimerListAdapter(this, infor, list);
//		listView.setAdapter(adapter);
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		addBtn = (ImageButton) findViewById(R.id.title_add);
		titleLayout = findViewById(R.id.insoletimerlist_title);
		listView = (ListView) findViewById(R.id.insoletimerlist_listview);
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.insole_timer);
		backBtn.setVisibility(View.VISIBLE);
		titleLayout.setBackgroundColor(getResources().getColor(R.color.insole_title_bgcolor));
		addBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.title_add:
			Intent intent = new Intent(this, AddInsoleTimerActivity.class);
//			intent.putExtra(MessageModel.ACTIVITY_VALUE,
//					infor.getPISKeyString());
			startActivityForResult(intent, Constant.REQUEST_CODE_ADDTIMER);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		default:
			break;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPipaRequestStatusListener(this);
//		manager.setOnPISChangedListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i(TAG, "onActivityResult(): resultCode = " + resultCode
				+ ", requestCode = " + requestCode);
		if (requestCode == Constant.REQUEST_CODE_TIMERLIST_ADDTIMER) {
			if (resultCode == RESULT_OK) {
				mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
			}
		} else {

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		manager.setOnPipaRequestStatusListener(null);
//		manager.setOnPISChangedListener(null);
	}
//
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
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis.mT1 == (byte)PISConstantDefine.PIS_INSOLE_T1
//				&& pis.mT2 == PISConstantDefine.PIS_INSOLE_T2) {
//			if (reqType == PISXinInsole.PIS_CMD_INSOLE_TIMEACTION_GET) {
//				mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
//			} else if (reqType == PISXinInsole.PIS_CMD_INSOLE_TIMEACTION_UNSET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					mHandler.sendEmptyMessage(MSG_DELETE_SUCCESS);
//				} else {
//					mHandler.sendEmptyMessage(MSG_DELETE_FAILED);
//				}
//			}
//		}
//	}

	@Override
	protected void onDestroy() {
		mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//
//	}

}