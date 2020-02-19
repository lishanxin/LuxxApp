package net.senink.seninkapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;

public class DeviceNameActivity extends BaseActivity implements OnClickListener {
	private EditText deviceName;
	private Button confirmBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_name_setting);
		initView();

	}

	private void initView() {
		deviceName = (EditText) findViewById(R.id.edit_device_name);
		confirmBtn = (Button) findViewById(R.id.confirm_device_name);
		confirmBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		Intent data = new Intent();
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.confirm_device_name:
			if (deviceName != null && deviceName.getText() != null) {
				data.putExtra("device_Name", deviceName.getText());
				setResult(10, data);
			} else {
				Toast.makeText(this,
						getResources().getString(R.string.device_name_hint),
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		finish();

	}

}