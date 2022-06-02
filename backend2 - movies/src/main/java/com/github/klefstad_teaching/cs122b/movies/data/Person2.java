package com.github.klefstad_teaching.cs122b.movies.data;

//DETAILED PERSON CLASS
public class Person2 {
    private String birthday;
    private String birthplace;
    private Float popularity;
    private String name;
    private Integer id;
    private String biography;
    private String profilePath;


    public Integer getId() {
        return id;
    }

    public Person2 setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person2 setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public Person2 setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getBiography() {
        return biography;
    }

    public Person2 setBiography(String biography) {
        this.biography = biography;
        return this;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public Person2 setBirthplace(String birthplace) {
        this.birthplace = birthplace;
        return this;
    }

    public Float getPopularity() {
        return popularity;
    }

    public Person2 setPopularity(Float popularity) {
        this.popularity = popularity;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public Person2 setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }



}
