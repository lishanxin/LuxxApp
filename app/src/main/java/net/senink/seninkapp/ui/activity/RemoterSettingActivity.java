package net.senink.seninkapp.ui.activity;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinRemoter;
import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISLightRemoter;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISLightRemoter.KeyInfor;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.CommonUtils;

/**
 * 用于遥控器设置界面
 * 
 * @author zhaojunfeng
 * @date 2015-08-28
 */
public class RemoterSettingActivity extends BaseActivity implements
		View.OnClickListener {
	public static final String TAG = "RemoterSettingActivity";
	private static final int DELAY_TIME = 5000;
	private static final int MSG_GET_KEYINFOR = 10;
	private static final int REQUEST_CODE_KEYCHANGE = 1000;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的取消按钮
	private Button cancelBtn;
	// 上下左右按钮
	private ImageButton[] btnViewArray = new ImageButton[4]; // leftBtn, rightBtn, topBtn, bottomBtn;
	//蓝牙未连接上的提示
//	private RelativeLayout connectedTip;
	//蓝牙未连接提示的取消按钮
//	private ImageButton cancelTipBtn;
	// a,b,c,d按钮
	private Button[] btnArray = new Button[4];// aBtn, bBtn, cBtn, dBtn;
	// abcd组对应的名称
	private TextView[] tvArray = new TextView[4]; // tvA, tvB, tvC, tvD;
	// 底部按钮
	private ImageButton tipBtn, promptBtn;
	// 遥控器基类
	private PISXinRemoter remoter;
	// 滑出动画
	private Animation outAnima = null;
	// 滑入动画
	private Animation inAnima = null;
	// 是跳转到遥控器设置界面
	private boolean isChanged = false;
	// 跳转时按了哪个键
	private short currentKey = -1;
	private PISManager manager;
	//蓝牙管理器
	private MeshController controller;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_KEYINFOR:
				if (msg.arg1 > 0 && msg.arg1 < 4) {
//					remoter.getRemoterKey((short)msg.arg1, true);
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remoter_setting);
		manager = PISManager.getInstance();
		controller = MeshController.getInstance(this);
		setData();
		initView();
		setView();
		setListener();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if (requestCode == REQUEST_CODE_KEYCHANGE) {
			switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
				case RESULT_OK:    //登录成功后由登录界面返回
					int keyValue = intent.getIntExtra("keyvalue", -1);
					if (keyValue != -1)
						setView();
					break;
				default:
					break;
			}
		}
	}


	/**
	 * 设置四个键的名称
	 * 
	 * @param value
	 */
	private void setText(String text, int value) {
		if (value < 0 || value > 3 || text == null)
			return;
		tvArray[value].setText(text);
	}

	/**
	 * 滑出动画
	 * 
	 * @param outBtn
	 * @param inBtn
	 */
	private void outAnima(final ImageButton outBtn, final ImageButton inBtn) {
		outBtn.setVisibility(View.GONE);
		outAnima = AnimationUtils.loadAnimation(RemoterSettingActivity.this,
				R.anim.anim_out_to_bottom);
		outAnima.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				inAnima(inBtn);
			}
		});
		outBtn.startAnimation(outAnima);
	}

	/**
	 * 滑入动画
	 * 
	 * @param btn
	 */
	private void inAnima(final ImageButton btn) {
		btn.setVisibility(View.VISIBLE);
		inAnima = AnimationUtils.loadAnimation(RemoterSettingActivity.this,
				R.anim.anim_in_from_bottom);
		btn.startAnimation(inAnima);
	}

	private void setView() {
		if (remoter != null && remoter.getName() != null) {
			tvTitle.setText(remoter.getName());
			for(int i=0; i<4; i++) {
				try {
					setText(remoter.getKeyobject(i).getName(), i);
				} catch (NullPointerException e) {
					setText("", i);
				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
			}
		}
		cancelBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		Intent intent = getIntent();
		if (intent == null)
			return;
		String value = intent.getStringExtra(MessageModel.PISBASE_KEYSTR);
		if (value == null)
			return;
		remoter = (PISXinRemoter)PISManager.getInstance().getPISObject(value);
		if (remoter == null)
			return;
		for (int i = 0; i < 4; i++) {
			PipaRequest req = remoter.updateKeyInfo(i);
			req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
				@Override
				public void onRequestStart(PipaRequest req) {

				}

				@Override
				public void onRequestResult(PipaRequest req) {
					if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
						setView();
				}
			});
			remoter.request(req);
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		cancelBtn.setOnClickListener(this);
		for(int i=0; i<4; i++) {
			btnArray[i].setOnClickListener(this);
			btnViewArray[i].setOnClickListener(this);
		}
		tipBtn.setOnClickListener(this);
		promptBtn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		cancelBtn.performClick();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		cancelBtn = (Button) findViewById(R.id.title_back);
//		cancelTipBtn = (ImageButton) findViewById(R.id.tip_cancel);
//		connectedTip = (RelativeLayout) findViewById(R.id.remoter_setting_bluetooth_tip);
		tvArray[0] = (TextView) findViewById(R.id.remoter_setting_group_a);
		tvArray[1] = (TextView) findViewById(R.id.remoter_setting_group_b);
		tvArray[2] = (TextView) findViewById(R.id.remoter_setting_group_c);
		tvArray[3] = (TextView) findViewById(R.id.remoter_setting_group_d);
		btnArray[0] = (Button) findViewById(R.id.remoter_setting_btn_a);
		btnArray[1] = (Button) findViewById(R.id.remoter_setting_btn_b);
		btnArray[2] = (Button) findViewById(R.id.remoter_setting_btn_c);
		btnArray[3] = (Button) findViewById(R.id.remoter_setting_btn_d);
		btnViewArray[0] = (ImageButton) findViewById(R.id.remoter_setting_left);
		btnViewArray[1] = (ImageButton) findViewById(R.id.remoter_setting_right);
		btnViewArray[2] = (ImageButton) findViewById(R.id.remoter_setting_top);
		btnViewArray[3] = (ImageButton) findViewById(R.id.remoter_setting_bottom);
		tipBtn = (ImageButton) findViewById(R.id.remoter_setting_tip);
		promptBtn = (ImageButton) findViewById(R.id.remoter_setting_prompt);
	}

	/**
	 * 跳转到下个界面
	 * 
	 * @param value
	 */
	private void toAnotherActivity(int value) {
		if (remoter != null) {
			Intent it = new Intent(RemoterSettingActivity.this,
					RemoterEditActivity.class);

			it.putExtra(MessageModel.PISBASE_KEYSTR, remoter.getPISKeyString());
			it.putExtra("keyvalue", value);
			startActivityForResult(it, REQUEST_CODE_KEYCHANGE);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
//			manager.upgradePISLightReomter(remoter);
			this.finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.remoter_setting_btn_a:
			toAnotherActivity(0);
			break;
		case R.id.remoter_setting_btn_b:
			toAnotherActivity(1);
			break;
		case R.id.remoter_setting_btn_c:
			toAnotherActivity(2);
			break;
		case R.id.remoter_setting_btn_d:
			toAnotherActivity(3);
			break;
		case R.id.remoter_setting_left:

			break;
		case R.id.remoter_setting_right:

			break;
		case R.id.remoter_setting_top:

			break;
		case R.id.remoter_setting_bottom:

			break;
		case R.id.remoter_setting_tip:
			outAnima((ImageButton) v, promptBtn);
			break;
		case R.id.remoter_setting_prompt:
			outAnima((ImageButton) v, tipBtn);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isChanged) {
			isChanged = false;

		}

	}

}