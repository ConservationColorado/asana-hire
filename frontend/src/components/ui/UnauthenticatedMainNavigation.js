import {Navbar} from 'flowbite-react/lib/cjs/components';
import logo from '../../images/logo.png';

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
        </Navbar>
    )
}

export default MainNavigation
