package edu.neu.madcourse.recoio.ui.yourlists;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position, Context context);
    }

    private final ArrayList<Review> lists;
    private ListRecyclerViewAdapter.ItemClickListener itemClickListener;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference listsRef = firebaseDatabase.child("lists");

    public ListRecyclerViewAdapter(ArrayList<Review> lists) {
        this.lists = lists;
    }

    public void setItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View list = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.category_cell, parent, false
        );
        return new ViewHolder(list, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = lists.get(position);
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
        return lists.size();
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
