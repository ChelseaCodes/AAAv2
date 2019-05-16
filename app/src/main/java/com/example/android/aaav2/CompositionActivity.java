package com.example.android.aaav2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android.aaav2.adapter.AudioClipPickerAdapter;
import com.example.android.aaav2.model.AudioComposition;
import com.example.android.aaav2.viewmodel.CompositionBuilderViewModel;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import static com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_END;

public class CompositionActivity extends AppCompatActivity implements AudioCategoryFragment.OnFragmentInteractionListener,
FloatingActionButton.OnClickListener, SaveWIPDialogFragment.OnFragmentInteractionListener,
        SaveWIPDialogFragment.OnCompositionSavedListener {

    public static final int PLAYBACK_PLAY = 1;
    public static final int PLAYBACK_PAUSE = 0;
    public static final int PLAYBACK_STOP = 2;

    private String TAG = "CompositionActivity";

    private CompositionBuilderViewModel mViewModel;

    @BindView(R.id.fab_save)
    FloatingActionButton fab_save;

    @BindView(R.id.navigation_compose)
    BottomAppBar nav_compose;

    @BindView(R.id.vp_audio_clip_view_pager)
    ViewPager viewPager;

    @BindView(R.id.pt_category_title)
    PagerTitleStrip titleStrip;

    AudioClipPickerAdapter adapterViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);

        //BK is 3rd party app for binding
        ButterKnife.bind(this);

        //init before using any Firebase components
        FirebaseApp.initializeApp(this);

        nav_compose.setFabAlignmentMode(FAB_ALIGNMENT_MODE_END);

        //associate ViewModel w this UI controller
        mViewModel = ViewModelProviders.of(this).get(CompositionBuilderViewModel.class);
        mViewModel.setDataDownloadedListener(new CompositionBuilderViewModel.DataDownloadedListener() {
            @Override
            public void onDataDownloaded() {
                adapterViewPager = new AudioClipPickerAdapter(getSupportFragmentManager());

                viewPager.setAdapter(adapterViewPager);
            }
        });

        //todo: REMOVE WHEN NO LONGER DEBUGGING.
        //mViewModel.RemoveUserWIP();

        fab_save.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
         //done composing, stop media player pool
        //will clean up mediaplayer instances.
        mViewModel.stopAudio(); //called in SaveWIPDialogFragment
        mViewModel.RemoveUserWIP();
    }

    @OnClick(R.id.fab_save)
    public void onClick(View v) {
        //create an AudioComposition and ship to SaveCompositionActivity
//        Intent i = new Intent(this, EditCompositionDialogFragment.class);
//
//        startActivity(i);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("SaveDialog");
        //ensure there's not already a dialog
        if(prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        SaveWIPDialogFragment saveDialog = SaveWIPDialogFragment.newInstance();
        saveDialog.setOnCompositionSavedListener(this);
        saveDialog.show(ft, "SaveDialog");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCompositionSavedListener() {
        Log.d(TAG, "Finish and Removing Task in Composition Activity");
        this.finishAndRemoveTask();
    }
}
