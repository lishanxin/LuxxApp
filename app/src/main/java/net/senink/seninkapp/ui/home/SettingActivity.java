package net.senink.seninkapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;

public class SettingActivity extends BaseActivity implements OnClickListener {
	private View backbt, quickSettingGuide, advanceSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		initView();

	}

	private void initView() {
		backbt = findViewById(R.id.back);
		quickSettingGuide = findViewById(R.id.quick_setting_guide);
		advanceSettings = findViewById(R.id.advanced_settings);
		backbt.setOnClickListener(this);
		quickSettingGuide.setOnClickListener(this);
		advanceSettings.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.quick_setting_guide:
			Intent intent = new Intent(this, SelectWorkingModeActivity.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			startActivity(intent);
			overridePendingTransition(0, 0);
			break;
		case R.id.advanced_settings:
			intent = new Intent(this, AdvanceSettingActivity.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			this.startActivity(intent);
			
			overridePendingTransition(0, 0);
			break;
		default:
			break;
		}

	}
}
