package com.recomtree.composite;

// Class for a movie object
public class Movie extends CatalogComponent {
    private double rating;
    private int ratingCount; // Number of ratings received
    private double totalRatingSum; // Sum of all ratings for averaging

    // Default constructor (needed for saving/loading)
    public Movie() {
        this.rating = 0.0;
        this.ratingCount = 0;
        this.totalRatingSum = 0.0;
    }

    // Constructor with title only (no initial rating)
    public Movie(String title) {
        super(title);
        this.rating = 0.0;
        this.ratingCount = 0;
        this.totalRatingSum = 0.0;
    }

    // Constructor with parameters (for backward compatibility)
    public Movie(String title, double rating) {
        super(title);
        this.rating = rating;
        this.ratingCount = 0;
        this.totalRatingSum = 0.0;
    }

    // Get the rating of the movie
    @Override
    public double getRating() {
        return rating;
    }

    // Get the number of ratings
    public int getRatingCount() {
        return ratingCount;
    }

    // Get the total rating sum
    public double getTotalRatingSum() {
        return totalRatingSum;
    }

    // Setter for rating
    public void setRating(double rating) {
        this.rating = rating;
    }

    // Setter for ratingCount
    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    // Setter for totalRatingSum
    public void setTotalRatingSum(double totalRatingSum) {
        this.totalRatingSum = totalRatingSum;
    }

    // Add a user rating and update the average
    public void addRating(double userRating) {
        this.totalRatingSum += userRating;
        this.ratingCount++;
        this.rating = this.totalRatingSum / this.ratingCount;
    }

    // Display the movie with indentation
    @Override
    public void display(StringBuilder sb, int depth) {
        // Add indentation based on depth
        for (int i = 0; i < depth; i++) {
            sb.append("   ");
        }

        // Add "- " prefix
        sb.append("- ");

        // Add movie name
        sb.append(name);
        sb.append(" ");

        // Add rating in format "Rating/10 (number of ratings)"
        if (ratingCount > 0) {
            sb.append(String.format("%.1f", rating));
            sb.append("/10 (");
            sb.append(ratingCount);
            sb.append(")");
        } else {
            sb.append("Not rated");
        }
        sb.append("\n");
    }
}