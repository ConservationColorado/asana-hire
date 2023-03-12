import {Navbar} from 'flowbite-react/lib/cjs/components';
import logo from '../../images/logo.png';

import NavigationLink from "./NavigationLink";

function MainNavigation() {
    return (
        <Navbar fluid={true} rounded={true}>
            <Navbar.Brand
                href="/"
            >
                <img
                    src={logo}
                    className="mr-1 h-10"
                    alt="asana-hire logo"
                />
            </Navbar.Brand>
            <Navbar.Toggle/>
            <Navbar.Collapse>
                <NavigationLink to="/" text="Home"/>
                <NavigationLink to="/jobs" text="View jobs"/>
                <NavigationLink to="/settings" text="Settings"/>
                <NavigationLink to="/help" text="Help"/>
            </Navbar.Collapse>
        </Navbar>
    )
}

export default MainNavigation
