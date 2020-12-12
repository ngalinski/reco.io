package edu.neu.madcourse.recoio.ui.profile;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

import edu.neu.madcourse.recoio.ui.addreview.AddReviewFragment;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 103;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference profilePictures = storage.getReference().child("profilePictures");

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference usersRef = databaseReference.child("users");
    final DatabaseReference currUser = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    final DatabaseReference currUserReviews = currUser.child("reviews");
    final DatabaseReference reviewsRef = databaseReference.child("reviews");

    private ImageButton settingsImageView;
    private ImageView profilePictureImageView;
    private TextView userNameTextView;
    private TextView currentUserFollowingCountTV;
    private TextView currentUserFollowersCountTV;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private RecyclerView profileReviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProfileReviewRecyclerViewAdapter adapter;

    private Bitmap newProfilePicBitmap;

    private String currentUserName;

    private ArrayList<Review> reviews;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsImageView = requireView().findViewById(R.id.settingsImageView);
        userNameTextView = requireView().findViewById(R.id.currentUserName);
        currentUserFollowingCountTV = requireView().findViewById(R.id.currentUserFollowingCountTextView);
        currentUserFollowersCountTV = requireView().findViewById(R.id.currentUserFollowersCountTextView);
        profilePictureImageView = requireView().findViewById(R.id.currentUserProfilePicImageView);

        reviews = new ArrayList<>();


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

        currUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = (String) snapshot.child("name").getValue();
                userNameTextView.setText((String) snapshot.child("name").getValue());
                currentUserFollowersCountTV.setText(String.valueOf(snapshot.child("followerCount").getValue()));
                currentUserFollowingCountTV.setText(String.valueOf(snapshot.child("followingCount").getValue()));
                if ((boolean) snapshot.child("hasProfilePic").getValue()) {
                    StorageReference userProfilePic = profilePictures
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Glide.with(requireView()).load(userProfilePic).into(profilePictureImageView);
                } else {
                    profilePictureImageView.setImageResource(R.drawable.ic_profile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                                (Boolean) snapshot.child("hasPicture").getValue(),
                                (Long) snapshot.child("likeCount").getValue(),
                                (String) snapshot.child("owner").getValue()
                        );
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

        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_profile, true)
                            .build();
                    NavHostFragment.findNavController(ProfileFragment.this)
                            .navigate(R.id.action_global_loginFragment, new Bundle(), navOptions);
                }
            }
        });

        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
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
        profileReviewRecyclerView = requireView().findViewById(R.id.otherUserReviews);
        layoutManager = new LinearLayoutManager(getActivity());
        profileReviewRecyclerView.setLayoutManager(layoutManager);
        adapter = new ProfileReviewRecyclerViewAdapter(reviews, currentUserName);
        profileReviewRecyclerView.setAdapter(adapter);
    }

    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Unable to take a picture!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePictureImageView.setImageBitmap(imageBitmap);
            newProfilePicBitmap = imageBitmap;
            if (newProfilePicBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newProfilePicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] pictureData = baos.toByteArray();

                final StorageReference newReviewImage = profilePictures.child(
                        FirebaseAuth.getInstance().getCurrentUser().getUid());

                UploadTask uploadTask = newReviewImage.putBytes(pictureData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "Can't upload photo", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressBar.setVisibility(View.INVISIBLE);
                        currUser.child("hasProfilePic").setValue(true);
                    }
                });
            }
        }
    }
}