import {Link} from 'react-router-dom';
import {useEffect, useState} from "react";
import {getJsonPromise} from '../../utils/PageUtils';
import {plainSpinner} from '../../utils/PageUtils';
import RejectableApplicant from './RejectableApplicant';
import {Table} from 'flowbite-react';
import {ExclamationTriangleIcon} from '@heroicons/react/24/solid';

function RejectableApplicantList({job}) {
    const [isLoading, setIsLoading] = useState(true);
    const [rejectableApplicants, setRejectableApplicants] = useState([]);

    useEffect(() => {
        getJsonPromise(`http://localhost:8080/applicants/${job.id}/reject`)
            .then((data) => {
                setIsLoading(false)
                setRejectableApplicants(data)
            });
    }, [job.id]);

    if (isLoading) {
        return loadingBody();
    } else {
        return (
            <div>
                {mapRejectableApplicants(rejectableApplicants)}
            </div>
        );
    }

    function loadingBody() {
        return (
            <div className="space-y-6">
                <div className="text-gray-500 dark:text-gray-400">
                    {plainSpinner("Hold on, we're searching for applicants...")}
                </div>
            </div>
        );
    }

    function mapRejectableApplicants(applicants) {
        if (!Array.isArray(applicants) || !applicants.length) {
            return emptyApplicantList();
        } else {
            return nonEmptyApplicantList(applicants);
        }
    }

    function emptyApplicantList() {
        return (
            <div>
                No applicants found!
            </div>
        );
    }

    function nonEmptyApplicantList(applicants) {
        return (
            <div className="space-y-4">
                <div>
                    We've found these applicants who the hiring manager is not looking to interview for this position.
                    We've excluded any candidates who we've already interviewed.
                </div>
                <div>
                    <Link to="/help#release">To learn more about the candidate release process, click here.</Link>
                </div>
                <div className="md:flex h-screen overflow-hidden max-h-64">
                    <Table striped={true} className="">
                        <Table.Head className="fixed-table-head">
                            <Table.HeadCell>
                                Full name
                            </Table.HeadCell>
                            <Table.HeadCell>
                                Name
                            </Table.HeadCell>
                            <Table.HeadCell>
                                Email
                            </Table.HeadCell>
                        </Table.Head>
                        <Table.Body>
                            {applicants.map((applicant) =>
                                <RejectableApplicant key={applicant.email} applicant={applicant}/>
                            )}
                        </Table.Body>
                    </Table>
                </div>
                <div className="pt-3 grid grid-cols-12">
                    <ExclamationTriangleIcon fill="#DC4C64"/>
                    <div className="pl-2 col-span-10">
                        <strong>Caution:</strong> You can not reverse or stop this process once it's started!
                        Please ensure that you <em>really</em> want to release all these applicants.
                    </div>
                </div>
            </div>
        );
    }
}

export default RejectableApplicantList
