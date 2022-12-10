import {Card, Table, Breadcrumb} from "flowbite-react"
import Job from "./Job";

function JobList(props) {
    return (
        <div>
            <Card>
                <Table>
                    <Table.Head>
                        <Table.HeadCell>
                            Job title
                        </Table.HeadCell>
                        <Table.HeadCell>
                            Team
                        </Table.HeadCell>
                        <Table.HeadCell>
                            Status
                        </Table.HeadCell>
                        <Table.HeadCell>
                            Hiring manager
                        </Table.HeadCell>
                    </Table.Head>
                    {props.jobs.map((job) =>
                        <Job
                            key={job.id}
                            id={job.id}
                            title={job.title}
                            originalSourceId={job.originalSourceId}
                            managerSourceId={job.managerSourceId}
                            status={job.status}
                            team={job.team}
                            hiringManagerEmail={job.hiringManagerEmail}
                            madeHire={job.madeHire}
                        />
                    )}
                </Table>
            </Card>
        </div>
    );
}

export default JobList
