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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;
import edu.neu.madcourse.recoio.ui.categories.search.SearchFragment;
import edu.neu.madcourse.recoio.ui.categories.search.SearchRecyclerViewAdapter;


public class AddListFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AddListRecyclerViewAdapter adapter;

    private ArrayList<Review> reviews;
    private ArrayList<String> listReviewUIDS = new ArrayList<>();
    private HashMap<Integer, Boolean> isClickedHashMap = new HashMap<>();


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference lists = databaseReference.child("lists");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private EditText listEditText;
    private EditText filterText;
    private Button createListButton;
    private Button filterProductsButton;
    private Button clearFilterButton;

    private String currentUserName;

    private ChildEventListener childEventListener;

    final DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");


    public static AddListFragment newInstance() {
        return new AddListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_list, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reviewsRef.removeEventListener(childEventListener);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reviews = new ArrayList<>();

        listEditText = requireView().findViewById(R.id.listNameEditText);
        filterText = requireView().findViewById(R.id.searchList);
        createListButton = requireView().findViewById(R.id.createListButton);
        filterProductsButton = requireView().findViewById(R.id.searchListButton);
        clearFilterButton = requireView().findViewById(R.id.clearButton);

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

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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
                newReview.setClicked(false);
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
                Review clickedReview = reviews.get(position);
                clickedReview.setClicked(!clickedReview.getClicked());
                if (clickedReview.getClicked()) {
                    listReviewUIDS.add(clickedReview.getUid());
                } else {
                    listReviewUIDS.remove(clickedReview.getUid());
                }
                adapter.notifyDataSetChanged();
            }
        });

        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newListClick();
            }
        });

        filterProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!filterText.getText().toString().equals("")){
                    // create list
                    // create new adapter


                    final ArrayList<Review> filteredReviews = new ArrayList<>();

                    for (int i = 0; i < reviews.size(); i++){

                        if (reviews.get(i).getProductTitle().toLowerCase().contains(filterText.getText().toString().toLowerCase()) ){
                            filteredReviews.add(reviews.get(i));

                        }
//                        adapter.notifyDataSetChanged();
                    }
                    final AddListRecyclerViewAdapter filteredAdapter = new AddListRecyclerViewAdapter(filteredReviews, currentUserName);
                    filteredAdapter.setItemClickListener(new AddListRecyclerViewAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(int position, Context context) {
                            Review clickedReview = filteredReviews.get(position);
                            clickedReview.setClicked(!clickedReview.getClicked());
                            if (clickedReview.getClicked()) {
                                listReviewUIDS.add(clickedReview.getUid());
                            } else {
                                listReviewUIDS.remove(clickedReview.getUid());
                            }
                            filteredAdapter.notifyDataSetChanged();
                        }
                    });
                    reviewRecyclerView.setAdapter(filteredAdapter);
                }
            }


        });

        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewRecyclerView.setAdapter(adapter);
            }
        });
    }

    public void newListClick() {
        if (!listEditText.getText().toString().equals("")) {
            final Date date = Calendar.getInstance().getTime();

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                final DatabaseReference user = databaseReference.child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUserName = (String) snapshot.child("name").getValue();
                        DatabaseReference newListRef = lists.child(String.valueOf(date.getTime()));

                        DatabaseReference userLists = user.child("lists");
                        userLists.child(String.valueOf(date.getTime())).setValue(true);

                        newListRef.child("name").setValue(listEditText.getText().toString());

                        for (String reviewUID: listReviewUIDS) {
                            newListRef.child("reviews").child(reviewUID).setValue(true);
                        }
                        NavHostFragment.findNavController(AddListFragment.this)
                                .navigate(R.id.navigation_your_lists);
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
        adapter = new AddListRecyclerViewAdapter(reviews, currentUserName);
        reviewRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}