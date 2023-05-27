package edu.uci.ics.fablixmobile.data.model;

import java.util.ArrayList;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String id;
    private final String name;
    private double rating = 0.0;
    private final short year;
    private final String director;
    private final ArrayList<String> genres;
    private final ArrayList<String> stars;


//Show search results: a list view of movies with title,
// year, director, first 3 genres, first 3 stars (see Movie List Page), sorting is optional.
    public Movie(String id, String name, short year, String director, ArrayList<String> genres, ArrayList<String> stars) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }
    public Movie(String id, String name,  double rating, short year, String director, ArrayList<String> genres, ArrayList<String> stars) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId(){return id;}

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public double getRating() {return rating;}

    public String getDirector(){return director;}

    public ArrayList<String> getGenres(){return genres;}

    public ArrayList<String> getStars(){return stars;}

}