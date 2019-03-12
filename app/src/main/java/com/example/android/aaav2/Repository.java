package com.example.android.aaav2;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.model.AudioComposition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CollectionReference audioCompositionsRef;

    private FirebaseStorage mStorage;
    private File mStorageDirectory;
    private FileHelper fileHelper;
    private StorageReference audioClipsRawRef;
    private static String mUserID;

    //data needed for compositions
    private MutableLiveData<ArrayList<AudioComposition>> mAudioCompositions;

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

    private MutableLiveData<AudioComposition> mAudioComposition;

    public int Num_Clips = 0;
    public int mNumber_clips_left_to_dl;

    private OnDataLoadedListener onDataLoadedListener;
    private OnAudioCompositionRetrievedListener onAudioCompositionRetrievedListener;
    private OnAudioCompositionSavedListener onAudioCompositionSavedListener;

    public interface OnDataLoadedListener {
        void onDataLoaded(ArrayList<List<AudioClip>> list);
    }
    public interface OnDataDownloadedListener {
        void onDataDownloaded(AudioClip clip);
    }
    public interface OnAudioCompositionRetrievedListener{
        void onAudioCompositionRetrieved(MutableLiveData<AudioComposition> ac);
    }
    public interface OnAudioCompositionSavedListener{
        void onAudioCompositionSavedListener();
    }

    public Repository(Application app, OnDataLoadedListener L, File storageDirectory){
        init();
        onDataLoadedListener = L;
        mStorageDirectory = storageDirectory;
        fileHelper = new FileHelper(app.getApplicationContext());
        mUserID = FirebaseAuth.getInstance().getUid();
    }

    //called from dialog fragment
    public Repository(Application app){
        mAudioComposition = new MutableLiveData<>();
        init();
        fileHelper = new FileHelper(app.getApplicationContext());
    }

    private void init(){
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        //reference to noSQL database
        audioClipsRef = mFirestore.collection("audio_clips");
        audioCompositionsRef = mFirestore.collection("compositions");

        //ref to Storage - where clip data is stored in the cloud
        audioClipsRawRef = mStorage.getReference().child("audio");
        mNumber_clips_left_to_dl = 0;
    }

    public void ReadAllClips(){
        ALLCLIPS = new ArrayList<>();
        mAllAudioClips = new MutableLiveData<>();
        mCategories = new MutableLiveData<>();
        mWaterWeatherClips = new MutableLiveData<>();
        mAnimalsCrittersClips = new MutableLiveData<>();
        mWavesClips = new MutableLiveData<>();
        mFireClips = new MutableLiveData<>();
        mCityClips = new MutableLiveData<>();
        mUserClips = new MutableLiveData<>();

        readAllAudioClip();
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

    private void readAllAudioClip(){
        //gets all audio clips and filters/adds to category lists
        audioClipsRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<AudioClip> mAllAudioClipsList = new ArrayList<>();

                            ArrayList<AudioClip> mWaterWeather = new ArrayList<>();
                            ArrayList<AudioClip> mAnimalCritter = new ArrayList<>();
                            ArrayList<AudioClip> mWaves = new ArrayList<>();
                            ArrayList<AudioClip> mFire = new ArrayList<>();
                            ArrayList<AudioClip> mCity = new ArrayList<>();

//                            ArrayList<AudioClip> mUserUpload = new ArrayList<>();
                            ArrayList<String> categories = new ArrayList<>();
                            //for every snapshot in the task returned create audio clip
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Num_Clips += 1;
                                AudioClip clip = new AudioClip();

                                clip.setUserID(mUserID);
                                clip.setTitle(doc.get("title").toString());
                                clip.setCategory(doc.get("category").toString());
                                clip.setFile_Name(doc.get("file_name").toString());
                                clip.setVolume("1");

                                //collect list of all categories will use to create tabs
                                if(!categories.contains(clip.getCategory())){
                                    categories.add(clip.getCategory());
                                }

                                //add clip by category to each category list
                                switch(clip.getCategory()){
                                    case "Water & Weather":
                                        mWaterWeather.add(clip);
                                        break;
                                    case "Animals & Critters":
                                        mAnimalCritter.add(clip);
                                        break;
                                    case "Waves":
                                        mWaves.add(clip);
                                        break;
                                    case "Fire":
                                        mFire.add(clip);
                                        break;
                                    case "City":
                                        mCity.add(clip);
                                        break;
                                    case "User Uploaded":
                                        //mUserUpload.add(clip);
                                        break;
                                    default:
                                        //nothing
                                        break;
                                }
                                mAllAudioClipsList.add(clip);

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
                            mWaterWeatherClips.postValue(mWaterWeather);
                            mAnimalsCrittersClips.postValue(mAnimalCritter);
                            mWavesClips.postValue(mWaves);
                            mFireClips.postValue(mFire);
                            mCityClips.postValue(mCity);
//                            mUserClips.postValue(mUserUpload);
                            mCategories.postValue(categories);
                            //todo: update user uploaded
                            ALLCLIPS.add(mWaterWeather);
                            ALLCLIPS.add(mAnimalCritter);
                            ALLCLIPS.add(mWaves);
                            ALLCLIPS.add(mFire);
                            ALLCLIPS.add(mCity);
                            //ALLCLIPS.add(mUserUploaded);
                            if(mNumber_clips_left_to_dl == 0){
                                initWIP(mAllAudioClipsList);
                                onDataLoadedListener.onDataLoaded(ALLCLIPS);
                            }
                        }
                        else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /*
     *   Because Firestore doesn't allow subcollections, and bc my Firestore requires
     *   that the audio_clip collection is only read, not modified, and bc
     *   the FirebasePagerAdapter loads from my audio_clip collection, when new views
     *   are made, nothing is set and volume bars dissapear. it reads fresh data each time.
     *   I need to track the volume and what tacks the user has selected IN THE VIEW
     *   not just backend wise.
     *
     *   This function queries for all audio clips, and creates a NEW document under the
     *   wip (work in progress) collection. This way users wont be modifying the same data set
     *   and they get their own.
     * */
    private void initWIP(final ArrayList<AudioClip> clips){
        WriteBatch batch = mFirestore.batch();

        for(AudioClip c : clips){
            //create empty document with auto id in
            DocumentReference ref = mFirestore.collection("wip").document();

            Map<String, Object> firestore_clip = new HashMap<>();

            firestore_clip.put("title", c.getTitle());
            firestore_clip.put("category", c.getCategory());
            firestore_clip.put("volume", c.getVolume());
            firestore_clip.put("file_name", c.getFile_Name());
            firestore_clip.put("userID", mUserID); //userID makes clip queryable from RV
            firestore_clip.put("documentID", ref.getId());
            firestore_clip.put("selected", false);

            //one document per ac. query by documentID and category
            //which will give me an easy list of snapshots.
            //I'm doing this bc Firestore doesn't support querying nested
            //Subcollections or lists/maps.
            //this removes the need to store a documentID referenceing a whole audio comp.
            //whe whole audio comp will be put together at the end, querying the wip collection
            batch.set(ref, firestore_clip);
        }

        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to create work in progress ");
            }
        });
    }

    public void RemoveUserWIP(){
        removeUserWIP();
    }

    private void removeUserWIP(){
        final WriteBatch batch = mFirestore.batch();

       Query query = mFirestore.collection("wip")
                .whereEqualTo("userID", mUserID);

       query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()) {

                   for (QueryDocumentSnapshot doc : task.getResult()) {
                       batch.delete(doc.getReference());
                   }

                   batch.commit().addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Log.d(TAG, "Failed to purge WIP collection");
                       }
                   });
               }
               else
                   Log.d(TAG, "Found no documents to purge");
           }
       });
    }
    //WorkInProgress state of AudioCompositions
    public MutableLiveData<AudioComposition> GetUserWIP(){
        if(mAudioComposition.getValue() == null){
            AudioComposition c = new AudioComposition();
            c.setUserId(mUserID);
            Log.d(TAG, "posting mutable composition");
            mAudioComposition.setValue(c);
        }
        return getUserWIP();}

    private MutableLiveData<AudioComposition> getUserWIP(){
        //if(mAudioComposition.getValue() == null) {
            Log.d(TAG, "Repo creating AudioComposition");


                Query query = mFirestore.collection("wip")
                        .whereEqualTo("userID", mUserID)
                        .whereEqualTo("selected", true);

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                AudioClip c = doc.toObject(AudioClip.class); //hopefully works

                                if(mAudioComposition.getValue() != null)
                                     mAudioComposition.getValue().addAudioClip(c);
                                else
                                    Log.d(TAG, "Composition not posted");
                            }

                            //firelistener that composition is ready
                            if(onAudioCompositionRetrievedListener != null)
                                onAudioCompositionRetrievedListener
                                        .onAudioCompositionRetrieved(mAudioComposition);
                        } else
                            Log.d(TAG, "Failed to get WIP collection");
                    }
                });
       // }

        //Log.d(TAG, "AudioComposition not null");
        return mAudioComposition;
    }

