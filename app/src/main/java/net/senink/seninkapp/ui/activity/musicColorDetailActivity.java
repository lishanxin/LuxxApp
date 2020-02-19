package net.senink.seninkapp.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.LuxxMusicColor;
import net.senink.seninkapp.R;
import net.senink.seninkapp.service.musicService;
import java.util.List;

/**
 * Created by wensttu on 2016/7/30.
 */
public class musicColorDetailActivity extends LightRGBDetailActivity implements
        View.OnClickListener{
    private static final String TAG = "LightRGBDetailActivity";
    private static final int CODE_FOR_WRITE_PERMISSION = 10;
    protected LuxxMusicColor musicInfor = null;

    private ImageButton ibMusicMore;
    private ImageButton ibMusicPlay;
    private ImageButton ibMusicNext;

    private TextView tvMusicTitle;
    private TextView tvMusicArtist;

    private musicService mMusic = null;

    /**
     * Callback for changes to the state of the connection.
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder rawBinder) {
            mMusic = ((musicService.LocalBinder)rawBinder).getService();
            if (mMusic != null) {
                updateMusicDisplay(mMusic.getCurrentMusic());
                //添加播放监听
                mMusic.setOnPlayListener(new musicService.OnPlayListener() {
                    @Override
                    public void onStart(int idx) {
                        if (mMusic != null)
                            updateMusicDisplay(mMusic.getMusic(idx));
                    }

                    @Override
                    public void onCompletion(int idx) {
                        if (mMusic != null)
                            updateMusicDisplay(mMusic.getMusic(idx));
                    }
                });
            }
        }

        public void onServiceDisconnected(ComponentName classname) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            savedInstanceState = new Bundle();
        savedInstanceState.putInt(LightRGBDetailActivity.LAYOUT_RESOURCEID, R.layout.activity_musiccolordetail);
        super.onCreate(savedInstanceState);
        try {
            initView();
            setData();
            updateView();
            setListener();
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(musicColorDetailActivity.this, e);
        }
        Intent bindIntent = new Intent(musicColorDetailActivity.this, musicService.class);
        this.startService(bindIntent);
    }

    /**
     * 获取传值
     */
    private void setData() {
        if (infor instanceof LuxxMusicColor)
            musicInfor = (LuxxMusicColor)infor;
    }

    /**
     * 设置监听器
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {

        effectsBtn.setOnClickListener(null);
        ibMusicMore.setOnClickListener(this);
        ibMusicPlay.setOnClickListener(this);
        ibMusicNext.setOnClickListener(this);

    }

    /**
     * 初始化组件
     */
    private void initView() {
        ibMusicMore = (ImageButton) findViewById(R.id.ib_music_more);
        ibMusicPlay = (ImageButton) findViewById(R.id.ib_music_play);
        ibMusicNext = (ImageButton) findViewById(R.id.ib_music_next);

        tvMusicArtist = (TextView) findViewById(R.id.tv_music_singer);
        tvMusicTitle  = (TextView) findViewById(R.id.tv_music_title);
    }

    protected void updateView() {
        effectsBtn.setVisibility(View.INVISIBLE);

        try {
            if (infor.ServiceType == PISBase.SERVICE_TYPE_GROUP) {
                ibMusicMore.setAlpha(0.1f);
                ibMusicNext.setAlpha(0.1f);
                ibMusicPlay.setAlpha(0.1f);

                ibMusicMore.setOnClickListener(null);
                ibMusicNext.setOnClickListener(null);
                ibMusicPlay.setOnClickListener(null);
            } else {
                ibMusicMore.setAlpha(1.0f);
                ibMusicNext.setAlpha(1.0f);
                ibMusicPlay.setAlpha(1.0f);

                ibMusicMore.setOnClickListener(this);
                ibMusicNext.setOnClickListener(this);
                ibMusicPlay.setOnClickListener(this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        PipaRequest playreq;

        switch (v.getId()) {
            case R.id.ib_music_more:
                if (net.senink.seninkapp.BuildConfig.ManufacturerID==5) {
                    return;
                }
                playreq = musicInfor.commitBtAudioStatus(!musicInfor.isSpeakerEnable());
                playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                    @Override
                    public void onRequestStart(PipaRequest req) {
                        showLoading();
                    }

                    @Override
                    public void onRequestResult(PipaRequest req) {
                        hideLoading();
                        if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED){
                            Toast.makeText(musicColorDetailActivity.this,
                                    R.string.retry_tips, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                playreq.NeedAck = true;
                playreq.setRetry(3);
                musicInfor.request(playreq);
//                try {
//                    intent = new Intent(musicColorDetailActivity.this, musicControlActivify.class);
//                    intent.putExtra("keystring", infor.getPISKeyString());
//                    startActivity(intent);
//                }catch (Exception e){
//                    PgyCrashManager.reportCaughtException(musicColorDetailActivity.this, e);
//                }
                break;
            case R.id.ib_music_play: {
                if (musicInfor.isSpeakerEnable()){
                    if (net.senink.seninkapp.BuildConfig.ManufacturerID == 5) {
                        playreq = musicInfor.commitBtAudioStatus(false);
                        playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                            @Override
                            public void onRequestStart(PipaRequest req) {
                                showLoading();
                            }

                            @Override
                            public void onRequestResult(PipaRequest req) {
                                hideLoading();
                                if (musicInfor.isSpeakerEnable())
                                    ibMusicPlay.setImageResource(R.drawable.btn_music_pause_selector);
                                else
                                    ibMusicPlay.setImageResource(R.drawable.btn_music_play_selector);
                                if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED)
                                    Toast.makeText(musicColorDetailActivity.this,
                                            R.string.retry_tips, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        playreq = playMusic(false);
                        playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                            @Override
                            public void onRequestStart(PipaRequest req) {
                                showLoading();
                            }

                            @Override
                            public void onRequestResult(PipaRequest req) {
                                hideLoading();
                                if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                    mMusic.pause();
                                } else
                                    Toast.makeText(musicColorDetailActivity.this,
                                            R.string.retry_tips, Toast.LENGTH_SHORT).show();
                                updateMusicDisplay(mMusic.getCurrentMusic());

                            }
                        });
                    }
                }
                else{
                    if (net.senink.seninkapp.BuildConfig.ManufacturerID == 5) {
                        playreq = musicInfor.commitBtAudioStatus(true);
                        playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                            @Override
                            public void onRequestStart(PipaRequest req) {
                                showLoading();
                            }

                            @Override
                            public void onRequestResult(PipaRequest req) {
                                hideLoading();
                                if (musicInfor.isSpeakerEnable())
                                    ibMusicPlay.setImageResource(R.drawable.btn_music_pause_selector);
                                else
                                    ibMusicPlay.setImageResource(R.drawable.btn_music_play_selector);
                                if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED)
                                    Toast.makeText(musicColorDetailActivity.this,
                                            R.string.retry_tips, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        playreq = playMusic(true); // musicInfor.commitBtAudioStatus(true);
                        playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                            @Override
                            public void onRequestStart(PipaRequest req) {
                                showLoading();
                            }

                            @Override
                            public void onRequestResult(PipaRequest req) {
                                hideLoading();
                                if (mMusic == null)
                                    return;
                                if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                    if (mMusic.getStatus() == musicService.MUSICSERVICE_STATUS_IDLE)
                                        mMusic.play(mMusic.getLocalSongs(), 0);
                                    else
                                        mMusic.play();
                                } else
                                    Toast.makeText(musicColorDetailActivity.this,
                                            R.string.retry_tips, Toast.LENGTH_SHORT).show();
                                updateMusicDisplay(mMusic.getCurrentMusic());
                            }
                        });
                    }
                }
                playreq.setRetry(3);
                playreq.NeedAck = true;
                infor.request(playreq);
            }
                break;
            case R.id.ib_music_next:
                if (mMusic == null)
                    return;
                if (net.senink.seninkapp.BuildConfig.ManufacturerID==5) {
                    return;
                }
                if (musicInfor.isSpeakerEnable()) {
                    mMusic.next();
                    updateMusicDisplay(mMusic.getCurrentMusic());
                }
                else{
                    playreq = playMusic(true); // musicInfor.commitBtAudioStatus(true);
                    playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {
                            showLoading();
                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            hideLoading();
                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                mMusic.next();
                                updateMusicDisplay(mMusic.getCurrentMusic());
                            }
                            else
                                Toast.makeText(musicColorDetailActivity.this,
                                        R.string.setting_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                    playreq.NeedAck = true;
                    playreq.setRetry(3);
                    infor.request(playreq);
                }
                break;
        }
    }

    private PipaRequest playMusic(boolean isStart){
        PipaRequest playreq = null;
        if (mMusic == null)
            return playreq;
        try {
            //找到所有音乐灯，并将其关闭
            List<PISBase> srvs = PISManager.getInstance()
                    .PIServicesWithQuery(infor.getIntegerType(), PISManager.EnumServicesQueryBaseonType);
            for (PISBase srv : srvs){
                if (srv.getPISKeyString().compareTo(infor.getPISKeyString()) == 0)
                    continue;
                LuxxMusicColor musicSrv = (LuxxMusicColor)srv;
                PipaRequest sdReq = musicSrv.commitBtAudioStatus(false);
                sdReq.setRetry(3);
                musicSrv.request(sdReq);
            }

            playreq = musicInfor.commitBtAudioStatus(isStart);
            playreq.NeedAck = true;
            playreq.setRetry(3);
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(musicColorDetailActivity.this, e);
            playreq = null;
        }
        return playreq;
    }

    private void updateMusicDisplay(musicService.musicInfor info){
        if (info == null)
            return;
        tvMusicArtist.setText(info.artist);
        tvMusicTitle.setText(info.title);
//        if(mMusic.getStatus() == musicService.MUSICSERVICE_STATUS_PLAYING)
        if (musicInfor.isSpeakerEnable())
            ibMusicPlay.setImageResource(R.drawable.btn_music_pause_selector);
        else
            ibMusicPlay.setImageResource(R.drawable.btn_music_play_selector);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //检查是否完成初始化动作（绑定服务）
                try {
                    Intent bindIntent = new Intent(musicColorDetailActivity.this, musicService.class);
                    if (!this.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE))
                        net.senink.piservice.util.LogUtils.e("mesh-connect", "bind music service failed!!!");
                    else
                        net.senink.piservice.util.LogUtils.e("mesh-connect", "bind music service successed!!!");
                    if (mMusic != null)
                        updateMusicDisplay(mMusic.getCurrentMusic());
                } catch (Exception e) {
                    PgyCrashManager.reportCaughtException(musicColorDetailActivity.this, e);
                }
            } else {
                //用户不同意，自行处理即可
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查是否具有外部存储访问权限
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_FOR_WRITE_PERMISSION);
                    return;
                }
            }

            //检查是否完成初始化动作（绑定服务）
            try {
                Intent bindIntent = new Intent(musicColorDetailActivity.this, musicService.class);
                if (!this.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE))
                    net.senink.piservice.util.LogUtils.e("mesh-connect", "bind music service failed!!!");
                else
                    net.senink.piservice.util.LogUtils.e("mesh-connect", "bind music service successed!!!");
                if (mMusic != null)
                    updateMusicDisplay(mMusic.getCurrentMusic());
            } catch (Exception e) {
                PgyCrashManager.reportCaughtException(musicColorDetailActivity.this, e);
            }
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(musicColorDetailActivity.this, e);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        //检查是否完成初始化动作（绑定服务）
        try {
            if (mMusic != null) {
                this.unbindService(mServiceConnection);
                mMusic = null;
            }
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(musicColorDetailActivity.this, e);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearData();

        if(mMusic != null){
            Intent bindIntent = new Intent(musicColorDetailActivity.this, musicService.class);
            this.unbindService(mServiceConnection);
            if (mMusic.getStatus() != musicService.MUSICSERVICE_STATUS_PLAYING)
                this.stopService(bindIntent);
            mMusic = null;
        }
    }
}
