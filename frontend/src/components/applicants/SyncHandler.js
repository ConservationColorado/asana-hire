import React, {useEffect, useState} from "react";
import Notification from '../ui/Notification';
import {putApiPromise, spinner} from '../../utils/PageUtils';

function SyncHandler({job}) {
    const [isSyncing, setIsSyncing] = useState(false);
    const [lastResponse, setLastResponse] = useState(null);

    useEffect(() => {
        let interval;
        if (isSyncing) {
            interval = setInterval(() => {
                putApiPromise('/sync/start/' + job.id)
                    .then(response => response.json())
                    .then(data => {
                        if (lastResponse !== null && JSON.stringify(data) !== JSON.stringify(lastResponse)) {
                            finishSyncing(data)
                        }
                    })
                    .catch(error => console.error(error));
            }, 1000);
        }
        return () => clearInterval(interval);
    }, [job.id, lastResponse, isSyncing]);

    function startSyncing() {
        setIsSyncing(true);
    }

    function finishSyncing(data) {
        setLastResponse(data);
        setIsSyncing(false);
    }

    function notification(message) {
        return (
            <div className="toast">
                <Notification message={message}/>
            </div>
        );
    }

    function showSyncNotStarted() {
        return (
            <div onClick={startSyncing}>Sync this job</div>
        );
    }

    function showSyncInProgress() {
        return (
            <div>
                <div className="disabled">Sync in progress</div>
                {notification(spinner(`️ Started ${job.title} sync`, 'sm'))}
            </div>
        );
    }

    function showSyncComplete() {
        return (
            <div>
                <div className="disabled">Sync complete!</div>
                {notification(`🗸 Finished ${job.title} sync`)}
            </div>
        );
    }

    return (
        <div>
            {isSyncing
                ? showSyncInProgress()
                : lastResponse
                    ? showSyncComplete()
                    : showSyncNotStarted()
            }
        </div>
    );
}

export default SyncHandler
