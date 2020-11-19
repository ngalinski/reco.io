package edu.neu.madcourse.recoio;

import com.google.firebase.database.DataSnapshot;

public class Comment {
    private String commenterName;
    private String commentContent;
    private String timestamp;
    private String commenterNameUID;

    public Comment(String commenterName, String commentContent, String timestamp,
                   String commenterNameUID) {
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.timestamp = timestamp;
        this.commenterNameUID = commenterNameUID;

    }

    public Comment(DataSnapshot dataSnapshot) {
        this.commenterName = (String) dataSnapshot.child("commenterName").getValue();
        this.commentContent = (String) dataSnapshot.child("commentContent").getValue();
        this.timestamp = (String) dataSnapshot.child("timestamp").getValue();
        this.commenterNameUID = (String) dataSnapshot.child("commenterNameUID").getValue();
    }

    public String getCommenterNameUID() {
        return commenterNameUID;
    }

    public void setCommenterNameUID(String commenterNameUID) {
        this.commenterNameUID = commenterNameUID;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
