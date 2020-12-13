package edu.neu.madcourse.recoio.ui.review;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.neu.madcourse.recoio.Comment;
import edu.neu.madcourse.recoio.R;

import static androidx.core.content.ContextCompat.getSystemService;

// TODO: exit out of review once posted
public class ReviewFragment extends Fragment {

    private ReviewViewModel mViewModel;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference reviews = mDatabase.child("reviews");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private StorageReference reviewPicturesReference = storageReference.child("reviewPictures");

    private ValueEventListener reviewEventListener;

    private TextView productTextName;
    private TextView reviewerNameTextView;
    ImageView productImage;
    RatingBar ratingBar;
    TextView reviewTextView;
    EditText commentEditText;
    Button commentButton;
    TextView numLikes;

    String reviewUID;
    String otherUserUID;
    ArrayList<Comment> commentsArrayList;

    RecyclerView commentsRecyclerView;
    RecyclerView.LayoutManager commentsLayoutManager;
    CommentRecyclerViewAdapter commentsAdapter;



    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.review_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productTextName = requireView().findViewById(R.id.productNameTextView);
        reviewerNameTextView = requireView().findViewById(R.id.reviewerNameTextView);
        productImage = requireView().findViewById(R.id.productImage);
        reviewTextView = requireView().findViewById(R.id.reviewTextView);
        ratingBar = requireView().findViewById(R.id.reviewRatingBar);
        commentButton = requireView().findViewById(R.id.commentButton);
        commentEditText = requireView().findViewById(R.id.commentEditText);
        numLikes = requireView().findViewById(R.id.reviewLikeCountTextView);

        commentsArrayList = new ArrayList<>();

        reviewUID = getArguments().getString("uid");



        reviewEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productTextName.setText((String)snapshot.child("product").getValue());
                reviewTextView.setText((String) snapshot.child("reviewText").getValue());
                ratingBar.setRating(Float.parseFloat((String) snapshot.child("rating").getValue()));
                numLikes.setText(String.valueOf(snapshot.child("likeCount").getValue()));
                reviewerNameTextView.setText((String) snapshot.child("ownerName").getValue());
                if ((Boolean) snapshot.child("hasPicture").getValue()) {
                    StorageReference pictureRef = reviewPicturesReference.child(reviewUID);
                    Glide.with(requireView()).load(pictureRef).into(productImage);
                }
                otherUserUID = (String) snapshot.child("owner").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reviews.child(reviewUID).addListenerForSingleValueEvent(reviewEventListener);

        createAdapter();
        reviews.child(reviewUID).child("comments").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Comment comment = new Comment(snapshot);
                        commentsArrayList.add(comment);
                        commentsAdapter.notifyDataSetChanged();
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
                }
        );

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        reviewerNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle otherUserInfoBundle = new Bundle();
                otherUserInfoBundle.putString("otherUserUID", otherUserUID);
                NavHostFragment.findNavController(ReviewFragment.this)
                        .navigate(R.id.action_reviewFragment_to_otherProfileFragment,
                                otherUserInfoBundle);
            }
        });
        commentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reviews.child(reviewUID).removeEventListener(reviewEventListener);
    }

    public void postComment() {
        final Date commentUID = Calendar.getInstance().getTime();
        final FirebaseUser commenter = FirebaseAuth.getInstance().getCurrentUser();
        if (!commentEditText.getText().toString().equals("")) {
            mDatabase.child("users").child(commenter.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Comment newComment = new Comment(
                            (String)snapshot.child("name").getValue(),
                            commentEditText.getText().toString(),
                            commentUID.toString(),
                            commenter.getUid()
                    );
                    DatabaseReference newCommentReference = reviews.child(reviewUID)
                            .child("comments").child(String.valueOf(commentUID.getTime()));
                    newCommentReference.setValue(newComment);
                    commentEditText.setText("");
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public void createAdapter() {
        commentsRecyclerView = requireView().findViewById(R.id.commentsRecyclerView);
        commentsLayoutManager = new LinearLayoutManager(requireActivity());
        commentsRecyclerView.setLayoutManager(commentsLayoutManager);
        commentsAdapter = new CommentRecyclerViewAdapter(commentsArrayList);
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}