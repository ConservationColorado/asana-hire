import {useEffect, useState} from "react";
import {getJsonPromise, loadingSpinner} from "../../utils/PageUtils";

function JobDropdown({selected, onDropdownChange}) {
    const [isLoading, setIsLoading] = useState(true);
    const [jobs, setJobs] = useState([]);

    useEffect(() => {
        getJsonPromise("http://localhost:8080/jobs")
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
                    <option key={job.title}>{job.title}</option>
                )}
            </select>
        );
    }
}

export default JobDropdown
