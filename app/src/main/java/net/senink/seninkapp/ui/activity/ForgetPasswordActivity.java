package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.senink.piservice.http.HttpRequest;
import net.senink.piservice.http.HttpUserInfo;
import net.senink.piservice.http.PISHttpManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.entity.MobCodeInfor;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

/**
 * 通过手机号码找回密码的界面
 * 
 * @author zhaojunfeng
 * @date 2015-11-01
 * 
 */
public class ForgetPasswordActivity extends BaseActivity implements
		OnClickListener {
	private EditText etAccount, etCode;
	private Button nextBtn, codeBtn, backBtn;
	private TextView tvTitle,tvTip;
	// 获取手机验证码成功
	private static final int MSG_MOBCODE_SUCCESS = 10;
	// 获取手机验证码失败
	private static final int MSG_MOBCODE_FAILED = 11;
	// 设置获取验证码按钮可用
	private static final int MSG_BUTTON_ENABLE = 12;
	// 返回手机验证码数据
	private MobCodeInfor codeInfor;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_MOBCODE_SUCCESS:
				tvTip.setText(R.string.register_send_message);
				break;
			case MSG_MOBCODE_FAILED:
				tvTip.setText("");
				ToastUtils.showToast(ForgetPasswordActivity.this,
						R.string.register_get_code_failed);
				break;
			case MSG_BUTTON_ENABLE:
				codeBtn.setEnabled(true);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		initView();
		setListener();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		backBtn = (Button) findViewById(R.id.title_back);
		tvTitle = (TextView) findViewById(R.id.title_name);
		etAccount = (EditText) findViewById(R.id.forget_pwd_tel);
		etCode = (EditText) findViewById(R.id.forget_pwd_inputcode);
		codeBtn = (Button) findViewById(R.id.forget_pwd_getcode);
		nextBtn = (Button) findViewById(R.id.forget_pwd_next);
		tvTip = (TextView) findViewById(R.id.forget_pwd_tip);
		setTitle();
	}

	private void setTitle() {
		backBtn.setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.title_get_back_pwd);
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		codeBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.forget_pwd_next:
			String tel = etAccount.getText().toString();
			String code = etCode.getText().toString();
			if (CommonUtils.isTelNumber(tel) && codeInfor != null
					&& !TextUtils.isEmpty(codeInfor.mobCode)
					&& !TextUtils.isEmpty(code)
					&& code.compareTo(codeInfor.mobCode)==0
					&& tel.compareTo(codeInfor.telNumber)==0) {
				Intent intent = new Intent(this, ResetPasswordActivity.class);
				intent.putExtra("tel", tel);
				intent.putExtra("mobCode", codeInfor.mobCode);
				startActivityForResult(intent, Constant.REQUEST_CODE_RESET_PWD);
				overridePendingTransition(R.anim.anim_in_from_right,
						R.anim.anim_out_to_left);
			} else if(TextUtils.isEmpty(code)){
				ToastUtils.showToast(this, R.string.forget_pwd_code_isempty);
			}else if(codeInfor != null
					&& !TextUtils.isEmpty(codeInfor.mobCode)
					&& !TextUtils.isEmpty(code)
					&& !code.equals(codeInfor.mobCode)){
				ToastUtils.showToast(this, R.string.forget_pwd_code_notequal);
			}else{
				ToastUtils.showToast(this, R.string.forget_pwd_format_error);
			}
			break;
		case R.id.forget_pwd_getcode:
			tvTip.setText(R.string.please_wait);
			commit();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.REQUEST_CODE_RESET_PWD && resultCode == RESULT_OK) {
			backBtn.performClick();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取验证码
	 */
	private void commit() {
		final String content = etAccount.getText().toString();
		if (TextUtils.isEmpty(content)) {
			ToastUtils.showToast(this, R.string.forget_pwd_input_error);
			return;
		}
		if (!CommonUtils.isTelNumber(content)) {
			ToastUtils.showToast(this, R.string.forget_pwd_format_error);
			return;
		}

		HttpUserInfo httpUInfo = new HttpUserInfo();
		HttpRequest req = httpUInfo.updateVerificationCode(content);
		req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
			@Override
			public void onRequestStart(HttpRequest req) {
				codeBtn.setEnabled(false);
			}

			@Override
			public void onRequestResult(HttpRequest req) {
				codeBtn.setEnabled(true);
				if (req != null && req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
					codeInfor = new MobCodeInfor();
					codeInfor.telNumber = ((HttpUserInfo.mobVerificationHttpRequest) req).getMobile();
					codeInfor.mobCode = ((HttpUserInfo.mobVerificationHttpRequest) req).getVerificationCode();
					handler.sendEmptyMessage(MSG_MOBCODE_SUCCESS);
				} else {
					handler.sendEmptyMessage(MSG_MOBCODE_FAILED);
				}
			}
		});
		PISHttpManager.getInstance(this).request(req);
	}
}
