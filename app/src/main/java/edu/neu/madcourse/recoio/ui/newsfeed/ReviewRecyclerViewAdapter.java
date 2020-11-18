package edu.neu.madcourse.recoio.ui.newsfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position, Context context);
    }

    private final ArrayList<Review> reviews;
    private ItemClickListener itemClickListener;

    public ReviewRecyclerViewAdapter(ArrayList<Review> reviews) {
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
        //TODO: load an image into the cell

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
