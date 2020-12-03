package edu.neu.madcourse.recoio.ui.review;

import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Likes {

    public void displayNumberOfLikes(String postId, String currentUserId){
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child('Post').child(postId);
        likesRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long numOfLikes = 0;
                    if(dataSnapshot.hasChild("likes")){
                        numOfLikes = dataSnapshot.child("likes").getValue(Long.class);
                    }

                    // check if liked already or not
                    btnLike.setSelected(dataSnapshot.hasChild(userId));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onLikeClicked(View v, String postId, String userId){
        DatabaseReference likes = FirebaseDatabase.getInstance().getReference().child('Post').child(postId).child("likes");
        likes.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numLikes = 0;
                if(dataSnapshot.exists()){
                    numLikes = dataSnapshot.getValue(Long.class);
                }
                boolean isLiked = btnLike.isSelected();
                if(isLiked){
                    // unlike
                    likes.set(numLikes-1);
                }else {
                    // like
                    likes.set(numLikes+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
