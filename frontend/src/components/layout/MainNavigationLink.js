import {useLinkClickHandler, useLocation} from "react-router-dom";
import {Navbar} from "flowbite-react";

export default function MainNavigationLink(props) {
    const location = useLocation();
    const clickHandler = useLinkClickHandler(props.to);

    return (
        <span onClick={clickHandler}>
            <Navbar.Link href={props.to} active={location.pathname === props.to}>
                {props.text}
            </Navbar.Link>
        </span>
    );
}
