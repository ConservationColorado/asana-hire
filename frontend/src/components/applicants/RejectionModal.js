import React, {Fragment, useState} from 'react'
import {Button, Modal} from 'flowbite-react'
import RejectableApplicantList from '../applicants/RejectableApplicantList'

function RejectionModal({job}) {
    const [isShown, setIsShown] = useState();
    return (
        <div>
            <div onClick={onClick}>
                Reject applicants
            </div>
            <Modal
                show={isShown}
                onClose={onClick}
            >
                <Modal.Header>
                    Release candidates from the {job.title} hiring process
                </Modal.Header>
                <Modal.Body>
                    <RejectableApplicantList job={job}/>
                </Modal.Body>
            </Modal>
        </div>
    );

    function onClick() {
        setIsShown(!isShown)
    }

    function onClose() {
        // exit process
    }
}

export default RejectionModal
