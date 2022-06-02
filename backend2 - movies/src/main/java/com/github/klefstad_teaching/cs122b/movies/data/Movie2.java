package com.github.klefstad_teaching.cs122b.movies.data;

import java.math.BigDecimal;

//DETAILED MOVIE CLASS
public class Movie2 {
    private String overview;
    private BigDecimal revenue;
    private Boolean hidden;
    private Integer year;
    private String director;
    private Double rating;
    private Integer numVotes;
    private Long id;
    private String backdropPath;
    private String title;
    private String posterPath;
    private Long budget;


    public Integer getNumVotes() {
        return numVotes;
    }

    public Movie2 setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
        return this;
    }

    public Long getBudget() {
        return budget;
    }

    public Movie2 setBudget(Long budget) {
        this.budget = budget;
        return this;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public Movie2 setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public Movie2 setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Movie2 setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Movie2 setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public Movie2 setYear(Integer year) {
        this.year = year;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public Movie2 setDirector(String director) {
        this.director = director;
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public Movie2 setRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Movie2 setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Movie2 setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public Movie2 setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
