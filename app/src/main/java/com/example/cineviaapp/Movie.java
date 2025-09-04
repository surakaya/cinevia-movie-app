package com.example.cineviaapp;

import java.io.Serializable;

public class Movie implements Serializable {
    private int movieId;
    private String title;
    private String genre;
    private String posterUrl;
    private String overview;
    private String posterPath;

    public Movie(int movieId, String title, String genre, String posterUrl, String overview, String posterPath) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.overview = overview;
        this.posterPath = posterPath;
    }

    // Getter metodları
    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
