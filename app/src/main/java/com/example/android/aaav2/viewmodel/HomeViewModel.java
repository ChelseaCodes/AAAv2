package com.example.android.aaav2.viewmodel;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.android.aaav2.MediaPlayerPool;
import com.example.android.aaav2.R;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.model.AudioComposition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class HomeViewModel extends AndroidViewModel implements MediaPlayer.OnPreparedListener {

    private static String TAG = "HomeViewModel";
    private String mUserID;

    private MediaPlayerPool mMediaPlayerPool;
    private HashMap<String, MediaPlayer> mStreamMap; //track audio with its player
    private AudioComposition mCurrentComposition;//composition user selected to play

    private boolean isPlaying;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mStreamMap = new HashMap<>();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "media player prepared, starting");
        mp.start();
        isPlaying = true;
    }

    public AudioComposition getCurrentComposition(){return mCurrentComposition; }

    /*
    * When a user clicks on their audio composition it will call this fctn
    * a new media player pool is made, so previous players will need to be cleaned
    * up before playing another composition.
    * */
    public void LoadComposition(AudioComposition c){
        mCurrentComposition = c;
        int size = c.getAudioClips().size(); //get number of media players to make
        Log.d(TAG, size + " audio clips to load");

        //if(isPlaying)
        mMediaPlayerPool = new MediaPlayerPool(getApplication().getApplicationContext(), size);

        for(AudioClip clip: c.getAudioClips()){
            MediaPlayer p = new MediaPlayer();
            float volume = Float.valueOf(clip.getVolume());
            Log.d(TAG, "volume: " + volume + "title: '" + clip.getTitle()+ "'");

            switch(clip.getTitle()){
                case "Gusty":
                    p = mMediaPlayerPool.Play(R.raw.zapsplat_zapsplat_nature_wind_strong_tall_trees_storm_001_17777, this, volume);
                    break;
                case "Open Window":
                    p = mMediaPlayerPool.Play(R.raw.gain_walkers_rain, this, volume);
                    break;
                case "Crickets":
                    p = mMediaPlayerPool.Play(R.raw.zapsplat_animals_insects_grasshopper_17949, this, volume);
                    break;
                case "Closed Window":
                    p = mMediaPlayerPool.Play(R.raw.ftus_rain_wind_blow_rain_against_window_drips, this, volume);
                    break;
                case "Frogs":
                    p = mMediaPlayerPool.Play(R.raw.frog_sound_effect, this, volume);
                    break;
                case "Birds":
                    p = mMediaPlayerPool.Play(R.raw.bird_sounds, this, volume);
                    break;
                case "Gentle Waves":
                    p = mMediaPlayerPool.Play(R.raw.ocean_waves_gentle, this, volume);
                    break;
                case "Small Campfire":
                    p = mMediaPlayerPool.Play(R.raw.small_campfire, this, volume);
                    break;
                case "Cafe":
                    p = mMediaPlayerPool.Play(R.raw.cafe_city_sounds, this, volume);

                    break;
                case "Delta":
                    p = mMediaPlayerPool.Play(R.raw.binural_delta_waves, this, volume);

                    break;
                case "Fireplace":
                    p = mMediaPlayerPool.Play(R.raw.fireplace_fire, this, volume);

                    break;
                case "Cats Purr":
                    p = mMediaPlayerPool.Play(R.raw.cat_purr, this, volume);

                    break;
                default:
                   Log.d(TAG, "There is an error with Audio Clip title");
                    break;
            }

            if( p != null){
                Log.d(TAG, "Added " + clip.getTitle() + " to the streamMap");
                mStreamMap.put(clip.getTitle(), p);
            }
            else
                Log.d(TAG, "MediaPlayerPool did not give a player for " + clip.getFile_Name());
        }
    }

    public void PausePlayComposition(){
        if(isPlaying) {
            mMediaPlayerPool.Pause(); //pause all playing media players.
            isPlaying = false;
        }else{
            mMediaPlayerPool.Play();
            isPlaying = true;
        }
    }

    //Clears the stremMap of open MediaPlayers
    //calls MediaPlayerPools destroy all to clean up the actual MediaPlayer objects
    //set the currentcomposition and MediaPlayerPool to null
    public void StopPlayback(){

        for(MediaPlayer p: mStreamMap.values()){
            p.pause();
            p.stop();
            p.reset();
        }

        mStreamMap.clear();

        if(isPlaying)
            mMediaPlayerPool.DestoryAll();

        mCurrentComposition = null;
        mMediaPlayerPool = null;
    }
}
