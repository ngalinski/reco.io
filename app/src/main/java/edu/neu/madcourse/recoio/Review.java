package edu.neu.madcourse.recoio;

public class Review {
    private String uid;
    private String productTitle;
    private String rating;
    private String reviewText;
    private String reviewer;
    private String reviewerUID;
    private Long likeCount;
    private Boolean hasPicture;

    public Review(String uid, String productTitle, String rating, String reviewText, String reviewer,
                  Boolean hasPicture, Long likeCount, String reviewerUID) {
        this.productTitle = productTitle;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewer = reviewer;
        this.uid = uid;
        this.hasPicture = hasPicture;
        this.likeCount = likeCount;
        this.reviewerUID = reviewerUID;
    }


    public Boolean hasPicture() {
        return hasPicture;
    }

    public void setHasPicture(Boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getReviewerUID() {
        return reviewerUID;
    }

    public void setReviewerUID(String reviewerUID) {
        this.reviewerUID = reviewerUID;
    }

    public Boolean getHasPicture() {
        return hasPicture;
    }
}
