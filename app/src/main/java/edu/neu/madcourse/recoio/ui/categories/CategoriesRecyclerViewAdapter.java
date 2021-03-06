package edu.neu.madcourse.recoio.ui.categories;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.neu.madcourse.recoio.R;

public class CategoriesRecyclerViewAdapter
        extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder> {

    private final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference categoriesReference = firebaseDatabase.child("categories");
    private final StorageReference pictureStorageRef = FirebaseStorage.getInstance().getReference()
            .child("reviewPictures");

    public interface ItemClickListener {
        void onItemClick(int position, Context context);
    }

    private CategoriesRecyclerViewAdapter.ItemClickListener itemClickListener;


    public void setItemClickListener(CategoriesRecyclerViewAdapter.ItemClickListener listener) {
        itemClickListener = listener;
    }


    private final String[] categories;

    public CategoriesRecyclerViewAdapter(String[] categories) {
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

        Query reviewsQuery = categoriesReference.child(categoryString).limitToFirst(2);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    reviewCounter[0]++;
                    String pictureUID = snapshot.getKey();
                assert pictureUID != null;
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
//                        case "3":
//                            Glide.with(holder.reviewThreeImgView).load(pictureReference)
//                                    .into(holder.reviewThreeImgView);
//                            break;
//                        case "4":
//                            Glide.with(holder.reviewFourImgView).load(pictureReference)
//                                    .into(holder.reviewFourImgView);
                    }

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
        };
        reviewsQuery.addChildEventListener(childEventListener);
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
