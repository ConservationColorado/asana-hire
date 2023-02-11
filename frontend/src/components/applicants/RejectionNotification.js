import {Toast} from 'flowbite-react';

function RejectionNotification({job}) {
    return (
        <Toast>
            <div className="">
                Started releasing applicants for {job.title}.
            </div>
            <Toast.Toggle />
        </Toast>
    );
}

export default RejectionNotification;
