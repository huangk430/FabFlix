import React from "react";
import {NavLink} from "react-router-dom";
import styled from "styled-components";
import {useUser} from "../hook/User";

const StyledNav = styled.nav`
  display: flex;
  justify-content: center;

  width: calc(100vw - 10px);
  height: 50px;
  padding: 5px;

  background-color: #fff;
`;

const StyledNavLink = styled(NavLink)`
  padding: 10px;
  font-size: 25px;
  color: #000;
  text-decoration: none;
`;

const NavBar = () => {
    const { accessToken } = useUser();

    if (accessToken) {
        console.log("User Login Page")
        return (
            <StyledNav>
                <StyledNavLink to="/movies/search">
                    Home
                </StyledNavLink>

                <StyledNavLink to="/cart">
                    Cart
                </StyledNavLink>

                <StyledNavLink to="/order/list">
                    Orders
                </StyledNavLink>

                <StyledNavLink to="/logout">
                    Logout
                </StyledNavLink>
            </StyledNav>
        );
    }
    else {
        console.log("Unregistered Login Page")
        return (
            <StyledNav>

                <StyledNavLink to="/login">
                    Login
                </StyledNavLink>
                <StyledNavLink to="/register">
                    Register
                </StyledNavLink>

            </StyledNav>
        );
    }

}

export default NavBar;
