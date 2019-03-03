package com.example.android.aaav2;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.aaav2.model.AudioClip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

/**
 * Repository modules handle data operations.
 * They provide a clean API so that the rest of the app can retrieve this data easily.
 * They know where to get the data from and what API calls to make when data is updated.
 * You can consider repositories to be mediators between different data sources,
 * such as persistent models, web services, and caches.
 *
 */

public class Repository {
    private String TAG = "Repository";
    private  static final int DEFAULT_BUFFER_SIZE = 1024 * 1024 * 5;//five mb

    private FirebaseFirestore mFirestore;
    private CollectionReference audioClipsRef;

    private FirebaseStorage mStorage;
    private File mStorageDirectory;
    private FileHelper fileHelper;
    private StorageReference audioClipsRawRef;

    //todo: have allaudioclips be a list of lists of audio clips and add each categorylist to it
    private ArrayList<List<AudioClip>> ALLCLIPS;
    private MutableLiveData<List<AudioClip>> mAllAudioClips;
    private MutableLiveData<List<String>> mCategories;
    private MutableLiveData<List<AudioClip>> mWaterWeatherClips;
    private MutableLiveData<List<AudioClip>> mAnimalsCrittersClips;
    private MutableLiveData<List<AudioClip>> mWavesClips;
    private MutableLiveData<List<AudioClip>> mFireClips;
    private MutableLiveData<List<AudioClip>> mCityClips;
    private MutableLiveData<List<AudioClip>> mUserClips;
    public int Num_Clips = 0;
    public int mNumber_clips_left_to_dl;

    private FirestoreCallback listener;
    //private WaterWeatherDownloadedListener waterWeatherDownloadedListener;

    public interface FirestoreCallback{
        void onDataLoaded(ArrayList<List<AudioClip>> list);
    }
    public interface DataDownloadedListener{
        void onDataDownloaded(AudioClip clip);
    }
    public interface WaterWeatherDownloadedListener{
        void onWaterWeatherDownloaded(List<AudioClip> list);
    }

    public Repository(Application app, FirestoreCallback L, File storageDirectory){
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        fileHelper = new FileHelper(app.getApplicationContext());
        //reference to noSQL database
        audioClipsRef = mFirestore.collection("audio_clips");

        ALLCLIPS = new ArrayList<>();
        mAllAudioClips = new MutableLiveData<>();
        mCategories = new MutableLiveData<>();
        mWaterWeatherClips = new MutableLiveData<>();
        mAnimalsCrittersClips = new MutableLiveData<>();
        mWavesClips = new MutableLiveData<>();
        mFireClips = new MutableLiveData<>();
        mCityClips = new MutableLiveData<>();
        mUserClips = new MutableLiveData<>();

        listener = L;
        mStorageDirectory = storageDirectory;

        //ref to Storage - where clip data is stored in the cloud
        audioClipsRawRef = mStorage.getReference().child("audio");
        mNumber_clips_left_to_dl = 0;

        readAllAudioClip();
        //readAllAudioClipsByCategory();


    }

    public MutableLiveData<List<String>> getCategories() {
        return mCategories;
    }

    public MutableLiveData<List<AudioClip>> getWaterWeatherClips() {
        return mWaterWeatherClips;
    }

    public MutableLiveData<List<AudioClip>> getAnimalsCrittersClips() {
        return mAnimalsCrittersClips;
    }

    public MutableLiveData<List<AudioClip>> getWavesClips() {
        return mWavesClips;
    }

    public MutableLiveData<List<AudioClip>> getFireClips() {
        return mFireClips;
    }

    public MutableLiveData<List<AudioClip>> getCityClips() {
        return mCityClips;
    }

    public MutableLiveData<List<AudioClip>> getUserClips() {
        return mUserClips;
    }

    public ArrayList<List<AudioClip>> getAllAudioClips(){ return ALLCLIPS; }

    public int getNum_Clips() {
        return Num_Clips;
    }

    //minimize queries. I will load by category and just add to this list too. by the end it'll
    //be all audio clips anyways.


    private void readAllAudioClip(){
        //gets all audio clips and filters/adds to category lists
        audioClipsRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<AudioClip> mAllAudioClipsList = new ArrayList<>();

                            ArrayList<AudioClip> mWaterWeatherClipsList = new ArrayList<>();
                            ArrayList<AudioClip> mAnimalCritterClipsList = new ArrayList<>();
//                            ArrayList<AudioClip> mWaves = new ArrayList<>();
//                            ArrayList<AudioClip> mFire = new ArrayList<>();
//                            ArrayList<AudioClip> mUserUpload = new ArrayList<>();
                            ArrayList<String> categories = new ArrayList<>();

                            //for every snapshot in the task returned create audio clip
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Num_Clips += 1;
                                AudioClip clip = new AudioClip();

                                clip.setDocumentID(doc.getId());
                                clip.setTitle(doc.get("title").toString());
                                clip.setCategory(doc.get("category").toString());
                                //clip.setEmoji(doc.get("emoji").toString());
                                clip.setFile_Name(doc.get("file_name").toString());
                                clip.setVolume("1.0");

                                //mAllAudioClipsList.add(clip);

                                //collect list of all categories will use to create tabs
                                if(!categories.contains(clip.getCategory())){
                                    categories.add(clip.getCategory());
                                }

                                //add clip by category to each category list
                                switch(clip.getCategory()){
                                    case "Water & Weather":
                                        mWaterWeatherClipsList.add(clip);
                                    case "Animals & Critters":
                                        mAnimalCritterClipsList.add(clip);
                                    case "Waves":
                                        //mWaves.add(clip);
                                    case "Fire":
                                        //mFire.add(clip);
                                    case "User Uploaded":
                                        //mUserUpload.add(clip);
                                    default:
                                        //nothing
                                }

                                //if(fileHelper.copyAssetToStorage(clip.getFileName())){
//                                    File f = fileHelper.getFile(clip.getFileName());
//
//                                    if(!f.isFile()) {
//                                        mNumber_clips_left_to_dl +=1;
//                                        _downloadBytes(clip);
//                                    }
                                //}
                            }

