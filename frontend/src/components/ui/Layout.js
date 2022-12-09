import MainNavigation from "./MainNavigation";
import React from "react";
import BottomFooter from "./Footer";

function Layout(props) {
    return (
        <div>
            <MainNavigation/>
            <main>{props.children}</main>
            <BottomFooter/>
        </div>
    );
}

export default Layout