//    private MutableLiveData<AudioComposition> createAudioComposition(){
//        //use AudioComposition POJO and populate with rudimentary data
//        AudioComposition c = new AudioComposition();
//        c.setUserId(mUserID);
//
//        audioCompositionsRef.add(c).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w(TAG, "Error adding document", e);
//            }
//        });
//
//    }

    //update audio composition as a whole
    public void UpdateUserCompositionWIP(ArrayList<AudioClip> clips){
        updateUserCompositionWIP(clips);
    }
    private void updateUserCompositionWIP(ArrayList<AudioClip> clips){
        for(AudioClip c : clips){
            updateClipWIP(c);
        }
    }

    //update audio clip in wip collection for audio composition
    public void UpdateClip(AudioClip c){
        updateClipWIP(c);
    }
    private void updateClipWIP(AudioClip c){
        //grab wip document for clip and update volume and selected.
        mFirestore.collection("wip")
                .document(c.getDocumentID())
                .update("volume", c.getVolume());

        mFirestore.collection("wip")
                .document(c.getDocumentID())
                .update("selected", c.isSelected());
    }

    public void SaveAudioComposition(AudioComposition ac){
        saveAudioComposition(ac);
    }
    private void saveAudioComposition(AudioComposition ac){
        Map<String, Object> docData = new HashMap<>();
        Log.d(TAG, "Saving composition to firestore");

        ArrayList<AudioClip> clips = mAudioComposition.getValue().getAudioClips();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> volumes = new ArrayList<>();
        ArrayList<String> filenames = new ArrayList<>();

        for(AudioClip c : clips){
            titles.add(c.getTitle());
            volumes.add(c.getVolume());
            filenames.add(c.getFile_Name());
        }

        docData.put("userID", mUserID);
        docData.put("title", ac.getCompositionTitle());
        docData.put("length",String.valueOf(ac.getLength()));
        docData.put("tags", ac.getTags());
        docData.put("ac_titles", titles);
        docData.put("ac_volumes", volumes);
        docData.put("ac_filenames", filenames);

        mFirestore.collection("compositions").add(docData).addOnCompleteListener(
                new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.d(TAG, "Saved composition to firestore");
                            if(onAudioCompositionSavedListener != null)
                                onAudioCompositionSavedListener.onAudioCompositionSavedListener();
                    }
                }
        );

    }

    public void setOnAudioCompositionRetrievedListener(OnAudioCompositionRetrievedListener listener){
        onAudioCompositionRetrievedListener = listener;
    }

    public void setOnAudioCompositionSavedListener(OnAudioCompositionSavedListener listener){
        onAudioCompositionSavedListener = listener;
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
//    private void readWaves{}
//    private void readFire{}
//    private void readCity{}
//    private void readUserUploaded{}

    private void _downloadBytes(final AudioClip clip_dl){
        final StorageReference clip = audioClipsRawRef.child(clip_dl.getFile_Name());

        clip.getBytes(DEFAULT_BUFFER_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
               new DownloadAudioAsync(clip_dl, bytes, mStorageDirectory, new OnDataDownloadedListener() {
                   @Override
                   public void onDataDownloaded(AudioClip clip) {
                       Log.d(TAG, clip.getTitle() + " Has been Downloaded");
                       if(mNumber_clips_left_to_dl != 0){
                           mNumber_clips_left_to_dl -= 1;
                           if(mNumber_clips_left_to_dl == 0){
                               onDataLoadedListener.onDataLoaded(ALLCLIPS);
                           }
                       }
                       else
                           onDataLoadedListener.onDataLoaded(ALLCLIPS);
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
        private OnDataDownloadedListener onDataDownloadedListener;
        private File mStorageDirectory;

        public DownloadAudioAsync(AudioClip clip, byte [] bytes
                , File storageDir, OnDataDownloadedListener listener){
            onDataDownloadedListener = listener;
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

            onDataDownloadedListener.onDataDownloaded(mClip);
        }
    }
}
