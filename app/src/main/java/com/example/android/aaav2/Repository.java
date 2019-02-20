package com.example.android.aaav2;

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
import androidx.lifecycle.LiveData;
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

    //This class is used to copy and retrieve files from storage
    private FirebaseStorage mStorage;
    private File mStorageDirectory;
    private StorageReference audioClipsRawRef;

    private MutableLiveData<List<AudioClip>> mAllAudioClips;

    private FirestoreCallback listener;

    public Repository(FirestoreCallback L, File storageDirectory){
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        //reference to noSQL database
        audioClipsRef = mFirestore.collection("audio_clips");
        mAllAudioClips = new MutableLiveData<>();
        listener = L;
        mStorageDirectory = storageDirectory;
        //ref to Storage - where clip data is stored in the cloud
        audioClipsRawRef = mStorage.getReference().child("audio");
        mNumber_clips_left_to_dl = 0;
        readData();
    }

    private void readData(){
       audioClipsRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<AudioClip> mAllAudioClipsList = new ArrayList<>();
                            //for every snapshot in the task returned create a fucking
                            //audio clip
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                AudioClip clip = new AudioClip();

                                clip.setCategory(doc.get("category").toString());
                                clip.setEmoji(doc.get("emoji").toString());
                                clip.setFileName(doc.get("file_name").toString());
                                clip.setTitle(doc.get("title").toString());
                                clip.setVolume(doc.get("volume").toString());

                                mAllAudioClipsList.add(clip);
                                File f = new File(mStorageDirectory, clip.getFileName());
                                if(!f.isFile()) {
                                    mNumber_clips_left_to_dl +=1;
                                    _downloadBytes(clip);
                                }
                            }

                            mAllAudioClips.postValue(mAllAudioClipsList);

                            if(mNumber_clips_left_to_dl == 0){
                                listener.onDataLoaded(mAllAudioClipsList);
                            }
                        }
                        else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public int mNumber_clips_left_to_dl;
    public interface FirestoreCallback{
        void onDataLoaded(List<AudioClip> list);
    }
    public interface DataDownloadedListener{
        void onDataDownloaded(AudioClip clip);
    }

    public LiveData<List<AudioClip>> getAllAudioClips(){ return mAllAudioClips; }

    private void _downloadBytes(final AudioClip clip_dl){
        final StorageReference clip = audioClipsRawRef.child(clip_dl.getFileName());

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
                               listener.onDataLoaded(mAllAudioClips.getValue());
                           }
                       }
                       else
                           listener.onDataLoaded(mAllAudioClips.getValue());
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
            File file = new File(mStorageDirectory, mClip.getFileName());

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
