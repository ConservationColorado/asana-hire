import React from "react";
import {Button} from "flowbite-react";
import {useGoogleLogin} from '@react-oauth/google';

function GoogleLoginButton({onSuccess, onError}) {

    const handleSuccess = async (tokenResponse) => {
        onSuccess(tokenResponse);
    };

    const handleFailure = (error) => {
        onError(error);
    };

    const handleClick = useGoogleLogin({
        onSuccess: handleSuccess,
        onFailure: handleFailure,
        flow: "auth-code",
        scope: "https://www.googleapis.com/auth/gmail.modify",
        redirect_uri: "http://localhost:8080/auth/google",
        ux_mode: "popup",
        hosted_domain: "conservationco.org"
    });

    return (
        <Button color="gray" pill={true} onClick={handleClick}>
            🔐 Continue with Google
        </Button>
    );
}

export default GoogleLoginButton;
