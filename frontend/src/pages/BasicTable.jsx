import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import {Tab} from "@mui/material";

function createData(
    name: string,
    calories: number,
    fat: number,
    carbs: number,
    protein: number,
) {
    return { name, calories, fat, carbs, protein };
}

const rows = [

];

export default function BasicTable() {
    return (
        <TableContainer component={Paper}>
            <Table sx={{ minWidth: 600 }} aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell>Overview</TableCell>
                        <TableCell>alksdjfkl</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Director</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Rating</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Budget</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Revenue</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Genres</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>People</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows.map((row) => (
                        <TableRow
                            key={row.name}
                            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                        >
                            <TableCell component="th" scope="row">
                                {row.name}
                            </TableCell>
                            <TableCell align="right">{row.calories}</TableCell>
                            <TableCell align="right">{row.fat}</TableCell>
                            <TableCell align="right">{row.carbs}</TableCell>
                            <TableCell align="right">{row.protein}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}