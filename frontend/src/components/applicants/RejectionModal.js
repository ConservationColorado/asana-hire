import React, {useState} from 'react'
import {Button, Modal} from 'flowbite-react'
import RejectableApplicantList from '../applicants/RejectableApplicantList'
import Notification from '../ui/Notification';

function RejectionModal({job}) {
    const [isModalShown, setIsModalShown] = useState();
    const [isToastShown, setIsToastShown] = useState();
    const [hasRun, setHasRun] = useState();

    return (
        <div>
            {!hasRun
                ? <div onClick={toggleModal}>Release applicants</div>
                : <div className="disabled">Applicants released!</div>
            }
            <Modal
                show={isModalShown}
                onClose={toggleModal}
            >
                <Modal.Header>
                    Release candidates from the {job.title} hiring process
                </Modal.Header>
                <Modal.Body>
                    {isModalShown &&
                        <RejectableApplicantList job={job} close={toggleModal} closeAndConfirm={toggleAndReject}/>
                    }
                </Modal.Body>
            </Modal>
            <div className="toast">
                {isToastShown && <Notification message={`✔️ Released ${job.title}`}/>}
            </div>
        </div>
    );

    function toggleModal() {
        setIsModalShown(!isModalShown);
    }

    function toggleAndReject() {
        toggleModal();
        setIsToastShown(true)
        setHasRun(true)
    }

}

export default RejectionModal