                            //mAllAudioClips.postValue(mAllAudioClipsList);
                            mWaterWeatherClips.postValue(mWaterWeatherClipsList);
//                            mAnimalsCrittersClips.postValue(mAnimalCritterClipsList);
//                            mWavesClips.postValue(mWaves);
//                            mFireClips.postValue(mFire);
//                            mUserClips.postValue(mUserUpload);
                            mCategories.postValue(categories);
                            //todo: update and add other clips to said list
                            ALLCLIPS.add(mWaterWeatherClipsList);
                            ALLCLIPS.add(mAnimalCritterClipsList);

                            if(mNumber_clips_left_to_dl == 0){
                                listener.onDataLoaded(ALLCLIPS);
                            }
                        }
                        else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void readAllAudioClipsByCategory(){
        readWaterWeather();
    }

    private void readWaterWeather(){
       audioClipsRef.whereEqualTo("category", "Water & Weather")
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           ArrayList<AudioClip> mWaterWeather = new ArrayList<>();

                           for(QueryDocumentSnapshot doc : task.getResult()){
                               //todo: might break
                               //doc.toObject(AudioClip.class);
                               AudioClip clip = new AudioClip();

                               clip.setTitle(doc.get("title").toString());
                               clip.setCategory(doc.get("category").toString());
                               clip.setFile_Name(doc.get("file_name").toString());

                               clip.setVolume("1.0");

                               mWaterWeather.add(clip);

                               //if(fileHelper.copyAssetToStorage(clip.getFileName())){
//                                    File f = fileHelper.getFile(clip.getFileName());
//
//                                    if(!f.isFile()) {
//                                        mNumber_clips_left_to_dl +=1;
//                                        _downloadBytes(clip);
//                                    }
                               //}
                           }

                           mWaterWeatherClips.postValue(mWaterWeather);
                       }
                       else{
                           Log.d(TAG, "Error getting documents: ", task.getException());
                       }
                   }
               });
    }
//    private void readAnimalCritter{}
//
//    private void readWaves{}
//    private void readFire{}
//    private void readCity{}
//    private void readUserUploaded{}

    private void _downloadBytes(final AudioClip clip_dl){
        final StorageReference clip = audioClipsRawRef.child(clip_dl.getFile_Name());

        clip.getBytes(DEFAULT_BUFFER_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
               new DownloadAudioAsync(clip_dl, bytes, mStorageDirectory, new DataDownloadedListener() {
                   @Override
                   public void onDataDownloaded(AudioClip clip) {
                       Log.d(TAG, clip.getTitle() + " Has been Downloaded");
                       if(mNumber_clips_left_to_dl != 0){
                           mNumber_clips_left_to_dl -= 1;
                           if(mNumber_clips_left_to_dl == 0){
                               listener.onDataLoaded(ALLCLIPS);
                           }
                       }
                       else
                           listener.onDataLoaded(ALLCLIPS);
                   }
               }).execute();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "FAILED TO DOWNLOAD");
            }
        });
    }

    private static class DownloadAudioAsync extends AsyncTask<Void, Void, Void> {
        String TAG = "DownloadAudioAsync";
        private AudioClip mClip;
        private byte[] mBytes;
        private DataDownloadedListener mListener;
        private File mStorageDirectory;

        public DownloadAudioAsync(AudioClip clip, byte [] bytes
                , File storageDir, DataDownloadedListener listener){
            mListener = listener;
            mBytes = bytes;
            mClip = clip;
            mStorageDirectory = storageDir;
        }

        /*
         * fileHelper is going to access FireStore and download directly from there.
         * */
        @Override
        protected Void doInBackground(Void... voids) {
            // Data for clip is returned and stored
            //decrements number of files to download
            //file to be saved under %AUDIOCLIPTITLE%
            File file = new File(mStorageDirectory, mClip.getFile_Name());

            //output stream needed to write files to
            OutputStream outputStream = null;

            try {
                //create stream to file under audio/%AUDIOCLIPTITLE%
                outputStream = new FileOutputStream(file);

                outputStream.write(mBytes);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try{
                    if(outputStream != null) {
                        outputStream.close();
                    }
                }catch(IOException ioe) {
                    //ignore
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mListener.onDataDownloaded(mClip);
        }
    }
}
