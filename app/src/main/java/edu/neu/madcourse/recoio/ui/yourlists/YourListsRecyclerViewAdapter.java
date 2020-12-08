package edu.neu.madcourse.recoio.ui.yourlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.neu.madcourse.recoio.R;

public class YourListsRecyclerViewAdapter
        extends RecyclerView.Adapter<YourListsRecyclerViewAdapter.ViewHolder> {

    private final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference listsReference = firebaseDatabase.child("lists");
    private final StorageReference pictureStorageRef = FirebaseStorage.getInstance().getReference()
            .child("reviewPictures");
    private final FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();

    public interface ItemClickListener {
        void onItemClick(int position, Context context);
    }

    private YourListsRecyclerViewAdapter.ItemClickListener itemClickListener;


    public void setItemClickListener(YourListsRecyclerViewAdapter.ItemClickListener listener) {
        itemClickListener = listener;
    }


    private final String[] categories;

    public YourListsRecyclerViewAdapter(String[] categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View category = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.category_cell, parent, false
        );
        return new ViewHolder(category, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String categoryString = categories[position];
        holder.categoryTextView.setText(categories[position]);
        final Integer[] reviewCounter = {0};

        Query reviewsQuery = listsReference.child(categoryString).limitToFirst(4);

        reviewsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewCounter[0]++;
                String pictureUID = snapshot.getKey();
                StorageReference pictureReference = pictureStorageRef.child(pictureUID);
                switch (reviewCounter[0].toString()) {
                    case "1":
                        Glide.with(holder.reviewOneImgView).load(pictureReference)
                                .into(holder.reviewOneImgView);
                        break;
                    case "2":
                        Glide.with(holder.reviewTwoImgView).load(pictureReference)
                                .into(holder.reviewTwoImgView);
                        break;
                    case "3":
                        Glide.with(holder.reviewThreeImgView).load(pictureReference)
                                .into(holder.reviewThreeImgView);
                        break;
                    case "4":
                        Glide.with(holder.reviewFourImgView).load(pictureReference)
                                .into(holder.reviewFourImgView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        ImageView reviewOneImgView;
        ImageView reviewTwoImgView;
        ImageView reviewThreeImgView;
        ImageView reviewFourImgView;

        public ViewHolder(@NonNull final View itemView,
                          final ItemClickListener listener) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            reviewOneImgView = itemView.findViewById(R.id.reviewOneImageView);
            reviewTwoImgView = itemView.findViewById(R.id.reviewTwoImageView);
//            reviewThreeImgView = itemView.findViewById(R.id.reviewThreeImageView);
//            reviewFourImgView = itemView.findViewById(R.id.reviewFourImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getLayoutPosition(), itemView.getContext());
                }
            });
        }
    }
}
