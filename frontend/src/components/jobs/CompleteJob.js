import {Card, Sidebar} from "flowbite-react";
import {ArrowTopRightOnSquareIcon, BoltIcon, ChatBubbleBottomCenterIcon} from "@heroicons/react/24/solid";
import {FolderOpenIcon} from "@heroicons/react/24/solid";
import {driveSearchString, emailLinkString, projectLinkString, taskLinkString} from "../../utils/LinkUtils";

function CompleteJob(props) {
    const job = props.job;
    const status = job.status;
    return (
        <Card>
            <div className="flex justify-between">
                <h5 className="text-3xl font-bold text-gray-900 dark:text-white">
                    {job.title}
                </h5>
                <a href={taskLinkString(job.id)} target="_blank">
                    <ArrowTopRightOnSquareIcon className="h-6 w-8"/>
                </a>
            </div>
            <p className="text-base text-gray-500 dark:text-gray-400 sm:text-lg">
                This position on our {job.team} team is <em>{status}.</em>
            </p>
            <div className="columns-2 sm:columns-3">
                <Sidebar>
                    <Sidebar.Items>
                        <Sidebar.ItemGroup>
                            <Sidebar.Collapse
                                icon={ChatBubbleBottomCenterIcon}
                                label="Updates"
                            >
                                <Sidebar.Item
                                    href={projectLinkString(job.managerSourceId) + "/progress"}
                                    target="_blank"
                                >
                                    Send panel an update
                                </Sidebar.Item>
                                <Sidebar.Item
                                    href={emailLinkString(job.hiringManagerEmail)}
                                    target="_blank"
                                >
                                    Email hiring manager
                                </Sidebar.Item>
                            </Sidebar.Collapse>
                        </Sidebar.ItemGroup>
                    </Sidebar.Items>
                </Sidebar>
                <Sidebar>
                    <Sidebar.Items>
                        <Sidebar.ItemGroup>
                            <Sidebar.Collapse
                                icon={FolderOpenIcon}
                                label="Resources"
                            >
                                <Sidebar.Item
                                    href={projectLinkString(job.originalSourceId)}
                                    target="_blank"
                                >
                                    Original project
                                </Sidebar.Item>
                                <Sidebar.Item
                                    href={projectLinkString(job.managerSourceId)}
                                    target="_blank"
                                >
                                    Manager project
                                </Sidebar.Item>
                                <Sidebar.Item
                                    href={driveSearchString(job.title)}
                                    target="_blank"
                                >
                                    Google Drive link
                                </Sidebar.Item>
                            </Sidebar.Collapse>
                        </Sidebar.ItemGroup>
                    </Sidebar.Items>
                </Sidebar>
                <Sidebar>
                    <Sidebar.Items>
                        <Sidebar.ItemGroup>
                            <Sidebar.Collapse
                                icon={BoltIcon}
                                label="Actions"
                            >
                                <Sidebar.Item
                                    href="#"
                                >
                                    Sync this job
                                </Sidebar.Item>
                                <Sidebar.Item
                                    href="#"
                                >
                                    Reject applicants
                                </Sidebar.Item>
                            </Sidebar.Collapse>
                        </Sidebar.ItemGroup>
                    </Sidebar.Items>
                </Sidebar>
            </div>
        </Card>
    );
}

export default CompleteJob
