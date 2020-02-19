package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.http.HttpRequest;
import net.senink.piservice.http.HttpUserInfo;
import net.senink.piservice.http.PISHttpManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.entity.MobCodeInfor;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

public class ResetPasswordActivity extends BaseActivity implements
		OnClickListener {
	private EditText etPwd, etPwdAgain;
	private Button commitBtn, backBtn;
	private TextView tvTitle;
	private ProgressDialog progressDialog;
	// 修改手机号码失败
//	private static final int MSG_MODIFY_PWD_FAILED = 11;
	// 修改手机密码成功
//	private static final int MSG_MODIFY_PWD_SUCCESS = 12;
	// 手机号码
	private String tel;
	// 手机验证码
	private String mobCode;
//	@SuppressLint("HandlerLeak")
//	private Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case MSG_MODIFY_PWD_FAILED:
//				if (progressDialog != null) {
//					progressDialog.dismiss();
//				}
//				ToastUtils.showToast(ResetPasswordActivity.this, R.string.find_password_fail);
//				break;
//			case MSG_MODIFY_PWD_SUCCESS:
//				if (progressDialog != null) {
//					progressDialog.dismiss();
//				}
//				ToastUtils.showToast(ResetPasswordActivity.this, R.string.find_password_success);
//				setResult(RESULT_OK);
//				backBtn.performClick();
//				break;
//			default:
//				break;
//			}
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);
		setData();
		initView();
		setTitle();
		setProgress();
		setListener();
	}

	private void setData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra("tel")) {
			tel = intent.getStringExtra("tel");
			if (intent.hasExtra("mobCode")) {
				mobCode = intent.getStringExtra("mobCode");
			}
		}
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		backBtn = (Button) findViewById(R.id.title_back);
		commitBtn = (Button) findViewById(R.id.reset_commit);
		etPwdAgain = (EditText) findViewById(R.id.reset_pwd_two);
		etPwd = (EditText) findViewById(R.id.reset_pwd_first);
		tvTitle = (TextView) findViewById(R.id.title_name);
	}

	/**
	 * 设置标题
	 */
	private void setTitle() {
		backBtn.setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.reset_pwd_title);
	}

	/**
	 * 设置加载框
	 */
	private void setProgress() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(
				R.string.finding_password));
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
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
		case R.id.reset_commit:
			String pwd = etPwd.getText().toString();
			String pwdAgain = etPwdAgain.getText().toString();
			if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(pwdAgain)
					&& pwd.compareTo(pwdAgain) == 0) {
				commitOnPwd(pwd);
			} else if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd)) {
				ToastUtils.showToast(ResetPasswordActivity.this, R.string.alert_password_no_null);
			} else {
				ToastUtils.showToast(ResetPasswordActivity.this, R.string.no_switch_password);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 提交新的密码
	 * 
	 * @param pwd
	 */
	private void commitOnPwd(final String pwd) {

		try {
			HttpUserInfo httpUInfo = new HttpUserInfo();
			HttpRequest req = httpUInfo.commitNewPossword(tel, pwd, mobCode);
			req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
				@Override
				public void onRequestStart(HttpRequest req) {
					progressDialog.show();
				}

				@Override
				public void onRequestResult(HttpRequest req) {
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					if (req != null && req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
						ToastUtils.showToast(ResetPasswordActivity.this, R.string.find_password_success);
						setResult(RESULT_OK);
						backBtn.performClick();
					} else {
						if (req.errorMessage != null)
							ToastUtils.showToast(ResetPasswordActivity.this, req.errorMessage);
						else
							ToastUtils.showToast(ResetPasswordActivity.this, R.string.find_password_fail);
					}
				}
			});
			PISHttpManager.getInstance(this).request(req);
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(ResetPasswordActivity.this, e);
		}

	}
}
