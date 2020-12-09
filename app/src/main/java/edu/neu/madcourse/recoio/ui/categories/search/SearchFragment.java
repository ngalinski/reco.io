package edu.neu.madcourse.recoio.ui.categories.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import edu.neu.madcourse.recoio.ui.newsfeed.NewsfeedViewModel;
import edu.neu.madcourse.recoio.ui.newsfeed.ReviewRecyclerViewAdapter;
import edu.neu.madcourse.recoio.ui.profile.ProfileFragment;
import edu.neu.madcourse.recoio.ui.profile.ProfileReviewRecyclerViewAdapter;

public class SearchFragment extends Fragment {

//    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ArrayList<Review> reviews;

    private SearchViewModel searchViewModel;


    private RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SearchRecyclerViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reviews = new ArrayList<>();
        final String searchText = getArguments().getString("searchText");
        final DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");

        // Based on searchText, only show results in reviewsRef that match the text

//        currUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                userNameTextView.setText((String) snapshot.child("name").getValue());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.child("product").getValue().toString().toLowerCase().contains(searchText.toLowerCase()) ||
                        snapshot.child("ownerName").getValue().toString().toLowerCase().contains(searchText.toLowerCase()) ||
                        snapshot.child("category").getValue().toString().toLowerCase().contains(searchText.toLowerCase())){

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
        adapter.setItemClickListener(new SearchRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Bundle reviewUID = new Bundle();
                reviewUID.putString("uid", reviews.get(position).getUid());
                NavHostFragment.findNavController(SearchFragment.this)
                        .navigate(R.id.action_searchFragment_to_reviewFragment, reviewUID);
            }
        });
    }

//        reviewsRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                reviewsRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Review newReview = new Review(
//                                (String) snapshot.child("uid").getValue(),
//                                (String) snapshot.child("product").getValue(),
//                                (String) snapshot.child("rating").getValue(),
//                                (String) snapshot.child("review").getValue(),
//                                (String) snapshot.child("ownerName").getValue(),
//                                (Boolean) snapshot.child("hasPicture").getValue(),
//                                (Long) snapshot.child("likeCount").getValue()
//                        );
//                        reviews.add(newReview);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        createAdapter();
//
//        settingsImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mAuth.getCurrentUser() != null) {
//                    mAuth.signOut();
//                    NavHostFragment.findNavController(ProfileFragment.this)
//                            .navigate(R.id.action_global_loginFragment);
//                }
//            }
//        });

    public void createAdapter() {
        reviewRecyclerView = requireView().findViewById(R.id.searchRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        reviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new SearchRecyclerViewAdapter(reviews);
        reviewRecyclerView.setAdapter(adapter);
    }

}