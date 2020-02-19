package net.senink.seninkapp.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.senink.seninkapp.R;

public class TopNavArea extends RelativeLayout implements OnClickListener {
	private Button leftBt;
	private Button rightBt;
	private TextView titleTv;
	private Activity activity;

	public TopNavArea(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TopNavArea(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TopNavArea(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.top_nav_area, this);
		initView();
	}

	private void initView() {
		leftBt = (Button) findViewById(R.id.title_btn);
		leftBt.setOnClickListener(this);
		rightBt = (Button) findViewById(R.id.back);
		rightBt.setOnClickListener(this);
		titleTv = (TextView) findViewById(R.id.title);
		activity = (Activity) getContext();
	}

	public void setVisibilityFromId(int id, int visibility) {
		View v = findViewById(id);
		if (v != null) {
			v.setVisibility(visibility);
		}
	}

	public void setTitle(int resId) {
		titleTv.setText(resId);
	}

	public void setTitle(String resId) {
		titleTv.setText(resId);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_btn:
			activity.finish();
			break;
		case R.id.back:
			activity.finish();
			activity.overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		}

	}

}
