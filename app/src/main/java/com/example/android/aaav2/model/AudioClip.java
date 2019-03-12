package com.example.android.aaav2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AudioClip implements Parcelable {
    private String documentID;
    private String compositionID;
    private String userID;
    private String category;
    private String title;
    private String volume;
    private String file_name;
    private boolean selected;


    public AudioClip(){}; //needed for Firebase auto mapping

    public AudioClip(Parcel in){
        documentID = in.readString();
        compositionID = in.readString();
        category = in.readString();
        title = in.readString();
        volume = in.readString();
        file_name = in.readString();
        //selected = in.readBooleanArray();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String mClipID) {
        this.documentID = mClipID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String mCategory) {
        this.category = mCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String mVolume) {
        this.volume = mVolume;
    }

    public String getFile_Name() {
        return file_name;
    }

    public void setFile_Name(String file_name) {
        this.file_name = file_name;
    }


    public String getCompositionID() {
        return compositionID;
    }

    public void setCompositionID(String compositionID) {
        this.compositionID = compositionID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentID);
        dest.writeString(compositionID);
        dest.writeString(category);
        dest.writeString(title);
        dest.writeString(volume);
        dest.writeString(file_name);
    }

    public static final Creator<AudioClip> CREATOR = new Creator<AudioClip>() {
        @Override
        public AudioClip createFromParcel(Parcel source) {
            return new AudioClip(source);
        }

        @Override
        public AudioClip[] newArray(int size) {
            return new AudioClip[size];
        }
    };
}

