package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.model.CommonMeshCommand;
import com.telink.sig.mesh.model.CustomScheduler;
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
import net.senink.seninkapp.ui.util.Utils;
import net.senink.seninkapp.ui.view.listview.SwipeMenu;
import net.senink.seninkapp.ui.view.listview.SwipeMenuCreator;
import net.senink.seninkapp.ui.view.listview.SwipeMenuItem;
import net.senink.seninkapp.ui.view.listview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by wensttu on 2016/8/31.
 */
public class LightTimerListActivity extends BaseActivity implements
        View.OnClickListener{
    private final static String TAG = "TimerListActivity";
    private final static int TIMER_MAX_COUNT = 5;


    // 超时消息
    private final static int MSG_TIMEOUT = 10;
    // 删除成功
    private final static int MSG_DELETE_SUCCESS = 11;
    // 删除失败
    private final static int MSG_DELETE_FAILED = 12;
    // 加载框显示的最长时间
    private final static int SHOW_MAX_TIME = 30000;

    // 标题名称
    private TextView tvTitle;
    private ImageView ivTitle;
    // 标题的中的返回按钮
    private Button backBtn;
    // 添加按钮
    private ImageButton addBtn;

    private AnimationDrawable anima;
    private RelativeLayout loadingLayout;
    private ImageView ivLoading;

    // 显示Timer列表的组件
    private SwipeMenuListView timerListView;
    // 列表组件的适配器
    private LightTimerAdater timerAdapter;
    // Telink列表适配器
    private TelinkLightTimerAdater telinkLightTimerAdater;

    private List<LightTimerItem> timerList;

    private PISxinColor infor;
    private PISDevice inforDevice;

    private PipaRequestData requestData;
    private boolean isTelink = false;
    private boolean isTelinkGroup = false;
    private boolean isTelinkDevice = false;
    private int telinkAddress = 0;
    private int hslEleAdr;
    private DeviceInfo telinkDevice;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_TIMEOUT:

                case MSG_DELETE_FAILED:
                    hideLoadingDialog();
                    removeMessages(MSG_TIMEOUT);
                    ToastUtils.showToast(LightTimerListActivity.this,
                            R.string.del_smart_fail);
                    break;
                case MSG_DELETE_SUCCESS:
                    refreshTimers();
                    hideLoadingDialog();
                    removeMessages(MSG_TIMEOUT);
                    ToastUtils.showToast(LightTimerListActivity.this,
                            R.string.del_smart_succ);
                    sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighttimerlist);

        initView();
        setData();
        setListener();
    }

    /**
     * 获取传值
     */
    private void setData() {
        if (getIntent() == null) {
            finish();
            return;
        }
        try {
            isTelink = getIntent().getBooleanExtra(TelinkApiManager.IS_TELINK_KEY, false);
            isTelinkGroup = getIntent().getBooleanExtra(TelinkApiManager.IS_TELINK_GROUP_KEY, false);
            telinkAddress = getIntent().getIntExtra(TelinkApiManager.TELINK_ADDRESS, 0);
            if(!isTelink){
                String key = getIntent()
                        .getStringExtra(MessageModel.PISBASE_KEYSTR);
                infor = (PISxinColor) PISManager.getInstance().getPISObject(key);
                inforDevice = infor.getDeviceObject();
            }
            isTelinkDevice = !isTelinkGroup && isTelink;
            if (isTelinkDevice) {
                telinkDevice = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(telinkAddress);
                if (telinkDevice == null){
                    finish();
                }else{
                    hslEleAdr = telinkDevice.getTargetEleAdr(SigMeshModel.SIG_MD_LIGHT_HSL_S.modelId);
                }
            }else if(isTelinkGroup){
                finish();
            }else if (infor == null || inforDevice == null){
                finish();
            }
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(LightTimerListActivity.this, e);
            finish();
        }

    }

    /**
     * 更新列表信息
     */
    private void refreshTimers() {
        if(inforDevice != null){
            //对timerList排序
            timerList =inforDevice.getTimerList();
            Collections.sort(timerList, new Comparator<LightTimerItem>() {
                @Override
                public int compare(LightTimerItem lhs, LightTimerItem rhs) {
                    return lhs.getTime() - rhs.getTime();
//                int t1 = lhs.getTime()%(3600*24);
//                int t2 = rhs.getTime()%(3600*24);
//                if (t1 > t2)
//                    return 1;
//                else
//                    return 0;
                }
            });

            if (timerAdapter == null)
                timerAdapter = new LightTimerAdater();
            timerListView.setAdapter(timerAdapter);
            timerAdapter.notifyDataSetChanged();
        }else if(isTelinkDevice){
            if (telinkLightTimerAdater == null)
                telinkLightTimerAdater = new TelinkLightTimerAdater(telinkDevice.getCustomSchedulers());
            timerListView.setAdapter(telinkLightTimerAdater);
            telinkLightTimerAdater.notifyDataSetChanged();
        }
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        backBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        setListViewListener();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.title_name);
        ivTitle = (ImageView) findViewById(R.id.title_logo_center);

        ivLoading = (ImageView) findViewById(R.id.addlighttimer_loading);
        loadingLayout = (RelativeLayout) findViewById(R.id.addlighttimer_loading_layout);

        backBtn = (Button) findViewById(R.id.title_back);
        addBtn  = (ImageButton) findViewById(R.id.title_add);

        timerListView = (SwipeMenuListView) findViewById(R.id.device_list);
