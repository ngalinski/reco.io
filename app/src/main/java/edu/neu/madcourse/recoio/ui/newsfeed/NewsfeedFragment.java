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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

public class NewsfeedFragment extends Fragment {

    private NewsfeedViewModel newsfeedViewModel;

    private RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ReviewRecyclerViewAdapter adapter;

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ValueEventListener currentUserEventListener;

    private String currentUserName;

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
        final DatabaseReference reviewsRef = databaseReference.child("reviews");
        final DatabaseReference usersRef = databaseReference.child("users");

        // this code generates a token for the logged in user.
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference users = database.getReference("users");
                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(token);
            }
        });

        currentUserEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = (String) snapshot.child("name").getValue();
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
                reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot reviewSnapshot: snapshot.getChildren()) {
                            Review newReview = new Review(
                                    (String) reviewSnapshot.child("uid").getValue(),
                                    (String) reviewSnapshot.child("product").getValue(),
                                    (String) reviewSnapshot.child("rating").getValue(),
                                    (String) reviewSnapshot.child("review").getValue(),
                                    (String) reviewSnapshot.child("ownerName").getValue(),
                                    (Boolean) reviewSnapshot.child("hasPicture").getValue(),
                                    (Long) reviewSnapshot.child("likeCount").getValue(),
                                    (String) reviewSnapshot.child("owner").getValue()
                            );
                            reviews.add(newReview);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(currentUserEventListener);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeEventListener(currentUserEventListener);
    }

    public void createAdapter() {
        reviewRecyclerView = requireView().findViewById(R.id.reviewRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        reviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new ReviewRecyclerViewAdapter(reviews, currentUserName);
        reviewRecyclerView.setAdapter(adapter);
    }
}