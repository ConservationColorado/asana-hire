import {Spinner} from "flowbite-react";

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

export function getJsonPromise(url) {
    const options = {
        method: "GET",
        credentials: 'include',
        headers: {
            "Content-Type": "application/json"
        }
    }
    return fetch(url, options)
        .then((response) => response.json());
}
