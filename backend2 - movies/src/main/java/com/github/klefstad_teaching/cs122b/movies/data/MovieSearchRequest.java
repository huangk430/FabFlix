package com.github.klefstad_teaching.cs122b.movies.data;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;

public class MovieSearchRequest {
    private String title;
    private Integer year;
    private String director;
    private String genre;
    private Integer limit = 10;
    private Integer page = 1;
    private String orderBy = "title";
    private String direction = "asc";

    public String getTitle() {
        return title;
    }

    public MovieSearchRequest setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public MovieSearchRequest setYear(Integer year) {
        this.year = year;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public MovieSearchRequest setDirector(String director) {
        this.director = director;
        return this;
    }

    public String getGenre() {
        return genre;
    }

    public MovieSearchRequest setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public MovieSearchRequest setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public MovieSearchRequest setPage(Integer page) {
        this.page = page;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public MovieSearchRequest setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public MovieSearchRequest setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public void validate() {
        if (this.limit != 10 && this.limit != 25 && this.limit != 50 && this.limit != 100) {
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        }

        if (this.page <= 0) {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }

        if (!this.orderBy.equals("title") && !this.orderBy.equals("rating") && !this.orderBy.equals("year")) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }

        if (!this.direction.equals("asc") && !this.direction.equals("desc")) {
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
    }
}