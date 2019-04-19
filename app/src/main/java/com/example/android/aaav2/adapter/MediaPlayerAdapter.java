package com.example.android.aaav2.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.media.MediaMetadataCompat;

import com.example.android.aaav2.MediaPlayerPool;
import com.example.android.aaav2.PlaybackInfoListener;
import com.example.android.aaav2.PlayerAdapter;

import java.util.HashMap;

import androidx.annotation.NonNull;

public final class MediaPlayerAdapter extends PlayerAdapter {

    private final Context mContext;
    private MediaPlayerPool mMediaPlayer;
    private HashMap<String, MediaPlayer> mStreamMap;

    private String mFilename;
    private PlaybackInfoListener mPlaybackInfoListener;
    private MediaMetadataCompat mCurrentMedia; //metadata about an item
    private int mState;

    private boolean mCurrentMediaPlayedToCompletion;

    public MediaPlayerAdapter(@NonNull Context context, PlaybackInfoListener listener) {
        super(context);
        mContext = context;
        mPlaybackInfoListener = listener;
    }

    /**
     * Once the {@link MediaPlayerPool} is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the {MainActivity} the { MediaPlayer} is
     * released. Then in the onStart() of the {@link MainActivity} a new {@link MediaPlayer}
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */
    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
           //init the MediaPlayerPool
        }
    }

    @Override
    public void playFromMedia(MediaMetadataCompat metadata) {

    }

    @Override
    public MediaMetadataCompat getCurrentMedia() {
        return null;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    protected void onPlay() {

    }

    @Override
    protected void onPause() {

    }

    @Override
    protected void onStop() {

    }

    @Override
    public void setVolume(float volume) {

    }
}
