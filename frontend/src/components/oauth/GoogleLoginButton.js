import React from "react";
import {GoogleLogin} from '@react-oauth/google';

function GoogleLoginButton({onSuccess, onError, type, theme}) {
    return (
        <GoogleLogin
            onSuccess={credentialResponse => {
                onSuccess(credentialResponse);
            }}
            onError={() => {
                onError();
            }}
            useOneTap
            type={type}
            theme={theme}
            shape="pill"
            size="large"
            width="300"
            text="continue_with"
        />
    );
}

export default GoogleLoginButton;
