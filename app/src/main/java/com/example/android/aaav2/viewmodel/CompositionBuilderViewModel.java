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
    private MutableLiveData<List<String>> mCategories;
    private String mUserID;
    private int [] audioClipIDs;
    private int [] audioStreamIDs;
    private SoundPool mSoundPool;
    private FileHelper fileHelper;

    private DataDownloadedListener downloadedListener;
    public CompositionBuilderViewModel(Application app){
        super(app);
        //mUserID = userID;
        sourceOfTruth = new Repository(app, new Repository.FirestoreCallback() {
            @Override
            public void onDataLoaded(List<AudioClip> list) {
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
    private void ViewInit(List<AudioClip> list){
        mAllAudioClips = new MutableLiveData<>();
        mAllAudioClips.setValue(list);
        mCategories = sourceOfTruth.getCategories();

        if(mAllAudioClips != null){
            int size = list.size();

           audioClipIDs = new int[size];
           audioStreamIDs = new int[size];

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                //audio attributes and SoundPool set up
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                mSoundPool = new SoundPool.Builder()
                        .setMaxStreams(size)
                        .setAudioAttributes(audioAttributes).build();

            }else{
                mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
            }
            createPoolIDs();

            if(downloadedListener != null)
                downloadedListener.onDataDownloaded();
        }
    }

    /*
    * populates audioClipIDs for reference when mSoundPool needs to Play/Pause/Stop
    *
    * */
    private void createPoolIDs(){
        Context ctx = getApplication().getApplicationContext();
        int i = 0;
        for (AudioClip c : mAllAudioClips.getValue()){
            try {
                audioClipIDs[i++] = mSoundPool.load(
                      ctx.getAssets().openFd(c.getFileName()) , 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LiveData<List<AudioClip>> getAllAudioClips(){
            return mAllAudioClips;
    }

    public void playAudio(int adapterPos){
        audioStreamIDs[adapterPos] = mSoundPool
                .play(audioClipIDs[adapterPos], 1,1,1, -1,1);
    }

    public void pauseAudio(int adapterPos){
        mSoundPool.pause(audioStreamIDs[adapterPos]);
    }

    public void setAudioVolume(int adapterPos, float volume){
        //setVol takes a leftVol and rightVol but for now this is just one vol. 3d audio??
        mSoundPool.setVolume(audioStreamIDs[adapterPos], volume, volume);
    }

    public interface OnDownloadCompleted{
        void onDownloadCompleted();
    }
}
