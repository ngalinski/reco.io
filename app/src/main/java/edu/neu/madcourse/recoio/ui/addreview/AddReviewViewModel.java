package edu.neu.madcourse.recoio.ui.addreview;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class AddReviewViewModel extends ViewModel {
    private MutableLiveData<String> thingToReview = new MutableLiveData<>();
    private MutableLiveData<Float> rating = new MutableLiveData<>();
    private MutableLiveData<String> review = new MutableLiveData<>();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference posts = mDatabase.child("posts");

    public MutableLiveData<String> getThingToReview() {
        return thingToReview;
    }

    public void setThingToReview(String thingToReview) {
        this.thingToReview.setValue(thingToReview);
    }

    public MutableLiveData<Float> getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating.setValue(rating);
    }

    public MutableLiveData<String> getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review.setValue(review);
    }

    public void postReview() {
        // TODO - Add the logged in user to the new post database entry
        Date date = Calendar.getInstance().getTime();
        DatabaseReference newPostRef = posts.child(String.valueOf(date.getTime()));
        newPostRef.child("reviewText").setValue(getReview());
        newPostRef.child("rating").setValue(getRating());
        newPostRef.child("owner").setValue("TODO: currentUser");
        newPostRef.child("likes").setValue(0);
    }
}