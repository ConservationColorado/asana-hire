import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import BreadcrumbNav from "../components/ui/BreadcrumbNav";
import CompleteJob from "../components/jobs/CompleteJob";
import {getJsonPromise, loadingSpinner} from "../utils/PageUtils";

function JobPage() {
    const {id} = useParams();
    const [isLoading, setIsLoading] = useState(true);
    const [loadedJob, setLoadedJob] = useState([]);

    useEffect(() => {
        getJsonPromise(`http://localhost:8080/jobs/${id}`)
            .then((data) => {
                setIsLoading(false)
                setLoadedJob(data)
            });
    }, []);

    if (isLoading) {
        return loadingSpinner();
    } else {
        return (
            <div>
                <BreadcrumbNav path={
                    new Map([
                        ["Home", "/"],
                        ["View jobs", "/jobs"],
                        [loadedJob.title, "#"]
                    ])
                }/>
                <CompleteJob job={loadedJob}/>
            </div>
        )
    }
}

export default JobPage
