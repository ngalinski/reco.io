package edu.neu.madcourse.recoio.ui.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.recoio.R;

public class CategoriesRecyclerViewAdapter
        extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder> {

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
        return new ViewHolder(category);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryTextView.setText(categories[position]);
        holder.reviewOneImgView.setImageResource(R.drawable.ic_comments);
        holder.reviewTwoImgView.setImageResource(R.drawable.ic_comments);
        holder.reviewThreeImgView.setImageResource(R.drawable.ic_comments);
        holder.reviewFourImgView.setImageResource(R.drawable.ic_comments);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            reviewOneImgView = itemView.findViewById(R.id.reviewOneImageView);
            reviewTwoImgView = itemView.findViewById(R.id.reviewTwoImageView);
            reviewThreeImgView = itemView.findViewById(R.id.reviewThreeImageView);
            reviewFourImgView = itemView.findViewById(R.id.reviewFourImageView);
        }
    }
}
