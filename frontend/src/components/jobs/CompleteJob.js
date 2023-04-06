import {useEffect, useState} from "react";
import {getApiPromise, loadingSpinner} from "../../utils/PageUtils";
import JobCard from "../../components/jobs/JobCard";

function CompleteJob({id}) {
    const [isLoading, setIsLoading] = useState(true);
    const [job, setJob] = useState([]);

    useEffect(() => {
        getApiPromise(`/jobs/${id}`)
            .then((response) => response.json())
            .then((data) => {
                setIsLoading(false)
                setJob(data)
            });
    }, [id]);

    if (isLoading) {
        return loadingSpinner();
    } else {
        return <JobCard job={job}/>;
    }
}

export default CompleteJob
