package com.example.android.aaav2.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AudioClip {
    private String documentID;
    private String category;
    private String title;
    private String volume;
    private String emoji;
    private String file_name;
    private int _StreamingID;
    private int _SoundPoolID;

    public AudioClip(String mClipID, String mCategory, String mTitle, String mVolume, String mEmoji, String mFileName) {
        this.documentID = mClipID;
        this.category = mCategory;
        this.title = mTitle;
        this.volume = mVolume;
        this.emoji = mEmoji;
        this.file_name = mFileName;
    }

    public AudioClip(){}; //needed for Firebase auto mapping

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

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String mEmoji) {
        this.emoji = mEmoji;
    }

    public String getFile_Name() {
        return file_name;
    }

    public void setFile_Name(String file_name) {
        this.file_name = file_name;
    }

    public int getStreamingID() {
        return _StreamingID;
    }

    public void setStreamingID(int _StreamingID) {
        this._StreamingID = _StreamingID;
    }

    public int get_SoundPoolID() {
        return _SoundPoolID;
    }

    public void set_SoundPoolID(int _SoundPoolID) {
        this._SoundPoolID = _SoundPoolID;
    }
}

