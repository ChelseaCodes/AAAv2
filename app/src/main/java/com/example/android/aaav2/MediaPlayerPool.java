package com.example.android.aaav2;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import androidx.annotation.RawRes;
import androidx.lifecycle.MutableLiveData;

public final class MediaPlayerPool {

    private static String TAG = "MediaPlayerPool";
    private int MAX_STREAMS;
    private Context mContext;
    private ArrayList<MediaPlayer> mMediaPlayerPool;
    private ArrayList<MediaPlayer> mPlayersInUse;
    private int COUNT;

    public MediaPlayerPool(Context ctx, int max_streams){
        mContext = ctx.getApplicationContext();
        MAX_STREAMS = max_streams;
        mMediaPlayerPool = new ArrayList<>();
        mPlayersInUse = new ArrayList<>();

        for(int i = 0; i < MAX_STREAMS; i++){
            mMediaPlayerPool.add(this.buildPlayer());
        }
    }

    private MediaPlayer requestPlayer(){
        if(!mMediaPlayerPool.isEmpty()){
            MediaPlayer m =  mMediaPlayerPool.remove(0);
            mPlayersInUse.add(m);
            return m;
        }
        else {
            Log.d(TAG, "PlayerPool is empty");
            return null;
        }
    }

    private MediaPlayer buildPlayer(){
        MediaPlayer p = new MediaPlayer();

//        p.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                recyclePlayer(mp);
//            }
//        });
        return p;
    }

    private void recyclePlayer(MediaPlayer m){
        m.reset();
        mPlayersInUse.remove(m);
        mMediaPlayerPool.add(m);
    }

    private void recyclePlayer(int PlayerID){
      MediaPlayer p =  mMediaPlayerPool.get(PlayerID);
      p.reset();
    }

    public MediaPlayer Play(@RawRes int rawID, MediaPlayer.OnPreparedListener listener, Float volume){
        try{
            AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(rawID);
            MediaPlayer p = requestPlayer();
            if(p != null){
                p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                p.setLooping(true);
                p.setVolume(volume, volume);
                p.setOnPreparedListener(listener);
                p.prepareAsync(); //listener needs to be set and implemented.
            }
            else{
                Log.d(TAG, "requested Player was not granted");
                return null;
            }
            afd.close();
            return p;
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }
        Log.d(TAG, "requested Player was not granted");
        return null;
    }

    public void DestoryAll(){
        for(int i = 0; i < mPlayersInUse.size(); i++){
            MediaPlayer m = mPlayersInUse.get(i);
            recyclePlayer(m);
        }
    }

    /* returns the MediaPlayer corresponding to the media player in class*/
    public int Load(@RawRes int rawID, MediaPlayer.OnPreparedListener listener){
        try{
            AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(rawID);
            MediaPlayer p = requestPlayer();
            if(p != null){
                p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                p.prepareAsync(); //listener needs to be set and implemented.
            }
            else
                return -1;
            afd.close();
            return COUNT++;

        }catch (Exception e){

        }
        return -1;
    }

    public void Pause(){
        //pause all playing media players....
        for(int i = 0; i < mPlayersInUse.size(); i++){
            MediaPlayer m = mPlayersInUse.get(i);
            m.pause();
        }
    }

    public void Play(){
        //play all paused media players....
        for(int i = 0; i < mPlayersInUse.size(); i++){
            MediaPlayer m = mPlayersInUse.get(i);
            m.start();
        }
    }

}
