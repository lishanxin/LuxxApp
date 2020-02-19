package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.http.HttpRequest;
import net.senink.piservice.http.HttpUserInfo;
import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.core.struct.PipaServiceInfo;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.ui.constant.MessageModel;

/**
 * 修改设备名称和组名
 * 
 * @author zhaojunfeng
 * @date 2015-10-28
 * 
 */
public class ModifyNameActivity extends BaseActivity implements OnClickListener{
	// 加载框显示的最长时间
	private final static int SHOW_MAX_TIME = 60000;
	private static final int MSG_MODIFY_FAILED = 100;
	private Button backBtn;
	private Button finishedBtn;
	private EditText etName;
	// 是否是组
	private boolean isGroup = false;
	// 标题名称
	private TextView tvTitle;
	private ImageView ivTitle;
	private ImageView ivLoading;
	private RelativeLayout loadingLayout;
	private AnimationDrawable anima;
	// 设备的对象
	private PISBase infor;
	// 组id
	private short groupId;
	// 组的对象
	private PISBase group;
	private PISManager manager;
	private PISMCSManager mcm;
	private MeshController controller;
	private MeshController.onFeedbackListener listener = new MeshController.onFeedbackListener() {
		
		@Override
		public void onNetSecurity() {
			
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
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 修改名称失败
			case MSG_MODIFY_FAILED:
				removeAllMessages();
				hideLoadingDialog();
				Toast.makeText(ModifyNameActivity.this,
						R.string.modifyname_modify_failed, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifyname);
		manager = PISManager.getInstance();
		mcm = manager.getMCSObject();
		controller = MeshController.getInstance(this);
		initView();
		setData();
		setTitle();
		setListener();
	}

	/**
	 * 设置传递数据
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

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		finishedBtn.setOnClickListener(this);
	}

	/**
	 * 设置标题内容
	 */
	private void setTitle() {
		if (isGroup) {
			tvTitle.setText(R.string.modifyname_groupname);
		} else {
			tvTitle.setText(R.string.modifyname_devicename);
		}
		tvTitle.setVisibility(View.GONE);
		ivTitle.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		backBtn = (Button) findViewById(R.id.title_back);
		finishedBtn = (Button) findViewById(R.id.title_finished);
		etName = (EditText) findViewById(R.id.modifyname_name);
		tvTitle = (TextView) findViewById(R.id.title_name);
		ivTitle = (ImageView) findViewById(R.id.title_logo_center);
		ivLoading = (ImageView) findViewById(R.id.modifyname_loading);
		loadingLayout = (RelativeLayout) findViewById(R.id.modifyname_loading_layout);
		backBtn.setVisibility(View.VISIBLE);
		finishedBtn.setVisibility(View.VISIBLE);
		finishedBtn.setBackgroundColor(Color.TRANSPARENT);
		finishedBtn.setText(R.string.finish);
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

	@Override
	public void onBackPressed() {
		if (loadingLayout.getVisibility() == View.VISIBLE) {
			hideLoadingDialog();
		} else {
			backBtn.performClick();
		}
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			this.finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;

		case R.id.title_finished:
			hideSoftKeyBoard(this,etName);
			String text = etName.getText().toString();
			if (!TextUtils.isEmpty(text) && text.getBytes().length <= 16) {
				if (infor.ServiceType == PISBase.SERVICE_TYPE_GROUP){
//					HttpUserInfo httpuInfo = new HttpUserInfo();
					PISMCSManager manager = PISManager.getInstance().getMCSObject();
					PipaRequest req = manager.modifyGroup(
							infor.getGroupId(), text, (byte)(infor.getT1()&0xFF), (byte)(infor.getT2()&0xFF));
					req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
						@Override
						public void onRequestStart(PipaRequest req) {
							showLoadingDialog();
						}

						@Override
						public void onRequestResult(PipaRequest req) {
							hideLoadingDialog();
							if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
								setResult(RESULT_OK);
								backBtn.performClick();
							}else{
								Toast.makeText(ModifyNameActivity.this,
										R.string.modifyname_modify_failed, Toast.LENGTH_SHORT)
										.show();
							}
						}
					});
					manager.request(req);
				}else{
					PIServiceInfo srvInfo = new PIServiceInfo((byte)(infor.getT1()&0xFF),
							(byte)(infor.getT2()&0xFF), infor.getServiceId());
					srvInfo.Location = (byte)(infor.getLocation() & 0xFF);
					srvInfo.Name = text;
					PipaRequest req = infor.commitPISInfo(srvInfo);
					req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
						@Override
						public void onRequestStart(PipaRequest req) {
							showLoadingDialog();
						}

						@Override
						public void onRequestResult(PipaRequest req) {
							hideLoadingDialog();
							if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
								setResult(RESULT_OK);
								backBtn.performClick();
							}else{
								Toast.makeText(ModifyNameActivity.this,
										R.string.modifyname_modify_failed, Toast.LENGTH_SHORT)
										.show();
							}
						}
					});
					infor.request(req);

				}
