package edu.neu.madcourse.recoio.ui.addreview;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

// TODO: add a search/match for reviews
public class AddReviewViewModel extends ViewModel {
    private MutableLiveData<String> thingToReview = new MutableLiveData<>();
    private MutableLiveData<Float> rating = new MutableLiveData<>();
    private MutableLiveData<String> review = new MutableLiveData<>();
    private MutableLiveData<String> uid = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasPicture = new MutableLiveData<>();

    private MutableLiveData<String> category = new MutableLiveData<>();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reviews = mDatabase.child("reviews");

    public MutableLiveData<String> getThingToReview() {
        return thingToReview;
    }

    public void setThingToReview(String thingToReview) {
        this.thingToReview.setValue(thingToReview);
    }

    public void setUid(String uid) {
        this.uid.setValue(uid);
    }

    public MutableLiveData<String> getUid() {
        return uid;
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

    public MutableLiveData<String> getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category.setValue(category);
    }

    public void setHasPicture(Boolean hasPicture) {
        this.hasPicture.setValue(hasPicture);
    }

    public boolean hasPicture() {
        return this.hasPicture.getValue();
    }

    public void postReview() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference user = mDatabase.child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DatabaseReference newPostRef = reviews.child(getUid().getValue());
                    newPostRef.child("product").setValue(getThingToReview().getValue());
                    newPostRef.child("reviewText").setValue(getReview().getValue());
                    newPostRef.child("rating").setValue(String.valueOf(getRating().getValue()));
                    newPostRef.child("owner").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    newPostRef.child("ownerName").setValue(snapshot.child("name").getValue());
                    newPostRef.child("likes").setValue(0);
                    newPostRef.child("category").setValue(getCategory().getValue());
                    newPostRef.child("uid").setValue(getUid().getValue());
                    newPostRef.child("hasPicture").setValue(hasPicture());
                    if (getCategory().getValue() != null) {
                        DatabaseReference category = mDatabase.child("categories");
                        category.child(getCategory().getValue()).child(getUid().getValue())
                                .child("owner").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}