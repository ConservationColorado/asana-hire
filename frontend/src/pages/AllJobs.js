import JobList from "../components/jobs/JobList";
import {useState} from "react";
import {Spinner} from "flowbite-react";

function AllJobsPage() {
    const [isLoading, setIsLoading] = useState(true);
    const [loadedJobs, setLoadedJobs] = useState([]);
    fetch("http://localhost:8080/jobs")
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            setIsLoading(false)
            setLoadedJobs(data)
        });

    if (isLoading) {
        return (
            <div className="text-center">
                <section>
                    <Spinner
                        aria-label="Center-aligned loading indicator"
                        size="xl"
                    />
                </section>
                <p className="pt-2">Loading...</p>
            </div>
        );
    } else {
        return (
            <div>
                <JobList jobs={loadedJobs}/>
            </div>
        )
    }
}

export default AllJobsPage