//				if (isGroup) {
//					if (mcm != null && group != null) {
//						showLoadingDialog();
//						etName.setCursorVisible(false);
//						mcm.changeGroup(groupId, group.mT1, group.mT2, text,
//								true);
//					} else {
//						mHandler.sendEmptyMessage(MSG_MODIFY_FAILED);
//					}
//				} else {
//					if (infor != null) {
//						showLoadingDialog();
//						etName.setCursorVisible(false);
//						PipaServiceInfo si = new PipaServiceInfo(infor);
//						si.setName(text);
//						infor.setPISInfo(si, true);
//					} else {
//						mHandler.sendEmptyMessage(MSG_MODIFY_FAILED);
//					}
//				}
			} else if (TextUtils.isEmpty(text)) {
				Toast.makeText(ModifyNameActivity.this,
						R.string.modifyname_isempty, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ModifyNameActivity.this,
						R.string.modifyname_exceed, Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	/**
	 * 隐藏软键盘
	 */
	private void hideSoftKeyBoard(Context context,View view){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);  
		imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);  
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		controller.setonFeedbackListener(listener);
//		manager.setOnPipaRequestStatusListener(this);
	}
	
	@Override
	protected void onPause() {
//		controller.setonFeedbackListener(null);
//		manager.setOnPipaRequestStatusListener(null);
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		removeAllMessages();
		super.onDestroy();
	}

	/**
	 * 移除所有消息
	 */
	private void removeAllMessages() {
		mHandler.removeMessages(MSG_MODIFY_FAILED);
	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (reqType == PISBase.PIS_CMD_SRVINFO_SET) {
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				hideLoadingDialog();
//				if (infor != null) {
//					PISManager.cacheMap.put(infor.getPISKeyString(), infor);
//					if (infor.mT1 == PISConstantDefine.PIS_SWITCH_T1
//                          && (infor.mT2 == PISConstantDefine.PIS_SWITCH_T2_1
//                          || infor.mT2 == PISConstantDefine.PIS_SWITCH_T2_2)) {
//						manager.upgradePISSwitch(infor);
//					}
//				}
//				setResult(RESULT_OK);
//				backBtn.performClick();
//			} else {
//               mHandler.sendEmptyMessage(MSG_MODIFY_FAILED);
//			}
//		}else if(reqType == PISMCSManager.PIS_CMD_MCM_CHG_GROUP){
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				hideLoadingDialog();
//				if (group != null) {
//					group.mName = mcm.tempGroupName;
//					manager.addDeviceGroup(group);
//				}
//				setResult(RESULT_OK);
//				backBtn.performClick();
//			} else {
//               mHandler.sendEmptyMessage(MSG_MODIFY_FAILED);
//			}
//		}
//	}
}
