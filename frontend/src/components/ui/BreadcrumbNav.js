import {Breadcrumb} from "flowbite-react";
import {Link} from 'react-router-dom';

function BreadcrumbNav(props) {
    return (
        <div>
            <Breadcrumb
                className="bg-gray-50 py-3 px-5 dark:bg-gray-900"
            >
                {Array
                    .from(props.path)
                    .map(([key, value]) =>
                        <Breadcrumb.Item key={key}>
                            <Link to={value}>{key}</Link>
                        </Breadcrumb.Item>
                    )
                }
            </Breadcrumb>
        </div>
    );
}

export default BreadcrumbNav
