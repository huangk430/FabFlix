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
import { format } from 'date-fns';


import DeleteIcon from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';
import {orderList} from "../backend/orders";



const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

function createData(
    saleId,
    total,
    orderDate,

) {
    return { saleId, total, orderDate };
}
let rows = [];
let added = 0;

const OrderHistory = () => {
    const {accessToken} = useUser();


    const [history, setHistory] = React.useState([]);
    const navigate = useNavigate();


    const viewHistory = () => {

        orderList(accessToken)
            .then(response => {
                    if (response.data.result.code != 3004) {
                        setHistory(response.data.sales)
                    }
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
    }

    if (history != null) {
        for (let i = added; i < history.length; i++) {

            rows.push(createData(history[i].saleId, history[i].total, history[i].orderDate));
            added++;
        }
    }


    useEffect(() => viewHistory(), []);

    return (
        <StyledDiv>
            {/*CART DISPLAY*/}
            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 800 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell><b>Order Number</b></TableCell>
                            <TableCell align="right"><b>Total</b></TableCell>
                            <TableCell align="right"><b>Date</b></TableCell>
                            <TableCell align="right"><b> </b></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map((row) => (
                            <TableRow
                                key={row.id}
                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                            >
                                <TableCell component="th" scope="row">
                                    <a href={"/order/detail/" + row.saleId}>{row.saleId}</a>
                                </TableCell>
                                <TableCell align="right">{row.total}</TableCell>
                                <TableCell align="right">{row.orderDate}</TableCell>
                                <TableCell align="right">
                                </TableCell>

                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>


        </StyledDiv>

    );
}

export default OrderHistory;