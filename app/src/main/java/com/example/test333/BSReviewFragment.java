package com.example.test333;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BSReviewFragment extends Fragment implements BSReviewDialog.BSReviewDialogListener {
    Button bsrev_b1;
    String hawker_name;
    ArrayList<frevReviewObject> reviewList;
    RecyclerView frev_recylerview;
    private RecyclerView.LayoutManager layoutManager;
    private ReviewAdapter ReviewAdapter;
    private FirebaseFirestore userdb;
    DatabaseReference database;
    StorageReference mStorage;
    Uri prof_uri;
    String cus_id,cus_review;
    float rate;
    sqliteDB sdb;
    String username;
    String c_uri,c_name,string_uri;

    BSReviewFragment(String h_name){this.hawker_name=h_name;}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bs_review, container, false);
        bsrev_b1 = v.findViewById(R.id.bsrev_b1);
        reviewList = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("HAWKERS").child(hawker_name).child("reviews");
        mStorage = FirebaseStorage.getInstance().getReference();
        userdb= FirebaseFirestore.getInstance();
        sdb = new sqliteDB(getContext());
        username=sdb.getDoc();

        frev_recylerview = v.findViewById(R.id.bsrev_recyclerview);
        frev_recylerview.setNestedScrollingEnabled(false);
        frev_recylerview.setHasFixedSize(false);
        frev_recylerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        frev_recylerview.setLayoutManager(layoutManager);

        ////by habban
        ReviewAdapter = new ReviewAdapter(reviewList);
        frev_recylerview.setAdapter(ReviewAdapter);
        updateData();

        bsrev_b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSReviewDialog bsreview_dialog = new BSReviewDialog();
                bsreview_dialog.setTargetFragment(BSReviewFragment.this, 696);
                bsreview_dialog.show(getParentFragmentManager(), "Add Review");
            }
        });

        return v;
    }



    private void updateData(){
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching Details");
        userdb.collection("HAWKERS").document(hawker_name).collection("reviews").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
    @Override
    public void getData(String review, Float ratings) {
        fetchdetails(review,ratings);

    }

    private void insertIntoDatabase(String review, Float ratings) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Adding item");
        pd.show();
        Map<String,String> items=new HashMap<>();
        items.put("review",review);
        items.put("rating",ratings.toString());
        items.put("profile_image",c_uri);
        items.put("name",c_name);
        DocumentReference docRef = userdb.collection("HAWKERS").document(hawker_name).collection("reviews").document(username);
        docRef.set(items).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "item Added", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });docRef.set(items).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Operation failed", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }
    public  void fetchdetails(String review, Float ratings){
        FirebaseFirestore.getInstance()
                .collection("CUSTOMER")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        c_name = documentSnapshot.getString("Name");
                        Log.d("details", c_name);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        StorageReference filepath = mStorage.child("USERS").child("CUSTOMER").child(username).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                c_uri = uri.toString();
                Log.d("details", c_uri);
                insertIntoDatabase(review,ratings);
            }
        });

    }


}