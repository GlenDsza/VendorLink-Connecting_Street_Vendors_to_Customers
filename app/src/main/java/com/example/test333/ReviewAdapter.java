package com.example.test333;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.frevReviewViewHolder> {

    private ArrayList <frevReviewObject> reviewlist;
    public ReviewAdapter(ArrayList <frevReviewObject> reviewlist){
        this.reviewlist = reviewlist;
    }

    @NonNull
    @Override
    public frevReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.frev_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);
        frevReviewViewHolder rev = new frevReviewViewHolder(layoutview);
        return rev;
    }

    @Override
    public void onBindViewHolder(@NonNull frevReviewViewHolder holder, int position) {
        holder.mUsername.setText(reviewlist.get(position).getUsername());
        holder.mReview.setText(reviewlist.get(position).getReview());
        holder.mRating.setRating(reviewlist.get(position).getRating());
        Uri uri = reviewlist.get(position).getUri();
        Picasso.get().load(uri).into(holder.mImage);
    }

    @Override
    public int getItemCount() {
       return reviewlist.size();
    }

    public class frevReviewViewHolder extends RecyclerView.ViewHolder{

        public TextView mUsername, mReview;
        public CircleImageView mImage;
        public RatingBar mRating;
        public frevReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.fav_tv_username);
            mReview = itemView.findViewById(R.id.fav_tv_review);
            mImage = itemView.findViewById(R.id.fav_profile_image);
            mRating = itemView.findViewById(R.id.fav_tv_rating);
        }
    }
}
