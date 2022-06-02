package com.github.klefstad_teaching.cs122b.movies.data;

public class Genre {
    private String name;
    private Long id;

    public Long getId() {
        return id;
    }

    public Genre setId(Long genreId) {
        this.id = genreId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Genre setName(String name) {
        this.name = name;
        return this;
    }
}
