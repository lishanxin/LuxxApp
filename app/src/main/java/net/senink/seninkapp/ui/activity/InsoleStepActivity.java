package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISXinInsole;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.view.CurveProfileView;
import net.senink.seninkapp.ui.view.LineView;

/**
 * 智能穿戴步数详情界面
 * 
 * @author zhaojunfeng
 * @date 2016-01-30
 * 
 */
public class InsoleStepActivity extends BaseActivity implements
		OnClickListener {
	private final static int MSG_REFRESH_VIEW = 1;
	private PISManager manager;
//	private PISXinInsole mInsole;
	// 标题名称
	private TextView tvTitle;
	// 步数
	private TextView tvSteps;
	// 时间
	private TextView tvTime;
	// 平均步数
	private TextView tvAverage;
	// 今天步数标题
	private TextView tvTodayStep;
	//曲线分布图
	private CurveProfileView curveView;
	// 年月日周天
	private CheckBox cbDay, cbWeek, cbMonth, cbYear;
	// 线
	private LineView dayView, weekView, monthView, yearView;
	// 返回按钮
	private Button backBtn;
	// 标题布局
	private View titleLayout;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_VIEW:
				setView();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insolestep);
		manager = PISManager.getInstance();
		initView();
		setData();
		setListener();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		cbDay.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setDateView(0);
				}
			}
		});
		cbWeek.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setDateView(1);
				}
			}
		});
		cbMonth.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setDateView(2);
				}
			}
		});
		cbYear.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setDateView(3);
				}
			}
		});
	}

	private void setView() {
       
	}

	/**
	 * 日： 0 周： 1 月：2 年： 3
	 * 
	 * @param index
	 */
	private void setDateView(int index) {
		if (index == 0) {
			cbWeek.setChecked(false);
			cbMonth.setChecked(false);
			cbYear.setChecked(false);
			dayView.setCheckedOnLine(true);
			weekView.setCheckedOnLine(false);
			monthView.setCheckedOnLine(false);
			yearView.setCheckedOnLine(false);
		} else if (index == 1) {
			cbDay.setChecked(false);
			cbMonth.setChecked(false);
			cbYear.setChecked(false);
			dayView.setCheckedOnLine(false);
			weekView.setCheckedOnLine(true);
			monthView.setCheckedOnLine(false);
			yearView.setCheckedOnLine(false);
		} else if (index == 2) {
			cbDay.setChecked(false);
			cbWeek.setChecked(false);
			cbYear.setChecked(false);
			dayView.setCheckedOnLine(false);
			weekView.setCheckedOnLine(false);
			monthView.setCheckedOnLine(true);
			yearView.setCheckedOnLine(false);
		} else if (index == 3) {
			cbDay.setChecked(false);
			cbWeek.setChecked(false);
			cbMonth.setChecked(false);
			dayView.setCheckedOnLine(false);
			weekView.setCheckedOnLine(false);
			monthView.setCheckedOnLine(false);
			yearView.setCheckedOnLine(true);
		}
	}

	/**
	 * 获取其他界面跳转的传值
	 */
	private void setData() {
		if (getIntent() != null
				&& getIntent().hasExtra(MessageModel.ACTIVITY_VALUE)) {
			String key = getIntent()
					.getStringExtra(MessageModel.ACTIVITY_VALUE);
			if (!TextUtils.isEmpty(key)) {
//				if (PISManager.cacheMap.get(key) != null) {
//					mInsole = (PISXinInsole) PISManager.cacheMap.get(key);
//				}
			}
		}
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		titleLayout = findViewById(R.id.insolestep_title);
		tvSteps = (TextView) findViewById(R.id.insolestep_steps);
		tvTodayStep = (TextView) findViewById(R.id.insolestep_todaystep);
		tvTime = (TextView) findViewById(R.id.insolestep_time);
		tvAverage = (TextView) findViewById(R.id.insolestep_stepnum);
		cbDay = (CheckBox) findViewById(R.id.insolestep_day);
		cbWeek = (CheckBox) findViewById(R.id.insolestep_week);
		cbMonth = (CheckBox) findViewById(R.id.insolestep_month);
		cbYear = (CheckBox) findViewById(R.id.insolestep_year);
		dayView = (LineView) findViewById(R.id.insolestep_day_line);
		weekView = (LineView) findViewById(R.id.insolestep_week_line);
		monthView = (LineView) findViewById(R.id.insolestep_month_line);
		yearView = (LineView) findViewById(R.id.insolestep_year_line);
		curveView = (CurveProfileView) findViewById(R.id.insolestep_curve);
		setTitle();
		cbDay.setChecked(true);
	}

	/**
	 * 设置标题相关信息
	 */
	private void setTitle() {
		tvTitle.setText(R.string.step);
		backBtn.setVisibility(View.VISIBLE);
		titleLayout.setBackgroundColor(getResources().getColor(
				R.color.insole_title_bgcolor));
	}

	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPipaRequestStatusListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		manager.setOnPipaRequestStatusListener(null);
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		default:
			break;
		}
	}
}
