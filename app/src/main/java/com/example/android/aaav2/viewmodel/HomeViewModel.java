package com.example.android.aaav2.viewmodel;

import android.app.Application;

import com.example.android.aaav2.model.AudioComposition;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class HomeViewModel extends AndroidViewModel {
    private String mUserID;

    private MutableLiveData<List<AudioComposition>> userCompositions;


    public HomeViewModel(@NonNull Application application) {
        super(application);
    }
}
