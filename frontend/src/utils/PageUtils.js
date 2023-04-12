import {Spinner} from "flowbite-react";
import Cookies from 'js-cookie';

export function loadingSpinner() {
    return (
        <div className="text-center pt-2">
            <section>
                <Spinner size="lg"/> Loading...
            </section>
        </div>
    );
}

export function plainSpinner(message) {
    return (
        <div className="text-center pt-1">
            <div className="pb-2"><Spinner size="xl"/></div>
            <div>{message}</div>
        </div>
    );
}

export function spinner(message, size) {
    return (
        <div className="text-center pt-1">
            <div><Spinner size={size}/>{message}</div>
        </div>
    );
}

export function getApiPromise(resource) {
    return fetchApi(
        resource,
        {
            method: "GET",
            credentials: 'include',
        }
    );
}

export function putApiPromise(resource) {
    return fetchApi(
        resource,
        {
            method: "PUT",
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": getXSRFToken()
            }
        }
    );
}

export function postApiPromise(resource) {
    return fetchApi(
        resource,
        {
            method: "POST",
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": getXSRFToken()
            }
        }
    );
}

export const API_URL = process.env.REACT_APP_API_SERVER_URL;

function fetchApi(resource, options) {
    const uri = API_URL + resource;
    return fetch(uri, options)
}

function getXSRFToken() {
    let csrfToken = Cookies.get('XSRF-TOKEN');
    return csrfToken;
}
