import {Spinner} from "flowbite-react";

export function loadingSpinner() {
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
