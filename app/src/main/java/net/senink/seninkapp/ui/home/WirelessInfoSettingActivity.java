package net.senink.seninkapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISGWConfig;

public class WirelessInfoSettingActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	private View securityModeView, WEPEncryptionModeView,
			WPAEncryptionModeView, WPAKeyAlgorithmModeView;
	private View backbt;
//	private String securityMode = PISGWConfig.SECURITY_MODE_NONE;
	private String WEPEncryptionMode = "Open";
	private String WPAEncryptionMode = "WPA";
	private String WPAKeyAlgorithmMode = "TKIP";
	private RadioButton radio01, radio02, radio03;
	private Intent intent;
	private TextView textInTitle;
	private int requestCode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wireless_advance_settings_security_mode_layout);
		intent = getIntent();
		requestCode = intent.getIntExtra("requestCode", 0);
		initView();
		initIntentDate();
	}

	private void initView() {
		backbt = findViewById(R.id.back);
		textInTitle = (TextView) findViewById(R.id.text_in_title);
		backbt.setOnClickListener(this);
	}

	private void initIntentDate() {
		if (intent != null) {
			switch (requestCode) {
			case 101:
				initSecurityMode();
				break;
			case 102:
				initWEPEncryptionMode();
				break;
			case 103:
				initWPAEncryptionMode();
				break;
			case 104:
				initKeyAlgorithmMode();
				break;
			default:
				break;

			}
		}

	}

	private void initSecurityMode() {
		securityModeView = findViewById(R.id.wireless_security_mode_select_layout);
		radio01 = (RadioButton) findViewById(R.id.wireless_security_mode_none);
		radio02 = (RadioButton) findViewById(R.id.wireless_security_mode_wep);
		radio03 = (RadioButton) findViewById(R.id.wireless_security_mode_wpa);
		// radio01.setOnCheckedChangeListener(this);
		radio01.setOnClickListener(this);
		// radio02.setOnCheckedChangeListener(this);
		// radio03.setOnCheckedChangeListener(this);
		radio03.setOnClickListener(this);
		radio02.setOnClickListener(this);
		if (securityModeView != null) {
			securityModeView.setVisibility(View.VISIBLE);
//			securityMode = intent.getStringExtra("securityMode");
//			if (securityMode.equals(PISGWConfig.SECURITY_MODE_WEP)) {
//				radio02.setChecked(true);
//			} else if (securityMode.equals(PISGWConfig.SECURITY_MODE_WPA)) {
//				radio03.setChecked(true);
//			} else {
//				radio01.setChecked(true);
//			}
		}

	}

	private void initWEPEncryptionMode() {
		WEPEncryptionModeView = findViewById(R.id.wireless_WEP_encryption_mode_layout);
		radio01 = (RadioButton) findViewById(R.id.wireless_wep_encryption_mode_open);
		radio02 = (RadioButton) findViewById(R.id.wireless_wep_encryption_mode_share);
		radio01.setOnCheckedChangeListener(this);
		radio02.setOnCheckedChangeListener(this);
		if (WEPEncryptionModeView != null) {
			textInTitle.setText(R.string.wireless_wep_encryption_mode_settings);
			WEPEncryptionModeView.setVisibility(View.VISIBLE);
			WEPEncryptionMode = intent.getStringExtra("encryptionMode");
			if (WEPEncryptionMode.equals("Open")) {
				radio01.setChecked(true);
			} else if (WEPEncryptionMode.equals("Share")) {
				radio02.setChecked(true);
			}
		}

	}

	private void initWPAEncryptionMode() {
		WPAEncryptionModeView = findViewById(R.id.wireless_WPA_encryption_mode_layout);
		radio01 = (RadioButton) findViewById(R.id.wireless_wpa_encryption_mode_wpa);
		radio02 = (RadioButton) findViewById(R.id.wireless_wpa_encryption_mode_wpa2);
		// radio01.setOnCheckedChangeListener(this);
		radio01.setOnClickListener(this);
		radio02.setOnClickListener(this);
		// radio02.setOnCheckedChangeListener(this);
		if (WPAEncryptionModeView != null) {
			textInTitle.setText(R.string.wireless_wpa_encryption_mode_settings);
			WPAEncryptionModeView.setVisibility(View.VISIBLE);
			WPAEncryptionMode = intent.getStringExtra("encryptionMode");
			Log.i("xj", "WPAEncryptionMode==>" + WPAEncryptionMode);
			if (WPAEncryptionMode.equals("WPA")) {
				radio01.setChecked(true);
			} else if (WPAEncryptionMode.equals("WPA2")) {
				radio02.setChecked(true);
			}
		}

	}

	private void initKeyAlgorithmMode() {
		WPAKeyAlgorithmModeView = findViewById(R.id.wireless_WPA_key_algorithm_layout);
		radio01 = (RadioButton) findViewById(R.id.wireless_WPA_key_algorithm_TKIP);
		radio02 = (RadioButton) findViewById(R.id.wireless_WPA_key_algorithm_CCMP);
		radio03 = (RadioButton) findViewById(R.id.wireless_WPA_key_algorithm_TKIPCCMP);
		// radio01.setOnCheckedChangeListener(this);
		// radio02.setOnCheckedChangeListener(this);
		// radio03.setOnCheckedChangeListener(this);
		radio01.setOnClickListener(this);
		radio02.setOnClickListener(this);
		radio03.setOnClickListener(this);
		if (WPAKeyAlgorithmModeView != null) {
			textInTitle.setText(R.string.wireless_wpa_encryption_mode_settings);
			WPAKeyAlgorithmModeView.setVisibility(View.VISIBLE);
			WPAKeyAlgorithmMode = intent.getStringExtra("KeyAlgorithmMode");
//			if (WPAKeyAlgorithmMode.equals(PISGWConfig.CYPHER_TKIP)) {
//				radio01.setChecked(true);
//			} else if (WPAKeyAlgorithmMode.equals(PISGWConfig.CYPHER_CCMP)) {
//				radio02.setChecked(true);
//			} else {
//				radio03.setChecked(true);
//			}
		}

	}

	@Override
	public void onClick(View view) {
		intent.putExtra("SSID", intent.getStringExtra("SSID"));
		intent.putExtra("requestCode", requestCode);
		switch (view.getId()) {
		case R.id.back:
			switch (requestCode) {
			case 101:
				intent.setClass(this, WirelessAdvanceSettingActivity.class);
//				intent.putExtra("securityMode", securityMode);
				startActivity(intent);
				break;
			case 102:
				intent.setClass(this, WirelessAdvanceSettingActivity.class);
				intent.putExtra("encryptionMode", WEPEncryptionMode);
				startActivity(intent);
				break;
			case 103:
				intent.setClass(this, WirelessAdvanceSettingActivity.class);
				intent.putExtra("encryptionMode", WPAEncryptionMode);
				startActivity(intent);
				break;
			case 104:
				intent.setClass(this, WirelessAdvanceSettingActivity.class);
				intent.putExtra("KeyAlgorithmMode", WPAKeyAlgorithmMode);
				startActivity(intent);
				break;
			default:
				break;
			}
			finish();
			break;
		case R.id.save:
			this.startActivity(new Intent(this, SettingActivity.class));
			overridePendingTransition(0, 0);
			finish();
			break;
		case R.id.wireless_security_mode_none:
			intent.setClass(this, WirelessAdvanceSettingActivity.class);
//			intent.putExtra("securityMode", PISGWConfig.SECURITY_MODE_NONE);
			startActivity(intent);
			finish();
			break;
		case R.id.wireless_security_mode_wpa:
			intent.setClass(this, WirelessAdvanceSettingActivity.class);
//			intent.putExtra("securityMode", PISGWConfig.SECURITY_MODE_WPA);
			startActivity(intent);
			finish();
			break;
		case R.id.wireless_wpa_encryption_mode_wpa:
			intent.setClass(this, WirelessAdvanceSettingActivity.class);
			intent.putExtra("encryptionMode", "WPA");
			startActivity(intent);
			break;
		case R.id.wireless_wpa_encryption_mode_wpa2:
			intent.setClass(this, WirelessAdvanceSettingActivity.class);
			intent.putExtra("encryptionMode", "WPA2");
			startActivity(intent);
			break;
		case R.id.wireless_WPA_key_algorithm_TKIP:
			intent.setClass(this, WirelessAdvanceSettingActivity.class);
//			intent.putExtra("KeyAlgorithmMode", PISGWConfig.CYPHER_TKIP);
			startActivity(intent);
			finish();
			break;
		case R.id.wireless_WPA_key_algorithm_CCMP:
			intent.setClass(this, WirelessAdvanceSettingActivity.class);
//			intent.putExtra("KeyAlgorithmMode", PISGWConfig.CYPHER_CCMP);
			startActivity(intent);
			finish();
			break;
		case R.id.wireless_WPA_key_algorithm_TKIPCCMP:
			intent.setClass(this, WirelessAdvanceSettingActivity.class);
//			intent.putExtra("KeyAlgorithmMode", PISGWConfig.CYPHER_TKIP_CCMP);
			startActivity(intent);
			finish();
			break;
		default:
			finish();
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton radioButton, boolean checked) {
//		if (checked) {
//			switch (radioButton.getId()) {
//			case R.id.wireless_security_mode_none:
//				securityMode = PISGWConfig.SECURITY_MODE_NONE;
//				break;
//			case R.id.wireless_security_mode_wep:
//				securityMode = "WEP";
//				break;
//			case R.id.wireless_security_mode_wpa:
//				securityMode = PISGWConfig.SECURITY_MODE_WPA;
//				break;
//			case R.id.wireless_wep_encryption_mode_open:
//				WEPEncryptionMode = "Open";
//				break;
//			case R.id.wireless_wep_encryption_mode_share:
//				WEPEncryptionMode = "Share";
//				break;
//			case R.id.wireless_wpa_encryption_mode_wpa:
//				WPAEncryptionMode = PISGWConfig.WPA_MODE_WPA;
//				break;
//			case R.id.wireless_wpa_encryption_mode_wpa2:
//				WPAEncryptionMode = PISGWConfig.WPA_MODE_WPA2;
//				break;
//			case R.id.wireless_WPA_key_algorithm_TKIP:
//				WPAKeyAlgorithmMode = PISGWConfig.CYPHER_TKIP;
//				break;
//			case R.id.wireless_WPA_key_algorithm_CCMP:
//				WPAKeyAlgorithmMode = PISGWConfig.CYPHER_CCMP;
//				break;
//			case R.id.wireless_WPA_key_algorithm_TKIPCCMP:
//				WPAKeyAlgorithmMode = PISGWConfig.CYPHER_TKIP_CCMP;
//				break;
//			default:
//				break;
//			}
//		}

	}

}