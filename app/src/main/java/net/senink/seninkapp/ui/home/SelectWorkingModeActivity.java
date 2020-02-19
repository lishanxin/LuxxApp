package net.senink.seninkapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISManager;

public class SelectWorkingModeActivity extends BaseActivity implements
		OnClickListener {
	private View backbt, nextBtn;
	private RadioButton ppoeConnect, wiredConnect, wirlessConnect;
	public final static String KUAI_OR_GAO_JI_SETTING_SWICTH="switch";
	private Intent intent;
	private boolean isAddDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_working_mode);
		intent = getIntent();
		isAddDevice = intent.getBooleanExtra("isAddDevice", false);
		initView();
	}

	private void initView() {
		PISManager pm = PISManager.getInstance();
//		PISGWConfig pisgwConfig = (PISGWConfig) pm.getPisByKey(getIntent()
//				.getStringExtra(HomeActivity.VALUE_KEY));
//		pisgwConfig.getAPMod();
		ppoeConnect = (RadioButton) findViewById(R.id.ppoe_connect);
		wiredConnect = (RadioButton) findViewById(R.id.wired_connect);
		wirlessConnect = (RadioButton) findViewById(R.id.wirless_connect);
		// ppoeConnect.setOnCheckedChangeListener(radioListener);
		// wiredConnect.setOnCheckedChangeListener(radioListener);
		// wirlessConnect.setOnCheckedChangeListener(radioListener);

		ppoeConnect.setOnClickListener(this);
		wiredConnect.setOnClickListener(this);
		wirlessConnect.setOnClickListener(this);

		backbt = findViewById(R.id.back);
		nextBtn = findViewById(R.id.selecet_working_mode_next);
		backbt.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.ppoe_connect:
			Intent intent = new Intent(SelectWorkingModeActivity.this,
					InternetAccountSetting.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			intent.putExtra(KUAI_OR_GAO_JI_SETTING_SWICTH, "kuai");
			intent.putExtra("isAddDevice", isAddDevice);
			startActivity(intent);
			overridePendingTransition(0, 0);
			break;
		case R.id.wired_connect:
			intent = new Intent(SelectWorkingModeActivity.this,
					WiredSetting.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			intent.putExtra(KUAI_OR_GAO_JI_SETTING_SWICTH, "kuai");
			intent.putExtra("isAddDevice", isAddDevice);
			startActivity(intent);
			overridePendingTransition(0, 0);
			break;
		case R.id.wirless_connect:
			Log.i("hxj", "wirless_connect");
			intent = new Intent(SelectWorkingModeActivity.this,
					WirlessSetting.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			intent.putExtra(KUAI_OR_GAO_JI_SETTING_SWICTH, "kuai");
			intent.putExtra("isAddDevice", isAddDevice);
			startActivity(intent);
			overridePendingTransition(0, 0);
			break;

		default:
			startActivity(new Intent(this, InternetAccountSetting.class));
			overridePendingTransition(0, 0);
			break;
		}

	}
}
