package com.example.test333;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BSAdapter extends FragmentStateAdapter {
    private String titles[] = new String[]{"ITEMS", "GALLERY", "REVIEWS"};
    String hawker_name;

    public BSAdapter(@NonNull BottomSheetFragment fragmentActivity,String h_name) {
        super(fragmentActivity);
        this.hawker_name = h_name;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new BSMenuFragment(hawker_name);
            case 1:
                return new BSImageFragment(hawker_name);
            case 2:
                return new BSReviewFragment(hawker_name);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
