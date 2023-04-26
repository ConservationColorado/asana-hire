import {Button, Card} from "flowbite-react";
import {ArrowRightIcon} from "@heroicons/react/24/solid";
import {Link} from 'react-router-dom';

function HomePage() {
    return (
        <section className="bg-white dark:bg-gray-900">
            <Card>
                <div className="grid max-w-screen-xl px-4 py-8 mx-auto lg:gap-8 xl:gap-0 lg:py-16 lg:grid-cols-12">
                    <div className="mr-auto place-self-center lg:col-span-7">
                        <h1 className="max-w-2xl mb-4 text-4xl font-extrabold tracking-tight leading-none md:text-5xl xl:text-6xl dark:text-white">
                            Manage our jobs <br/> in just a few clicks
                        </h1>
                        <p className="max-w-2xl mb-6 font-light text-gray-500 lg:mb-8 md:text-lg lg:text-xl dark:text-gray-400">
                            Easily manage and automate our positions we're hiring for. <br/> Designed to make your life
                            easy, start to finish!
                        </p>
                        <Link
                            to="/jobs"
                        >
                            <Button
                                className="inline-flex px-5 py-1 text-white rounded-lg bg-primary-700 hover:bg-primary-800 focus:ring-4 focus:ring-primary-300 dark:focus:ring-primary-900">
                                Get started
                                <ArrowRightIcon className="h-6 w-10"/>
                            </Button>
                        </Link>
                    </div>
                </div>
            </Card>
        </section>
    );
}

export default HomePage;
