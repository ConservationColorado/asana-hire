import JobDropdown from "../components/jobs/JobDropdown";
import BreadcrumbNav from "../components/ui/BreadcrumbNav";
import CompleteJob from "../components/jobs/CompleteJob";
import {useState} from "react";

function AllJobsPage(props) {
    const [loadedJob, setLoadedJob] = useState([]);

    return (
        <div>
            <BreadcrumbNav path={
                new Map([
                    ["Home", "/"],
                    ["View jobs", "/jobs"],
                    [<JobDropdown selected="Select a position" onDropdownChange={(e) => selectPosition(e)}/>, "#"]
                ])
            }/>
            {loadedJob}
        </div>
    )

    function selectPosition(e) {
        const jobId = e.target.selectedIndex
        setLoadedJob(<CompleteJob id={jobId}/>)
    }
}

export default AllJobsPage
