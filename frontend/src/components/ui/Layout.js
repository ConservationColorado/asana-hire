import MainNavigation from "./MainNavigation";
import UnauthenticatedMainNavigation from "./UnauthenticatedMainNavigation";
import React, {useContext} from "react";
import BottomFooter from "./Footer";
import { AuthContext } from "../oauth/AuthProvider";

function Layout(props) {
    const {isAuthenticated} = useContext(AuthContext);
    return (
        <>
            {isAuthenticated ? <MainNavigation/> : <UnauthenticatedMainNavigation/>}
            <main>{props.children}</main>
            <BottomFooter/>
        </>
    );
}

export default Layout
