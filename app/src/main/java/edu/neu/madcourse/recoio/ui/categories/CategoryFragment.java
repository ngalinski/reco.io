package edu.neu.madcourse.recoio.ui.categories;

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
import android.widget.TextView;

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
import java.util.Comparator;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

public class CategoryFragment extends Fragment {

    public CategoryFragment() {
        // Required empty public constructor
    }

    private String categoryString;
    private RecyclerView categoryRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CategoryRecyclerViewAdapter adapter;

    private TextView categoryTitleTextView;

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String currentUserName;

    private ArrayList<Review> reviews;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryTitleTextView = requireView().findViewById(R.id.categoryTitleTextView);
        categoryTitleTextView.setText(getArguments().getString("category"));

        categoryString = getArguments().getString("category");

       requireActivity().setTitle(categoryString);
        reviews = new ArrayList<>();
        final DatabaseReference reviewsRef = databaseReference.child("reviews");
        final DatabaseReference categoryReference = databaseReference.child("categories").child(categoryString);

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

        categoryReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                currentUserName = (String) snapshot.child("name").getValue();
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
                        System.out.println(newReview);
                        reviews.add(newReview);
                        reviews.sort(new Comparator<Review>() {
                            @Override
                            public int compare(Review o1, Review o2) {
                                return o2.getUid().compareTo(o1.getUid());
                            }
                        });
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
        adapter.setItemClickListener(new CategoryRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Bundle reviewUID = new Bundle();
                reviewUID.putString("uid", reviews.get(position).getUid());
                NavHostFragment.findNavController(CategoryFragment.this)
                        .navigate(R.id.action_categoryFragment_to_reviewFragment, reviewUID);
            }
        });
    }

    public void createAdapter() {
        categoryRecyclerView = requireView().findViewById(R.id.categoryRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        categoryRecyclerView.setLayoutManager(layoutManager);
        adapter = new CategoryRecyclerViewAdapter(reviews, currentUserName);
        categoryRecyclerView.setAdapter(adapter);
    }

}