package edu.neu.madcourse.recoio.ui.yourlists;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;
import edu.neu.madcourse.recoio.ui.addreview.AddReviewFragment;
import edu.neu.madcourse.recoio.ui.newsfeed.ReviewRecyclerViewAdapter;


public class AddListFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AddListRecyclerViewAdapter adapter;

    private ArrayList<Review> reviews;
    private ArrayList<String> reviewUIDS = new ArrayList<>();
    private HashMap<Integer, Boolean> isClickedHashMap = new HashMap<>();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference lists = databaseReference.child("lists");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private EditText listEditText;

    public static AddListFragment newInstance() {
        return new AddListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reviews = new ArrayList<>();
        final DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");
        listEditText = requireView().findViewById(R.id.listNameEditText);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Review newReview = new Review(
                        (String) snapshot.child("uid").getValue(),
                        (String) snapshot.child("product").getValue(),
                        (String) snapshot.child("rating").getValue(),
                        (String) snapshot.child("review").getValue(),
                        (String) snapshot.child("ownerName").getValue(),
                        (Boolean) snapshot.child("hasPicture").getValue(),
                        (Long) snapshot.child("likeCount").getValue()
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

        adapter.setItemClickListener(new AddListRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                if (isClickedHashMap.containsKey(position) && isClickedHashMap.get(position));
                reviewUIDS.add(reviews.get(position).getUid());
                isClickedHashMap.put(position, true);
            }
        });
    }

    public void newListClick() {
        if (!listEditText.getText().toString().equals("")) {
            final Date date = Calendar.getInstance().getTime();

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                final DatabaseReference user = databaseReference.child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DatabaseReference newListRef = lists.child(String.valueOf(date.getTime()));

                        DatabaseReference userLists = user.child("lists");
                        userLists.child(String.valueOf(date.getTime())).setValue(true);

                        NavHostFragment.findNavController(AddListFragment.this)
                                .navigate(R.id.listFragment);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    public void createAdapter() {
        reviewRecyclerView = requireView().findViewById(R.id.addListRecyclerView);
        layoutManager = new LinearLayoutManager(requireActivity());
        reviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new AddListRecyclerViewAdapter(reviews);
        reviewRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}