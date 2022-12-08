import {Navbar} from 'flowbite-react/lib/cjs/components';

import MainNavigationLink from "./MainNavigationLink";

function MainNavigation() {
    return (
        <Navbar fluid={true} rounded={true}>
            <Navbar.Brand href="/">
                <img
                    src="https://conservationco.org/wp-content/uploads/2022/03/Conservation-Colorado-Logo-C4-Color.png"
                    className="mr-1 h-10"
                    alt="Conservation Colorado Logo"
                />
                <span
                    className="self-center whitespace-nowrap text-xl font-semibold dark:text-white">Hiring admin console</span>
            </Navbar.Brand>
            <Navbar.Toggle/>
            <Navbar.Collapse>
                <MainNavigationLink to="/" text="Home"/>
                <MainNavigationLink to="/jobs" text="View jobs"/>
                <MainNavigationLink to="/manage" text="Manage sync"/>
                <MainNavigationLink to="/settings" text="Settings"/>
                <MainNavigationLink to="/help" text="Help"/>
            </Navbar.Collapse>
        </Navbar>
    )
}

export default MainNavigation
