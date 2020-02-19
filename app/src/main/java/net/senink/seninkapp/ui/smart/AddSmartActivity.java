package net.senink.seninkapp.ui.smart;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.view.TopNavArea;

public class AddSmartActivity extends BaseActivity implements OnClickListener {
	private TopNavArea topNavArea;
	private View backBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_smart_activity);
		initView();

	}

	private void initView() {
		topNavArea = (TopNavArea) findViewById(R.id.icon_search_top);
		topNavArea.setTitle(R.string.device_info_title);
		backBt = findViewById(R.id.back);
		backBt.setVisibility(View.GONE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}

	@Override
	public void finish() {
		super.finish();
	}
}
