package com.example.android.aaav2.viewmodel;

import android.app.Application;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.example.android.aaav2.FileHelper;
import com.example.android.aaav2.Repository;
import com.example.android.aaav2.model.AudioClip;

import java.io.IOException;
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
public class CompositionBuilderViewModel extends AndroidViewModel {
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

    private HashMap<String, Integer> mSoundMap;
    private HashMap<String, Integer> mStreamMap;
    private int [] audioClipIDs;
    private int [] audioStreamIDs;
    private SoundPool mSoundPool;
    private FileHelper fileHelper;

    private DataDownloadedListener downloadedListener;

    public CompositionBuilderViewModel(Application app){
        super(app);
        //mUserID = userID;
        mSoundMap = new HashMap<>();
        mStreamMap = new HashMap<>();
        sourceOfTruth = new Repository(app, new Repository.FirestoreCallback() {
            @Override
            public void onDataLoaded(ArrayList<List<AudioClip>> list) {
                Log.d(TAG, "Data has been loaded and recieved from Repo");

                //on Data Loaded we start initting
                ViewInit(list);
            }
        }, app.getApplicationContext().getFilesDir() );
        mAllAudioClips = null;
        fileHelper = new FileHelper(app.getApplicationContext());
    }

    public void setDataDownloadedListener(DataDownloadedListener L){
        downloadedListener = L;
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
        //mAllAudioClips = new MutableLiveData<>();
        //mAllAudioClips.setValue(list);
        //mCategories = sourceOfTruth.getCategories();
        mWaterWeatherClips = sourceOfTruth.getWaterWeatherClips();
        mAnimalsCrittersClips = sourceOfTruth.getAnimalsCrittersClips();

        //todo: get other categories

//        //if(mAllAudioClips != null){
//        int size = sourceOfTruth.getNum_Clips();
//
//       audioClipIDs = new int[size];
//       audioStreamIDs = new int[size];
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//
//            //audio attributes and SoundPool set up
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .build();
//
//            mSoundPool = new SoundPool.Builder()
//                    .setMaxStreams(size)
//                    .setAudioAttributes(audioAttributes).build();
//
//        }else{
//            mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
//        }

        
            createPoolIDs();

            //let Activity know data is ready.
            if(downloadedListener != null)
                downloadedListener.onDataDownloaded();
        //}
    }

    /*
    * populates audioClipIDs for reference when mSoundPool needs to Play/Pause/Stop
    *
    * */
    private void createPoolIDs(){
        Context ctx = getApplication().getApplicationContext();
        int i = 0;
        for(List<AudioClip> list : ALLCLIPS) {
            for (AudioClip c : list) {
                try {
                    if (c.getFile_Name() != "") {
                        //audioClipIDs[i++] = mSoundPool.load(
//                        c.set_SoundPoolID(
//                                mSoundPool.load(
//                                        ctx.getAssets().openFd(c.getFile_Name()), 1)
//                        );

                        mSoundMap.put(c.getTitle(), mSoundPool.load(
                                      ctx.getAssets().openFd(c.getFile_Name()), 1) );
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public LiveData<List<AudioClip>> getAllAudioClips(){
            return mAllAudioClips;
    }

    public MutableLiveData<List<AudioClip>> getWaterWeatherClips() {
        return mWaterWeatherClips;
    }

    public void playAudio(int adapterPos){
//        audioStreamIDs[adapterPos] = mSoundPool
////                .play(audioClipIDs[adapterPos], 1,1,1, -1,1);

    }

    public void playAudio(AudioClip clip){
        String title = clip.getTitle();
//        clip.setStreamingID(mSoundPool
//                .play(clip.get_SoundPoolID(), 1,1,1, -1,1)
        try {
            mStreamMap.put(title, mSoundPool.play(mSoundMap.get(title), 1,1,1,-1,1));
        } catch (Exception e) {
            //todo: implement listener to handle this situation
            e.printStackTrace();
        }
//        );


    }

    public void pauseAudio(AudioClip ac){
        //mSoundPool.pause(audioStreamIDs[adapterPos]);
        mSoundPool.pause(mStreamMap.get(ac.getTitle()));
    }

    public void setAudioVolume(int adapterPos, float volume){
        //setVol takes a leftVol and rightVol but for now this is just one vol. 3d audio??
        mSoundPool.setVolume(audioStreamIDs[adapterPos], volume, volume);
    }

    public void setAudioVolume(AudioClip ac, float volume){
        //todo: dbl check volume is an okay value to set
        mSoundPool.setVolume(mStreamMap.get(ac.getTitle()), volume, volume);
    }

    public interface OnDownloadCompleted{
        void onDownloadCompleted();
    }
}
