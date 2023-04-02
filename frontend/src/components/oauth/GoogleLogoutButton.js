import {Button} from "flowbite-react";
import {useContext} from "react";
import {AuthContext} from "../../components/oauth/AuthProvider";

function GoogleLogoutButton() {
    const {logout} = useContext(AuthContext);

    function handleClick() {
        logout();
    }

    return (
        <Button color="gray" pill={true} onClick={handleClick} >
            Sign out
        </Button>
    );
}

export default GoogleLogoutButton;
