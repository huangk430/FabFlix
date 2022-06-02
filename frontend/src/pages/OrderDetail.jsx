import React, {useEffect} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {useNavigate, useParams} from "react-router-dom";
import {orderDetails} from "../backend/orders";
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
    quantity
) {
    return { movieTitle, unitPrice, quantity };
}
let rows = [];
let added = 0;

const Cart = () => {
    const {accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();
    const {saleId} = useParams();

    // cart
    const [items, setItems] = React.useState([]);
    const [total, setTotal] = React.useState(0);
    const navigate = useNavigate();



    const viewOrders = () => {

        orderDetails(saleId, accessToken)
            .then(response => {
                if (response.data.result.code != 3004) {
                    setItems(response.data.items)
                }
                setTotal(response.data.total)
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
    }


    for (let i = added; i < items.length; i++) {
        rows.push(createData(items[i].movieTitle, items[i].unitPrice, items[i].quantity, items[i].quantity));
        added++;
    }
    console.log(items)

    useEffect(() => viewOrders(), []);

    return (
        <StyledDiv>
            {/*TOTAL DISPLAY*/}
            <TableContainer component={Paper}>
                <Table sx={{ maxWidth: 800 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell align="right" ><b>Order Details</b></TableCell>
                            <TableCell align="right"><b>Total:</b> ${total}</TableCell>
                        </TableRow>
                    </TableHead>
                </Table>
            </TableContainer>
            {/*CART DISPLAY*/}
            <br/>
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
                                <TableCell align="right">{row.quantity}</TableCell>

                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <br/>
            <br/>

        </StyledDiv>

    );
}

export default Cart;