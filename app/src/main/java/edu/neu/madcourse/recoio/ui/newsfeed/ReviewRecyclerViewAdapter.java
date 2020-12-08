package edu.neu.madcourse.recoio.ui.newsfeed;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.Review;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position, Context context);
    }

    private final ArrayList<Review> reviews;
    private String currentUserName;
    private ItemClickListener itemClickListener;
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference usersReference = databaseReference.child("users");


    public ReviewRecyclerViewAdapter(ArrayList<Review> reviews, String currentUserName) {
        this.reviews = reviews;
        this.currentUserName = currentUserName;
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

        if (review.getUid() == null) {
            return;
        }

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

        reviewReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendNotification(position);
                        }
                    }).start();


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

    public void sendNotification(final int position) {
        final JSONObject payLoad = new JSONObject();
        final JSONObject notification = new JSONObject();

        usersReference.child(reviews.get(position).getReviewerUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            notification.put("title", "Your review was helpful!");
                            notification.put("body", String.format(
                                    "%s liked your post!", currentUserName));
                            notification.put("sound", "default");
                            notification.put("badge", "1");

                            payLoad.put("to", (String) snapshot.child("token").getValue());
                            payLoad.put("priority", "high");
                            payLoad.put("notification", notification);

                            URL url = new URL("https://fcm.googleapis.com/fcm/send");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            String SERVER_KEY = "key=AAAA3ghhIBE:APA91bG8jTRhUNdgezCDcMtGLLNRHO-pYWpLHfnbNlWMKacu6trrm-hKeNi-YIiIHI5i7sUun71e9R6ncbpGvrr1ubF7iayfZgAebvp80RzPNcRpKtCUGBUqr5WbcrVFZDI_z3H40IKK\t\n";
                            conn.setRequestProperty("Authorization", SERVER_KEY);
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setDoOutput(true);

                            OutputStream outputStream = conn.getOutputStream();
                            outputStream.write(payLoad.toString().getBytes());
                            outputStream.close();

                            InputStream inputStream = conn.getInputStream();
                            System.out.println(inputStream.toString());
                            System.out.println("HELLOOOOO WOOOORLDDD");
                        } catch (Exception error) {
                            error.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
