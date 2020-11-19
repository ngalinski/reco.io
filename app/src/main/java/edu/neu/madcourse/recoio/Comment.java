package edu.neu.madcourse.recoio;

public class Comment {
    private String commenterName;
    private String commentContent;
    private String timestamp;

    public Comment(String commenterName, String commentContent, String timestamp) {
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.timestamp = timestamp;
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
