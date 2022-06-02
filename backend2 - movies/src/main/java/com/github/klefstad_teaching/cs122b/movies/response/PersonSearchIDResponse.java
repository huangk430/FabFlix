package com.github.klefstad_teaching.cs122b.movies.response;

import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.movies.data.Person2;

public class PersonSearchIDResponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public PersonSearchIDResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    private Person2 person;

    public Person2 getPerson() {
        return person;
    }

    public PersonSearchIDResponse setPerson(Person2 person) {
        this.person = person;
        return this;
    }


}
