import {Breadcrumb} from "flowbite-react";

function BreadcrumbNav(props) {
    return (
        <div>
            <Breadcrumb
                aria-label="Solid background breadcrumb example"
                className="bg-gray-50 py-3 px-5 dark:bg-gray-900"
            >
                {Array
                    .from(props.path)
                    .map(([key, value]) =>
                        <Breadcrumb.Item
                            href={value}
                            key={value}
                        >
                            {key}
                        </Breadcrumb.Item>
                    )
                }
            </Breadcrumb>
        </div>
    );
}

export default BreadcrumbNav
