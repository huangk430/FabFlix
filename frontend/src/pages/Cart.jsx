import React, {useEffect} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {useNavigate} from "react-router-dom";
import {cartDelete, cartUpdate, insertCart, retrieveCart, cartClear} from "../backend/cart";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import Button from '@mui/material/Button';


import DeleteIcon from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';




const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const StyledH1 = styled.h1`
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`

function createData(
    movieTitle,
    unitPrice,
    quantity,
    movieId
) {
    return { movieTitle, unitPrice, quantity, movieId };
}
let rows = [];
let added = 0;

const Cart = () => {
    const {accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();


    const [cart, setCart] = React.useState([]);
    const [total, setTotal] = React.useState(0);
    const navigate = useNavigate();

    const viewCart = () => {

        retrieveCart({}, accessToken)
            .then(response => {
                if (response.data.result.code != 3004) {
                    setCart(response.data.items)
                }

                setTotal(response.data.total)
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
    }

    const updateMovie = (movieId, quantity) => {
        const payload = {
            movieId: movieId,
            quantity: quantity
        }

        cartUpdate(payload, accessToken)
            .then(
                response => {
                    setCart(response.data.items);
                    setTotal(response.data.total);
                }
            )
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));

        window.location.reload();
    }


    const clearCart = () => {
        cartClear(accessToken)
            .then(response => {
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));

        window.location.reload();

    }

    const deleteMovie = (movieId, quantity) => {
        const payload = {
            movieId: movieId,
            quantity: quantity
        }

        cartDelete(payload, accessToken)
            .then(
                response => {
                    setCart(response.data.items)
                    setTotal(response.data.total)
                })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
        window.location.reload();

    }

    for (let i = added; i < cart.length; i++) {
        rows.push(createData(cart[i].movieTitle, cart[i].unitPrice, cart[i].quantity, cart[i].movieId));
        added++;
    }



    useEffect(() => viewCart(), []);

    return (
        <StyledDiv>
            {/*CART DISPLAY*/}
            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 800 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell><b>Movie Title</b></TableCell>
                            <TableCell align="right"><b>Price</b></TableCell>
                            <TableCell align="right"><b>Quantity</b></TableCell>
                            <TableCell align="right"><b> </b></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map((row) => (
                            <TableRow
                                key={row.movieTitle}
                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                            >
                                <TableCell component="th" scope="row">{row.movieTitle}</TableCell>
                                <TableCell align="right">{row.unitPrice}</TableCell>
                                <TableCell align="right">
                                    {row.quantity}
                                    <IconButton onClick={() => updateMovie(row.movieId, row.quantity+1)}
                                        color="primary" aria-label="add to shopping cart">
                                        <AddShoppingCartIcon />
                                    </IconButton>
                                </TableCell>


                                <TableCell align="right">
                                    <IconButton onClick={() => {
                                        if (row.quantity === 1) {
                                            deleteMovie(row.movieId, row.quantity);
                                        } else {
                                            updateMovie(row.movieId, row.quantity-1);
                                        }
                                    }}
                                                aria-label="delete">
                                        <DeleteIcon />
                                    </IconButton>
                                </TableCell>

                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <br/>
            <br/>
            {/*TOTAL DISPLAY*/}
            <TableContainer component={Paper}>
                <Table sx={{ maxWidth: 800 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell><Button onClick={() => { clearCart()}}>Clear Cart</Button></TableCell>
                            <TableCell align="right"><b>Total:</b> ${total}</TableCell>
                            <TableCell align="right">            </TableCell>
                            <TableCell align="right"><Button onClick={() => { navigate("/orders")}}>Checkout</Button></TableCell>

                        </TableRow>
                    </TableHead>
                </Table>
            </TableContainer>

        </StyledDiv>

    );
}

export default Cart;