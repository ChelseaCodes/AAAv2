package com.example.android.aaav2;

import android.content.Context;
import com.example.android.aaav2.viewmodel.CompositionBuilderViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.annotation.NonNull;

/*
* FileHelper is a class that takes the fileName given and searches Firebase Storage
* for the audio to download.
* */
public class FileHelper {
    private  static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

    private FirebaseStorage storage;
    private File storageDirectory;
    private Context context;
    //ref to upload, dl, del, update metadata - just a pointer to a file
    private StorageReference storageReference;
    private String mFileName;
    private CompositionBuilderViewModel.OnDownloadCompleted mListener;

    public FileHelper(Context context) {
        this.context = context;
        //returns the abs path to directory on the filesystem where FileOutput files are
        //stored
        this.storageDirectory = context.getFilesDir();
        storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference().child("audio");
    }

    public void setOnDownloadCompleteListener(CompositionBuilderViewModel.OnDownloadCompleted listener){
        mListener = listener;
    }
    //called from main activity
    public void copyToDevice(String fileName){
        //create reference with filename
        mFileName = fileName;
        //precheck and don't download if already downloaded.
        File F = getFile(mFileName);
        if(!F.isFile())
            requestBytes();
    }

    //called from main activity
    public void copyToDevice(String fileName, byte[] bytes){
        copy(bytes, fileName);
    }

    private void requestBytes(){
        StorageReference clip = storageReference.child(mFileName);

        clip.getBytes(DEFAULT_BUFFER_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for clip is returned and stored
                //decrements number of files to download
                //copy(bytes);

                if(mListener != null){
                    mListener.onDownloadCompleted();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void copy(byte[] bytes, String fileName){
        //file to be saved under %AUDIOCLIPTITLE%
        File file = new File(storageDirectory, fileName);

        //output stream needed to write files to
        OutputStream outputStream = null;

        try {
            //create stream to file under audio/%AUDIOCLIPTITLE%
            outputStream = new FileOutputStream(file);

            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(outputStream);
            if(mListener != null){
                mListener.onDownloadCompleted();
            }
        }
    }

    public File getFile(String fileName) {
        return new File(storageDirectory, fileName);
    }

    private static void closeStream(Closeable stream){
        try{
            if(stream != null) {
                stream.close();
            }
        }catch(IOException ioe) {
            //ignore
        }
    }
}
