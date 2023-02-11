package com.example.test333;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BSImageFragment extends Fragment {

    RecyclerView bsimg_recyclerview;
    ArrayList <String> fimg_images;
    ImageAdapter imageAdapter;
    String hawker_name;
    private StorageReference mStorage;

    BSImageFragment(String h_name){this.hawker_name = h_name;}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fimg_images = new ArrayList<>();
        View bsimg_rootView =inflater.inflate(R.layout.fragment_bs_image, container, false);
        bsimg_recyclerview= bsimg_rootView.findViewById(R.id.bsimg_recyclerview);
        mStorage = FirebaseStorage.getInstance().getReference();
        initialize(hawker_name);
        imageAdapter = new ImageAdapter(getContext(), fimg_images);
        GridLayoutManager bsimg_gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        bsimg_recyclerview.setLayoutManager(bsimg_gridLayoutManager);
        //bsimg_recyclerview.setAdapter(imageAdapter);

        return bsimg_rootView;
    }

    public void initialize(String user){
        /*        retrieve images from database*/
        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("USERS").child("HAWKERS").child(user).child("gallery");
        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference file:listResult.getItems()){
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            fimg_images.add(uri.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            bsimg_recyclerview.setAdapter(imageAdapter);
                        }
                    });
                }
            }
        });
    }
}