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
        flow: 'implicit'
    });

    return (
        <Button color="gray" pill={true} onClick={handleClick}>
            🔐 Continue with Google
        </Button>
    );
}

export default GoogleLoginButton;
