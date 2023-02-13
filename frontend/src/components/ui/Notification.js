import {Toast} from 'flowbite-react';

function Notification({message}) {
    return (
        <Toast>
            {message}
            <Toast.Toggle/>
        </Toast>
    );
}

export default Notification;
