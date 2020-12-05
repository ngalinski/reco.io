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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final boolean[] isLiked = new boolean[1];
        final Review review = reviews.get(position);

        final DatabaseReference likesReference = databaseReference.child("reviews")
                .child(review.getUid()).child("likes");
        final DatabaseReference reviewReference = databaseReference.child("reviews")
                .child(review.getUid());


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

        reviewReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue() == null ||
                        !((boolean) snapshot.child("likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue())) {
                    likesReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(false);
                    isLiked[0] = false;
                    holder.likeCountTextView.setText(String.valueOf(review.getLikeCount()));
                    holder.likeButtonImageView.setImageResource(R.drawable.ic_not_liked);
                } else {
                    holder.likeButtonImageView.setImageResource(R.drawable.ic_like);
                    holder.likeCountTextView.setText(String.valueOf(review.getLikeCount()));
                    isLiked[0] = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.likeButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isLiked[0] = !isLiked[0];
                if (isLiked[0]) {
                    likesReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(true);
                    review.setLikeCount(review.getLikeCount() + 1);
                    reviewReference.child("likeCount").setValue(review.getLikeCount());
                    holder.likeCountTextView.setText(String.valueOf(review.getLikeCount()));
                    holder.likeButtonImageView.setImageResource(R.drawable.ic_like);

                } else {
                    likesReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(false);
                    review.setLikeCount(review.getLikeCount() - 1);
                    reviewReference.child("likeCount").setValue(review.getLikeCount());
                    holder.likeCountTextView.setText(String.valueOf(review.getLikeCount()));
                    holder.likeButtonImageView.setImageResource(R.drawable.ic_not_liked);
                }
            }
        });


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
        ImageView likeButtonImageView;
        TextView likeCountTextView;

        public ViewHolder(@NonNull final View itemView, final ItemClickListener listener) {
            super(itemView);
            productTextView = itemView.findViewById(R.id.productTitleTextView);
            productRatingBar = itemView.findViewById(R.id.productRatingBar);
            productImageView = itemView.findViewById(R.id.productImageView);
            reviewTextView = itemView.findViewById(R.id.productReviewTextView);
            reviewerTextView = itemView.findViewById(R.id.reviewerTextView);
            likeButtonImageView = itemView.findViewById(R.id.likeImageView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getLayoutPosition(), itemView.getContext());
                }
            });
        }
    }
}
