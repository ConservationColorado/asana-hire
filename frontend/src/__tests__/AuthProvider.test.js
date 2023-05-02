import '@testing-library/jest-dom';
import React from "react";
import {render, act} from "@testing-library/react";
import AuthProvider, {AuthContext} from "../components/oauth/AuthProvider";

describe("AuthProvider", () => {

    let location = window.location;
    beforeAll(() => {
        delete window.location;
        window.location = {...location, replace: jest.fn()};
    });

    test("should render children", () => {
        const {getByText} = render(
            <AuthProvider>
                <div>Test</div>
            </AuthProvider>
        );
        expect(getByText("Test")).toBeInTheDocument();
    });

    test("should provide authentication context", async () => {
        const {getByTestId} = render(
            <AuthProvider>
                <AuthContext.Consumer>
                    {({isAuthenticated}) => (
                        <div data-testid="is-authenticated">{isAuthenticated.toString()}</div>
                    )}
                </AuthContext.Consumer>
            </AuthProvider>
        );
        expect(getByTestId("is-authenticated").textContent).toBe("false");
    });

    test("should provide login and logout functions", () => {
        const {getByTestId} = render(
            <AuthProvider>
                <AuthContext.Consumer>
                    {({oauthLogin, logout}) => (
                        <div>
                            <button onClick={() => oauthLogin("google")} data-testid="login-button">
                                Login
                            </button>
                            <button onClick={logout} data-testid="logout-button">
                                Logout
                            </button>
                        </div>
                    )}
                </AuthContext.Consumer>
            </AuthProvider>
        );

        const loginButton = getByTestId("login-button");
        const logoutButton = getByTestId("logout-button");

        expect(window.location.replace).not.toHaveBeenCalled();

        // Click the login button and verify the window.location.replace method was called
        act(() => {
            loginButton.click();
        });
        expect(window.location.replace).toHaveBeenCalledWith(
            "http://127.0.0.1:8080/oauth2/authorization/google"
        );

        // Click the logout button and verify the window.location.replace method was called
        act(() => {
            logoutButton.click();
        });
        expect(window.location.replace).toHaveBeenCalled();
    });

});
