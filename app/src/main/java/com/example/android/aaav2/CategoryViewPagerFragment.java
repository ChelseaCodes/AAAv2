package com.example.android.aaav2;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.aaav2.adapter.AudioClipPickerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class CategoryViewPagerFragment extends Fragment {
    AudioClipPickerAdapter adapterViewPager;
    ViewPager viewPager;
    PagerTitleStrip pagerTitleStrip;
    public CategoryViewPagerFragment() {
    }

    public static CategoryViewPagerFragment newInstance(){
        CategoryViewPagerFragment c = new CategoryViewPagerFragment();
        return  c;
    }

    /*
    *
    * ensures that the fragment's root view is non-null.
    * Any view setup should happen here. E.g., view lookups, attaching listeners.*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_screen_slide, container, false);

        viewPager = v.findViewById(R.id.vp_audio_clip_view_pager);
        adapterViewPager = new AudioClipPickerAdapter(getChildFragmentManager());

        viewPager.setAdapter(adapterViewPager);
        viewPager.setBackgroundColor(Color.CYAN);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup any handles to view objects here
        //viewPager = view.findViewById(R.id.vp_audio_clip_view_pager);
//
        viewPager.setAdapter(new AudioClipPickerAdapter(getChildFragmentManager()));
    }

    //    @Override
//    public void onFragmentInteraction(Uri uri) {
//        //todo: implment bc i'm sure this doesn't do anything
//        //it is used to pass infomation between fragments. This will be needed form .
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
