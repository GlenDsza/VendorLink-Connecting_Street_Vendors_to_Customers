package com.example.test333;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VHomeVPAdapter extends FragmentStateAdapter {

    private String titles[] = new String[]{"ITEMS", "GALLERY", "REVIEWS"};

    public VHomeVPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MenuFragment();
            case 1:
                return new ImageFragment();
            case 2:
                return new ReviewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
