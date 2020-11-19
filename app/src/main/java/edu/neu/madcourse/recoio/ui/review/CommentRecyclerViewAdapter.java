package edu.neu.madcourse.recoio.ui.review;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.Comment;
import edu.neu.madcourse.recoio.R;

public class CommentRecyclerViewAdapter extends
        RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder>{

    private ArrayList<Comment> comments;

    public CommentRecyclerViewAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setComments(ArrayList newComments){
        this.comments = newComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View comment = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment, parent, false);
        return new ViewHolder(comment);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.commenterName.setText(comment.getCommenterName());
        holder.commentTimestamp.setText(comment.getTimestamp());
        holder.commentTextView.setText(comment.getCommentContent());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView commenterProfilePic;
        TextView commenterName;
        TextView commentTimestamp;
        TextView commentTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commenterProfilePic = itemView.findViewById(R.id.userProfilePicture);
            commenterName = itemView.findViewById(R.id.nameTextView);
            commentTimestamp = itemView.findViewById(R.id.commentTimestampTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}
