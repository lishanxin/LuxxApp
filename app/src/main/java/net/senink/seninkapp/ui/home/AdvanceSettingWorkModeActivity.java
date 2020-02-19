package net.senink.seninkapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;

public class AdvanceSettingWorkModeActivity extends BaseActivity implements OnClickListener {
	private View backbt, more_setting, work_mode_item,
			net_lianjia_item, ju_yu_net_item, wu_xian_item;
//	private TextView work_mode, net_lianjia, ju_yu_net, wu_xian;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance_settings_layout);
		initView();
	}

	private void initView() {
		backbt = findViewById(R.id.back);
		// saveBtn = findViewById(R.id.save);
		backbt.setOnClickListener(this);
		// saveBtn.setOnClickListener(this);
		more_setting = findViewById(R.id.more_setting);
		more_setting.setOnClickListener(this);
		work_mode_item = findViewById(R.id.work_mode_item);
		work_mode_item.setOnClickListener(this);
//		work_mode = (TextView) findViewById(R.id.work_mode);
		net_lianjia_item = findViewById(R.id.net_lianjia_item);
		net_lianjia_item.setOnClickListener(this);
		ju_yu_net_item = findViewById(R.id.ju_yu_net_item);
		ju_yu_net_item.setOnClickListener(this);
		wu_xian_item = findViewById(R.id.wu_xian_item);
		wu_xian_item.setOnClickListener(this);
//		net_lianjia = (TextView) findViewById(R.id.net_lianjia);
//		ju_yu_net = (TextView) findViewById(R.id.ju_yu_net);
//		wu_xian = (TextView) findViewById(R.id.wu_xian);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.save:
			this.startActivity(new Intent(this, SettingActivity.class));
			overridePendingTransition(0, 0);
			break;
		case R.id.more_setting:
			popupMenuList(view);
			break;
		case R.id.work_mode_item:
			break;
		case R.id.net_lianjia_item:
			break;
		case R.id.ju_yu_net_item:
			break;
		case R.id.wu_xian_item:
			break;
		default:
			break;
		}

	}

	@SuppressLint("InflateParams")
	private void popupMenuList(View view) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View popupView = inflater.inflate(R.layout.advance_more_menu, null);
		initMenu(popupView);
		final PopupWindow pop = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		pop.setBackgroundDrawable(new ColorDrawable(this.getResources()
				.getColor(R.color.home_menu_bg)));
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAsDropDown(view);
	}

	private void initMenu(View popupView) {

	}
}