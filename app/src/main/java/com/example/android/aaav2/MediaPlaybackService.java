package com.example.android.aaav2;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;



public class MediaPlaybackService extends MediaBrowserServiceCompat {
    public static final String LOG_TAG = "MediaPlaybackService";
    public static final String MEDIA_ROOT_ID = "media_root_id";
    public static final String EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    /*
    * A media session is responsible for all communication with the player.
     * */
    //private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

    private MediaSessionCompat mediaSession;
    private PlayerAdapter mPlayback;
    private MediaSessionCallback mCallback;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaNotificationManager mMediaNotificationManager;
    private AudioFocusRequest audioFocusRequest;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;



    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, LOG_TAG);

        //enable callbacks to be handled from media button and trasport controlls
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());

        mMediaNotificationManager = new MediaNotificationManager(this);

        //todo: handle callbacks from the media controller
       // mediaSession.setCallback(new );

        setSessionToken(mediaSession.getSessionToken());
    }



    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
       // if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            return new BrowserRoot(MEDIA_ROOT_ID, null);
        //} else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            //return new BrowserRoot(MEDIA_ROOT_ID, null);
        //}
    }

    // provides the ability for a client to build and display a menu of the Service's content
    //dont nned.
    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }
    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    public class MediaSessionCallback extends MediaSessionCompat.Callback {
        private MediaMetadataCompat mPreparedMedia;

        @Override
        public void onPrepare() {

//            final String mediaId = mPlaylist.get(mQueueIndex).getDescription().getMediaId();
//            mPreparedMedia = MusicLibrary.getMetadata(MusicService.this, mediaId);
//            mediaSession.setMetadata(mPreparedMedia);
//
//            if (!mSession.isActive()) {
//                mSession.setActive(true);
//            }

            //mPreparedMedia = the AudioComposition.getMetaData()
        }

        @Override
        public void onPlay() {
            //if (!isReadyToPlay()) {
                // Nothing to play.
             //   return;
            //}

            if (mPreparedMedia == null) {
                onPrepare();
            }

            mPlayback.playFromMedia(mPreparedMedia);
            Log.d(LOG_TAG, "onPlayFromMediaId: MediaSession active");
        }

        @Override
        public void onPause() {
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mediaSession.setActive(false);
        }

        //private boolean isReadyToPlay() {
        //    return (!mPlaylist.isEmpty());
        //}
    }

}
