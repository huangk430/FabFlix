package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.response.MovieSearchIDResponse;
import com.github.klefstad_teaching.cs122b.movies.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
public class MovieController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
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
        if (orderBy.isPresent() && !orderBy.get().equalsIgnoreCase("title") && !orderBy.get().equalsIgnoreCase("rating") && !orderBy.get().equalsIgnoreCase("year")) {
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public Integer checkIsAdmin(SignedJWT user) throws ParseException {
        int admin = 0; //0 = not admin, 1 = admin
        List<String> roles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        //check if a user is admin or employee
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).equalsIgnoreCase("admin") || roles.get(i).equalsIgnoreCase("employee"))
            {
                admin = 1;
            }
        }
        return admin;
    }

    //search by title, director, genre
    @GetMapping("/movie/search")
    public ResponseEntity<MovieSearchResponse> movieSearch(
            @AuthenticationPrincipal SignedJWT user,
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> director,
            @RequestParam Optional<String> genre,
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> orderBy,
            @RequestParam Optional<String> direction
    ) throws ParseException {

        MovieSearchResponse response = null;

        //ERROR CHECK REQUEST PARAMS
        paramErrorCheck(limit, page, orderBy, direction);
        System.out.println("HERE");
        int isAdmin = checkIsAdmin(user);

        response = repo.getMovie(
                title.isPresent() ? title.get() : null,
                director.isPresent() ? director.get() : null,
                genre.isPresent() ? genre.get() : null,
                year.isPresent() ? year.get() : null,
                limit.isPresent() ? limit.get() : null,
                page.isPresent() ? page.get() : null,
                orderBy.isPresent() ? orderBy.get() : null,
                direction.isPresent() ? direction.get() : null,
                isAdmin, null);


        return ResponseEntity.status(HttpStatus.OK)
                .body(response);

    }

    //MOVIE SEARCH BY PERSON ID

    @GetMapping("/movie/search/person/{personId}")
    public ResponseEntity<MovieSearchResponse> movieSearch(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long personId,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> orderBy,
            @RequestParam Optional<String> direction
    ) throws ParseException {

        MovieSearchResponse response = null;
        //error check
        paramErrorCheck(limit, page, orderBy, direction);

        //check admin
        int isAdmin = checkIsAdmin(user);

        response = repo.getMovie(
                null, null, null, null,
                limit.isPresent() ? limit.get() : null,
                page.isPresent() ? page.get() : null,
                orderBy.isPresent() ? orderBy.get() : null,
                direction.isPresent() ? direction.get() : null,
                isAdmin, personId);


        return ResponseEntity.status(HttpStatus.OK)
                .body(response);

    }

    //MOVIE SEARCH BY MOVIE ID

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<MovieSearchIDResponse> movieIDSearch(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long movieId,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> orderBy,
            @RequestParam Optional<String> direction
    ) throws ParseException {

        MovieSearchIDResponse response = null;
        //error check
        paramErrorCheck(limit, page, orderBy, direction);

        //check admin
        int isAdmin = checkIsAdmin(user);

        response = repo.getMovieByMovieID(
                movieId,
                limit.isPresent() ? limit.get() : null,
                page.isPresent() ? page.get() : null,
                orderBy.isPresent() ? orderBy.get() : null,
                direction.isPresent() ? direction.get() : null,
                isAdmin);


        return ResponseEntity.status(HttpStatus.OK)
                .body(response);

    }


}
