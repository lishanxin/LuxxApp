package net.senink.seninkapp.ui.setting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.util.HttpUtils;

public class ModifyPasswordActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvTitle;
	private Button backBtn;
	public static final String REG = "1";
	private EditText etPwd, etConfirmPwd;
	private Button register_commit;
	private ProgressDialog progressDialog;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modify_password_activity);
		initView();
		setListener();
	}

	private void initView() {
		etPwd = (EditText) findViewById(R.id.register_pwd);
		etConfirmPwd = (EditText) findViewById(R.id.register_confirm_pwd);
		register_commit = (Button) findViewById(R.id.register_commit);
		backBtn = (Button) findViewById(R.id.title_back);
		tvTitle = (TextView) findViewById(R.id.title_name);
		tvTitle.setText(R.string.modify_password);
		backBtn.setVisibility(View.VISIBLE);
		setProgress();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		register_commit.setOnClickListener(this);
	}

	private void setProgress() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(R.string.saving));
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

	public void doClick(View v) {
		Log.i("hxj", "doClick(View v)");
		if (v.getId() == R.id.register_commit) {
			final String pwd = etPwd.getText().toString().trim();
			final String pwdConfirm = etConfirmPwd.getText().toString().trim();

			if (pwd != null && pwd.length() > 0 && pwd.getBytes().length <= 16) {
				if ((pwdConfirm != null) && pwdConfirm.length() > 0
						&& pwd.equals(pwdConfirm)) {
					try {
						progressDialog.show();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					new Thread(new Runnable() {

						@Override
						public void run() {
							final boolean result = HttpUtils
									.submitModifyPasswordData(
											ModifyPasswordActivity.this, pwd);
							handler.post(new Runnable() {

								@Override
								public void run() {
									try {
										progressDialog.dismiss();
									} catch (Exception e) {
										PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
									}
									if (result) {
										Toast.makeText(
												ModifyPasswordActivity.this,
												getResources().getString(
														R.string.update_succe),
												Toast.LENGTH_SHORT).show();
										// utils.putPassword(pwd);
										// utils.putChecked(true);
										finish();
									} else {
										Toast.makeText(
												ModifyPasswordActivity.this,
												getResources()
														.getString(
																R.string.update_password_fail),
												Toast.LENGTH_SHORT).show();
									}
								}
							});

						}
					}).start();

				} else {
					etConfirmPwd.requestFocus();
					Toast.makeText(
							this,
							getResources().getString(
									R.string.no_switch_password),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				etPwd.requestFocus();
				if (null == pwd || pwd.length() == 0) {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.alert_password_no_null),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.alert_password_no_too_long),
							Toast.LENGTH_SHORT).show();
				}
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_commit:
			doClick(v);
			break;
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		default:
			break;
		}
	}
}
