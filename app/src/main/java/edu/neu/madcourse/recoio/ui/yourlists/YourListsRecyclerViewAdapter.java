package edu.neu.madcourse.recoio.ui.yourlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.List;
import edu.neu.madcourse.recoio.R;

public class YourListsRecyclerViewAdapter
        extends RecyclerView.Adapter<YourListsRecyclerViewAdapter.ViewHolder> {

    private final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference listsReference = firebaseDatabase.child("lists");
    private final StorageReference pictureStorageRef = FirebaseStorage.getInstance().getReference()
            .child("reviewPictures");

    private ChildEventListener childEventListener;

    public interface ItemClickListener {
        void onItemClick(int position, Context context);
    }

    private YourListsRecyclerViewAdapter.ItemClickListener itemClickListener;


    public void setItemClickListener(YourListsRecyclerViewAdapter.ItemClickListener listener) {
        itemClickListener = listener;
    }


    private final ArrayList<List> lists;

    public YourListsRecyclerViewAdapter(ArrayList<List> lists) {
        this.lists = lists;
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
        holder.categoryTextView.setText(lists.get(position).getListName());
        holder.reviewTwoImgView.setVisibility(View.GONE);
        holder.spaceMiddle.setVisibility(View.GONE);

        final Integer[] reviewCounter = {0};

        DatabaseReference listReviewsReference = listsReference.child(lists.get(position).getListUID()).child("reviews");

        Query reviewsQuery = listReviewsReference.limitToFirst(1);



        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                reviewCounter[0]++;
                String pictureUID = snapshot.getKey();
                assert pictureUID != null;
                StorageReference pictureReference = pictureStorageRef.child(pictureUID);
                if ("1".equals(reviewCounter[0].toString())) {
                    Glide.with(holder.reviewOneImgView).load(pictureReference)
                            .into(holder.reviewOneImgView);
                    //                    case "2":
//                        Glide.with(holder.reviewTwoImgView).load(pictureReference)
//                                .into(holder.reviewTwoImgView);
//                        break;
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
        return lists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        ImageView reviewOneImgView;
        ImageView reviewTwoImgView;
        ImageView reviewThreeImgView;
        ImageView reviewFourImgView;
        Space spaceMiddle;

        public ViewHolder(@NonNull final View itemView,
                          final ItemClickListener listener) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            reviewOneImgView = itemView.findViewById(R.id.reviewOneImageView);
            reviewTwoImgView = itemView.findViewById(R.id.reviewTwoImageView);
//            reviewThreeImgView = itemView.findViewById(R.id.reviewThreeImageView);
//            reviewFourImgView = itemView.findViewById(R.id.reviewFourImageView);

            spaceMiddle = itemView.findViewById(R.id.spaceMiddle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getLayoutPosition(), itemView.getContext());
                }
            });
        }
    }
}
