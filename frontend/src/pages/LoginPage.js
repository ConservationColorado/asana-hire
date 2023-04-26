import React from 'react';
import {Card} from "flowbite-react";
import GoogleLoginButton from "../components/oauth/GoogleLoginButton"

function LoginPage() {
    return (
        <Card>
            <div className="grid px-4 py-8 mx-auto lg:gap-8 xl:gap-0 lg:py-16 lg:grid-cols-12">
                <div className="mr-auto place-self-center lg:col-span-10">
                    <h1 className="mb-2 font-extrabold tracking-tight sm:text-2xl md:text-3xl xl:text-4xl">
                        Job Management Console
                    </h1>
                    <p className="max-w-2xl mb-4 font-light text-gray-500 lg:mb-8 md:text-lg lg:text-xl">
                        To continue, log with an authorized Google account.
                    </p>
                    <GoogleLoginButton/>
                </div>
            </div>
        </Card>
    );
}

export default LoginPage;
