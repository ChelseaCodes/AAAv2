package com.example.android.aaav2.viewmodel;

import android.app.Application;
import android.util.Log;

import com.example.android.aaav2.Repository;
import com.example.android.aaav2.model.AudioComposition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/*
* EditCompositionVM prepares and manages data for EditComposition activity.
*
* */
public class EditCompositionViewModel extends AndroidViewModel implements Repository.OnAudioCompositionRetrievedListener
{

    public static final String TAG = "EditCompositionViewModel";

    //CompositionViewModel to talk to MediaPlayerPool instance and
    //change volume of playback as they edit.
    private CompositionBuilderViewModel mBuilderViewModel;

    private Repository sourceOfTruth;
    private MutableLiveData<AudioComposition> mAudioComposition;
    private String mUserID;

    private FirebaseFirestore mFirestore;
    private CollectionReference mWIPCollection;

    private OnCompositionRetrievedListener onCompositionRetrievedListener;
    private Repository.OnAudioCompositionSavedListener onAudioCompositionSavedListener;

    public EditCompositionViewModel(Application app){
        super(app);
        mFirestore = FirebaseFirestore.getInstance();
        mUserID = FirebaseAuth.getInstance().getUid();
        mWIPCollection = mFirestore.collection("wip");

        mAudioComposition = new MutableLiveData<>();
        sourceOfTruth = new Repository(app);
        sourceOfTruth.setOnAudioCompositionRetrievedListener(this);

    }

    public MutableLiveData<AudioComposition> getAudioComposition(){
        if(mAudioComposition.getValue() == null){
            Log.d(TAG, "Composition empty, Getting");

            sourceOfTruth.GetUserWIP();
        }
        return mAudioComposition;
    }

    @Override
    public void onAudioCompositionRetrieved(MutableLiveData<AudioComposition> ac) {
        Log.d(TAG, "Composition Retrieved");
        mAudioComposition = ac;

        if(onCompositionRetrievedListener != null)
            onCompositionRetrievedListener.onCompositionRetrieved(ac);
    }

    //Change Events
    public interface OnCompositionRetrievedListener{
        void onCompositionRetrieved(MutableLiveData<AudioComposition> ac);
    }

    public void setOnCompositionRetrieved(OnCompositionRetrievedListener listener){
        onCompositionRetrievedListener = listener;
    }

    public void setOnAudioCompositionSavedListener(Repository.OnAudioCompositionSavedListener listener) {
        onAudioCompositionSavedListener = listener;
        sourceOfTruth.setOnAudioCompositionSavedListener(listener);
    }

    public void SaveComposition(AudioComposition ac){
        sourceOfTruth.SaveAudioComposition(ac);
    }

}
