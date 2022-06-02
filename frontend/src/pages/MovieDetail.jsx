import React, {useEffect} from "react";
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import {useUser} from "../hook/User";
import styled from "styled-components";
import Movies from "../backend/movies";
import BasicTable from "./BasicTable";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";

import ButtonGroup from "@material-ui/core/ButtonGroup";
import Badge from "@material-ui/core/Badge";
import ShoppingCartIcon from "@material-ui/icons/ShoppingCart";
import Button from "@material-ui/core/Button";
import AddIcon from "@material-ui/icons/Add";
import RemoveIcon from "@material-ui/icons/Remove";
import {insertCart} from "../backend/cart";


const Title = styled.div`
    display: flex,
    align-items: center,
    flex-direction: column;
`

const Column = styled.div`
  display: flex;
  flex-direction: column;
  max-width: 600px;
  gap: 8px;`

const Row = styled.div`
  display: flex;
  flex-direction: row;
  gap: 25px;

`

const StyledH1 = styled.h1`
    display: 'flex';
    justifyContent:'center';
     alignItems:'center;
`

const StyledP = styled.p`
    display: 'flex';
    justifyContent:'center';
    alignItems:'center;
`

const StyledButton = styled.button`
`
const MovieDetail = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const [itemCount, setItemCount] = React.useState(0);
    const [movie, setMovie] = React.useState(0);
    const [genres, setGenres] = React.useState([]);
    const [persons, setPersons] = React.useState([]);

    const {movieId} = useParams();
    const navigate = useNavigate();
    let genreString = "";
    let peopleString = "";
    const result = "";
    const people = "";


    const getMovie = () => {


        Movies.getMovieDetail(movieId, accessToken)
            .then(response => {
                setGenres(response.data.genres);
                setMovie(response.data.movie);
                setPersons(response.data.persons);
                console.log(genres)

            })

            for (let i = 0; i < genres.length; i++) {
                genreString = genreString.concat(genres[i].name, ", ")
            }
            result.concat(genreString.substring(0, genreString.length - 2));

            for (let i = 0; i < persons.length; i++) {
                peopleString = peopleString.concat(persons[i].name, ", ")
            }
            people.concat(genreString.substring(0, genreString.length - 2));

            console.log(result)
            console.log(people)


    }

    const addCart = (movieId, quantity) => {
        console.log(quantity)
        const payload = {
            movieId: movieId,
            quantity: quantity
        }

        insertCart(payload, accessToken)
            .then(
                response => {
                    alert(JSON.stringify(response.data, null, 2))
                })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));

    }

    React.useEffect(() => getMovie(), []);


    const rows = [];

    return (
        <Title>
            <h1>{movie.title}</h1>
            <br/>

            <Row>
                {/*POSTER IMAGE*/}
                <Column>
                    <img src={"https://image.tmdb.org/t/p/w200" + movie.posterPath}/>
                </Column>

                {/*DETAILS TABLE*/}
                <Column>
                <TableContainer component={Paper}>
                    <Table sx={{ maxWidth: 600 }} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell>Overview</TableCell>
                                <TableCell>{movie.overview}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Director</TableCell>
                                <TableCell>{movie.director}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Rating</TableCell>
                                <TableCell>{movie.rating}/10</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Budget</TableCell>
                                <TableCell>${movie.budget}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell>Revenue</TableCell>
                                <TableCell>${movie.revenue}</TableCell>
                            </TableRow>
                        </TableHead>
                    </Table>
                </TableContainer>
                </Column>

            </Row>

            {/*ADD TO CART */}
            <div style={{ display: "block", padding: 30}}>
                <div>
                    <Badge color="secondary" badgeContent={itemCount}>
                        <ShoppingCartIcon />{" "}
                    </Badge>
                    <ButtonGroup>
                        {/*DELETE ITEM*/}
                        <Button
                            onClick={() => {
                                setItemCount(Math.max(itemCount - 1, 0));
                            }}
                        >
                            {" "}
                            <RemoveIcon fontSize="small" />
                        </Button>
                        {/*ADD ITEM*/}
                        <Button
                            onClick={() => {
                                setItemCount(itemCount + 1);
                            }}
                        >
                            {" "}
                            <AddIcon fontSize="small" />
                        </Button>
                    </ButtonGroup>
                    <br/>
                    <br/>
                    <Button variant="outlined" onClick={() => { addCart(movieId, itemCount)}}>Add To Cart</Button>
                </div>
            </div>
        </Title>


    );

}

export default MovieDetail;