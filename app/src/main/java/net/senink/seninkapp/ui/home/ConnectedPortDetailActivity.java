package net.senink.seninkapp.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISGWConfig.ClientInfo;

@SuppressLint("SimpleDateFormat")
public class ConnectedPortDetailActivity extends BaseActivity implements
		OnClickListener {
	private TextView text_in_title, connected_time_digit,
			previous_total_data_info, download_speed;
	private CheckBox visit_internet_radio;
//	ClientInfo clientInfo = null;
	private View backBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connected_port_detail);
		try {
			initView();
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	@SuppressLint("DefaultLocale")
	private void initView() {
//		clientInfo = (ClientInfo) getIntent().getSerializableExtra(
//				HomeActivity.VALUE_KEY);
//		text_in_title = (TextView) findViewById(R.id.text_in_title);
//		text_in_title.setText(clientInfo.getName());
//		visit_internet_radio = (CheckBox) findViewById(R.id.visit_internet_radio);
//		visit_internet_radio.setChecked(clientInfo.isBlack);
//		visit_internet_radio.setOnClickListener(this);
//		connected_time_digit = (TextView) findViewById(R.id.connected_time_digit);
//		// DateFormat formatter = new SimpleDateFormat("hh时mm");
//		// long now = clientInfo.onlineTime*1000;
//		// Calendar calendar = Calendar.getInstance();
//		// calendar.setTimeInMillis(now);
//		long h = clientInfo.onlineTime / 3600;
//		long min = clientInfo.onlineTime / 60;
//		String time = h + "时" + min + "分";
//		connected_time_digit.setText(time);
//		previous_total_data_info = (TextView) findViewById(R.id.previous_total_data_info);
//		String result = String.format("%.2f",
//				(clientInfo.downloadFlow + clientInfo.uploadFlow) / 1024.0f);
//		previous_total_data_info.setText(result + "MB");
//		download_speed = (TextView) findViewById(R.id.download_speed);
//		download_speed.setText(clientInfo.dkbpsTotal + "KB");
//		backBt = findViewById(R.id.back);
//		backBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.visit_internet_radio:
//			if (clientInfo != null && !visit_internet_radio.isChecked()) {
//				clientInfo.addBlackListsUsr(clientInfo._mac, clientInfo._name);
//			} else if (clientInfo != null && visit_internet_radio.isChecked()) {
//				clientInfo.delBlackListsUsr(clientInfo._mac);
//			}
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

}
