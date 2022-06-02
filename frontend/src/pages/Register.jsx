import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import Idm from "backend/idm";
import {useNavigate} from "react-router-dom";
import FormControl from "@mui/material/FormControl";
import TextField from "@mui/material/TextField";
import Button from "@material-ui/core/Button";
import Alert from '@mui/material/Alert';



const StyledDiv = styled.div`
 
  display: flex;
  flex-direction: column;
`
const LoginDiv = styled.div`
 
  display: flex;
  flex-direction: column;
  margin-top: 30%

`

const StyledH1 = styled.h1`
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`
/**
 * useUser():
 * <br>
 * This is a hook we will use to keep track of our accessToken and
 * refreshToken given to use when the user calls "login".
 * <br>
 * For now, it is not being used, but we recommend setting the two tokens
 * here to the tokens you get when the user completes the login call (once
 * you are in the .then() function after calling login)
 * <br>
 * These have logic inside them to make sure the accessToken and
 * refreshToken are saved into the local storage of the web browser
 * allowing you to keep values alive even when the user leaves the website
 * <br>
 * <br>
 * useForm()
 * <br>
 * This is a library that helps us with gathering input values from our
 * users.
 * <br>
 * Whenever we make a html component that takes a value (<input>, <select>,
 * ect) we call this function in this way:
 * <pre>
 *     {...register("email")}
 * </pre>
 * Notice that we have "{}" with a function call that has "..." before it.
 * This is just a way to take all the stuff that is returned by register
 * and <i>distribute</i> it as attributes for our components. Do not worry
 * too much about the specifics of it, if you would like you can read up
 * more about it on "react-hook-form"'s documentation:
 * <br>
 * <a href="https://react-hook-form.com/">React Hook Form</a>.
 * <br>
 * Their documentation is very detailed and goes into all of these functions
 * with great examples. But to keep things simple: Whenever we have a html with
 * input we will use that function with the name associated with that input,
 * and when we want to get the value in that input we call:
 * <pre>
 * getValue("email")
 * </pre>
 * <br>
 * To Execute some function when the user asks we use:
 * <pre>
 *     handleSubmit(ourFunctionToExecute)
 * </pre>
 * This wraps our function and does some "pre-checks" before (This is useful if
 * you want to do some input validation, more of that in their documentation)
 */
const Register = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();


    const {register, getValues, handleSubmit} = useForm();
    const navigate = useNavigate();

    const submitRegister = () => {
        const email = getValues("email");
        const password = getValues("password");

        const payLoad = {
            email: email,
            password: password.split('')
        }

        Idm.register(payLoad)
            .then(response => {
                navigate("/login");
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }

    return (
        <StyledDiv>
            <LoginDiv>
            <FormControl sx={{ width: '33ch' }}>

                <TextField
                    {...register("email")}
                    type="email"
                    helperText=" "
                    id="demo-helper-text-aligned-no-helper"
                    label="Email"
                />
                <TextField
                    {...register("password")}
                    type="password"
                    helperText=" "
                    id="demo-helper-text-aligned-no-helper"
                    label="Password"
                />
            </FormControl>
            <Button variant="outlined" onClick={handleSubmit(submitRegister)}>Register</Button>
            </LoginDiv>
        </StyledDiv>
    );
}

export default Register;
