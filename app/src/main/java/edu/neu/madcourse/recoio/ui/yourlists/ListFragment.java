package edu.neu.madcourse.recoio.ui.yourlists;

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

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;
import edu.neu.madcourse.recoio.ui.categories.CategoryFragment;
import edu.neu.madcourse.recoio.ui.categories.CategoryRecyclerViewAdapter;

public class ListFragment extends Fragment {


    public ListFragment() {
        // Required empty public constructor
    }

    private String listName;
    private String listUID;
    private RecyclerView listRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ListRecyclerViewAdapter adapter;

    private TextView listTitleTextView;

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ArrayList<Review> reviews;

    private String currentUserName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            listName = getArguments().getString("listName");
            listUID = getArguments().getString("listUID");
        }

        listTitleTextView = requireView().findViewById(R.id.listTitleTextView);

        listTitleTextView.setText(listName);

        reviews = new ArrayList<>();
        final DatabaseReference reviewsRef = databaseReference.child("reviews");
        final DatabaseReference listReference = databaseReference.child("lists").child(listUID).child("reviews");

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

        listReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                reviewsRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUserName = (String) snapshot.child("name").getValue();
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
        adapter.setItemClickListener(new ListRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Bundle reviewUID = new Bundle();
                reviewUID.putString("uid", reviews.get(position).getUid());
                NavHostFragment.findNavController(ListFragment.this)
                        .navigate(R.id.action_listFragment_to_reviewFragment, reviewUID);
            }
        });
    }

    public void createAdapter() {
        listRecyclerView = requireView().findViewById(R.id.listRecyclerView);
        layoutManager = new LinearLayoutManager(requireActivity());
        listRecyclerView.setLayoutManager(layoutManager);
        adapter = new ListRecyclerViewAdapter(reviews, currentUserName);
        listRecyclerView.setAdapter(adapter);
    }
}