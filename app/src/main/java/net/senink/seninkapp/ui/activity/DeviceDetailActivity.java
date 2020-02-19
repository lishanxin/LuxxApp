package net.senink.seninkapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.home.DevicePostionActivity;

public class DeviceDetailActivity extends BaseActivity implements
		OnClickListener {
	//the button on backing to the last interface
	private View backBtn;
	//the name of the switch
	private TextView deviceName;
	//the layout on location
	private View devicePosition;
	//the name of the position
	private TextView devicePositionInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_detail);
		initView();
	}

	private void initView() {
		backBtn = findViewById(R.id.back);
		deviceName = (TextView) findViewById(R.id.device_name);
		devicePosition = findViewById(R.id.device_position);
		devicePositionInfo = (TextView) findViewById(R.id.device_position_info);
		backBtn.setVisibility(View.VISIBLE);
		setListener();
	}
    
	private void setListener() {
		backBtn.setOnClickListener(this);
		deviceName.setOnClickListener(this);
		devicePosition.setOnClickListener(this);
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
		case R.id.device_name:
			// startActivityForResult(new Intent(this,
			// DeviceNameActivity.class), 10);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		case R.id.device_position:
			startActivityForResult(
					new Intent(this, DevicePostionActivity.class), 20);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 10:
			String mName = data.getExtras().getString("device_name");
			devicePositionInfo.setText(mName);
			break;
		case 20:
			String mPosition = data.getExtras().getString("device_position");
			devicePositionInfo.setText(mPosition);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
