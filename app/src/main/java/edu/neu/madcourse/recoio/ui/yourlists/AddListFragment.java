package edu.neu.madcourse.recoio.ui.yourlists;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;
import edu.neu.madcourse.recoio.ui.newsfeed.ReviewRecyclerViewAdapter;


public class AddListFragment extends Fragment {

    private RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AddListRecyclerViewAdapter adapter;

    private ArrayList<Review> reviews;
    private ArrayList<String> reviewUIDS = new ArrayList<>();
    private HashMap<Integer, Boolean> isClickedHashMap = new HashMap<>();

    public AddListFragment() {
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
        return inflater.inflate(R.layout.fragment_add_list, container, false);
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
                        (String) snapshot.child("ownerName").getValue(),
                        (Boolean) snapshot.child("hasPicture").getValue()
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

//        adapter.setItemClickListener(new AddListRecyclerViewAdapter.ItemClickListener() {
//            @Override
//            public void onItemClick(int position, Context context) {
//                if (isClickedHashMap.containsKey(position) && isClickedHashMap.get(position).)
//                reviewUIDS.add(reviews.get(position).getUid());
//                isClickedHashMap.put(position, true);
//            }
//        });
    }

    public void createAdapter() {
        reviewRecyclerView = requireView().findViewById(R.id.addListRecyclerView);
        layoutManager = new LinearLayoutManager(requireActivity());
        reviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new AddListRecyclerViewAdapter(reviews);
        reviewRecyclerView.setAdapter(adapter);
    }
}