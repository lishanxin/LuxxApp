package net.senink.seninkapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISManager;

public class AdvanceSelectWorkingModeActivity extends BaseActivity implements
		OnClickListener {
	private View backbt, nextBtn;
	private RadioButton biao_zhun, zhong_duan, qiao_jie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance_select_working_mode);
		try {
			initView();
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	private void initView() {
		biao_zhun = (RadioButton) findViewById(R.id.bo_hao);
		zhong_duan = (RadioButton) findViewById(R.id.wu_xian);
		qiao_jie = (RadioButton) findViewById(R.id.you_xian);

		biao_zhun.setOnClickListener(this);
		zhong_duan.setOnClickListener(this);
		qiao_jie.setOnClickListener(this);

		backbt = findViewById(R.id.back);
		nextBtn = findViewById(R.id.selecet_working_mode_next);
		backbt.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		if (AdvanceSettingActivity.mode == AdvanceSettingActivity.BO_HAO_NET_MODE) {
			biao_zhun.setChecked(true);
		} else if (AdvanceSettingActivity.mode == AdvanceSettingActivity.WU_XIAN_MODE) {
			zhong_duan.setChecked(true);
		} else if (AdvanceSettingActivity.mode == AdvanceSettingActivity.YOU_XIAN_MODE) {
			qiao_jie.setChecked(true);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.bo_hao:
			AdvanceSettingActivity.mode = AdvanceSettingActivity.BO_HAO_NET_MODE;
			Intent intent = new Intent(this, InternetAccountSetting.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			startActivity(intent);
			break;
		case R.id.wu_xian:
			AdvanceSettingActivity.mode = AdvanceSettingActivity.WU_XIAN_MODE;
			intent = new Intent(this, WirlessSetting.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			startActivity(intent);
			break;
		case R.id.you_xian:
			AdvanceSettingActivity.mode = AdvanceSettingActivity.YOU_XIAN_MODE;
//			PISManager pm = PISManager.getInstance();
//			PISGWConfig pisgwConfig = (PISGWConfig) pm.getPisByKey(getIntent()
//					.getStringExtra(HomeActivity.VALUE_KEY));
//			pisgwConfig.setAPModStandard();
			finish();
			break;

		default:
			break;
		}

	}
}
