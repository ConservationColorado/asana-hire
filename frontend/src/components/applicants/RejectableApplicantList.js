import {Link} from 'react-router-dom';
import {useEffect, useState} from "react";
import {getJsonPromise, plainSpinner} from '../../utils/PageUtils';
import RejectableApplicant from './RejectableApplicant';
import {Button, Table} from 'flowbite-react';

function RejectableApplicantList({job, close, closeAndConfirm}) {
    const [isLoading, setIsLoading] = useState(true);
    const [rejectableApplicants, setRejectableApplicants] = useState([]);

    useEffect(() => {
        getJsonPromise(`http://localhost:8080/applicants/${job.id}/reject`)
            .then((data) => {
                setIsLoading(false)
                setRejectableApplicants(data)
            });
    }, []);

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
            <div className="space-y-5">
                <p>
                    We didn't find any applicants to release from this hiring process!
                </p>
                <p>
                    <Link to="/help#release">To learn more about the candidate release process, click here.</Link>
                </p>
                <div className="justify-center md:flex space-x-3">
                    <Button color="light" onClick={close}>Close</Button>
                </div>
            </div>
        );
    }

    function nonEmptyApplicantList(applicants) {
        return (
            <div className="space-y-5">
                <div>
                    We've found these applicants who the hiring manager is not looking to interview for this position.
                    We've excluded any candidates who we've already interviewed.
                </div>
                <div className="justify-center overflow-hidden md:flex max-h-64">
                    <Table striped={true}>
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
                <div>
                    <strong>⚠️ Caution:</strong> You can't reverse or stop this process once it's started!
                    Please ensure that you <em>really</em> want to release all these applicants.
                </div>
                <div>
                    <Link to="/help#release">To learn more about the candidate release process, click here.</Link>
                </div>
                <div className="justify-center md:flex space-x-3">
                    <Button color="failure" onClick={function () {
                        rejectAll(applicants)
                    }}>I understand, please continue.</Button>
                    <Button color="light" onClick={close}>Cancel</Button>
                </div>
            </div>
        );
    }

    function rejectAll(applicants) {
        const options = {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(applicants)
        };
        fetch(`http://localhost:8080/applicants/reject-all`, options)
        closeAndConfirm();
    }

}

export default RejectableApplicantList
