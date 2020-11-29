package net.senink.seninkapp.ui.activity;


import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.senink.piservice.http.HttpRequest;
import net.senink.piservice.http.HttpUserInfo;
import net.senink.piservice.http.PISHttpManager;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.Foreground;
import net.senink.seninkapp.R;
import net.senink.seninkapp.sqlite.UserService;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.util.Config;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.StringUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pinm.PINMoMC.PinmOverMC;
import net.senink.piservice.pis.PISManager;

@SuppressWarnings("deprecation")
public class LoginActivity extends BaseActivity implements
        View.OnClickListener {
    // 被选中的名称
    private static final int MSG_HAS_SELECTED = 10;
    //登录超时
    private static final int MSG_LOGIN_TIMEOUT = 11;
    private View reg, forget_password, login;
    private ImageView head_icon;
    private EditText login_username, login_password;
    private ProgressDialog progressDialog;
    private CheckBox login_save_password;
    private CheckBox login_local;

    private ImageView login_logo;
    private UserService mUserService;

    //PINM Receiver
    private BroadcastReceiver mPinmReceiver = null;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_HAS_SELECTED) {
                if (msg.obj != null) {
                    try {
                        mCurrentUser = (UserInfo) msg.obj;
                        initViewData();

                        loginLayout(login_local.isChecked(), mCurrentUser != null ? mCurrentUser.loginUser : null);
                    }catch (Exception e){
                        PgyCrashManager.reportCaughtException(LoginActivity.this, e);
                    }
                }
            } else if (msg.what == MSG_LOGIN_TIMEOUT) {
                loginFailed();
            }
        }
    };

    private UserInfo mCurrentUser = null;
//    private List<UserInfo> mUserInfos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_login);
            mUserService = new UserService(this);
//            if (!SharePreferenceUtils.isFirst(this)) {
//                if (net.senink.seninkapp.BuildConfig.ManufacturerID == ProductClassifyInfo.MFID_SENINK) {
//                    firstUse();
//                }
//                PgyCrashManager.reportCaughtException(LoginActivity.this, new Exception("first run Application"));
//                SharePreferenceUtils.setIsFirst(LoginActivity.this);
//            }

            initView();

        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(LoginActivity.this, e);
        }
    }

//    private void umengInit() {
        // mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        // mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "100424468",
        // "c7394704798a158208a74ab60104f0ba");
        // qqSsoHandler.addToSocialSDK();
        // UMWXHandler wxHandler = new UMWXHandler(this,appId,appSecret);
        // wxHandler.addToSocialSDK();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** 使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
