package com.example.android.aaav2.adapter;

import android.os.Bundle;

import com.example.android.aaav2.AudioCategoryFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class AudioClipPickerAdapter extends FragmentStatePagerAdapter {

    public AudioClipPickerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        AudioCategoryFragment f = new AudioCategoryFragment();
        position += 1;

        Bundle b = new Bundle();
        b.putString("message", "Fragment : " + position);
        f.setArguments(b);
        return f;

        //one way to implement this would be to have a fragment per view.
//        switch(position){
//            case 0:
//                return new AudioCategoryFragment();
//            case 1:
//            case 2:
//            case 3:
//            case 4:
//            case 5:
//            case 6:
//            default:
//                return null;
//        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}
