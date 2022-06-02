package com.github.klefstad_teaching.cs122b.movies.data;

public class Person {
    private String name;
    private Long id;

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Person setId(Long id) {
        this.id = id;
        return this;
    }
}
