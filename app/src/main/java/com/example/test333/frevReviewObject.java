package com.example.test333;

import android.net.Uri;

public class frevReviewObject {
    private String username, review;
    Uri uri;
    float rating;

    public frevReviewObject(){}

    public frevReviewObject(String username, String review, Uri uri,float rating){
            this.username=username;
            this.review=review;
            this.uri = uri;
            this.rating = rating;
    }

    public String getUsername(){
        return username;
    }

    public String getReview() {
        return review;
    }
    public Uri getUri() {
        return uri;
    }
    public float getRating() {
        return rating;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setReview(String review) {
        this.review = review;
    }


    public void setUri(Uri uri) {
        this.uri = uri;
    }




}
