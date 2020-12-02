package edu.neu.madcourse.recoio.ui.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import edu.neu.madcourse.recoio.ui.newsfeed.NewsfeedFragment;
import edu.neu.madcourse.recoio.ui.newsfeed.ReviewRecyclerViewAdapter;
import edu.neu.madcourse.recoio.ui.signup.SignUpFragment;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;

    private ImageView settingsImageView;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        final TextView profileTextView = requireView().findViewById(R.id.profile_name);

        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                profileTextView.setText(s);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsImageView = requireView().findViewById(R.id.settingsImageView);
        reviews = new ArrayList<>();


        final DatabaseReference usersRef = databaseReference.child("users");
        final DatabaseReference currUser = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference currUserReviews = currUser.child("reviews");
        final DatabaseReference reviewsRef = databaseReference.child("reviews");

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
                                (Boolean) snapshot.child("hasPicture").getValue()
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
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    NavHostFragment.findNavController(ProfileFragment.this)
                            .navigate(R.id.action_global_loginFragment);
                }
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
        profileReviewRecyclerView = requireView().findViewById(R.id.userReviews);
        layoutManager = new LinearLayoutManager(getActivity());
        profileReviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new ProfileReviewRecyclerViewAdapter(reviews);
        profileReviewRecyclerView.setAdapter(adapter);
    }
}