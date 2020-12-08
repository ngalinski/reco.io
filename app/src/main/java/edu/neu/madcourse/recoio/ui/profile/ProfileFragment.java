package edu.neu.madcourse.recoio.ui.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;

    private ImageButton settingsImageView;
    private TextView userNameTextView;
    private TextView currentUserFollowingCountTV;
    private TextView currentUserFollowersCountTV;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private RecyclerView profileReviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProfileReviewRecyclerViewAdapter adapter;

    private ArrayList<Review> reviews;

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsImageView = requireView().findViewById(R.id.settingsImageView);
        userNameTextView = requireView().findViewById(R.id.currentUserName);
        currentUserFollowingCountTV = requireView().findViewById(R.id.currentUserFollowingCountTextView);
        currentUserFollowersCountTV = requireView().findViewById(R.id.currentUserFollowersCountTextView);

        reviews = new ArrayList<>();


        final DatabaseReference usersRef = databaseReference.child("users");
        final DatabaseReference currUser = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference currUserReviews = currUser.child("reviews");
        final DatabaseReference reviewsRef = databaseReference.child("reviews");

        currUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNameTextView.setText((String) snapshot.child("name").getValue());
                currentUserFollowersCountTV.setText(String.valueOf(snapshot.child("followerCount").getValue()));
                currentUserFollowingCountTV.setText(String.valueOf(snapshot.child("followingCount").getValue()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        currUserReviews.addChildEventListener(new ChildEventListener() {
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

        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu prof_dropdown = new PopupMenu(requireContext(), settingsImageView);
                prof_dropdown.getMenuInflater().inflate(R.menu.profile_dropdown_menu, prof_dropdown.getMenu());

                prof_dropdown.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                // TODO: add profile edit page
                                return true;
                            case R.id.menu_privacy:
                                // TODO: fix this navigation
                               // NavHostFragment.findNavController(ProfileFragment.this)
                                 //       .navigate(R.id.action_navigation_profile_to_privacy);
                                return true;
                            case R.id.menu_logout:
                                if (mAuth.getCurrentUser() != null) {
                                    mAuth.signOut();
                                    NavHostFragment.findNavController(ProfileFragment.this)
                                            .navigate(R.id.action_global_loginFragment);
                                }
                            default:
                                prof_dropdown.show();
                        }
                    return true;}
                });
            }
        });




        adapter.setItemClickListener(new ProfileReviewRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Bundle reviewUID = new Bundle();
                reviewUID.putString("uid", reviews.get(position).getUid());
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_navigation_profile_to_reviewFragment, reviewUID);
            }
        });
    }

    public void createAdapter() {
        profileReviewRecyclerView = requireView().findViewById(R.id.otherUserReviews);
        layoutManager = new LinearLayoutManager(getActivity());
        profileReviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new ProfileReviewRecyclerViewAdapter(reviews);
        profileReviewRecyclerView.setAdapter(adapter);
    }
}