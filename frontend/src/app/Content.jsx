import React from "react";
import {Route, Routes} from "react-router-dom";

import MovieDetail from "pages/MovieDetail";
import Register from "pages/Register";
import Login from "pages/Login";
import Home from "pages/Home";
import styled from "styled-components";
import Cart from "../pages/Cart";
import {useUser} from "../hook/User";
import Logout from "../pages/Logout";
import App from "../pages/App";
import OrderHistory from "../pages/OrderHistory"
import OrderDetail from "../pages/OrderDetail"


const StyledDiv = styled.div`
  display: flex;
  justify-content: center;

  width: 100vw;
  height: 100vh;
  padding: 25px;

  background: #ffffff;
  box-shadow: inset 0 3px 5px -3px #000000;
`



const Content = () => {

    return (
        <StyledDiv>
            <Routes>
                <Route path="/order/detail/:saleId" element={<OrderDetail/>}/>
                <Route path="/order/list" element={<OrderHistory/>}/>
                <Route path="/orders" element={<App/>}/>
                <Route path="/cart" element={<Cart/>}/>
                <Route path="/movie/:movieId" element={<MovieDetail/>}/>
                <Route path="/movies/search" element={<Home/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/logout" element={<Logout/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/" element={<Login/>}/>
            </Routes>
        </StyledDiv>
    );
}

export default Content;
