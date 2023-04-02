import React, {useContext} from "react";
import {Button} from "flowbite-react";
import {AuthContext} from "./AuthProvider";

function GoogleLoginButton() {
    const {oauthLogin} = useContext(AuthContext);

    function handleClick() {
        const provider = "google";
        oauthLogin(provider);
    }

    return (
        <Button pill={true} onClick={handleClick}>
            🔐 Continue with Google
        </Button>
    );
}

export default GoogleLoginButton;
