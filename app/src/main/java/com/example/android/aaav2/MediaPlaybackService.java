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
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

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

    MediaSessionCompat.Callback callback = new
            MediaSessionCompat.Callback() {
                @Override
                public void onPlay() {
                    AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                    // Request audio focus for playback, this registers the afChangeListener
                    AudioAttributes attrs = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();
                    audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setOnAudioFocusChangeListener(afChangeListener)
                            .setAudioAttributes(attrs)
                            .build();
                    int result = am.requestAudioFocus(audioFocusRequest);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        // Start the service
                        startService(new Intent(getBaseContext(), MediaBrowserService.class));
                        // Set the session active  (and update metadata and state)
                        mediaSession.setActive(true);
                        // start the player (custom call)
                        //player.start();
                        // Register BECOME_NOISY BroadcastReceiver
                        //registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                        // Put the service in the foreground, post notification
                        //startForeground(id, myPlayerNotification);
                    }
                }

                @Override
                public void onStop() {
                    AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                    // Abandon audio focus
                    am.abandonAudioFocusRequest(audioFocusRequest);
                    //unregisterReceiver(myNoisyAudioStreamReceiver);
                    // Stop the service
                    stopSelf();
                    // Set the session inactive  (and update metadata and state)
                    mediaSession.setActive(false);
                    // stop the player (custom call)
                    //player.stop();
                    // Take the service out of the foreground
                    //stopForeground(false);
                }

                @Override
                public void onPause() {
                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    // Update metadata and state
                    // pause the player (custom call)
                    player.pause();
                    // unregister BECOME_NOISY BroadcastReceiver
                    unregisterReceiver(myNoisyAudioStreamReceiver);
                    // Take the service out of the foreground, retain the notification
                    service.stopForeground(false);
                }
            };
}
