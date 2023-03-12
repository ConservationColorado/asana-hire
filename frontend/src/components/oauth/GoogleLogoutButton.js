import {Button} from "flowbite-react";
import {googleLogout} from '@react-oauth/google';

function GoogleLogoutButton() {
    function handleLogout() {
        googleLogout();
    }

    return (
        <Button className="g_id_signout" onClick={handleLogout}>
            Sign out
        </Button>
    );
}

export default GoogleLogoutButton;
