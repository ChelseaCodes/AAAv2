package com.example.android.aaav2.viewmodel;

import android.app.Application;
import android.media.MediaPlayer;

import com.example.android.aaav2.MediaPlayerPool;
import com.example.android.aaav2.model.AudioComposition;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class HomeViewModel extends AndroidViewModel {
    private String mUserID;

    private MutableLiveData<List<AudioComposition>> userCompositions;
    private MediaPlayerPool mMediaPlayerPool;
    private HashMap<String, MediaPlayer> mStreamMap;
    private AudioComposition mComposition;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mStreamMap = new HashMap<>();
        mComposition = new AudioComposition();

    }
}
