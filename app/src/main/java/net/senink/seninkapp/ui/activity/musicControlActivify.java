package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.LuxxMusicColor;
import net.senink.piservice.util.LogUtils;
import net.senink.seninkapp.R;
import net.senink.seninkapp.service.musicService;

import java.util.List;

/**
 * Created by wensttu on 2016/8/3.
 */
public class musicControlActivify extends Activity implements View.OnClickListener {
    private ImageButton btn_effect, btn_play, btn_mode, btn_next, btn_more;
    private RelativeLayout popLayout;
    private ListView songsList;
    private musicService  mMusic = null;
    private SparseArray<musicService.musicInfor> songs = null;
    private SongListAdapter mAdapter = null;

    TextView tvMusicArtist;
    TextView tvMusicTitle;
    TextView tvMusicCount;

    private SongListAdapter.Holder mCurrentHolder = null;

    private LuxxMusicColor infor = null;

    /**
     * Callback for changes to the state of the connection.
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder rawBinder) {
            mMusic = ((musicService.LocalBinder)rawBinder).getService();
            if (mMusic != null) {
                songs = mMusic.getLocalSongs();
                mAdapter = new SongListAdapter();
                songsList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                //添加播放监听
                mMusic.setOnPlayListener(new musicService.OnPlayListener() {
                    @Override
                    public void onStart(int idx) {
                        if(mMusic != null)
                            updateMusicDisplay(mMusic.getMusic(idx));
                    }

                    @Override
                    public void onCompletion(int idx) {
                        if (mMusic != null)
                            updateMusicDisplay(mMusic.getMusic(idx));
                    }
                });

                if (mMusic.getCurrentMusic() != null) {
                    updateMusicDisplay(mMusic.getCurrentMusic());
//                    songsList.setSelection(mMusic.getCurrentMusic().id);
                }
            }
        }

        public void onServiceDisconnected(ComponentName classname) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_control);

        btn_effect = (ImageButton) findViewById(R.id.ib_effect);
        btn_mode   = (ImageButton) findViewById(R.id.ib_playmode);
        btn_play   = (ImageButton) findViewById(R.id.ib_music_play);
        btn_next   = (ImageButton) findViewById(R.id.ib_music_next);
        btn_more   = (ImageButton) findViewById(R.id.ib_music_more);
        popLayout = (RelativeLayout) findViewById(R.id.music_controlpanel);
        tvMusicArtist = (TextView) findViewById(R.id.tv_music_singer);
        tvMusicTitle  = (TextView) findViewById(R.id.tv_music_title);
        tvMusicCount  = (TextView) findViewById(R.id.tv_music_count);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        songsList = (ListView) findViewById(R.id.lv_songs_list);
        ((TextView)findViewById(R.id.tv_music_count_title)).setText(R.string.tv_music_count_title);
        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        popLayout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        //添加按钮监听
        btn_effect.setOnClickListener(this);
        btn_mode.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_more.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            String key = intent.getStringExtra("keystring");
            if (key != null){
                infor = (LuxxMusicColor) PISManager.getInstance().getPISObject(key);
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        //检查是否完成初始化动作（绑定服务）
        try {
            Intent bindIntent = new Intent(musicControlActivify.this, musicService.class);
            LogUtils.e("mesh-connect", "begin to bind mesh service!!!");

            if (!this.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE))
                LogUtils.e("mesh-connect", "bind music service failed!!!");
            else
                LogUtils.e("mesh-connect", "bind music service successed!!!");
        } catch (Exception exc) {
            Log.e("mesh-connect", "bind music service threw exception!", exc);
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            this.unbindService(mServiceConnection);
            mServiceConnection = null;
        }

    }

    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_effect:
                break;
            case R.id.ib_playmode:
                if (mMusic != null) {
                    switch (mMusic.getPlayMode()) {
                        case musicService.MUSIC_PLAY_MODE_SIGNLE:
                            btn_mode.setImageResource(R.drawable.btn_music_mode_random_selector);
                            mMusic.setPlayMode(musicService.MUSIC_PLAY_MODE_RANDOM);
                            break;
                        case musicService.MUSIC_PLAY_MODE_RANDOM:
                            btn_mode.setImageResource(R.drawable.btn_music_mode_order_selector);
                            mMusic.setPlayMode(musicService.MUSIC_PLAY_MODE_ORDER);
                            break;
                        case musicService.MUSIC_PLAY_MODE_ORDER:
                            btn_mode.setImageResource(R.drawable.btn_music_mode_single_selector);
                            mMusic.setPlayMode(musicService.MUSIC_PLAY_MODE_SIGNLE);
                            break;
                    }
                }
                break;
            case R.id.ib_music_play: {
                PipaRequest playreq;
                if (mMusic.getStatus() == musicService.MUSICSERVICE_STATUS_IDLE) {
                    playreq = playMusic(true);
                    playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {

                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                mMusic.play(mMusic.getLocalSongs(), 0);

                            } else
                                Toast.makeText(musicControlActivify.this,
                                        R.string.setting_failed, Toast.LENGTH_SHORT).show();
                            updateMusicDisplay(mMusic.getCurrentMusic());
                        }
                    });
                } else if (mMusic.getStatus() == musicService.MUSICSERVICE_STATUS_PAUSE) {
                    playreq = playMusic(true);
                    playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {
                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                mMusic.play();
                            } else
                                Toast.makeText(musicControlActivify.this,
                                        R.string.setting_failed, Toast.LENGTH_SHORT).show();
                            updateMusicDisplay(mMusic.getCurrentMusic());

                        }
                    });
                } else {
                    playreq = playMusic(false);
                    playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {
                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                mMusic.pause();
                            } else
                                Toast.makeText(musicControlActivify.this,
                                        R.string.setting_failed, Toast.LENGTH_SHORT).show();
                            updateMusicDisplay(mMusic.getCurrentMusic());

                        }
                    });
                }
                playreq.setRetry(3);
                playreq.NeedAck = true;
                infor.request(playreq);
            }
            break;
            case R.id.ib_music_next:
                if (mMusic == null)
                    return;
                if (infor.isSpeakerEnable()) {
                    mMusic.next();
                    updateMusicDisplay(mMusic.getCurrentMusic());
                } else {
                    PipaRequest playreq = playMusic(true);
                    playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {
                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                mMusic.next();
                                updateMusicDisplay(mMusic.getCurrentMusic());
                            } else
                                Toast.makeText(musicControlActivify.this,
                                        R.string.setting_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                    playreq.NeedAck = true;
                    playreq.setRetry(3);
                    infor.request(playreq);
                }
                break;
            case R.id.ib_music_more:
                finish();
                break;
            default:
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
            if (isStart)
                mMusic.setPlayDevice(infor.getPISKeyString());
            else
                mMusic.setPlayDevice(null);

            playreq = infor.commitBtAudioStatus(isStart);
            playreq.NeedAck = true;
            playreq.setRetry(3);
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(musicControlActivify.this, e);
            playreq = null;
        }
        return playreq;
    }

    private void updateControlPanel(SongListAdapter.Holder holder, boolean isSelected){
        if (holder == null)
            return;

        if (isSelected) {
            holder.tv_artist.setTextColor(Color.rgb(0x51, 0xA4, 0xFC));
            holder.tv_title.setTextColor(Color.rgb(0x51, 0xA4, 0xFC));
        }
        else{
            holder.tv_artist.setTextColor(Color.rgb(0x99, 0x99, 0x99));
            holder.tv_title.setTextColor(Color.rgb(0x33, 0x33, 0x33));
        }

        holder.tv_sn.setText(String.valueOf(holder.infor.id));
        holder.tv_artist.setText(holder.infor.artist);
        holder.tv_title.setText(holder.infor.title);

    }

    private void updateMusicDisplay(musicService.musicInfor info){
        if (info == null)
            return;

        songsList.setSelection(info.id);
        tvMusicArtist.setText(info.artist);
        tvMusicTitle.setText(info.title);
        tvMusicCount.setText(String.valueOf(songs.size()));
//        if(mMusic.getStatus() == musicService.MUSICSERVICE_STATUS_PLAYING)
        if (infor.isSpeakerEnable())
            btn_play.setImageResource(R.drawable.btn_music_pause_selector);
        else
            btn_play.setImageResource(R.drawable.btn_music_play_selector);

        switch (mMusic.getPlayMode()){
            case musicService.MUSIC_PLAY_MODE_SIGNLE:
                btn_mode.setImageResource(R.drawable.btn_music_mode_single_selector);
                break;
            case musicService.MUSIC_PLAY_MODE_RANDOM:
                btn_mode.setImageResource(R.drawable.btn_music_mode_random_selector);
                break;
            case musicService.MUSIC_PLAY_MODE_ORDER:
                btn_mode.setImageResource(R.drawable.btn_music_mode_order_selector);
                break;
        }
    }

    public class SongListAdapter extends BaseAdapter {
        public SongListAdapter() {
        }

        @Override
        public int getCount() {
            if (null == songs) {
                songs = new SparseArray<>();
            }
            return songs.size();
        }

        @Override
        public musicService.musicInfor getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            musicService.musicInfor music = getItem(position);
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(musicControlActivify.this)
                        .inflate(R.layout.song_list_item, null);
                holder.tv_sn = (TextView) convertView.findViewById(R.id.song_sn);
                holder.tv_title = (TextView) convertView.findViewById(R.id.song_title);
                holder.tv_artist = (TextView) convertView.findViewById(R.id.song_artist);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final SongListAdapter.Holder holder = (SongListAdapter.Holder) v.getTag();

                    if (infor.isSpeakerEnable()) {
                        mMusic.play(holder.infor);
                    }
                    else{
                        PipaRequest playreq = playMusic(true);
                        playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                            @Override
                            public void onRequestStart(PipaRequest req) {
                            }

                            @Override
                            public void onRequestResult(PipaRequest req) {
                                if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                    mMusic.play(holder.infor);
                                }
                                else
                                    Toast.makeText(musicControlActivify.this,
                                            R.string.setting_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                        playreq.NeedAck = true;
                        playreq.setRetry(3);
                        infor.request(playreq);
                    }
                    if (mCurrentHolder != null){
                        updateControlPanel(mCurrentHolder, false);
                    }
                    updateControlPanel(holder, true);
                    updateMusicDisplay(holder.infor);
                    mCurrentHolder = holder;
                }
            });
            try {
                holder.infor = music;

                if (music.id == mMusic.getCurrentMusic().id){
                    updateControlPanel(holder, true);
                    updateMusicDisplay(music);
                    mCurrentHolder = holder;
                }
                else
                    updateControlPanel(holder, false);

            }catch (Exception e){
                PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
            }
            return convertView;
        }

        public class Holder {
            TextView tv_title;
            TextView tv_artist;
            TextView tv_sn;
            musicService.musicInfor infor;
        }
    }
}

