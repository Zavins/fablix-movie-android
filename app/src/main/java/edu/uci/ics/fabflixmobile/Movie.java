package edu.uci.ics.fabflixmobile;

public class Movie {
    private final String name;
    private final short year;

    public Movie(String name, short year) {
        this.name = name;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }
}