import {Card, Dropdown} from "flowbite-react"
import {FolderOpenIcon} from "@heroicons/react/24/solid";
import {projectLinkString} from "../../utils/LinkUtils";

function JobList(props) {
    return (
        <div>
            <Card>
                <Dropdown
                    icon={FolderOpenIcon}
                    label="Select a position"
                    placement="right-start"
                    inline={true}
                >
                    {props.jobs.map((job) =>
                        <Dropdown.Item
                            href={projectLinkString(job.applicationProjectId)}
                            target="_blank"
                        >
                            {job.title}
                        </Dropdown.Item>
                    )}
                </Dropdown>
            </Card>
        </div>
    );
}

export default JobList
