package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISMarquee;
//import com.senink.seninkapp.core.PISMarquee.LightColor;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.WaterDrop;

/**
 * 跑马灯颜色设置界面
 * 
 * @author zhaojunfeng
 * @date 2016-01-20
 */

public class MarqueeColorActivity extends BaseActivity implements
		android.view.View.OnClickListener {
	//超时消息
	private final static int MSG_TIME_OUT = 1;
	//超时时间
	private final static int MAX_TIME = 2000;
	// 进度条
	private SeekBar bgBar, frontBar;
	// 标题
	private TextView tvTitle;
	// 随机按钮
	private CheckBox cbSwitch;
	// 返回按钮
	private Button backBtn;
	// 底色背景组件
	private WaterDrop mWaterDrop,mFrontWaterDrop;
	// 底色背景组件
	private View bgView;
	
	// 两个滚动轴的整个布局
	private RelativeLayout contentLayout;
	// 完成按钮
	private Button finishedBtn;
	// 数据管理器
	private PISManager manager;
	// 跑马灯对象
//	private PISMarquee mPisMarquee;
	private Bitmap bgBitmap = null;
	private int bgColor = 0;
	private int frontColor = 0;
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(android.os.Message msg) {
    		if (msg.what == MSG_TIME_OUT) {
    			ToastUtils.showToast(MarqueeColorActivity.this,
						R.string.marquee_color_set_failed);
			}
    	}
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_marqueecolor);
		manager = PISManager.getInstance();
		setData();
		initView();
		setView();
		setListener();
	}

	/**
	 * 设置组件信息
	 */
	private void setView() {
		setTitle();
		setSwitch();
		setBars();
	}

	/**
	 * 设置开关状态
	 */
	private void setSwitch() {
//		if(mPisMarquee != null && mPisMarquee.getColor() != null){
//		   boolean status = mPisMarquee.getColor().isRandom;
//		   cbSwitch.setChecked(status);
//		   seVisiableOnLayout(!status);
//		}
	}

	/**
	 * 设置进度条的进度
	 */
	private void setBars() {
//		if (mPisMarquee != null && mPisMarquee.getColor() != null) {
//			LightColor mLightColor = mPisMarquee.getColor();
//			int bgRed = (int) (mLightColor.bgRed & 0x00ff);
//			int bgGreen = (int) (mLightColor.bgGreen & 0x00ff);
//			int bgBlue = (int) (mLightColor.bgBlue & 0x00ff);
//			int frontRed = (int) (mLightColor.foreRed & 0x00ff);
//			int frontGreen = (int) (mLightColor.foreGreen & 0x00ff);
//			int frontBlue = (int) (mLightColor.foreBlue & 0x00ff);
//			int bgColor = Color.rgb(bgRed, bgGreen, bgBlue);
//			int frontColor = Color.rgb(frontRed, frontGreen, frontBlue);
//			int bgProgress = getProgress(bgColor);
//			int frontProgress = getProgress(frontColor);
//			if (bgProgress < 0) {
//				bgProgress = 0;
//			} else if (bgProgress > 255) {
//				bgProgress = 255;
//			}
//			if (frontProgress < 0) {
//				frontProgress = 0;
//			} else if (frontProgress > 255) {
//				frontProgress = 255;
//			}
//			bgBar.setProgress(bgProgress);
//			frontBar.setProgress(frontProgress);
//		}
	}

	/**
	 * 设置传值
	 */
	private void setData() {
		if (!TextUtils.isEmpty(getIntent().getStringExtra(
				MessageModel.ACTIVITY_VALUE))) {
			String key = getIntent()
					.getStringExtra(MessageModel.ACTIVITY_VALUE);
//			PISBase base = PISManager.cacheMap.get(key);
//			if (base != null && base.getT1() == PISConstantDefine.PIS_SMART_CELL_T1
//					&& base.getT2() == PISConstantDefine.PIS_SMART_CELL_T2) {
//				mPisMarquee = (PISMarquee) base;
//				if (null == mPisMarquee.getColor()) {
//					mPisMarquee.getLightColor();
//				}
//			}
		}
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		finishedBtn.setOnClickListener(this);
		cbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				seVisiableOnLayout(!isChecked);
			}
		});
		bgBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mWaterDrop.setVisibility(false);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mWaterDrop.setVisibility(true);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mWaterDrop.setProgress(progress,bgBar.getMax());
				bgColor = getBgColor(progress);
			}
		});
		frontBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mFrontWaterDrop.setVisibility(false);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mFrontWaterDrop.setVisibility(true);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mFrontWaterDrop.setProgress(progress,bgBar.getMax());
				frontColor = getBgColor(progress);
			}
		});
	}

	/**
	 * 设置颜色布局是否可见
	 * @param visiable
	 */
	private void seVisiableOnLayout(boolean visiable) {
		if (!visiable) {
			contentLayout.setVisibility(View.GONE);
		} else {
			contentLayout.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 根据进度条获取对应的颜色
	 * 
	 * @param progress
	 * @return
	 */
	private int getBgColor(int progress) {
		return mWaterDrop.getColor();
	}

	private int getFrontColor(int progress) {
		return mFrontWaterDrop.getColor();
	}
	
	/**
	 * 根据颜色值获取进度条的进度
	 * 
	 * @param color
	 * @return
	 */
	private int getProgress(int color) {
		int progress = 0;
		if (bgBitmap != null) {
			int max = bgBitmap.getWidth();
			int y = bgBitmap.getHeight() / 2;
			if (max == 0) {
				max = bgBar.getMax();
			}
			for (int i = 0; i < max; i++) {
				int value = bgBitmap.getPixel(i, y);
				if (color == value) {
					progress = i;
					break;
				}
			}
			progress = 255 * progress / max;
		}
		return progress;
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		cbSwitch = (CheckBox) findViewById(R.id.marqueecolor_switch);
		bgBar = (SeekBar) findViewById(R.id.marqueecolor_bgbar);
		frontBar = (SeekBar) findViewById(R.id.marqueecolor_fore_bar);
		finishedBtn = (Button) findViewById(R.id.title_finished);
		mWaterDrop = (WaterDrop) findViewById(R.id.marqueecolor_bg_view);
		mFrontWaterDrop = (WaterDrop) findViewById(R.id.marqueecolor_front_waterdrop);
		bgView = findViewById(R.id.marqueecolor_bg_view1);
		contentLayout = (RelativeLayout) findViewById(R.id.marqueecolor_content_layout);
		bgBitmap = ((BitmapDrawable) bgView.getBackground()).getBitmap();
		mWaterDrop.setBitmap(bgBitmap);
		mFrontWaterDrop.setBitmap(bgBitmap);
	}

	/**
	 * 设置标题
	 */
	private void setTitle() {
		tvTitle.setText(R.string.color);
		backBtn.setVisibility(View.VISIBLE);
		finishedBtn.setVisibility(View.VISIBLE);
		finishedBtn.setBackgroundColor(Color.TRANSPARENT);
		finishedBtn.setText(R.string.finish);
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

	/**
	 * 提交设置颜色是命令
	 */
	private void commit() {
//		if (mPisMarquee != null) {
//			frontColor = getFrontColor(frontBar.getProgress());
//			bgColor = getBgColor(bgBar.getProgress());
//			mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, MAX_TIME);
//			if (cbSwitch.isChecked()) {
//				mPisMarquee.setLightColor(true, 0, 0, true);
//			} else {
//				mPisMarquee.setLightColor(false, bgColor, frontColor, true);
//			}
//		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.title_finished:
			commit();
			break;
		}
	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis.mT1 == PISConstantDefine.PIS_SMART_CELL_T1
//				&& pis.mT2 == PISConstantDefine.PIS_SMART_CELL_T2) {
//			if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_COLOR_SET) {
//				LogUtils.i("PISMarquee",
//						"PIS_CMD_HORSE_RACE_COLOR_SET -> SUCCESS = " + result);
//				mHandler.removeMessages(MSG_TIME_OUT);
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					manager.upgradePISSmartCell(mPisMarquee);
//					ToastUtils.showToast(this,
//							R.string.marquee_color_set_success);
//					setResult(RESULT_OK);
//					backBtn.performClick();
//				} else {
//					ToastUtils.showToast(this,
//							R.string.marquee_color_set_failed);
//				}
//			} else if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_COLOR_GET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					setSwitch();
//					setBars();
//				}
//			}
//		}
//	}
}
