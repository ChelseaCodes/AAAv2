package com.example.android.aaav2;

import android.net.Uri;
import android.os.Bundle;

import com.example.android.aaav2.adapter.AudioClipPickerAdapter;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioClipPickerActivity extends FragmentActivity implements AudioCategoryFragment.OnFragmentInteractionListener {
    private static final int NUM_PAGES = 6;

    AudioClipPickerAdapter audioCategoryPickerAdapter;

    @BindView(R.id.vp_audio_clip_view_pager)
    ViewPager viewpager;

    @BindView(R.id.pt_category_title)
    PagerTitleStrip titleStrip;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        ButterKnife.bind(this);

       audioCategoryPickerAdapter = new AudioClipPickerAdapter(getSupportFragmentManager());
       viewpager.setAdapter(audioCategoryPickerAdapter);

    }

    @Override
    public void onBackPressed() {
        if (viewpager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
        }
    }




    @Override
    public void onFragmentInteraction(Uri uri) {
        //todo: implment bc i'm sure this doesn't do anything
        //it is used to pass infomation between fragments. This will be needed form .
    }
}

