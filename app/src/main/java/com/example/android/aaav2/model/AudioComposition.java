package com.example.android.aaav2.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AudioComposition {
    private String mCompositionID;
    private String mUserId;
    private String mCompositionTitle;
    private double mLength;
    private List<String> mTags;
    private List<AudioClip> mAudioClips;

    public AudioComposition(FirebaseUser user, String CompositionTitle
            , double Length, List<String> Tags, List<AudioClip> AudioClips) {
        this.mUserId = user.getUid();
        this.mCompositionTitle = CompositionTitle;
        this.mLength = Length;
        this.mTags = Tags;
        this.mAudioClips = AudioClips;
    }
    public AudioComposition(FirebaseUser user){
        mUserId = user.getUid();
    };

    public String getCompositionTitle() {
        return mCompositionTitle;
    }

    public void setCompositionTitle(String compositionTitle) {
        this.mCompositionTitle = compositionTitle;
    }

    public double getLength() {
        return mLength;
    }

    public void setLength(double Length) {
        this.mLength = Length;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> Tags) {
        this.mTags = Tags;
    }

}
