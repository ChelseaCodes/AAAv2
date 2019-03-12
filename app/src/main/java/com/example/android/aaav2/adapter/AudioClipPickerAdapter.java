package com.example.android.aaav2.adapter;

import android.os.Bundle;
import android.util.SparseArray;

import com.example.android.aaav2.AudioCategoryFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
public class AudioClipPickerAdapter extends FragmentStatePagerAdapter {

    public AudioClipPickerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
//        AudioCategoryFragment f = new AudioCategoryFragment();
//        position += 1;
//
//        Bundle b = new Bundle();
//        b.putString("message", "Fragment : " + position);
//        f.setArguments(b);
//        return f;

        //one way to implement this would be to have a fragment per view.
        switch(position){
            case 0:
                return AudioCategoryFragment.newInstance("Water & Weather", "");
            case 1:
                return AudioCategoryFragment.newInstance("Animals & Critters", "");
            case 2:
                return AudioCategoryFragment.newInstance("Fire", "");
            case 3:
                return AudioCategoryFragment.newInstance("City", "");
            case 4:
                return AudioCategoryFragment.newInstance("Waves", "");
            case 5:
                return AudioCategoryFragment.newInstance("Meditation", "");
            //case 6:
                //return AudioCategoryFragment.newInstance("My Uploads", "");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch(position){
            case 0:
                return "Water & Weather";
            case 1:
                return "Animals & Critters";
            case 2:
                return "Fire";
            case 3:
                return "City";
            case 4:
                return "Waves";
            case 5:
                return "Meditation";
            //case 6:
            //return AudioCategoryFragment.newInstance("My Uploads", "");
            default:
                return null;
        }
    }

}
