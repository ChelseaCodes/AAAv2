package com.example.android.aaav2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class HomeViewModel extends AndroidViewModel {
    private String mUserID;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }
}
