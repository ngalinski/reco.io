package edu.neu.madcourse.recoio.ui.addreview;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import edu.neu.madcourse.recoio.R;

import static android.app.Activity.RESULT_OK;

// TODO: add a photo
public class AddReviewFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 103;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference reviewImages = storage.getReference().child("reviewPictures");

    private AddReviewViewModel mViewModel;

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
        mViewModel = new ViewModelProvider(this).get(AddReviewViewModel.class);

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

//    private void setupCameraPermission() {
//        int permission = ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.CAMERA);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
//                    Manifest.permission.CAMERA)) {
//                AlertDialog.Builder builder= new AlertDialog.Builder(requireContext());
//                builder.setMessage("Adding a picture will boost your likes!")
//                        .setTitle("Camera Permission");
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        makeCameraPermissionRequest();
//                    }
//                });
//            }
//        }
//    }

//    protected void makeCameraPermissionRequest() {
//        ActivityCompat.requestPermissions(requireActivity(), new String[]{
//                Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_REQUEST_CODE) {
//            if (grantResults.length == 0
//                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Snackbar.make(requireView(), "Unable to access camera - permission required",
//                        Snackbar.LENGTH_SHORT).show();
//            } else {
//                takePicture();
//            }
//        }
//    }



    public void postReviewPressed() {
        if (!productSearchEditText.getText().toString().equals("")) {
            Date date = Calendar.getInstance().getTime();
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
            mViewModel.setUid(String.valueOf(date.getTime()));
            mViewModel.setThingToReview(productSearchEditText.getText().toString());
            mViewModel.setRating(ratingBar.getRating());
            mViewModel.setCategory(categoriesSpinner.getSelectedItem().toString());
            mViewModel.setReview(!reviewEditText.getText().toString().isEmpty()
                    ? reviewEditText.getText().toString() : "");
            mViewModel.setHasPicture(hasPicture);
            mViewModel.postReview();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}