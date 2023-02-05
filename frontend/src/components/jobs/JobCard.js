import {Card, Sidebar} from "flowbite-react";
import {ArrowTopRightOnSquareIcon, BoltIcon, FolderOpenIcon} from "@heroicons/react/24/solid";
import {driveSearchString, projectLinkString, websiteJobLink} from "../../utils/LinkUtils";

function JobCard({job}) {
    return (
        <Card>
            <div className="flex justify-between">
                <h5 className="text-3xl font-bold text-gray-900 dark:text-white">
                    {job.title}
                </h5>
            </div>
            <div className="columns-2 sm:columns-3">
                <Sidebar>
                    <Sidebar.Items>
                        <Sidebar.ItemGroup>
                            <Sidebar.Collapse
                                icon={FolderOpenIcon}
                                label="Projects"
                            >
                                <Sidebar.Item
                                    href={projectLinkString(job.applicationProjectId)}
                                >
                                    Original project
                                </Sidebar.Item>
                                <Sidebar.Item
                                    href={projectLinkString(job.interviewProjectId)}
                                >
                                    Manager project
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
                                <Sidebar.Item href="#">
                                    Sync this job
                                </Sidebar.Item>
                                <Sidebar.Item href="#">
                                    Reject applicants
                                </Sidebar.Item>
                            </Sidebar.Collapse>
                        </Sidebar.ItemGroup>
                    </Sidebar.Items>
                </Sidebar>
                <Sidebar>
                    <Sidebar.Items>
                        <Sidebar.ItemGroup>
                            <Sidebar.Collapse
                                icon={ArrowTopRightOnSquareIcon}
                                label="Links"
                            >
                                <Sidebar.Item
                                    href={websiteJobLink(job.title)}
                                >
                                    Website link
                                </Sidebar.Item>
                                <Sidebar.Item
                                    href={driveSearchString(job.title)}
                                >
                                    Google Drive link
                                </Sidebar.Item>
                            </Sidebar.Collapse>
                        </Sidebar.ItemGroup>
                    </Sidebar.Items>
                </Sidebar>
            </div>
        </Card>
    );
}

export default JobCard
