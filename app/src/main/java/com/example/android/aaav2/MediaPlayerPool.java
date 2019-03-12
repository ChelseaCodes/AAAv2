package com.example.android.aaav2;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;

import androidx.annotation.RawRes;
import androidx.lifecycle.MutableLiveData;

public final class MediaPlayerPool {

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

    private MediaPlayer requestPlayer(MediaPlayer.OnPreparedListener listener){
        if(!mMediaPlayerPool.isEmpty()){
            MediaPlayer m =  mMediaPlayerPool.remove(0);
            m.setOnPreparedListener(listener);
            mPlayersInUse.add(m);
            return m;
        }
        else
            return null;
    }

    private MediaPlayer buildPlayer(){
        MediaPlayer p = new MediaPlayer();

        p.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recyclePlayer(mp);
            }
        });
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

    public MediaPlayer Play(@RawRes int rawID, MediaPlayer.OnPreparedListener listener){
        try{
            AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(rawID);
            MediaPlayer p = requestPlayer(listener);
            if(p != null){
                p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                p.setLooping(true);
                p.prepareAsync(); //listener needs to be set and implemented.
            }
            else
                return null;

            afd.close();
            return p;
        } catch (Exception e){

        }
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
            MediaPlayer p = requestPlayer(listener);
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

}
