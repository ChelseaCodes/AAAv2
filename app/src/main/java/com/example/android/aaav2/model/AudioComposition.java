package com.example.android.aaav2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AudioComposition implements Parcelable {
    private String mUserId;
    private String mCompositionTitle;
    private double mLength;
    private ArrayList<String> mTags;
    private ArrayList<AudioClip> mAudioClips;

    public AudioComposition(FirebaseUser user, String CompositionTitle
            , double Length, ArrayList<String> Tags, ArrayList<AudioClip> AudioClips) {
        this.mUserId = user.getUid();
        this.mCompositionTitle = CompositionTitle;
        this.mLength = Length;
        this.mTags = Tags;
        this.mAudioClips = AudioClips;
    }
    public AudioComposition(){
        mAudioClips = new ArrayList<>();
        mTags = new ArrayList<>();
    }
    public AudioComposition(Parcel in){

        this.mUserId = in.readString();
        this.mCompositionTitle = in.readString();
        this.mLength = Double.parseDouble(in.readString());
        this.mTags = in.readArrayList(null);
        mAudioClips = in.createTypedArrayList(AudioClip.CREATOR);
    }

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

    public ArrayList<String> getTags() {
        return mTags;
    }

    public void setTags(ArrayList<String> Tags) {
        this.mTags = Tags;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public ArrayList<AudioClip> getAudioClips() {
        return mAudioClips;
    }

    public void setAudioClips(ArrayList<AudioClip> mAudioClips) {
        this.mAudioClips.addAll(mAudioClips);
    }

    /*
    *   Initially, new AudioCompositions will contain a copy of the audio clip library
    * */
    public void initAllAudioClips(ArrayList<List<AudioClip>> allClips){
        for(List<AudioClip> list : allClips){
            this.mAudioClips.addAll(list);
        }
    }

    public void addAudioClip(AudioClip c){
        if(c != null){
            this.mAudioClips.add(c);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUserId);
        dest.writeString(mCompositionTitle);
        dest.writeString(String.valueOf(mLength));
        dest.writeList(mTags);
        dest.writeTypedList(mAudioClips);
    }

    public static final Creator<AudioComposition> CREATOR = new Creator<AudioComposition>() {
        @Override
        public AudioComposition createFromParcel(Parcel source) {
            return new AudioComposition(source);
        }

        @Override
        public AudioComposition[] newArray(int size) {
            return new AudioComposition[size];
        }
    };
}
