package com.github.klefstad_teaching.cs122b.movies.response;

import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.movies.data.Person2;

import java.util.List;

public class PersonSearchResponse {
    private Result result;
    private List<Person2> persons;

    public Result getResult() {
        return result;
    }

    public PersonSearchResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<Person2> getPersons() {
        return persons;
    }

    public PersonSearchResponse setPersons(List<Person2> persons) {
        this.persons = persons;
        return this;
    }


}
