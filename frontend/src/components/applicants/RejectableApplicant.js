import {Table} from 'flowbite-react'
import {taskLinkString} from '../../utils/LinkUtils'

function RejectableApplicant({applicant}) {
    return (
        <Table.Row className="bg-white dark:border-gray-700 dark:bg-gray-800">
            <Table.Cell className="whitespace-nowrap font-medium text-gray-900 dark:text-white">
                <a href={taskLinkString(applicant.id)}>{applicant.name}</a>
            </Table.Cell>
            <Table.Cell>
                {applicant.preferredName}
            </Table.Cell>
            <Table.Cell>
                {applicant.email}
            </Table.Cell>
        </Table.Row>
    );
}

export default RejectableApplicant;
