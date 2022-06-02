package com.github.klefstad_teaching.cs122b.movies.response;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.movies.data.Genre;
import com.github.klefstad_teaching.cs122b.movies.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.data.Movie2;
import com.github.klefstad_teaching.cs122b.movies.data.Person;

import java.util.List;

public class MovieSearchIDResponse {
    private Result result;
    private Movie2 movie;
    private List<Genre> genres;
    private List<Person> persons;

    public Result getResult() {
        return result;
    }

    public MovieSearchIDResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public Movie2 getMovie() {
        return movie;
    }

    public MovieSearchIDResponse setMovie(Movie2 movie) {
        this.movie = movie;
        return this;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public MovieSearchIDResponse setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public MovieSearchIDResponse setPersons(List<Person> persons) {
        this.persons = persons;
        return this;
    }
}
