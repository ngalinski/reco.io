package edu.neu.madcourse.recoio.ui.addreview;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import edu.neu.madcourse.recoio.R;

import static android.app.Activity.RESULT_OK;

public class AddReviewFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 103;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference reviews = databaseReference.child("reviews");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference reviewImages = storage.getReference().child("reviewPictures");


    private EditText productSearchEditText;
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button postReviewButton;
    private Spinner categoriesSpinner;
    private Button pictureButton;
    private ImageView newProductImageView;

    private Bitmap newProductBitMap;


    public static AddReviewFragment newInstance() {
        return new AddReviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_review, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productSearchEditText = requireView().findViewById(R.id.productSearchEditText);
        ratingBar = requireView().findViewById(R.id.ratingBar);
        reviewEditText = requireView().findViewById(R.id.reviewEditText);
        postReviewButton = requireView().findViewById(R.id.postReviewButton);
        categoriesSpinner = requireView().findViewById(R.id.categorySpinner);
        pictureButton = requireView().findViewById(R.id.pictureButton);
        newProductImageView = requireView().findViewById(R.id.newProductImageView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.requireContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(adapter);
        categoriesSpinner.setPrompt("Category");
        System.out.println(categoriesSpinner.getSelectedItem().toString());
        postReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReviewPressed();
            }
        });

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureButtonPressed();
            }
        });
    }

    private void pictureButtonPressed() {
//        setupCameraPermission();
        takePicture();
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
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            newProductImageView.setImageBitmap(imageBitmap);
            newProductBitMap = imageBitmap;
        }
    }




    public void postReviewPressed() {
        if (!productSearchEditText.getText().toString().equals("")) {
            final Date date = Calendar.getInstance().getTime();
            boolean hasPicture = false;
            // this code is based off of the google firebase docs code
            if (newProductBitMap != null) {
                hasPicture = true;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newProductBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                final StorageReference newReviewImage = reviewImages.child(String.valueOf(date.getTime()));

                UploadTask uploadTask = newReviewImage.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "Can't upload photo", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });
            }

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                final DatabaseReference user = databaseReference.child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                final boolean finalHasPicture = hasPicture;
                user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DatabaseReference newPostRef = reviews.child(String.valueOf(date.getTime()));
                        newPostRef.child("product").setValue(productSearchEditText.getText().toString());
                        newPostRef.child("reviewText").setValue(!reviewEditText.getText().toString().isEmpty()
                    ? reviewEditText.getText().toString() : "");
                        newPostRef.child("rating").setValue(String.valueOf(ratingBar.getRating()));
                        newPostRef.child("owner").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        newPostRef.child("ownerName").setValue(snapshot.child("name").getValue());
                        newPostRef.child("likeCount").setValue(0);
                        newPostRef.child("category").setValue(categoriesSpinner.getSelectedItem().toString());
                        newPostRef.child("uid").setValue(String.valueOf(date.getTime()));
                        newPostRef.child("hasPicture").setValue(finalHasPicture);

                        DatabaseReference category = databaseReference.child("categories");
                        category.child(categoriesSpinner.getSelectedItem().toString())
                                .child(String.valueOf(date.getTime()))
                                .child("owner").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        DatabaseReference userReviews = user.child("reviews");
                        userReviews.child(String.valueOf(date.getTime())).setValue(true);

                        NavHostFragment.findNavController(AddReviewFragment.this)
                                .navigate(R.id.navigation_newsfeed);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}