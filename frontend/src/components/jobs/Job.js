import {Table} from "flowbite-react"

function Job(props) {
    return (
        <Table.Body className="divide-y">
            <Table.Row className="bg-white dark:border-gray-700 dark:bg-gray-800">
                <Table.Cell className="whitespace-nowrap font-medium text-gray-900 dark:text-white">
                    {props.title}
                </Table.Cell>
                <Table.Cell>
                    {props.team}
                </Table.Cell>
                <Table.Cell>
                    {props.status}
                </Table.Cell>
                <Table.Cell>
                    {props.madeHire}
                </Table.Cell>
                <Table.Cell>
                    <a
                        href={`/jobs/${props.id}`}
                        className="font-medium text-blue-600 hover:underline dark:text-blue-500"
                    >
                        ➡
                    </a>
                </Table.Cell>
            </Table.Row>
        </Table.Body>
    );
}

export default Job
