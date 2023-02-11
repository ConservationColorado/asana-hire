import React, {useState} from 'react'
import {Button, Modal} from 'flowbite-react'
import RejectableApplicantList from '../applicants/RejectableApplicantList'
import RejectionNotification from './RejectionNotification';

function RejectionModal({job}) {
    const [isShown, setIsShown] = useState();
    return (
        <div>
            <div onClick={toggleModal}>
                Reject applicants
            </div>
            <Modal
                show={isShown}
                onClose={toggleModal}
            >
                <Modal.Header>
                    Release candidates from the {job.title} hiring process
                </Modal.Header>
                <Modal.Body>
                    <RejectableApplicantList job={job} close={toggleModal} closeAndConfirm={toggleAndReject}/>
                </Modal.Body>
            </Modal>
        </div>
    );

    function toggleModal() {
        setIsShown(!isShown);
    }

    function toggleAndReject() {
        toggleModal();
        return <RejectionNotification job={job}/>
    }

}

export default RejectionModal
