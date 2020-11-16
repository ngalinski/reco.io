package edu.neu.madcourse.recoio.ui.addreview;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class AddReviewViewModel extends ViewModel {
    private MutableLiveData<String> thingToReview = new MutableLiveData<>();
    private MutableLiveData<Float> rating = new MutableLiveData<>();
    private MutableLiveData<String> review = new MutableLiveData<>();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reviews = mDatabase.child("reviews");

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
        DatabaseReference newPostRef = reviews.child(String.valueOf(date.getTime()));
        newPostRef.child("product").setValue(getThingToReview().getValue());
        newPostRef.child("reviewText").setValue(getReview().getValue());
        newPostRef.child("rating").setValue(String.valueOf(getRating().getValue()));
        newPostRef.child("owner").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newPostRef.child("likes").setValue(0);
    }
}