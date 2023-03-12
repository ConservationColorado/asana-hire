import MainNavigation from "./MainNavigation";
import UnauthenticatedMainNavigation from "./UnauthenticatedMainNavigation";
import React from "react";
import BottomFooter from "./Footer";

function Layout(props) {
    return (
        <div>
            {props.isAuthenticated ? <MainNavigation/> : <UnauthenticatedMainNavigation/>}
            <main>{props.children}</main>
            <BottomFooter/>
        </div>
    );
}

export default Layout
