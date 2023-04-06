import {useEffect, useState} from "react";
import {getApiPromise, loadingSpinner} from "../../utils/PageUtils";

function JobDropdown({selected, onDropdownChange}) {
    const [isLoading, setIsLoading] = useState(true);
    const [jobs, setJobs] = useState([]);

    useEffect(() => {
        getApiPromise("/jobs")
            .then((response) => response.json())
            .then((data) => {
                setIsLoading(false)
                setJobs(data)
            });
    }, []);

    if (isLoading) {
        return loadingSpinner()
    } else {
        return (
            <select defaultValue={selected} onChange={onDropdownChange}>
                <option disabled key={selected}>{selected}</option>
                {jobs.map((job) =>
                    <option key={job.id}>{job.title}</option>
                )}
            </select>
        );
    }
}

export default JobDropdown
