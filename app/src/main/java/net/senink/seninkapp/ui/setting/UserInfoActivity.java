package net.senink.seninkapp.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

public class UserInfoActivity extends BaseActivity implements OnClickListener {
	private final static int MSG_SAVE_SUCCESS = 1;
	private final static int MSG_SAVE_FAILED = 2;
	private final static int MSG_TIMEOUT = 3;
	private Button backBtn,saveBtn;
	private TextView tel, email,tvTitle;
	private View modify_password_area;
    private EditText etUser,etAddress;
    private ImageView ivLoading;
    private AnimationDrawable anima;
    
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(android.os.Message msg) {
    		stopAnima();
    		saveBtn.setEnabled(true);
    		removeMessages(MSG_TIMEOUT);
    		switch (msg.what) {
			case MSG_SAVE_SUCCESS:
				TabSettingActivity.userInfo.nickName = etUser.getText().toString();
				TabSettingActivity.userInfo.address = etAddress.getText().toString();
				ToastUtils.showToast(UserInfoActivity.this, R.string.user_infor_save_success);
				backBtn.performClick();
				break;

			case MSG_SAVE_FAILED:
			case MSG_TIMEOUT:
				ToastUtils.showToast(UserInfoActivity.this, R.string.user_infor_save_failed);
				break;
			}
    	}
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_activity);
		initView();
		setListener();
	}

	private void initView() {
		backBtn = (Button)findViewById(R.id.title_back);
		saveBtn = (Button) findViewById(R.id.title_finished);
		tvTitle = (TextView) findViewById(R.id.title_name);
		email = (TextView) findViewById(R.id.email);
		etUser = (EditText) findViewById(R.id.user_name);
		ivLoading = (ImageView) findViewById(R.id.userinfor_loading);
		modify_password_area = findViewById(R.id.modify_password_area);
		etAddress = (EditText) findViewById(R.id.address);
		tel = (TextView) findViewById(R.id.tel);
		setTitle();
		if (TabSettingActivity.userInfo != null) {
			etUser.setText(TabSettingActivity.userInfo.nickName);
			etAddress.setText(TabSettingActivity.userInfo.address);
			tel.setText(TabSettingActivity.userInfo.tel);
			email.setText(TabSettingActivity.userInfo.email);
		}
		tvTitle.setText(R.string.user_info_title);
		etAddress.setEnabled(false);
		etUser.setEnabled(false);
	}

	private void setTitle() {
		backBtn.setVisibility(View.VISIBLE);
		saveBtn.setVisibility(View.VISIBLE);
		saveBtn.setBackgroundColor(Color.TRANSPARENT);
		saveBtn.setText(R.string.Edit);
	}

	private void startAnima(){
		if (null == anima) {
			anima = (AnimationDrawable) ivLoading.getBackground();
		}
		ivLoading.setVisibility(View.VISIBLE);
		anima.start();
		mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, 20000);
	}
	
	private void stopAnima(){
		if (null != anima) {
			anima.stop();
		}
		ivLoading.setVisibility(View.GONE);
	}
	
	private void setListener() {
		modify_password_area.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			if (ivLoading.getVisibility() != View.VISIBLE) {
				finish();
				overridePendingTransition(R.anim.anim_in_from_left,
						R.anim.anim_out_to_right);
			}else{
				stopAnima();
			}
			break;
		case R.id.modify_password_area:
			startActivity(new Intent(this, ModifyPasswordActivity.class));
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		case R.id.title_finished:
			if (!etUser.isEnabled()) {
				saveBtn.setText(R.string.save);
				etAddress.setEnabled(true);
				etUser.setEnabled(true);
			}else{
				save();
			}
			break;
		}
	}

	/**
	 * 保存用户信息
	 */
	private void save() {
		String user = etUser.getText().toString();
		String addr = etAddress.getText().toString();
		if (!TextUtils.isEmpty(user)) {
			saveBtn.setEnabled(false);
			startAnima();
			UserInfo infor = new UserInfo();
			infor.nickName = user;
			if (!TextUtils.isEmpty(addr)) {
				infor.address = addr;
			}
			new Thread(new ModfiyThread(this, infor)).start();
		}else{
			ToastUtils.showToast(this, R.string.user_infor_nickname_empty);
		}
	}

    private class ModfiyThread implements Runnable{
       private Context mContext;
       private UserInfo infor;
    	public ModfiyThread(Context context,UserInfo infor){
    		this.mContext = context;
    		this.infor = infor;
    	}
		@Override
		public void run() {
			boolean state = HttpUtils.modifyUserInfor(mContext, infor);
			if (state) {
				mHandler.sendEmptyMessage(MSG_SAVE_SUCCESS);
			} else {
				mHandler.sendEmptyMessage(MSG_SAVE_FAILED);
			}
		}
    }
}
