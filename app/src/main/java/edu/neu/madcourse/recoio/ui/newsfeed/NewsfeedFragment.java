package edu.neu.madcourse.recoio.ui.newsfeed;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

public class NewsfeedFragment extends Fragment {

    private NewsfeedViewModel newsfeedViewModel;

    private RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ReviewRecyclerViewAdapter adapter;

    private ArrayList<Review> reviews;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        newsfeedViewModel = new ViewModelProvider(this).get(NewsfeedViewModel.class);
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reviews = new ArrayList<>();
        final DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Review newReview = new Review(
                        (String) snapshot.child("uid").getValue(),
                        (String) snapshot.child("product").getValue(),
                        (String) snapshot.child("rating").getValue(),
                        (String) snapshot.child("review").getValue(),
                        (String) snapshot.child("ownerName").getValue()
                );
                reviews.add(newReview);
                adapter.notifyDataSetChanged();
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
        };
        reviewsRef.addChildEventListener(childEventListener);
        createAdapter();
        adapter.setItemClickListener(new ReviewRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Bundle reviewUID = new Bundle();
                reviewUID.putString("uid", reviews.get(position).getUid());
                NavHostFragment.findNavController(NewsfeedFragment.this)
                        .navigate(R.id.action_navigation_newsfeed_to_reviewFragment, reviewUID);
            }
        });
    }

    public void createAdapter() {
        reviewRecyclerView = requireView().findViewById(R.id.reviewRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        reviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new ReviewRecyclerViewAdapter(reviews);
        reviewRecyclerView.setAdapter(adapter);
    }
}