//                requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
    }


    @Override
    public void onBackPressed() {
        Intent aintent = new Intent(LoginActivity.this, HomeActivity.class);
        LoginActivity.this.setResult(RESULT_CANCELED, aintent);
        finish();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        try {
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels; // 屏幕宽度（像素）
            int height = metric.heightPixels; // 屏幕高度（像素）
            float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
            int densityDpi = metric.densityDpi;
            LogUtils.i("Login", "width = " + width + ",height = " + height
                    + ",densityDpi = " + densityDpi + ",density = " + density);

//            mUserInfos = SharePreferenceUtils.getInstance(this).loadUserList();
            String user = SharePreferenceUtils.getInstance(this).getCurrentUser();
            if (user != null) {
                mCurrentUser = SharePreferenceUtils.getInstance(this).loadUser(user);
            }

            initViewData();
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(LoginActivity.this, e);
        }
    }

    private void initView() {
        reg = findViewById(R.id.btn_login_rigester);
        forget_password = findViewById(R.id.btn_forget_password);
        login = findViewById(R.id.btn_login);
        head_icon = (ImageView) findViewById(R.id.login_head_icon);
        login_username = (EditText) findViewById(R.id.login_username);
        login_save_password = (CheckBox) findViewById(R.id.login_save_password);
        login_local = (CheckBox)findViewById(R.id.login_local);
        login_password = (EditText) findViewById(R.id.login_password);
        login_logo = (ImageView) findViewById(R.id.login_icon);

        login_logo.setBackgroundResource(net.senink.seninkapp.BuildConfig.Login_icon);
        login_local.setChecked(net.senink.seninkapp.BuildConfig.isLocal_default);
        login_save_password.setChecked(net.senink.seninkapp.BuildConfig.isSave_default);

        if (net.senink.seninkapp.BuildConfig.isLocal_visible)
            login_local.setVisibility(View.VISIBLE);
        else
            login_local.setVisibility(View.INVISIBLE);

        if (net.senink.seninkapp.BuildConfig.isSave_visible)
            login_save_password.setVisibility(View.VISIBLE);
        else
            login_save_password.setVisibility(View.INVISIBLE);

        loginLayout(login_local.isChecked(), null);
        try {
            Activity home = Foreground.getHomeActivity();
            if (home != null){
                final String userPath = SharePreferenceUtils.getUserDataPath(home,
                        SharePreferenceUtils.FILENAME_PISMANAGER,
                        getString(R.string.local_user_name));
                PISManager mgr = PISManager.loadPISManagerObject(home, userPath);
                String pwd = null;
                if (mgr != null) {
                    pwd = mgr.getUserObject().pwd;
                }
                if(pwd != null){
                    login_username.setText(pwd);
                    login_password.setText(pwd);
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        setListener();

        setProgressDialog();


    }

    private void initViewData() {
        if (mCurrentUser != null){
            if (mCurrentUser.hasCloud)
                login_username.setText(mCurrentUser.loginUser);
            else {
                login_username.setText("");
                login_username.setAlpha(0.1f);
            }
            login_local.setChecked(!mCurrentUser.hasCloud);
            login_username.setEnabled(mCurrentUser.hasCloud);
            forget_password.setEnabled(mCurrentUser.hasCloud);
            reg.setEnabled(mCurrentUser.hasCloud);

            login_save_password.setChecked(mCurrentUser.isAuto);
            login_local.setChecked(!mCurrentUser.hasCloud);
            if (mCurrentUser.isAuto) {
                login_password.setText(mCurrentUser.pwd);
            }
        }
    }

    private void setListener() {
        forget_password.setOnClickListener(this);
        reg.setOnClickListener(this);
        login.setOnClickListener(this);
        login_username.setOnClickListener(this);
        login_save_password.setOnClickListener(this);
        login_local.setOnClickListener(this);
    }

    private void setProgressDialog() {
        if (progressDialog != null)
            return;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.logining));
    }


    private void loginLayout(boolean isLocal, String curUser){
        forget_password.setEnabled(!isLocal);
        reg.setEnabled(!isLocal);
        if (isLocal){
            head_icon.setImageResource(R.drawable.icon_password);
            if (curUser == null) {
                login_username.setHint(R.string.input_password);
                login_password.setHint(R.string.re_input_password);

                login_username.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            }
            else {
                login_username.setEnabled(false);
            }
        }
        else{
            head_icon.setImageResource(R.drawable.icon_user);
            login_username.setHint(R.string.username_hint);
            login_password.setHint(R.string.password_hint);
            login_username.setEnabled(true);

            // 显示为密码
            login_username.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        }
    }

    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.btn_login_rigester:
                startActivity(new Intent(this, RegisterActivity.class));
                overridePendingTransition(R.anim.anim_in_from_right,
                        R.anim.anim_out_to_left);
                break;
            case R.id.btn_forget_password:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                overridePendingTransition(R.anim.anim_in_from_right,
                        R.anim.anim_out_to_left);
                break;
            case R.id.btn_login:
                String user;
                String pwd = login_password.getText().toString();
                user = getString(R.string.local_user_name);
                UserInfo localUser = SharePreferenceUtils.getInstance(LoginActivity.this)
                        .loadUser(user);
                //如果localUser为null，则表示本地账号第一次登陆
                if (localUser == null) {
                    if (login_username.getText().toString().compareTo(
                            login_password.getText().toString()) != 0) {
                        ToastUtils.showToast(this, R.string.no_switch_password);
                        return;
                    }
                }
                else if (localUser.pwd.compareTo(pwd) != 0){
                    ToastUtils.showToast(this, R.string.login_error_userorpasswd);
                    return;
                }

                if (TextUtils.isEmpty(user)) {
                    ToastUtils.showToast(this, R.string.zhang_hao_no_null);
                } else if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.showToast(this, R.string.alert_password_no_null);
                } else {
                    pwd = pwd.trim();
                    user = user.trim();
                    loginProcess(user, pwd);
                }
                break;
