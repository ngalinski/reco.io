package edu.neu.madcourse.recoio.ui.otherprofile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;
import edu.neu.madcourse.recoio.ui.profile.ProfileFragment;
import edu.neu.madcourse.recoio.ui.profile.ProfileReviewRecyclerViewAdapter;


public class OtherProfileFragment extends Fragment {

    private RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private OtherProfileReviewRecyclerViewAdapter adapter;

    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference profilePictures = storage.getReference().child("profilePictures");

    private TextView userNameTextView;
    private Button followButton;
    private Long otherUserFollowerCount;
    private Long currentUserFollowingCount;

    private TextView otherUserFollowingCountTextView;
    private TextView otherUserFollowerCountTextView;
    private ImageView otherUserProfilePictureImageView;

    String otherUserUID;
    String currentUserUID;
    final boolean[] isFollowing = new boolean[1];

    private ArrayList<Review> reviews;

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public OtherProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        followButton = requireView().findViewById(R.id.followButton);
        userNameTextView = requireView().findViewById(R.id.otherUserName);
        otherUserFollowingCountTextView = requireView().findViewById(R.id.otherUserFollowingCountTextView);
        otherUserFollowerCountTextView = requireView().findViewById(R.id.otherUserFollowersCountTextView);
        otherUserProfilePictureImageView = requireView().findViewById(R.id.otherUserPFImageView);



        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        assert getArguments() != null;
        otherUserUID = getArguments().getString("otherUserUID");

        reviews = new ArrayList<>();


        final DatabaseReference usersRef = databaseReference.child("users");
        assert getArguments() != null;
        final DatabaseReference otherUserRef = usersRef.child(getArguments().getString("otherUserUID"));
        final DatabaseReference otherUserReviews = otherUserRef.child("reviews");
        final DatabaseReference currentUserRef = usersRef.child(currentUserUID);
        final DatabaseReference reviewsRef = databaseReference.child("reviews");

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserFollowingCount = (Long) snapshot.child("followingCount").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        otherUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNameTextView.setText((String) snapshot.child("name").getValue());
                otherUserFollowerCountTextView.setText(String.valueOf(snapshot.child("followerCount").getValue()));
                otherUserFollowingCountTextView.setText(String.valueOf(snapshot.child("followingCount").getValue()));
                otherUserFollowerCount = (Long) snapshot.child("followerCount").getValue();
                if ( snapshot.child("hasProfilePic").getValue() != null
                        || (boolean) snapshot.child("hasProfilePic").getValue()) {
                    StorageReference userProfilePic = profilePictures
                            .child(otherUserUID);
                    Glide.with(requireView()).load(userProfilePic).into(otherUserProfilePictureImageView);
                }
                // this code will set up the following feature
                if (snapshot.child("followers").child(currentUserUID).getValue() == null
                        || !((boolean) snapshot.child("followers").child(currentUserUID).getValue())) {
                    otherUserRef.child("followers").child(currentUserUID).setValue(false);
                    isFollowing[0] = false;
                    followButton.setText("Follow");
                    followButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    isFollowing[0] = true;
                    followButton.setText("Un-follow");
                    followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        otherUserReviews.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                reviewsRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Review newReview = new Review(
                                (String) snapshot.child("uid").getValue(),
                                (String) snapshot.child("product").getValue(),
                                (String) snapshot.child("rating").getValue(),
                                (String) snapshot.child("review").getValue(),
                                (String) snapshot.child("ownerName").getValue(),
                                (Boolean) snapshot.child("hasPicture").getValue(),
                                (Long) snapshot.child("likeCount").getValue(),
                                (String) snapshot.child("owner").getValue()
                        );
                        reviews.add(newReview);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        createAdapter();
        adapter.setItemClickListener(new OtherProfileReviewRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Bundle reviewUID = new Bundle();
                reviewUID.putString("uid", reviews.get(position).getUid());
                NavHostFragment.findNavController(OtherProfileFragment.this)
                        .navigate(R.id.action_navigation_profile_to_reviewFragment, reviewUID);
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFollowing[0] = !isFollowing[0];
                if (isFollowing[0]) {
                    otherUserRef.child("followers").child(currentUserUID).setValue(true);
                    otherUserFollowerCount = otherUserFollowerCount + 1;
                    otherUserRef.child("followerCount").setValue(otherUserFollowerCount);
                    currentUserFollowingCount += 1;
                    currentUserRef.child("followingCount").setValue(currentUserFollowingCount);
                    currentUserRef.child("following").child(otherUserUID).setValue(true);
                    otherUserFollowerCountTextView.setText(String.valueOf(otherUserFollowerCount));
                    followButton.setText("Un-follow");
                    followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    otherUserRef.child("followers").child(currentUserUID).setValue(false);
                    otherUserFollowerCount = otherUserFollowerCount - 1;
                    otherUserRef.child("followerCount").setValue(otherUserFollowerCount);
                    currentUserFollowingCount -= 1;
                    currentUserRef.child("followingCount").setValue(currentUserFollowingCount);
                    currentUserRef.child("following").child(otherUserUID).setValue(false);
                    otherUserFollowerCountTextView.setText(String.valueOf(otherUserFollowerCount));
                    followButton.setText("Follow");
                    followButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                }
            }
        });

    }

    public void createAdapter() {
        reviewRecyclerView = requireView().findViewById(R.id.otherUserReviews);
        layoutManager = new LinearLayoutManager(getActivity());
        reviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new OtherProfileReviewRecyclerViewAdapter(reviews);
        reviewRecyclerView.setAdapter(adapter);
    }
}