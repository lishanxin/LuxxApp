package net.senink.seninkapp.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;

public class WirelessAdvanceSettingActivity extends BaseActivity implements
		OnClickListener {

	private View backbt, saveBtn;
	private TextView securityModeView, mWEPEncryptionModeView,
			mWPAEncryptionModeView, mWPAKeyAlgorithmView;
	private EditText SSIDView;
	private Intent intent;
	private int requestCode = 0;
	private String securityMode = "None";
	private String keyAlgorithmMode = "SKIP";
	private View WEPView, WPAView;
//	private PISGWConfig pisgwConfig = null;
	private PISManager pm;
	private ProgressDialog progressDialog;
	private EditText wireless_password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wireless_advance_settings_layout);
		initView();
	}

	private void initView() {
		backbt = findViewById(R.id.back);
		saveBtn = findViewById(R.id.save);
		SSIDView = (EditText) findViewById(R.id.ssid);
		securityModeView = (TextView) findViewById(R.id.wireless_security_mode);
		mWEPEncryptionModeView = (TextView) findViewById(R.id.wireless_WEP_encryption_mode);
		mWPAEncryptionModeView = (TextView) findViewById(R.id.wireless_WPA_encryption_mode);
		mWPAKeyAlgorithmView = (TextView) findViewById(R.id.wireless_WPA_key_algorithm);
		WEPView = findViewById(R.id.wireless_wep_layout);
		WPAView = findViewById(R.id.wireless_wpa_layout);
		wireless_password = (EditText) findViewById(R.id.wireless_password);
		backbt.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		securityModeView.setOnClickListener(this);
		mWEPEncryptionModeView.setOnClickListener(this);
		mWPAEncryptionModeView.setOnClickListener(this);
		mWPAKeyAlgorithmView.setOnClickListener(this);
		pm = PISManager.getInstance();
//		pisgwConfig = (PISGWConfig) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
//		progressDialog = new ProgressDialog(this);
//		progressDialog.setCancelable(false);
//		progressDialog.setMessage(getResources().getString(R.string.saving));
//		SSIDView.setText(pisgwConfig.mSSID);
//		mWPAEncryptionModeView.setText(pisgwConfig.get_wpaMode());
//		mWPAEncryptionModeView.setTag(PISGWConfig.WPA_MODE_WPA2);
//		if (pisgwConfig.get_wpaMode() == null
//				|| "".equals(pisgwConfig.get_wpaMode())) {
//			mWPAEncryptionModeView.setText("WPA");
//			mWPAEncryptionModeView.setTag(PISGWConfig.WPA_MODE_WPA);
//		}
//		mWPAKeyAlgorithmView.setText(pisgwConfig.get_cypher());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (intent != null) {
			requestCode = intent.getIntExtra("requestCode", 0);
			if (requestCode != 0) {
				updateViewWithIntentData();
			}
		}
	}

	@Override
	protected void onNewIntent(Intent mIntent) {
		super.onNewIntent(mIntent);
		intent = mIntent;
	}

	private void updateViewWithIntentData() {
		if (intent != null) {
			if (requestCode == 101) {
				securityMode = intent.getStringExtra("securityMode");
				securityModeView.setText(securityMode);
//				if (securityMode.equals("WEP")) {
//					securityModeView
//							.setText(R.string.wireless_security_mode_WEP);
//					WEPView.setVisibility(View.GONE);
//					WPAView.setVisibility(View.GONE);
//				} else if (securityMode.equals(PISGWConfig.SECURITY_MODE_WPA)) {
//					securityModeView
//							.setText(R.string.wireless_security_mode_WPA);
//					WEPView.setVisibility(View.GONE);
//					WPAView.setVisibility(View.VISIBLE);
//				} else {
//					securityModeView
//							.setText(R.string.wireless_security_mode_none);
//					WEPView.setVisibility(View.GONE);
//					WPAView.setVisibility(View.GONE);
//				}
			} else if (requestCode == 102) {
				mWEPEncryptionModeView.setText(intent
						.getStringExtra("encryptionMode"));
				WEPView.setVisibility(View.GONE);
				WPAView.setVisibility(View.GONE);
			} else if (requestCode == 103) {
				mWPAEncryptionModeView.setText(intent
						.getStringExtra("encryptionMode"));
//				if ("WPA".equals(intent.getStringExtra("encryptionMode"))) {
//					mWPAEncryptionModeView.setTag(PISGWConfig.WPA_MODE_WPA);
//				} else if ("WPA2".equals(intent
//						.getStringExtra("encryptionMode"))) {
//					mWPAEncryptionModeView.setTag(PISGWConfig.WPA_MODE_WPA2);
//				}
				WEPView.setVisibility(View.GONE);
				WPAView.setVisibility(View.VISIBLE);
			} else if (requestCode == 104) {
				keyAlgorithmMode = intent.getStringExtra("KeyAlgorithmMode");
				mWPAKeyAlgorithmView.setText(keyAlgorithmMode);

				WEPView.setVisibility(View.GONE);
				WPAView.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		intent.putExtra("SSID", SSIDView.getText());
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.save:
			// this.startActivity(new Intent(this, SettingActivity.class));
			// overridePendingTransition(0, 0);
//			pisgwConfig.setAPMod(String.valueOf(securityModeView.getText()),
//					String.valueOf(mWPAEncryptionModeView.getTag()),
//					String.valueOf(mWPAKeyAlgorithmView.getText()),
//					pisgwConfig.get_apMode(),
//					String.valueOf(SSIDView.getText()),
//					String.valueOf(wireless_password.getText()),
//					pisgwConfig.getWepPrimaryKey(), pisgwConfig.get_wepKey1(),
//					pisgwConfig.get_wepKey2(), pisgwConfig.get_wepKey3(),
//					pisgwConfig.get_wepKey4(), pisgwConfig.get_repSsId(),
//					pisgwConfig.get_repPskKey());
			progressDialog.show();
			break;
		case R.id.wireless_security_mode:
			intent.setClass(this, WirelessInfoSettingActivity.class);
			intent.putExtra("securityMode", securityModeView.getText()
					.toString());
			intent.putExtra("requestCode", 101);
			startActivity(intent);
		case R.id.wireless_WEP_encryption_mode:
			intent.setClass(this, WirelessInfoSettingActivity.class);
			intent.putExtra("encryptionMode", mWEPEncryptionModeView.getText()
					.toString());
			intent.putExtra("requestCode", 102);
			startActivity(intent);
		case R.id.wireless_WPA_encryption_mode:
			intent.setClass(this, WirelessInfoSettingActivity.class);
			intent.putExtra("encryptionMode", mWPAEncryptionModeView.getText()
					.toString());
			intent.putExtra("requestCode", 103);
			startActivity(intent);
		case R.id.wireless_WPA_key_algorithm:
			intent.setClass(this, WirelessInfoSettingActivity.class);
			intent.putExtra("KeyAlgorithmMode", mWPAKeyAlgorithmView.getText()
					.toString());
			intent.putExtra("requestCode", 104);
			startActivity(intent);
		default:
			break;
		}

	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis == null) {
//			return;
//		}
//		if (pis instanceof PISGWConfig) {
//			if (reqType == PISGWConfig.PIS_CMD_CONFIG_SET_APMODE) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE
//						|| result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					try {
//						progressDialog.dismiss();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					finish();
//				}
//			}
//		}
//
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onFirmwareInfoChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		// TODO Auto-generated method stub
//
//	}

}