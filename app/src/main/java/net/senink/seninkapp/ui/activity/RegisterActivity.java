package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.senink.piservice.http.HttpRequest;
import net.senink.piservice.http.HttpUserInfo;
import net.senink.piservice.http.PISHttpManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.entity.MobCodeInfor;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

public class RegisterActivity extends BaseActivity implements OnClickListener {

	// 获取手机验证码成功
	private static final int MSG_MOBCODE_SUCCESS = 10;
	// 获取手机验证码失败
	private static final int MSG_MOBCODE_FAILED = 11;
	// 设置获取验证码按钮可用
	private static final int MSG_BUTTON_ENABLE = 12;
	// 手机注册成功
	private static final int MSG_REGISTER_SUCCESS = 13;
	// 手机注册失败
	private static final int MSG_REGISTER_FAILED = 14;

	public static final String REG = "1";
	private EditText etAccount, etPwd, etConfirmPwd, etMobCode;
	// 验证码提示语/标题
	private TextView tvTip, tvTitle;
	private SharePreferenceUtils utils = null;
	//按钮
	private Button commitBtn, codeBtn, backBtn, loginBtn;
	//加载框
	private ProgressDialog progressDialog;
	// 返回手机验证码数据
	private MobCodeInfor codeInfor;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_MOBCODE_SUCCESS:
				setTip(R.string.register_send_message);
				break;
			case MSG_MOBCODE_FAILED:
				setTip("");
				ToastUtils.showToast(RegisterActivity.this,
						R.string.register_get_code_failed);
				break;
			case MSG_BUTTON_ENABLE:
				codeBtn.setEnabled(true);
				break;
			case MSG_REGISTER_SUCCESS:
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				ToastUtils.showToast(RegisterActivity.this,R.string.reg_succe);
				backBtn.performClick();
				break;
			case MSG_REGISTER_FAILED:
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				String error = null;
				if (msg.obj != null) {
					error = (String) msg.obj;
				} else {
                   error = RegisterActivity.this.getString(R.string.register_failed);
				}
				ToastUtils.showToast(RegisterActivity.this, error);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		utils = SharePreferenceUtils.getInstance(this);
		initView();
		setListener();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		etAccount = (EditText) findViewById(R.id.register_account);
		etPwd = (EditText) findViewById(R.id.register_pwd);
		etConfirmPwd = (EditText) findViewById(R.id.register_confirm_pwd);
		tvTip = (TextView) findViewById(R.id.register_tip);
		etMobCode = (EditText) findViewById(R.id.register_mobcode);
		codeBtn = (Button) findViewById(R.id.register_sendmsg);
		commitBtn = (Button) findViewById(R.id.register_commit);
		backBtn = (Button) findViewById(R.id.title_back);
		tvTitle = (TextView) findViewById(R.id.title_name);
		loginBtn = (Button) findViewById(R.id.register_login);
		setTip("");
		setTitle();
		setProgress();
	}

	/**
	 * 设置标题框
	 */
	private void setTitle() {
		backBtn.setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.title_rigester);
	}

	/**
	 * 设置激活码提示语
	 * 
	 * @param resId
	 */
	private void setTip(int resId) {
		tvTip.setText(resId);
	}

	/**
	 * 设置激活码提示语
	 * 
	 * @param tip
	 */
	private void setTip(String tip) {
		tvTip.setText(tip);
	}

	/**
	 * 设置进度条
	 */
	private void setProgress() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(R.string.reging));
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		commitBtn.setOnClickListener(this);
		codeBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

	/**
	 * 提交数据
	 */
	private void commit() {
		String tel = etAccount.getText().toString();
		String pwd = etPwd.getText().toString();
		String pwdConfirm = etConfirmPwd.getText().toString();
		String code = etMobCode.getText().toString();
		if (!CommonUtils.isTelNumber(tel)) {
			etAccount.requestFocus();
			ToastUtils.showToast(RegisterActivity.this, R.string.register_telephone_isnot_format);
			return;
		}
		if (codeInfor == null || TextUtils.isEmpty(codeInfor.mobCode) ||
				TextUtils.isEmpty(code) || !code.equals(codeInfor.mobCode)){
			etMobCode.requestFocus();
			ToastUtils.showToast(RegisterActivity.this, R.string.register_input_error);
			return;
		}
		if (pwd != null && pwd.length() > 0 && pwd.getBytes().length <= 16) {
			if (!TextUtils.isEmpty(pwdConfirm) && pwd.equals(pwdConfirm)) {
				try {
					progressDialog.show();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				HttpUserInfo httpUInfo = new HttpUserInfo();
				HttpRequest req = httpUInfo.commitUserRegister(tel, pwd, codeInfor.mobCode);
				req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
					@Override
					public void onRequestStart(HttpRequest req) {

					}

					@Override
					public void onRequestResult(HttpRequest req) {
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
						if (req == null) {
							ToastUtils.showToast(RegisterActivity.this, RegisterActivity.this.getString(R.string.register_failed));
							return;
						}
						if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
//							handler.sendEmptyMessage(MSG_REGISTER_SUCCESS);
							ToastUtils.showToast(RegisterActivity.this,R.string.reg_succe);
							backBtn.performClick();
						}else {
//							handler.sendEmptyMessage(MSG_REGISTER_FAILED);
							String error = null;
							if (req.errorMessage != null) {
								error = req.errorMessage;
							} else {
								error = RegisterActivity.this.getString(R.string.register_failed);
							}
							ToastUtils.showToast(RegisterActivity.this, error);
						}
					}
				});
				PISHttpManager.getInstance(this).request(req);
//				new Thread(new RegisterThread(RegisterActivity.this,
//						tel, codeInfor.mobCode, pwd)).start();
			} else {
				etConfirmPwd.requestFocus();
				ToastUtils.showToast(RegisterActivity.this,
						R.string.no_switch_password);
			}
		} else {
			etPwd.requestFocus();
			if (TextUtils.isEmpty(pwd)) {
				ToastUtils.showToast(RegisterActivity.this,
						R.string.alert_password_no_null);
			} else {
				ToastUtils.showToast(RegisterActivity.this,
						R.string.alert_password_no_too_long);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
			case R.id.register_login:
				this.finish();
				overridePendingTransition(R.anim.anim_in_from_left,
						R.anim.anim_out_to_right);
				break;
			case R.id.register_commit:
				commit();
				break;
			// 发送手机验证码
			case R.id.register_sendmsg: {
				String tel = etAccount.getText().toString();
				if (TextUtils.isEmpty(tel)) {
					ToastUtils.showToast(RegisterActivity.this,
							R.string.register_telephone_isempty);
					break;
				}
				if (!CommonUtils.isTelNumber(tel)) {
					ToastUtils.showToast(RegisterActivity.this,
							R.string.register_telephone_isnot_format);
					break;
				}
				setTip(R.string.please_wait);

				/**由于PISHttpManager依赖于PISManager，因此需要先创建一个PISManager*/
				HttpUserInfo httpUser = new HttpUserInfo();
				HttpUserInfo.mobVerificationHttpRequest req =
						(HttpUserInfo.mobVerificationHttpRequest)httpUser.updateVerificationCode(tel);
				req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
					@Override
					public void onRequestStart(HttpRequest req) {
						codeBtn.setEnabled(false);
						handler.sendEmptyMessageDelayed(MSG_BUTTON_ENABLE, 60 * 1000);
					}

					@Override
					public void onRequestResult(HttpRequest req) {
						if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
							codeInfor = new MobCodeInfor();
							codeInfor.telNumber = ((HttpUserInfo.mobVerificationHttpRequest) req).getMobile();
							codeInfor.mobCode = ((HttpUserInfo.mobVerificationHttpRequest) req).getVerificationCode();

							handler.sendEmptyMessage(MSG_MOBCODE_SUCCESS);
						} else {
							codeInfor = null;
							handler.sendEmptyMessage(MSG_MOBCODE_FAILED);
						}
					}
				});
				PISHttpManager.getInstance(this).request(req);
			}
			break;
			default:
				break;
		}
	}

}
