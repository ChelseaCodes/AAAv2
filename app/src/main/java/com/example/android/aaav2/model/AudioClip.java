package com.example.android.aaav2.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AudioClip {
    private String mClipID;
    private String category;
    private String title;
    private String length;
    private String volume;
    private String emoji;
    private String file_name;

    public AudioClip(String mClipID, String mCategory, String mTitle, String mLength, String mVolume, String mEmoji, String mFileName) {
        this.mClipID = mClipID;
        this.category = mCategory;
        this.title = mTitle;
        this.length = mLength;
        this.volume = mVolume;
        this.emoji = mEmoji;
        this.file_name = mFileName;
    }
    public AudioClip(){};

    public String getClipID() {
        return mClipID;
    }

    public void setClipID(String mClipID) {
        this.mClipID = mClipID;
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

    public String getLength() {
        return length;
    }

    public void setLength(String mLength) {
        this.length = mLength;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String mVolume) {
        this.volume = mVolume;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String mEmoji) {
        this.emoji = mEmoji;
    }

    public String getFileName() {
        return file_name;
    }

    public void setFileName(String mFileName) {
        this.file_name = mFileName;
    }
}
