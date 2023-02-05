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

export function getJsonPromise(url) {
    return fetch(
        url,
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }
    ).then((response) => response.json())
}
