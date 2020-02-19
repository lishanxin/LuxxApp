package net.senink.seninkapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.activity.DeviceDetailActivity;

public class DeviceManagerActivity extends BaseActivity implements
		OnClickListener {
	private View backbt;
	private View plug001, plug002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);
		initView();

	}

	private void initView() {
		backbt = findViewById(R.id.back);
		plug001 = findViewById(R.id.plug_001);
		plug002 = findViewById(R.id.plug_002);
		backbt.setOnClickListener(this);
		plug001.setOnClickListener(this);
		plug002.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.plug_001:
			this.startActivity(new Intent(this, DeviceDetailActivity.class));
			overridePendingTransition(0, 0);
			break;
		case R.id.plug_002:
			this.startActivity(new Intent(this, DeviceDetailActivity.class));
			overridePendingTransition(0, 0);
			break;
		default:
			break;
		}

	}
}
