package com.example.android.aaav2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.example.android.aaav2.model.AudioComposition;
import com.example.android.aaav2.viewmodel.EditCompositionViewModel;

public class EditComposition extends AppCompatActivity {


    private EditCompositionViewModel mViewModel;
    private MutableLiveData<AudioComposition> mAudioComposition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_composition);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mViewModel = ViewModelProviders.of(this).get(EditCompositionViewModel.class);
    }
}
