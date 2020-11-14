package edu.neu.madcourse.recoio.ui.addreview;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import edu.neu.madcourse.recoio.R;

public class AddReviewFragment extends Fragment {

    private AddReviewViewModel mViewModel;

    private EditText productSearchEditText;
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button postReviewButton;

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
        productSearchEditText = getView().findViewById(R.id.productSearchEditText);
        ratingBar = getView().findViewById(R.id.ratingBar);
        reviewEditText = getView().findViewById(R.id.reviewEditText);
        postReviewButton = getView().findViewById(R.id.postReviewButton);

        postReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReviewPressed();
            }
        });

    }

    public void postReviewPressed() {
        if (!productSearchEditText.getText().toString().equals("")) {
            mViewModel.setThingToReview(productSearchEditText.getText().toString());
            mViewModel.setRating(ratingBar.getRating());
            mViewModel.setReview(!reviewEditText.getText().toString().isEmpty()
                    ? reviewEditText.getText().toString() : "");
            mViewModel.postReview();
        }

    }
}