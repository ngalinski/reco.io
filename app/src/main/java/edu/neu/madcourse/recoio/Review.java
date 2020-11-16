package edu.neu.madcourse.recoio;

public class Review {
    private String productTitle;
    private String rating;
    private String reviewText;
    private String reviewer;
    private Integer likes;

    public Review(String productTitle, String rating, String reviewText, String reviewer) {
        this.productTitle = productTitle;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewer = reviewer;
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

}