//      设置标题内容
        setTitle();
    }

    private void setListViewListener() {
        timerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {



            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(0xFFFD430A));
                deleteItem.setWidth(Utils.dpToPx(50,
                        LightTimerListActivity.this.getResources()));
                deleteItem.setTitle(R.string.delete);
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        timerListView.setMenuCreator(creator);

        timerListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu,
                                           int index) {
                if(inforDevice!= null){
                    LightTimerItem ti = timerAdapter.getItem(position);
                    switch (index) {
                        case 0: {//delete
                            PipaRequest req = inforDevice.removeTimerItem(ti.getTimerId());
                            req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                                @Override
                                public void onRequestStart(PipaRequest req) {
                                    showLoadingDialog();
                                }

                                @Override
                                public void onRequestResult(PipaRequest req) {
                                    hideLoadingDialog();
                                    if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
                                        refreshTimers();
                                    else{
                                        ToastUtils.showToast(LightTimerListActivity.this,
                                                R.string.del_smart_fail);
                                    }
                                }
                            });
                            inforDevice.request(req);
                        }
                        break;
                    }
                }else if(isTelinkDevice && telinkDevice != null){
                    CustomScheduler scheduler = telinkLightTimerAdater.getItem(position);
                    telinkDevice.removeCustomSchedule(scheduler);
                    scheduler.setEnable(false);
                    TelinkApiManager.getInstance().setCommonCommand(hslEleAdr, scheduler.getWholeCommand());
                    refreshTimers();
                }
                return false;
            }
        });
    }

    /**
     * 设置标题的组件
     */
    private void setTitle() {
//        tvTitle.setText(R.string.insole_timer);
        tvTitle.setVisibility(View.GONE);
        ivTitle.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        addBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回按钮
            case R.id.title_back:
                finish();
                overridePendingTransition(R.anim.anim_in_from_left,
                        R.anim.anim_out_to_right);
                break;
            case R.id.title_add:
                try {
                    if(isTelinkDevice){
                        if (telinkDevice == null || telinkDevice.getCustomSchedulers().size() >= TIMER_MAX_COUNT){
                            ToastUtils.showToast(LightTimerListActivity.this,
                                    R.string.addtimer_maximum_count);
                            break;
                        }
                        Intent intent = new Intent(LightTimerListActivity.this, LightTimerAddActivity.class);
                        intent.putExtra(TelinkApiManager.IS_TELINK_DEVICE_KEY, isTelinkDevice);
                        intent.putExtra(TelinkApiManager.TELINK_ADDRESS, telinkAddress);
                        intent.putExtra(MessageModel.ACTIVITY_MODE, 0);
                        startActivityForResult(intent, Constant.REQUEST_CODE_ADDTIMER);
                        overridePendingTransition(R.anim.anim_in_from_right,
                                R.anim.anim_out_to_left);
                    }else{
                        if (inforDevice == null || inforDevice.getTimerList().size() >= TIMER_MAX_COUNT){
                            ToastUtils.showToast(LightTimerListActivity.this,
                                    R.string.addtimer_maximum_count);
                            break;
                        }
                        Intent intent = new Intent(LightTimerListActivity.this, LightTimerAddActivity.class);
                        intent.putExtra(MessageModel.PISBASE_KEYSTR, infor.getPISKeyString());
                        intent.putExtra(MessageModel.ACTIVITY_MODE, 0);
                        startActivityForResult(intent, Constant.REQUEST_CODE_ADDTIMER);
                        overridePendingTransition(R.anim.anim_in_from_right,
                                R.anim.anim_out_to_left);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(infor != null && inforDevice != null){
            if (inforDevice.getTimerList() != null) {
                refreshTimers();
            }

            PipaRequest req = inforDevice.fecthTimerCount();
            req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                @Override
                public void onRequestStart(PipaRequest req) {

                }

                @Override
                public void onRequestResult(PipaRequest req) {
                    if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED)
                        return;
                    List<LightTimerItem> tls = inforDevice.getTimerList();
                    for (LightTimerItem ti : tls){
                        PipaRequest tiReq = inforDevice.fecthTimer(ti.getTimerId());
                        tiReq.setRetry(2);
                        tiReq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                            @Override
                            public void onRequestStart(PipaRequest req) {

                            }

                            @Override
                            public void onRequestResult(PipaRequest req) {
                                if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED)
                                    return;
//                            timerList = new ArrayList<>(inforDevice.getTimerList());
                                refreshTimers();
                            }
                        });
                        inforDevice.request(tiReq);
                    }
                }
            });
            inforDevice.request(req);
        }

        if(isTelinkDevice){
            refreshTimers();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i(TAG, "onActivityResult(): resultCode = " + resultCode
                + ", requestCode = " + requestCode);
        if (requestCode == Constant.REQUEST_CODE_ADDTIMER) {
            if (resultCode == RESULT_OK) {
                mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
//		manager.setOnPipaRequestStatusListener(null);
//		manager.setOnPISChangedListener(null);
    }



    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        backBtn.performClick();
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

    public class LightTimerAdater extends BaseAdapter {
        public LightTimerAdater() {
        }

        @Override
        public int getCount() {
            if (null == timerList) {
                timerList = new ArrayList<>();
            }
            return timerList.size();
        }

        @Override
        public LightTimerItem getItem(int position) {
            return timerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void removeItem(LightTimerItem ti) {
            timerList.remove(ti.getTimerId());
            notifyDataSetChanged();
        }

        private int[] effectResid = {R.string.effect_spa,
                R.string.effect_sunrise,
                R.string.effect_breath,
                R.string.effect_random};
        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            LightTimerItem ti = getItem(position);
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(LightTimerListActivity.this)
                        .inflate(R.layout.item_lighttimer, null);
                holder.actionTv = (TextView) convertView.findViewById(R.id.timer_action);
                holder.timeTv = (TextView) convertView.findViewById(R.id.timer_time);
                holder.repeatTv = (TextView) convertView.findViewById(R.id.timer_repeat);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            try {
                holder.timeTv.setText(ti.getTimeString());
                if (ti.getRepeatType() == LightTimerItem.CYCLE_NONE)
                    holder.repeatTv.setText(R.string.repeat_once);
                else
                    holder.repeatTv.setText(R.string.repeat_day);

                lastRequestData = ti.getRequestData();
                PICommandProperty prop = infor.getCommandProperty(lastRequestData.Command);
                holder.actionTv.setBackgroundColor(Color.TRANSPARENT);
                holder.actionTv.setText("");
                switch(lastRequestData.Command){
                    case PISxinColor.PIS_CMD_EFFECT_SET:{
                        int effectMode = lastRequestData.Data[0];
                        int actionResid= effectResid[effectMode];
                        holder.actionTv.setText(actionResid);
                    }
                    break;
                    case PISxinColor.PIS_CMD_LIGHT_ONOFF_SET:{
                        int onoff = lastRequestData.Data[0];
                        int actionResid;
                        if (onoff == 0){
                            actionResid = R.string.close;
                        }else
                            actionResid = R.string.open;
                        holder.actionTv.setText(actionResid);
                    }
                    break;
                    case PISxinColor.PIS_CMD_LIGHT_RGBW_SET:{
                        //颜色需要放大，否则显示黑色
                        int maxColor = 1;
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
                        holder.actionTv.setBackgroundColor(currentColor);
                    }
                    break;
                    default:
                        if (prop != null){
                            holder.actionTv.setText(prop.PICmdName);
                            holder.actionTv.setHint(prop.PICmdTips);
                        }
                        break;
                }
            }catch (Exception e){
                PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
            }
            return convertView;
        }

        public class Holder {
            TextView actionTv;
            TextView timeTv;
            TextView repeatTv;
        }
    }

    public class TelinkLightTimerAdater extends BaseAdapter {
        private List<CustomScheduler> schedulers;
        public TelinkLightTimerAdater(List<CustomScheduler> data) {
            schedulers = data;
        }

        @Override
        public int getCount() {
            if (null == schedulers) {
               return 0;
            }

            return schedulers.size();
        }

        @Override
        public CustomScheduler getItem(int position) {
            return schedulers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void removeItem(CustomScheduler ti) {
            schedulers.remove(ti);
            notifyDataSetChanged();
        }

        private int[] effectResid = { R.string.effect_sunrise,
                R.string.effect_breath,
                R.string.effect_random,
                R.string.effect_spa
                };
        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            CustomScheduler ti = getItem(position);
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(LightTimerListActivity.this)
                        .inflate(R.layout.item_lighttimer, null);
                holder.actionTv = (TextView) convertView.findViewById(R.id.timer_action);
                holder.timeTv = (TextView) convertView.findViewById(R.id.timer_time);
                holder.repeatTv = (TextView) convertView.findViewById(R.id.timer_repeat);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            try {
                holder.timeTv.setText(ti.getHours() + ":" + ti.getMinutes());
                if (ti.getMode() == CustomScheduler.CYCLE_NONE)
                    holder.repeatTv.setText(R.string.repeat_once);
                else
                    holder.repeatTv.setText(R.string.repeat_day);

                holder.actionTv.setBackgroundColor(Color.TRANSPARENT);
                holder.actionTv.setText("");
                byte[] action = ti.getAction();
                switch(action[0]){
                    case CommonMeshCommand.TWINKLE_CMD:{
                        int effectMode = action[1];
                        int actionResid= effectResid[effectMode];
                        holder.actionTv.setText(actionResid);
                    }
                    break;
                    case CommonMeshCommand.ON_OFF_SET:{
                        int onoff = action[1];
                        int actionResid;
                        if (onoff == 0){
                            actionResid = R.string.close;
                        }else
                            actionResid = R.string.open;
                        holder.actionTv.setText(actionResid);
                    }
                    break;
                    case CommonMeshCommand.BIBOO_CMD_RGB_SET:{

                        int currentColor = Color.rgb((action[1] & 0xFF),
                                (action[2] & 0xFF),
                                (action[3]) & 0xFF);
                        holder.actionTv.setBackgroundColor(currentColor);
                    }
                    break;
                    default:
                        break;
                }
            }catch (Exception e){
                PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
            }
            return convertView;
        }

        public class Holder {
            TextView actionTv;
            TextView timeTv;
            TextView repeatTv;
        }
    }


}