package com.example.test333;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageFragment extends Fragment {

    static MutableLiveData<String> listen = new MutableLiveData<>();
    RecyclerView fimg_recyclerview;
    ArrayList <String> fimg_images;
    ImageAdapter imageAdapter;
    Button fimg_b1;
    ImageAdapter adapter;

    Uri insert;
    sqliteDB sdb;
    String username;
    private StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fimg_images = new ArrayList<>();
        adapter=new ImageAdapter(getContext(),fimg_images);
        // Inflate the layout for this fragment
        View fimg_rootView = inflater.inflate(R.layout.fragment_image, container, false);
        fimg_recyclerview= fimg_rootView.findViewById(R.id.fimg_recyclerview);
        sdb = new sqliteDB(getContext());
        username = sdb.getDoc();
        mStorage = FirebaseStorage.getInstance().getReference();
        initialize(username);
        imageAdapter = new ImageAdapter(getContext(), fimg_images);
        GridLayoutManager fimg_gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        fimg_recyclerview.setLayoutManager(fimg_gridLayoutManager);
        fimg_recyclerview.setAdapter(imageAdapter);


        //to add new images
        fimg_b1 = fimg_rootView.findViewById(R.id.fimg_b1);
        fimg_b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 ImagePicker.with(getActivity())
                        .cropSquare()	    			//Crop image(Optional), Check Customization for more
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(96);

            }
        });


        return fimg_rootView;
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
                            fimg_recyclerview.setAdapter(adapter);
                        }
                    });
                }
            }
        });
    }
}