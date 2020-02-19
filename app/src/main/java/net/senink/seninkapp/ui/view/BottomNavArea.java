package net.senink.seninkapp.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.setting.TabSettingActivity;
import net.senink.seninkapp.ui.smart.SmartMainActivity;

public class BottomNavArea extends RelativeLayout implements OnClickListener {

	private TextView mEquipmentPage, mSmartPage, mSettingPage;
	private ImageView equipment_icon,smart_icon,setting_icon;

	public BottomNavArea(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BottomNavArea(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BottomNavArea(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.bottom_nav_area,
				this);
		initView();
	}

	

	private void initView() {
		mEquipmentPage = (TextView) findViewById(R.id.equipment);
		mSmartPage = (TextView) findViewById(R.id.smart);
		mSettingPage = (TextView) findViewById(R.id.setting);
		
		equipment_icon = (ImageView) findViewById(R.id.equipment_icon);
		smart_icon = (ImageView) findViewById(R.id.smart_icon);
		setting_icon = (ImageView) findViewById(R.id.setting_icon);
		
	
		
		findViewById(R.id.equipment_area).setOnClickListener(this);
		findViewById(R.id.setting_area).setOnClickListener(this);
		/**暂时取消智能功能*/
//		findViewById(R.id.smart_area).setOnClickListener(this);
		smart_icon.setClickable(false);

		String temp = String.valueOf(getContentDescription());
//		if ("setting".equals(temp)) {
//			findViewById(R.id.setting_area).setBackgroundColor(0x55ffffff);
//			findViewById(R.id.setting_area).setOnClickListener(null);
//			mSettingPage.setTextColor(0xff4C82BE);
//			setting_icon.setImageResource(R.drawable.icon_menu03_link);
//		} else if ("home".equals(temp)) {
//			findViewById(R.id.equipment_area).setBackgroundColor(0x55ffffff);
//			findViewById(R.id.equipment_area).setOnClickListener(null);
//			mEquipmentPage.setTextColor(0xff4C82BE);
//			equipment_icon.setImageResource(R.drawable.icon_menu01_link);
//		} else if ("smart".equals(temp)) {
//			findViewById(R.id.smart_area).setBackgroundColor(0x55ffffff);
//			findViewById(R.id.smart_area).setOnClickListener(null);
//			mSmartPage.setTextColor(0xff4C82BE);
//			smart_icon.setImageResource(R.drawable.icon_menu02_link);
//		}
		updateSelectState(temp);
	
	}
	public void updateSelectState(String temp){
		mSettingPage.setTextColor(0xff888888);
		mEquipmentPage.setTextColor(0xff888888);
		mSmartPage.setTextColor(0xff888888);
//		findViewById(R.id.setting_area).setBackgroundColor(0xffdddddd);
		equipment_icon.setImageResource(R.drawable.icon_menu01_link_normal);
		smart_icon.setImageResource(R.drawable.icon_menu02_link_normal);
		setting_icon.setImageResource(R.drawable.icon_menu03_link_normal);
		if ("setting".equals(temp)) {
//			findViewById(R.id.setting_area).setBackgroundColor(0x55ffffff);
			findViewById(R.id.setting_area).setOnClickListener(null);
			mSettingPage.setTextColor(0xffF28c08);
			setting_icon.setImageResource(R.drawable.icon_menu03_link);
		} else if ("home".equals(temp)) {
//			findViewById(R.id.equipment_area).setBackgroundColor(0x55ffffff);
			findViewById(R.id.equipment_area).setOnClickListener(null);
			mEquipmentPage.setTextColor(0xffF28c08);
			equipment_icon.setImageResource(R.drawable.icon_menu01_link);
		} else if ("smart".equals(temp)) {
//			findViewById(R.id.smart_area).setBackgroundColor(0x55ffffff);
			findViewById(R.id.smart_area).setOnClickListener(null);
			mSmartPage.setTextColor(0xffF28c08);
			smart_icon.setImageResource(R.drawable.icon_menu02_link);
		}
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.equipment_area:
			getContext().startActivity(
					new Intent(getContext(), HomeActivity.class));
			((Activity) getContext()).overridePendingTransition(0, 0);
			
			break;
		case R.id.smart_area:
			getContext().startActivity(
					new Intent(getContext(), SmartMainActivity.class));
			((Activity) getContext()).overridePendingTransition(0, 0);
			break;
		case R.id.setting_area:
			getContext().startActivity(
					new Intent(getContext(), TabSettingActivity.class));
			((Activity) getContext()).overridePendingTransition(0, 0);
			break;

		}

	}

}
