package com.github.klefstad_teaching.cs122b.movies.response;

import com.github.klefstad_teaching.cs122b.core.result.Result;

import java.util.List;

public class MovieSearchResponse<Movie> {
    private Result result;
    private List<Movie> movies;

    public Result getResult() {
        return result;
    }

    public MovieSearchResponse<Movie> setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public MovieSearchResponse<Movie> setMovies(List<Movie> movies) {
        this.movies = movies;
        return this;
    }
}
