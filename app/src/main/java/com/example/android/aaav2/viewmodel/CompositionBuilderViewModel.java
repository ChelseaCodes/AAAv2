package com.example.android.aaav2.viewmodel;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.android.aaav2.FileHelper;
import com.example.android.aaav2.MediaPlayerPool;
import com.example.android.aaav2.R;
import com.example.android.aaav2.Repository;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.model.AudioComposition;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/*
* ViewModel is a class that is responsible for preparing and managing the data for an Activity or a Fragment.
* It also handles the communication of the Activity / Fragment with the rest of the application
* (e.g. calling the business logic classes).
*
* Model is for when user is selecting audio clips to include in their composition
* */
public class CompositionBuilderViewModel extends AndroidViewModel implements MediaPlayer.OnPreparedListener {
    String TAG = "CompositionBuilderViewModel";

    private Repository sourceOfTruth;
    private MutableLiveData<List<AudioClip>> mAllAudioClips;
    private ArrayList<List<AudioClip>> ALLCLIPS;
    private MutableLiveData<List<AudioClip>> mWaterWeatherClips;
    private MutableLiveData<List<AudioClip>> mAnimalsCrittersClips;
    private MutableLiveData<List<AudioClip>> mWavesClips;
    private MutableLiveData<List<AudioClip>> mFireClips;
    private MutableLiveData<List<AudioClip>> mCityClips;
    private MutableLiveData<List<AudioClip>> mUserClips;

    private MutableLiveData<List<String>> mCategories;
    private String mUserID;

    private MediaPlayerPool mMediaPlayerPool;

    private HashMap<String, MediaPlayer> mStreamMap;
    private FileHelper fileHelper;

    private DataDownloadedListener downloadedListener;
    private AudioComposition mComposition;

    //Firestore Arrays to keep UI information
    ObservableSnapshotArray<AudioClip> mWaterWeatherSnapshotArray;

    public CompositionBuilderViewModel(Application app){
        super(app);
        mUserID = FirebaseAuth.getInstance().getUid();
        mStreamMap = new HashMap<>();
        mComposition = new AudioComposition();
        sourceOfTruth = new Repository(app, new Repository.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(ArrayList<List<AudioClip>> list) {
                Log.d(TAG, "Data has been loaded and recieved from Repo");

                //on Data Loaded we start initting
                ViewInit(list);
            }
        }, app.getApplicationContext().getFilesDir() );
        sourceOfTruth.ReadAllClips();
        mAllAudioClips = null;
        fileHelper = new FileHelper(app.getApplicationContext());
    }

    public void RemoveUserWIP(){
        sourceOfTruth.RemoveUserWIP();
    }

    public String getUserID(){ return mUserID; }
    public void setDataDownloadedListener(DataDownloadedListener L){
        downloadedListener = L;
    }

    public ObservableSnapshotArray<AudioClip> getWaterWeatherSnapshotArray() {
        if(mWaterWeatherSnapshotArray != null)
            return mWaterWeatherSnapshotArray;
        else
            return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public interface DataDownloadedListener{
         void onDataDownloaded();
    }
    /*
    * ViewInit takes the list of AudioClips and sets this->mAllAudioClips
    * Then initiates SoundPool and loads all clips into this->mSoundPool
    * */
    private void ViewInit(ArrayList<List<AudioClip>> list){
        ALLCLIPS = list;

        //if(mAllAudioClips != null){
        int size = sourceOfTruth.getNum_Clips();

        mMediaPlayerPool = new MediaPlayerPool(getApplication().getApplicationContext(), size);

        //let Activity know data is ready.
        if(downloadedListener != null)
            downloadedListener.onDataDownloaded();

    }

    public LiveData<List<AudioClip>> getAllAudioClips(){
            return mAllAudioClips;
    }

    public MutableLiveData<List<AudioClip>> getWaterWeatherClips() {
        mWaterWeatherClips = sourceOfTruth.getWaterWeatherClips();
        return mWaterWeatherClips;
    }

    public void playAudio(AudioClip clip){
        //Context ctx = getApplication().getApplicationContext();
        String title = clip.getTitle();
        MediaPlayer p = null;

        switch(title){
            case "Gusty":
                p = mMediaPlayerPool.Play(R.raw.zapsplat_zapsplat_nature_wind_strong_tall_trees_storm_001_17777, this);
                break;
            case "Open Window":
                p = mMediaPlayerPool.Play(R.raw.gain_walkers_rain, this);
                break;
            case "Crickets":
                p = mMediaPlayerPool.Play(R.raw.zapsplat_animals_insects_grasshopper_17949, this);
                break;
            case "Closed Window":
                p = mMediaPlayerPool.Play(R.raw.ftus_rain_wind_blow_rain_against_window_drips, this);
                break;
            case "Frogs":
                p = mMediaPlayerPool.Play(R.raw.frog_sound_effect, this);
                break;
            case "Birds":
                p = mMediaPlayerPool.Play(R.raw.bird_sounds, this);
                break;
            case "Gentle Waves":
                p = mMediaPlayerPool.Play(R.raw.ocean_waves_gentle, this);
                break;
            case "Small Campfire":
                p = mMediaPlayerPool.Play(R.raw.small_campfire, this);
                break;
            case "Cafe":
                p = mMediaPlayerPool.Play(R.raw.cafe_city_sounds, this);

                break;
            case "Delta":
                p = mMediaPlayerPool.Play(R.raw.binural_delta_waves, this);

                break;
            case "Fireplace":
                p = mMediaPlayerPool.Play(R.raw.fireplace_fire, this);

                break;
            case "Cats Purr":
                p = mMediaPlayerPool.Play(R.raw.cat_purr, this);

                break;
            default:
                //nothing
                break;
        }

        if( p != null){
            mStreamMap.put(title, p);
            sourceOfTruth.UpdateClip(clip);
        }

    }

    public void pauseAudio(AudioClip ac){
        MediaPlayer m = mStreamMap.get(ac.getTitle());
        if(m.isPlaying()){
            m.stop();
            m.reset();
            sourceOfTruth.UpdateClip(ac);
        }
    }
    public void setAudioVolume(AudioClip ac, float volume){
        MediaPlayer m = mStreamMap.get(ac.getTitle());
        if(m.isPlaying()){
            m.setVolume(volume, volume);
        }
        sourceOfTruth.UpdateClip(ac);
    }

    /* Called when composition activity is done composing
    * */
    public void stopAudio(){
        for(MediaPlayer m : mStreamMap.values()){
            m.stop();
            m.reset();
        }
        mStreamMap.clear();
        mMediaPlayerPool.DestoryAll();
    }

    /*************************************************
    *
     *     Audio Composition Bits
    *
    * ************************************************/

    public interface OnDownloadCompleted{
        void onDownloadCompleted();
    }
}
