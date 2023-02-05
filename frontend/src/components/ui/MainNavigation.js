import {Navbar} from 'flowbite-react/lib/cjs/components';
import {Link} from 'react-router-dom';
import logo from '../../images/logo.png';

import NavigationLink from "./NavigationLink";

function MainNavigation() {
    return (
        <Navbar fluid={true} rounded={true}>
            <Link to="/">
                <Navbar.Brand>
                    <img
                        src={logo}
                        className="mr-1 h-10"
                        alt="asana-hire logo"
                    />
                    <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
                        Hiring Admin Console
                    </span>
                </Navbar.Brand>
            </Link>
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
