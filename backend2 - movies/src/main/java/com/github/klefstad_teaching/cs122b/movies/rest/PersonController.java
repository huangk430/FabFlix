package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.repo.PersonRepo;
import com.github.klefstad_teaching.cs122b.movies.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.response.PersonSearchIDResponse;
import com.github.klefstad_teaching.cs122b.movies.response.PersonSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class PersonController
{
    private final PersonRepo repo;

    @Autowired
    public PersonController(PersonRepo repo)
    {
        this.repo = repo;
    }

    public void paramErrorCheck(Optional<Integer> limit, Optional<Integer> page, Optional<String> orderBy, Optional<String> direction) {
        if (limit.isPresent() && limit.get() != 10 && limit.get() != 25 && limit.get() != 50 && limit.get() != 100) {
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        }
        if (page.isPresent() && page.get() <= 0) {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }
        //invalid direction
        if (direction.isPresent() && !direction.get().equalsIgnoreCase("asc") && !direction.get().equalsIgnoreCase("desc")) {
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
        //invalid orderby
        if (orderBy.isPresent() && !orderBy.get().equalsIgnoreCase("name") && !orderBy.get().equalsIgnoreCase("birthday") && !orderBy.get().equalsIgnoreCase("popularity")) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    //search person
    @GetMapping("/person/search")
    public ResponseEntity<PersonSearchResponse> personSearch(
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> birthday,
            @RequestParam Optional<String> movieTitle,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> orderBy,
            @RequestParam Optional<String> direction) {

        PersonSearchResponse response = null;

        //ERROR CHECK REQUEST PARAMS
        paramErrorCheck(limit, page, orderBy, direction);

        response = repo.getPersons(
                name.isPresent() ? name.get() : null,
                birthday.isPresent() ? birthday.get() : null,
                movieTitle.isPresent() ? movieTitle.get() : null,
                limit.isPresent() ? limit.get() : null,
                page.isPresent() ? page.get() : null,
                orderBy.isPresent() ? orderBy.get() : null,
                direction.isPresent() ? direction.get() : null
                );


        return ResponseEntity.status(HttpStatus.OK)
                .body(response);

    }

    //search by person ID
    //search person
    @GetMapping("/person/{personId}")
    public ResponseEntity<PersonSearchIDResponse> personSearchById(
            @PathVariable Long personId) {

        PersonSearchIDResponse response = repo.getPersonById(personId);


        return ResponseEntity.status(HttpStatus.OK)
                .body(response);

    }


}
