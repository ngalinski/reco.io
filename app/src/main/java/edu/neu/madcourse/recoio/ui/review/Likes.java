package edu.neu.madcourse.recoio.ui.review;

import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.recoio.R;

// TODO: finish implementing this class
public class Likes {

    private ImageView btnLike;

    public void displayNumberOfLikes(String postId, String currentUserId){
        DatabaseReference likes = FirebaseDatabase.getInstance().getReference().child('Post').child(postId);
        final FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        btnLike = findViewById(R.id.likeImageView);
        likes.addValueEventListener(new ValueEventListener(){
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
        final DatabaseReference likes = FirebaseDatabase.getInstance().getReference().child('Post').child(postId).child("likes");
        final FirebaseUser like_user = FirebaseAuth.getInstance().getCurrentUser();
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
                    likes.setValue(numLikes-1);
                }else {
                    // like
                    likes.setValue(numLikes+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
