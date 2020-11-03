package net.senink.seninkapp.ui.activity;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.datepicker.WheelView;
//import com.datepicker.adapter.NumericWheelAdapter;
//import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.contrarywind.view.WheelView;
import com.kyleduo.switchbutton.SwitchButton;
import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.TelinkApplication;
import com.telink.sig.mesh.model.CommonMeshCommand;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.SigMeshModel;

import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISxinColor;
import net.senink.piservice.struct.LightTimerItem;
import net.senink.piservice.struct.PipaRequestData;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;

import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.LogUtils;

import net.senink.seninkapp.ui.util.ToastUtils;


/**
 * 智能控制之定时器添加界面
 *
 * @author zhaojunfeng
 * @date 2016-02-01
 */
public class LightTimerAddActivity extends BaseActivity implements
        View.OnClickListener {
    private final static String TAG = "AddTimerActivity";
    // 添加成功
    private final static int MSG_TIMER_ADD_SUCCESS = 10;
    // 添加失败
    private final static int MSG_TIMER_ADD_FAILED = 11;
    // 修改成功
    private final static int MSG_TIMER_MODIFY_SUCCESS = 12;
    // 修改失败
    private final static int MSG_TIMER_MODIFY_FAILED = 13;

    // 执行定时器的设置
    private final static int MSG_TELINK_TIMER_MODIFY_EXE = 100;

    // 加载框显示的最长时间
    private final static int SHOW_MAX_TIME = 30000;
    // 超时消息
    private final static int MSG_TIMEOUT = 9;
    // 标题名称
    private TextView tvTitle;
    private ImageView ibTitle;
    // 标题的中的返回按钮
    private Button backBtn;
    //
    // 添加按钮
    private Button saveBtn;
    private ImageView ivLoading;
    private RelativeLayout loadingLayout;
    private RelativeLayout actionLayout;
    private TextView actionName;
    private TextView repeatName;

    private AnimationDrawable anima;
    // 重复开关
    private SwitchButton cbRepeat;


    private WheelView mins;
    private WheelView hours;
    private PISxinColor infor;
    private PISDevice inforDevice;

    private int editMode = 0;   //0 - 新增, 1 - 修改
    // 定时器任务id
    private int timerid = 0;

    private boolean isTelinkDevice = false;
    private int telinkAddress = 0;
    private int hslEleAdr;
    private DeviceInfo telinkDevice;
    private byte[] lastTelinkAction;
    private byte[] telinkScheduleWholeCommand;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_TIMEOUT:
                    removeMessages(MSG_TIMER_ADD_FAILED);
                    removeMessages(MSG_TIMEOUT);
                    hideLoadingDialog();
                    ToastUtils.showToast(LightTimerAddActivity.this,
                            R.string.addtimer_commit_failed);
                    break;
                case MSG_TIMER_ADD_SUCCESS:
                    removeMessages(MSG_TIMER_ADD_FAILED);
                    ToastUtils.showToast(LightTimerAddActivity.this,
                            R.string.addtimer_add_success);
                    setResult(RESULT_OK);
                    backBtn.performClick();
                    break;
                case MSG_TIMER_ADD_FAILED:
                    hideLoadingDialog();
                    removeMessages(MSG_TIMER_ADD_FAILED);
                    ToastUtils.showToast(LightTimerAddActivity.this,
                            R.string.addtimer_add_failed);
                    break;
                case MSG_TIMER_MODIFY_SUCCESS:
                    ToastUtils.showToast(LightTimerAddActivity.this,
                            R.string.addtimer_modify_success);
                    setResult(RESULT_OK);
                    backBtn.performClick();
                    break;
                case MSG_TIMER_MODIFY_FAILED:
                    ToastUtils.showToast(LightTimerAddActivity.this,
                            R.string.addtimer_modify_failed);
                    break;
                case MSG_TELINK_TIMER_MODIFY_EXE:
                    if(telinkScheduleWholeCommand != null){
                        TelinkApiManager.getInstance().setCommonCommand(hslEleAdr, telinkScheduleWholeCommand);
                        TelinkApiManager.getInstance().setCommonCommand(hslEleAdr, telinkScheduleWholeCommand);
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlighttimer);
        initView();
        setData();
        setView();
        initTimePicker();
        setListener();
    }

    @Override
    protected  void onStop(){
        super.onStop();
        //设置完毕定时器，关闭灯光
        if(isTelinkDevice){
            TelinkApiManager.getInstance().setCommonCommand(hslEleAdr, CommonMeshCommand.getOnOffCommand(false));
        }else{
            if (lastRequestData != null){
                infor.request(infor.commitLightOnOff(false));
            }
        }

    }

    /**
     * 给组件赋值
     */
    private void setView() {
        repeatName.setText(R.string.repeat_once);
        cbRepeat.setChecked(false);
    }

    /**
     * 获取传值
     */
    private void setData() {
        Intent intent = getIntent();

        if (intent != null) {
            try {
                String key = intent.getStringExtra(MessageModel.PISBASE_KEYSTR);
                editMode = intent.getIntExtra(MessageModel.ACTIVITY_MODE, 0xFF);
                timerid = intent.getIntExtra(MessageModel.TIMER_ID, 0xFF);

                isTelinkDevice = getIntent().getBooleanExtra(TelinkApiManager.IS_TELINK_DEVICE_KEY, false);
                telinkAddress = getIntent().getIntExtra(TelinkApiManager.TELINK_ADDRESS, 0);

                if (isTelinkDevice) {
                    telinkDevice = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(telinkAddress);
                    if (telinkDevice == null){
                        finish();
                    }else{
                        hslEleAdr = telinkDevice.getTargetEleAdr(SigMeshModel.SIG_MD_LIGHT_HSL_S.modelId);
                    }
                }else{
                    infor = (PISxinColor)PISManager.getInstance().getPISObject(key);
                    inforDevice = infor.getDeviceObject();
                }

            } catch (Exception e) {
                PgyCrashManager.reportCaughtException(this, e);
            }
        }
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        backBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        actionLayout.setOnClickListener(this);

        cbRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    repeatName.setText(R.string.repeat_day);
                }
                else
                    repeatName.setText(R.string.repeat_once);
            }
        });
    }

    /**
     * 初始化时间组件
     */
    private void initTimePicker() {
        int curHours;
        int curMinutes;
        Calendar c = Calendar.getInstance();
        if(isTelinkDevice){

        }else{
            LightTimerItem ti = null;

            if (timerid != 0xFF){
                ti = inforDevice.getTimerItem(timerid);
            }

            if (ti != null){
                c.setTime(new Date(ti.getTime()));
            }
        }

        curHours = c.get(Calendar.HOUR_OF_DAY);
        curMinutes = c.get(Calendar.MINUTE);

        hours = (WheelView) findViewById(R.id.lighttimerlist_hour);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(0, 23); // new NumericWheelAdapter(this, 0, 23, "%02d", true);
//        hours.setViewAdapter(hourAdapter, true);
        hours.setAdapter(hourAdapter);
        hours.setCyclic(true);

//        NumericWheelAdapter minsAdapter = new NumericWheelAdapter(this, 0, 59, "%02d", false);
        NumericWheelAdapter minsAdapter = new NumericWheelAdapter(0, 59);
        mins = (WheelView) findViewById(R.id.lighttimerlist_mins);
//        mins.setViewAdapter(minsAdapter, false);
        mins.setAdapter(minsAdapter);
        mins.setCyclic(true);

        hours.setCurrentItem(curHours);
        mins.setCurrentItem(curMinutes);

        hours.setBackgroundResource(R.color.bg_color);
//        hours.setWheelBackground(R.color.bg_color);
        hours.setBackgroundColor(getResources().getColor(R.color.bg_color));

        mins.setBackgroundResource(R.color.bg_color);
        mins.setBackgroundColor(getResources().getColor(R.color.bg_color));
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.title_name);
        ibTitle = (ImageView) findViewById(R.id.title_logo_center);
        backBtn = (Button) findViewById(R.id.title_back);
        saveBtn = (Button) findViewById(R.id.title_finished);
        ivLoading = (ImageView) findViewById(R.id.addlighttimer_loading);
        loadingLayout = (RelativeLayout) findViewById(R.id.addlighttimer_loading_layout);
        actionLayout  = (RelativeLayout) findViewById(R.id.addlighttime_heat_layout);
        actionName = (TextView) findViewById(R.id.addlighttime_action);
        repeatName = (TextView) findViewById(R.id.tvRepeat);
        cbRepeat = (SwitchButton) findViewById(R.id.cbRepeat);

        // 设置标题内容
        setTitle();
    }

    /**
     * 设置标题的组件
     */
    private void setTitle() {
        //tvTitle.setText(R.string.addtimer_add);
        tvTitle.setVisibility(View.GONE);
        ibTitle.setVisibility(View.VISIBLE);

        saveBtn.setText(R.string.finish);
        saveBtn.setBackgroundColor(Color.TRANSPARENT);

        backBtn.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 显示加载动画
     */
    private void showLoadingDialog() {
        mHandler.removeMessages(MSG_TIMEOUT);
        if (null == anima) {
            anima = (AnimationDrawable) ivLoading.getBackground();
        }
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
        }
        anima.start();
        mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, SHOW_MAX_TIME);
    }

    /**
     * 显示加载动画
     */
    private void hideLoadingDialog() {
        if (anima != null) {
            anima.stop();
        }
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
        mHandler.removeMessages(MSG_TIMEOUT);
    }

    private int[] effectResid = {R.string.effect_spa,
    R.string.effect_sunrise,
    R.string.effect_breath,
    R.string.effect_random};

    private int[] telinkEffectResid = { R.string.effect_sunrise,
            R.string.effect_breath,
            R.string.effect_random,
            R.string.effect_spa
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            LogUtils.i(TAG, "onActivityResult(): resultCode = " + resultCode
                    + ", requestCode = " + requestCode);
            if (requestCode == Constant.REQUEST_CODE_ADDTIMER) {
                if (resultCode == RESULT_OK) {
                    mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
                }
            } else if (requestCode == Constant.REQUEST_CODE_TIMER_ACTION) {
                if (resultCode == RESULT_OK) {
                    if(isTelinkDevice){
                        lastTelinkAction = data.getByteArrayExtra(TelinkApiManager.TELINK_Timer_Action_Data);
                        actionName.setBackgroundColor(Color.TRANSPARENT);
                        actionName.setText("");
                        switch(lastTelinkAction[0]){
                            case CommonMeshCommand.TWINKLE_CMD:{
                                int effectMode = lastTelinkAction[1];
                                int actionResid= effectResid[effectMode];
                                actionName.setText(actionResid);
                            }
                            break;
                            case CommonMeshCommand.ON_OFF_SET:{
                                int onoff = lastTelinkAction[1];
                                int actionResid;
                                if (onoff == 0){
                                    actionResid = R.string.close;
                                }else
                                    actionResid = R.string.open;
                                actionName.setText(actionResid);
                            }
                            break;
                            case CommonMeshCommand.BIBOO_CMD_RGB_SET:{

                                int currentColor = Color.rgb((lastTelinkAction[1] & 0xFF),
                                        (lastTelinkAction[2] & 0xFF),
                                        (lastTelinkAction[3]) & 0xFF);
                                actionName.setBackgroundColor(currentColor);
                            }
                            break;
                            default:
                                break;
                        }
                    }else{
                        lastRequestData = (PipaRequestData) data.getSerializableExtra(MessageModel.PIPAREQUEST_DATA);
                        PICommandProperty prop = infor.getCommandProperty(lastRequestData.Command);
                        actionName.setBackgroundColor(Color.TRANSPARENT);
                        actionName.setText("");
                        switch(lastRequestData.Command){
                            case PISxinColor.PIS_CMD_EFFECT_SET:{
                                int effectMode = lastRequestData.Data[0];
                                int actionResid= effectResid[effectMode];
                                actionName.setText(actionResid);
                            }
                            break;
                            case PISxinColor.PIS_CMD_LIGHT_ONOFF_SET:{
                                int onoff = lastRequestData.Data[0];
                                int actionResid;
                                if (onoff == 0){
                                    actionResid = R.string.close;
                                }else
                                    actionResid = R.string.open;
                                actionName.setText(actionResid);
                            }
                            break;
                            case PISxinColor.PIS_CMD_LIGHT_RGBW_SET:{
                                int maxColor = 0;
                                int tmpColor = 0;
                                byte[] rgbColor = new byte[3];

                                for (int i = 0; i < 3; i++) {
                                    tmpColor = 0xFF & lastRequestData.Data[i];
                                    if (tmpColor > maxColor)
                                        maxColor = tmpColor;
                                }
                                byte coef = (byte)(0xFF/maxColor);
                                rgbColor[0] = (byte)((coef * lastRequestData.Data[0])&0xFF);
                                rgbColor[1] = (byte)((coef * lastRequestData.Data[1])&0xFF);
                                rgbColor[2] = (byte)((coef * lastRequestData.Data[2])&0xFF);

                                int currentColor = Color.rgb((rgbColor[0] & 0xFF),
                                        (rgbColor[1] & 0xFF),
                                        (rgbColor[2]) & 0xFF);
                                actionName.setBackgroundColor(currentColor);
                            }
                            break;
                            default:
                                if (prop != null){
                                    actionName.setText(prop.PICmdName);
                                    actionName.setHint(prop.PICmdTips);
                                }
                                break;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回按钮
            case R.id.title_back:
                if (loadingLayout.getVisibility() == View.GONE) {
                    finish();
                    overridePendingTransition(R.anim.anim_in_from_left,
                            R.anim.anim_out_to_right);
                }
                break;
            case R.id.title_finished:
                save();

                break;
            case R.id.addlighttime_heat_layout:
                if(isTelinkDevice && telinkDevice != null){
                    Intent intent = new Intent(this, LightRGBDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(TelinkApiManager.TELINK_ADDRESS, telinkDevice.meshAddress);
                    bundle.putBoolean(TelinkApiManager.IS_TELINK_KEY, true);
                    bundle.putBoolean(TelinkApiManager.IS_TELINK_GROUP_KEY, false);
                    intent.putExtras(bundle);

                    intent.putExtra(MessageModel.ACTIVITY_MODE, Constant.REQUEST_CODE_TIMER_ACTION);
                    startActivityForResult(intent, Constant.REQUEST_CODE_TIMER_ACTION);
                    overridePendingTransition(R.anim.anim_in_from_right,
                            R.anim.anim_out_to_left);
                }else{
                    Intent intent = new Intent(this, LightRGBDetailActivity.class);
                    intent.putExtra(MessageModel.PISBASE_KEYSTR, infor.getPISKeyString());
                    intent.putExtra(MessageModel.ACTIVITY_MODE, Constant.REQUEST_CODE_TIMER_ACTION);
                    startActivityForResult(intent, Constant.REQUEST_CODE_TIMER_ACTION);
                    overridePendingTransition(R.anim.anim_in_from_right,
                            R.anim.anim_out_to_left);
                }

                break;
            default:
                break;
        }
    }

    /**
     * 保存定时器
     */
    private void save() {
        if(isTelinkDevice){
            if(telinkDevice == null || lastTelinkAction == null){
                ToastUtils.showToast(LightTimerAddActivity.this,
                        R.string.addtimer_action_empty);
                return;
            }
            int cycle = 0;
            if (cbRepeat.isChecked())
                cycle = 1;
            byte[] wholeCommand = CommonMeshCommand.getClockSetCommand(true,
                    cycle,
                    telinkDevice.getIndexOfCustomScheduler(),
                    hours.getCurrentItem(),
                    mins.getCurrentItem(),
                    lastTelinkAction);
            telinkDevice.addCustomSchedule(wholeCommand);
            MyApplication.getInstance().getMesh().saveOrUpdate(this);
            telinkScheduleWholeCommand = wholeCommand;
            mHandler.sendEmptyMessage(MSG_TELINK_TIMER_MODIFY_EXE);

        }else{
            if (infor == null || lastRequestData == null){
                ToastUtils.showToast(LightTimerAddActivity.this,
                        R.string.addtimer_action_empty);
                return;
            }

            int cycle = LightTimerItem.CYCLE_NONE;
            if (cbRepeat.isChecked())
                cycle = LightTimerItem.CYCLE_DAY;

            PipaRequest req  = inforDevice.commitTimerItem(cycle,
                    (hours.getCurrentItem()*3600) + (mins.getCurrentItem()*60),
                    infor.getServiceId(),
                    lastRequestData);
            req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                @Override
                public void onRequestStart(PipaRequest req) {
                    if (loadingLayout.getVisibility() == View.GONE) {
                        showLoadingDialog();
                    }
                }

                @Override
                public void onRequestResult(PipaRequest req) {
                    if (loadingLayout.getVisibility() != View.GONE) {
                        hideLoadingDialog();
                    }
                    if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
                        finish();
                    }
                    else
                        ToastUtils.showToast(LightTimerAddActivity.this,
                                R.string.retry_tips);
                }
            });
            req.setRetry(3);
            inforDevice.request(req);
        }

    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (loadingLayout.getVisibility() == View.GONE) {
            backBtn.performClick();
        } else {
            loadingLayout.setVisibility(View.GONE);
        }
    }
}
