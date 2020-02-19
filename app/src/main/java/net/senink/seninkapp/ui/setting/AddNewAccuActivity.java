package net.senink.seninkapp.ui.setting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.view.TopNavArea;

public class AddNewAccuActivity extends BaseActivity implements OnClickListener {
	private TopNavArea topNavArea;
	private View backBt;
	int type;
	private EditText accu, password, passwordOk;
	private Button add_accu;
	ProgressDialog dialog;
	private TextView add_succeful_alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_accu_activity);
		initView();

	}

	private Handler handler = new Handler();

	private void initView() {
		topNavArea = (TopNavArea) findViewById(R.id.icon_search_top);
		topNavArea.setTitle(R.string.add_new_accu_title);
		backBt = findViewById(R.id.back);
		backBt.setVisibility(View.GONE);
		type = getIntent().getIntExtra("type", 1);
		accu = (EditText) findViewById(R.id.accu);
		password = (EditText) findViewById(R.id.password);
		passwordOk = (EditText) findViewById(R.id.password_ok);
		add_accu = (Button) findViewById(R.id.add_accu);
		add_accu.setOnClickListener(this);
		add_succeful_alert = (TextView) findViewById(R.id.add_succeful_alert);
		if (type == 2) {
			password.setVisibility(View.GONE);
			passwordOk.setVisibility(View.GONE);
			accu.setHint(R.string.shao_qing_accu);
			add_accu.setText(R.string.send_yao_qing);
		}
		dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		dialog.setMessage(getResources().getString(R.string.wait));
	}

	private void requestData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final boolean flag = HttpUtils.isYaoQingSucced(
						AddNewAccuActivity.this, String.valueOf(accu.getText()));
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (flag) {
							findViewById(R.id.succeful_alert).setVisibility(
									View.VISIBLE);
							password.setVisibility(View.GONE);
							passwordOk.setVisibility(View.GONE);
							accu.setVisibility(View.GONE);
							add_accu.setVisibility(View.GONE);
							add_succeful_alert.setText(getResources()
									.getString(R.string.shou_quan_left)
									+ String.valueOf(accu.getText())
									+ getResources().getString(
											R.string.shou_quan_right));
						} else {
							Toast.makeText(
									AddNewAccuActivity.this,
									getResources().getString(
											R.string.yao_qing_fail),
									Toast.LENGTH_SHORT).show();
						}
						closeDialog();

					}
				});
			}
		}).start();
	}

	private void closeDialog() {
		try {
			dialog.dismiss();
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_accu:
			String accuTxt=String.valueOf(accu.getText()).trim();
			if(accuTxt!=null&&!"".equals(accuTxt)){
				requestData();
				dialog.show();
			}else {
				Toast.makeText(
						AddNewAccuActivity.this,
						getResources().getString(
								R.string.hint_input),
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	@Override
	public void finish() {
		super.finish();
		closeDialog();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDialog();
	}
}
