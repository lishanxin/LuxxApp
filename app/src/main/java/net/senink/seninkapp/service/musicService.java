package net.senink.seninkapp.service;

/**
 * Created by wensttu on 2016/8/4.
 */

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.pgyersdk.Pgy;
import com.pgyersdk.crash.PgyCrashManager;

public class musicService extends Service {
    //为日志工具设置标签
    private static String TAG = "MusicService";
    //定义音乐播放器变量
    private MediaPlayer mPlayer = null;

    private SparseArray<musicInfor> musicList = null;

    private OnPlayListener mListener = null;

    private int mPlayMode = MUSIC_PLAY_MODE_SIGNLE;

    private int mCurrrentIdx = 0;

    private int mStatus = MUSICSERVICE_STATUS_IDLE;

    private LocalBinder mBinder = new LocalBinder();

    private String mPlayDeviceKeyString = null;

    //该服务不存在需要被创建时被调用，不管startService()还是bindService()都会启动时调用该方法
    @Override
    public void onCreate() {
        if (mPlayer == null){
            mPlayer = new MediaPlayer();
        }
        PgyCrashManager.register(musicService.this);

        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //扫描SD卡中的MP3文件
        try {
            musicList = getLocalSongs();
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(musicService.this, e);
        }
        super.onStart(intent, startId);
    }
    @Override
    public void onDestroy() {
        PgyCrashManager.unregister();

        if (mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        super.onDestroy();
    }
    //其他对象通过bindService 方法通知该Service时该方法被调用
    @Override
    public IBinder onBind(Intent intent) {

        try {
            if (mPlayer == null)
                mPlayer = new MediaPlayer();
            if (mBinder == null)
                mBinder = new LocalBinder();

        }catch (Exception e){
            PgyCrashManager.reportCaughtException(musicService.this, e);
        }
        return mBinder;
    }
    //其它对象通过unbindService方法通知该Service时该方法被调用
    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    public void setPlayDevice(String keyStr){
        mPlayDeviceKeyString = keyStr;
    }

    public String getPlayDevice(){
        return mPlayDeviceKeyString;
    }

    public void play(){
        if (mPlayer == null)
            return;
        try{
            if (mCurrrentIdx == -1)
                return;
            mPlayer.start();
            mStatus = MUSICSERVICE_STATUS_PLAYING;
            if (mListener != null)
                mListener.onStart(mCurrrentIdx);
        }catch (Exception e){
            mPlayer.reset();
            PgyCrashManager.reportCaughtException(musicService.this, e);
        }
    }
    public void play(String url){
        if (mPlayer == null)
            return ;

        try {
            mPlayer.reset();
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mStatus = MUSICSERVICE_STATUS_PLAYING;

                    if (mListener != null)
                        mListener.onStart(mCurrrentIdx);
                }
            });
        }catch (Exception e){
            mPlayer.reset();
            PgyCrashManager.reportCaughtException(musicService.this, e);
        }
    }

    public void play(musicInfor infor){
        mCurrrentIdx = infor.id;
        play(infor.url);
    }

    public void play(SparseArray<musicInfor> urls, int idx){
        if (mPlayer == null || urls == null)
            return;

        if (musicList.size() == 0 || idx >= musicList.size())
            return;

        try {
            mCurrrentIdx = idx;

            play(musicList.get(idx));
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(musicService.this, e);
        }
    }

    public void pause(){
        if (mPlayer == null)
            return;
        mPlayer.pause();
        mStatus = MUSICSERVICE_STATUS_PAUSE;
    }

    public void stop(){
        if (mPlayer == null)
            return;
        mPlayer.stop();
        mStatus = MUSICSERVICE_STATUS_IDLE;
    }

    public void next(){
        playNext();
    }

    public static final int MUSICSERVICE_STATUS_IDLE = 0;
    public static final int MUSICSERVICE_STATUS_PLAYING = 1;
    public static final int MUSICSERVICE_STATUS_PAUSE = 2;

    public int getStatus(){
        return mStatus;
    }

    public musicInfor getCurrentMusic(){
        return getMusic(mCurrrentIdx);
    }

    public musicInfor getMusic(int idx){
        if (musicList == null || musicList.size() == 0 ||
                idx < 0 || idx >= musicList.size())
            return null;
        return musicList.get(idx);
    }

    public static final int MUSIC_PLAY_MODE_SIGNLE = 0;
    public static final int MUSIC_PLAY_MODE_RANDOM = 1;
    public static final int MUSIC_PLAY_MODE_ORDER  = 2;

    public int getPlayMode(){
        return mPlayMode;
    }

    public void setPlayMode(int mode){
        if (mode < 0 || mode > MUSIC_PLAY_MODE_ORDER)
            return;
        mPlayMode = mode;
    }

    public void startScan(){

    }

    public void startScan(String url){

    }

    public void stopScan(){

    }

    public SparseArray<musicInfor> getLocalSongs(){
        if (musicList == null)
            musicList = new SparseArray<>();
        else
            musicList.clear();

        int idx = 0;
        ContentResolver cr = musicService.this.getContentResolver();
        if(cr != null){
            // 获取所有歌曲
            Cursor cursor = cr.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if(null== cursor){
                return musicList;
            }
            if(!cursor.moveToFirst())
                return musicList;
            do {
                musicInfor mi = new musicInfor();
                mi.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                mi.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                if("<unknown>".equals(singer)){
//                    singer = "未知艺术家";
//                }
                mi.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                mi.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                mi.time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                mi.url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                mi.displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String sbr = mi.displayName.substring(mi.displayName.length() - 3,
                        mi.displayName.length());
                mi.id = musicList.size();
                if (sbr.equals("mp3")) {
                    musicList.put(mi.id, mi);
                }
            }
            while(cursor.moveToNext());
        }
        return musicList;
    }

    public void setOnPlayListener(final OnPlayListener l){
        if (l != null){
            mListener = l;
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try{
                        if (musicList != null && musicList.size() > 0){
                            playNext();
                        }
                        else if (mListener != null) {
                            mStatus = MUSICSERVICE_STATUS_IDLE;
                            mListener.onCompletion(mCurrrentIdx);
                        }
                    }catch (Exception e){
                        PgyCrashManager.reportCaughtException(musicService.this, e);
                    }
                }
            });
        }
    }

    private void playNext(){
        if (mPlayMode == MUSIC_PLAY_MODE_RANDOM){
            mCurrrentIdx = (int)(Math.random()*musicList.size());
        }
        else if (mPlayMode == MUSIC_PLAY_MODE_ORDER){
            if (mCurrrentIdx < (musicList.size()-1))
                mCurrrentIdx ++;
            else
                mCurrrentIdx = 0;
        }

        play(musicList.get(mCurrrentIdx).url);
    }

    public interface OnPlayListener {
        void onStart(int idx);
        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param idx the idx that reached the end of the file
         */
        void onCompletion(int idx);
    }

    public class musicInfor{
        public String displayName;
        public String title;
        public String album;
        public String artist;

        public int duration;
        public long size;
        public long time;

        public int id;
        public String url;

    }

    public class LocalBinder extends Binder {
        public musicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return musicService.this;
        }
    }

}