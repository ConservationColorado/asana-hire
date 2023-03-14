import {Button} from "flowbite-react";
import {googleLogout} from '@react-oauth/google';
import {useContext} from "react";
import {AuthContext} from "../../components/oauth/AuthProvider";

function GoogleLogoutButton() {

    const {logout} = useContext(AuthContext);

    const handleClick = () => {
        googleLogout();
        logout();
    };

    return (
        <Button color="gray" pill={true} className="g_id_signout" onClick={handleClick} >
            Sign out
        </Button>
    );
}

export default GoogleLogoutButton;
