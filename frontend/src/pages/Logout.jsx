import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {useNavigate} from "react-router-dom";
import Idm from "backend/idm";
import FormControl, { useFormControl } from '@mui/material/FormControl';

import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';



const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const LogoutDiv = styled.div`
 
  display: flex;
  flex-direction: column;
  margin-top: 100px;

`

const Logout = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        window.localStorage.clear();
        navigate("/login");
        window.location.reload();
    };

    const cancel = () => {
        navigate("/");
    }

    return (
        <StyledDiv>
            <LogoutDiv>

                <Button size="large" variant="contained" color="success" onClick={handleLogout}>
                    Logout
                </Button>
                <br/>
                <Button size="large" variant="contained" color="error" onClick={cancel}>
                    Cancel
                </Button>
            </LogoutDiv>
        </StyledDiv>
    );
}

export default Logout;
