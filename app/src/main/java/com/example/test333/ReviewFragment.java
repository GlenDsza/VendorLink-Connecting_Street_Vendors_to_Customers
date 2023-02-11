package com.example.test333;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {
    private ReviewAdapter ReviewAdapter;
    RecyclerView frev_recylerview;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList <frevReviewObject> reviewList;
    sqliteDB sdb;
    String username;
    private FirebaseFirestore userdb;
    DatabaseReference database;
    ArrayList <String> l_id;
    StorageReference mStorage;
    Uri prof_uri;
    String cus_id,cus_review;
    float rate;
    String string_uri;
    List<Uri> listURI;
    //firebaseDetails fd = new firebaseDetails(getContext());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ////Habban Addition
        sdb = new sqliteDB(getContext());
        username=sdb.getDoc();
        userdb= FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance().getReference("HAWKERS").child(username).child("reviews");
        l_id = new ArrayList<String>();
        mStorage = FirebaseStorage.getInstance().getReference();
        listURI= new ArrayList<Uri>();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        reviewList = new ArrayList<>();
        frev_recylerview = rootView.findViewById(R.id.frev_recyclerview);
        frev_recylerview.setNestedScrollingEnabled(false);
        frev_recylerview.setHasFixedSize(false);
        frev_recylerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        frev_recylerview.setLayoutManager(layoutManager);

        ////by habban
        ReviewAdapter = new ReviewAdapter(reviewList);
        frev_recylerview.setAdapter(ReviewAdapter);
        updateData();
        //////////////////////////////////
        return rootView;
    }

    private void updateData(){
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching Details");
        dialog.show();
        userdb.collection("HAWKERS").document(username).collection("reviews").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                reviewList.clear();
                for (DocumentSnapshot doc : value) {
                    dialog.show();
                    if (error != null) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        cus_id = doc.getString("name");
                        cus_review = doc.getString("review");
                        rate = Float.parseFloat(doc.get("rating").toString());
                        string_uri = doc.getString("profile_image");

                        if (string_uri!=null) {
                            prof_uri = Uri.parse(string_uri);
                            frevReviewObject frevReviewObject = new frevReviewObject(cus_id, cus_review, prof_uri, rate);
                            reviewList.add(frevReviewObject);
                            Toast.makeText(getContext(), "if executed", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(), "else executed", Toast.LENGTH_SHORT).show();
                            frevReviewObject frevReviewObject = new frevReviewObject(cus_id, cus_review, prof_uri, rate);
                            reviewList.add(frevReviewObject);
                        }
                        ReviewAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });
    }
}