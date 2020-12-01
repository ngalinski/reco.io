package edu.neu.madcourse.recoio.ui.categories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position, Context context);
    }

    private final ArrayList<Review> reviews;
    private ItemClickListener itemClickListener;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public CategoryRecyclerViewAdapter(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public void setItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View newReview = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.review_cell, parent, false
        );
        return new ViewHolder(newReview, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.productTextView.setText(review.getProductTitle());
        holder.productRatingBar.setRating((Float.parseFloat(review.getRating())));
        holder.reviewTextView.setText(review.getReviewText());
        holder.reviewerTextView.setText(String.format(Locale.getDefault(),
                "Reviewed by: %s", review.getReviewer()));
        if (review.hasPicture()) {
            StorageReference reviewImage = storageReference.child("reviewPictures")
                    .child(review.getUid());
            Glide.with(holder.itemView)
                    .load(reviewImage).into(holder.productImageView);
        }


    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTextView;
        RatingBar productRatingBar;
        ImageView productImageView;
        TextView reviewTextView;
        TextView reviewerTextView;

        public ViewHolder(@NonNull final View itemView, final ItemClickListener listener) {
            super(itemView);
            productTextView = itemView.findViewById(R.id.productTitleTextView);
            productRatingBar = itemView.findViewById(R.id.productRatingBar);
            productImageView = itemView.findViewById(R.id.productImageView);
            reviewTextView = itemView.findViewById(R.id.productReviewTextView);
            reviewerTextView = itemView.findViewById(R.id.reviewerTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getLayoutPosition(), itemView.getContext());
                }
            });
        }
    }
}
