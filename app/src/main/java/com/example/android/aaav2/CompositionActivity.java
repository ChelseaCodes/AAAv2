package com.example.android.aaav2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.example.android.aaav2.adapter.CompositionBuilderAdapter;
import com.example.android.aaav2.model.AudioClip;
import com.example.android.aaav2.viewmodel.CompositionBuilderViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

import static com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_END;

public class CompositionActivity extends AppCompatActivity implements AudioCategoryFragment.OnFragmentInteractionListener {

    public static final int PLAYBACK_PLAY = 1;
    public static final int PLAYBACK_PAUSE = 0;
    public static final int PLAYBACK_STOP = 2;

    private String TAG = "CompositionActivity";

    private CompositionBuilderAdapter mAdapter;
    private CompositionBuilderViewModel mViewModel;

    private FirebaseAuth mAuth; //needed to authenticate users when App boots
    private FirebaseFirestore mFirestore; //needed to access collections/docs
    private Query mQuery;

    @BindView(R.id.rv_audio_clips)
    RecyclerView recyclerView;

    @BindView(R.id.fab_save)
    FloatingActionButton fab_save;

    @BindView(R.id.navigation_compose)
    BottomAppBar nav_compose;

//    @BindView(R.id.tb_toolbar)
//    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);

        //BK is 3rd party app for binding
        ButterKnife.bind(this);

        //init before using any Firebase components
        FirebaseApp.initializeApp(this);
        //Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        nav_compose.setFabAlignmentMode(FAB_ALIGNMENT_MODE_END);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.category_fragment, new AudioCategoryFragment())
                .commit();
    }

//    private void initRecyclerView(){
//        mQuery = mFirestore.collection("audio_clips");
//
//        mAdapter = new CompositionBuilderAdapter(mQuery, this, this){
//            @Override
//            protected void onDataChanged(){
//                Log.d(TAG, "DATA WAS CHANGED IN CompBuilderAdapter");
//                //data should not change
//            }
//            @Override
//            protected void onError(FirebaseFirestoreException e) {
//                // Show a snackbar on errors
//                Snackbar.make(findViewById(android.R.id.content),
//                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
//            }
//        };
//
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.setAdapter(mAdapter);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        //associate ViewModel w this UI controller
//        mViewModel = ViewModelProviders.of(this).get(CompositionBuilderViewModel.class);
//        mViewModel.setDataDownloadedListener(new CompositionBuilderViewModel.DataDownloadedListener() {
//            @Override
//            public void onDataDownloaded() {
//                initRecyclerView();
//
//                if(mAdapter != null)
//                    mAdapter.startListening();
//            }
//        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //use viewmodel i assume
    }

    /*
    * Audio Clip was selected from the RecyclerView.
    * Send to the ViewModel to handle the request to play/pause
    * */
//    @Override
//    public void onAudioClipSelected(DocumentSnapshot AudioClip, int playback, int adapterPos) {
//        com.example.android.aaav2.model.AudioClip clip = AudioClip.toObject(AudioClip.class);
//
//        switch(playback){
//            case PLAYBACK_PAUSE:
//                mViewModel.pauseAudio(adapterPos);
//                break;
//            case PLAYBACK_PLAY:
//                mViewModel.playAudio(adapterPos);
//                break;
//        }
//    }
//
//    /*
//    * Called when user adjusts the volume via slider on a playing audio clip
//    * */
//    @Override
//    public void onVolumeChangedListener(DocumentSnapshot AudioClip, float volume, int adapterPos) {
//        mViewModel.setAudioVolume(adapterPos, volume);
//    }
}