//            case R.id.login_save_password:
//			SharePreferenceUtils.getInstance(this).putChecked(login_save_password.isChecked());
//                break;
//            case R.id.login_local: {
//                try {
//                    UserInfo localUser = SharePreferenceUtils.getInstance(LoginActivity.this)
//                            .loadUser(getString(R.string.local_user_name));
//                    loginLayout(login_local.isChecked(), localUser != null ? localUser.loginUser : null);
//                }catch (Exception e){
//                    PgyCrashManager.reportCaughtException(LoginActivity.this, e);
//                }
//            }
//                break;
//            case R.id.show_more_user_switch:
//                try {
//                    int width = login.getWidth();
//                    if (usersDialog == null) {
//                        setPopupWindow();
//                    }
//                    usersDialog.showAsDropDown(show_more_user_switch,
//                            -((int) login.getX() + 4 * width / 7 + 4), 32);
//                }catch (Exception e){
//                    PgyCrashManager.reportCaughtException(LoginActivity.this, e);
//                }
//                break;
            default:
                break;
        }

    }

    /**
     * 设置超时时间
     */
    private void setTimeOut() {
        mHandler.sendEmptyMessageDelayed(MSG_LOGIN_TIMEOUT, 10000);
    }

    private void setReceiver(final UserInfo ui){
        if (mPinmReceiver == null) {
//            final String userPath = SharePreferenceUtils.getUserDataPath(LoginActivity.this,
//                    SharePreferenceUtils.FILENAME_PISMANAGER,
//                    ui.loginUser);
            mPinmReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!intent.getAction().equals(PinmInterface.PINM_CONNECT_ACTION))
                        return;
                    try {
                        Bundle bundle = intent.getExtras();
//                    PinmInterface pif = (PinmInterface) bundle.get(PinmInterface.PINM_CONNECT_ExtraObject);
                        PinmInterface pif = PISManager.getInstance()
                                .getPinmObjectWithType((int) bundle.get(PinmInterface.PINM_CONNECT_ExtraObject));
                        int newStatus = bundle.getInt(PinmInterface.PINM_CONNECT_ExtraStatus);

                        if (pif.getConnectType() != PinmInterface.TYPE_MC)
                            return;

                        switch (newStatus) {
                            case PinmInterface.PINM_CONNECT_STATUS_CONNECTED: {
                                loginSuccessed(pif);
                            }
                            break;
                            case PinmInterface.PINM_CONNECT_STATUS_CONNECT_TIMEOUT:
                                ToastUtils.showToast(getApplicationContext(), R.string.login_fail);
                                break;
                            case PinmInterface.PINM_CONNECT_STATUS_CONNECTING:
                                break;
                            case PinmInterface.PINM_CONNECT_STATUS_USERPASSWD_ERROR:
                                ToastUtils.showToast(getApplicationContext(), R.string.login_error_userorpasswd);
                                break;
                        }
                    }catch (Exception e){
                        PgyCrashManager.reportCaughtException(LoginActivity.this, e);
                    }
                }
            };
            this.getApplicationContext().registerReceiver(mPinmReceiver,
                    new IntentFilter(PinmInterface.PINM_CONNECT_ACTION));
        }
    }

    private void localLogin(PISManager mgr, UserInfo uInfo){

        mgr.Start();

        Activity home = Foreground.getHomeActivity();
        if (uInfo.isAuto) {
            SharePreferenceUtils.getInstance(home).saveUser(uInfo);
            SharePreferenceUtils.getInstance(home).setCurrentUser(uInfo.loginUser);
        }
        Intent aintent = new Intent(LoginActivity.this, HomeActivity.class);
        Bundle bUserinfo = new Bundle();
        bUserinfo.putSerializable("userinfo", uInfo);
        aintent.putExtras(bUserinfo);


        LoginActivity.this.setResult(RESULT_OK, aintent); //这理有2个参数(int resultCode, Intent intent)
        finish();
    }

    private void remoteLogin(PISManager mgr, UserInfo uInfo){
        if (progressDialog != null) {
            progressDialog.show();
            setTimeOut();
        }

        /**以下处理需要连接云端的处理*/
        mgr.Start();

        /**当前每一种类型的PINM链接只允许存在一个实例*/
        PinmOverMC.MCParameter mcPara = null;
        PinmInterface pom = mgr.getPinmObjectWithType(PinmInterface.TYPE_MC);
        if (pom == null) {
            mcPara = (PinmOverMC.MCParameter)PinmOverMC.CreateParameter();
            mcPara.Username = uInfo.loginUser;
            mcPara.Password = StringUtils.MD5(StringUtils.MD5(uInfo.pwd));
            mcPara.HostUrl = Config.HOST_IP;
            pom = new PinmOverMC(mcPara);
        }
        else{
            mcPara = (PinmOverMC.MCParameter)pom.getParameter();
            if (mcPara.Username.compareTo(uInfo.loginUser) != 0){
                mcPara = (PinmOverMC.MCParameter)PinmOverMC.CreateParameter();
                mcPara.Username = uInfo.loginUser;
                mcPara.Password = StringUtils.MD5(StringUtils.MD5(uInfo.pwd));
                mcPara.HostUrl  = Config.HOST_IP;
            }
        }

        switch(mgr.Connect(pom, mcPara, LoginActivity.this)){
            case PinmInterface.PINM_RESULT_SUCCESSED:
                if (pom.getStatus() == PinmInterface.PINM_CONNECT_STATUS_CONNECTED)
                    loginSuccessed(pom);
                else
                    setReceiver(uInfo);
                break;
            case PinmInterface.PINM_RESULT_NETWORK_INVAILD:
                ToastUtils.showToast(getApplicationContext(), R.string.login_invaild_network);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                break;
            default:
                ToastUtils.showToast(getApplicationContext(), R.string.login_fail);
                break;
        }
    }

    private void loginProcess(String user, String pwd){
        UserInfo uInfo = new UserInfo();
        uInfo.loginUser = user;
        uInfo.pwd = pwd;
        uInfo.isAuto = login_save_password.isChecked();
        uInfo.hasCloud = !(login_local.isChecked());

        try{
            Activity home = Foreground.getHomeActivity();
            if (home == null)
                return;
            final String userPath = SharePreferenceUtils.getUserDataPath(home,
                    SharePreferenceUtils.FILENAME_PISMANAGER,
                    uInfo.loginUser);
            PISManager mgr = PISManager.loadPISManagerObject(home, userPath);

            if (mgr == null){
                mgr = PISManager.getInstance(home, uInfo, uInfo.hasCloud);
                PISManager.SavePISManagerObject(mgr, userPath);
            }
            else{
                mgr.getUserObject().loginUser = uInfo.loginUser;
                mgr.getUserObject().pwd = uInfo.pwd;
                mgr.getUserObject().isAuto = uInfo.isAuto;
                mgr.getUserObject().hasCloud = uInfo.hasCloud;
            }
            if (!uInfo.hasCloud){
                /**检查是否已经存在PISManager记录，如果有则验证密码*/
                if (mgr.getUserObject().loginUser.compareTo(uInfo.loginUser) != 0 ||
                        mgr.getUserObject().pwd.compareTo(uInfo.pwd) != 0) {
                    ToastUtils.showToast(getApplicationContext(), R.string.login_error_userorpasswd);
                    return;
                }
                localLogin(mgr, uInfo);
            }
            else{
                remoteLogin(mgr, uInfo);
            }
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(LoginActivity.this, e);
        }
    }
    private void loginSuccessed(PinmInterface pif){
        if (progressDialog != null) {
            progressDialog.dismiss();
            mHandler.removeMessages(MSG_LOGIN_TIMEOUT);
        }
        final UserInfo uInfo = PISManager.getInstance().getUserObject();
        if (uInfo.isAuto) {
            SharePreferenceUtils.getInstance(LoginActivity.this).saveUser(uInfo);
            SharePreferenceUtils.getInstance(LoginActivity.this).setCurrentUser(uInfo.loginUser);
            //从服务器下载完整的用户信息
            HttpRequest req = (new HttpUserInfo()).updateUserInfo();
            req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
                @Override
                public void onRequestStart(HttpRequest req) {

                }

                @Override
                public void onRequestResult(HttpRequest req) {
                    PISManager pm = PISManager.getInstance();
                    HttpUserInfo.userInfoHttpRequest uReq = (HttpUserInfo.userInfoHttpRequest)req;
                    UserInfo userinfo = uReq.getUserInfo();
                    if (userinfo == null || userinfo.loginUser.compareTo(uInfo.loginUser) != 0)
                        return;
                    pm.setUserObject(userinfo);

                    SharePreferenceUtils.getInstance(LoginActivity.this).saveUser(pm.getUserObject());
                    SharePreferenceUtils.getInstance(LoginActivity.this).setCurrentUser(pm.getUserObject().loginUser);
                }
            });
            PISManager.getPISHttpManager().request(req);
        }
        Intent aintent = new Intent(LoginActivity.this, HomeActivity.class);
        Bundle bUserinfo = new Bundle();
        bUserinfo.putSerializable("userinfo", uInfo);
        aintent.putExtras(bUserinfo);

        LoginActivity.this.setResult(RESULT_OK, aintent); //这理有2个参数(int resultCode, Intent intent)

        finish();
    }
    private void loginFailed() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        PinmInterface pom = PISManager.getInstance().getPinmObjectWithType(PinmInterface.TYPE_MC);
        switch (pom.getStatus()){
            case PinmInterface.PINM_CONNECT_STATUS_USERPASSWD_ERROR:
                ToastUtils.showToast(getApplicationContext(), R.string.login_error_userorpasswd);
                break;
            case PinmInterface.PINM_RESULT_NETWORK_INVAILD:
                ToastUtils.showToast(getApplicationContext(), R.string.login_invaild_network);
                break;
            default:
                ToastUtils.showToast(getApplicationContext(), R.string.login_fail);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    class UserListAdapter extends BaseAdapter {
        private List<UserInfo> infos;

        public UserListAdapter() {
            infos = SharePreferenceUtils.getInstance(LoginActivity.this).loadUserList();
        }


        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public UserInfo getItem(int arg0) {
            Log.i("hxj", "arg0==>" + arg0);
            return infos.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View view, ViewGroup arg2) {
            UserInfo userInfo = getItem(position);
            if (view == null) {
                view = LayoutInflater.from(LoginActivity.this).inflate(
                        R.layout.more_user_item, null);
            }
            final ImageView icon = (ImageView) view.findViewById(R.id.icon);
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(userInfo.loginUser);

            if (userInfo.userIcon != null){
                icon.setBackground(new BitmapDrawable(userInfo.userIcon));
            }else if(userInfo.nickSrcMid != null){
                    HttpUserInfo httpUInfo = new HttpUserInfo();
                    HttpRequest req = httpUInfo.updateUserHeaderImage(userInfo.nickSrcMid);
                    req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(HttpRequest req) {

                        }

                        @Override
                        public void onRequestResult(HttpRequest req) {
//                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
//                                icon.src(new BitmapDrawable(((HttpUserInfo.HeaderImageRequest)req).image));
                        }
                    });
                    PISHttpManager.getInstance(LoginActivity.this).request(req);
            }
            view.setTag(userInfo);
            return view;
        }

        private Handler handler = new Handler();

        class LoadIcon implements Runnable {
            private String iconUrl;
            private ImageView imageView;
            Bitmap bit;

            public LoadIcon(ImageView imageView, String iconUrl) {
                this.iconUrl = iconUrl;
                this.imageView = imageView;
            }

            @Override
            public void run() {
                if (iconUrl != null) {
                    try {
                        bit = HttpUtils.getImage(LoginActivity.this,
                                iconUrl, null);
                        int radius = (bit.getWidth() < bit.getHeight() ? bit
                                .getWidth() : bit.getHeight()) / 2;
                        bit = SDCardUtils.getCroppedRoundBitmap(bit, radius);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                imageView.setImageBitmap(bit);
                            }
                        });
                    } catch (Exception e) {
                        PgyCrashManager.reportCaughtException(LoginActivity.this, e);
                    }
                }
            }
        }
    }
}