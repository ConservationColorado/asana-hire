import React, {createContext, useState} from "react";
import {GoogleOAuthProvider} from '@react-oauth/google';

export const AuthContext = createContext();

function AuthProvider({children}) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [credential, setCredential] = useState("");

    function login(credentialResponse) {
        setIsAuthenticated(true);
        setCredential(credentialResponse);
        // POST credentialResponse to http://localhost:8080/login
        // receive back a refresh token and store it in cookies (with HttpOnly and Secure flags)
    }

    function logout() {
        setIsAuthenticated(false);
        setCredential("");
        // POST refresh token to http://localhost:8080/logout
        // destroy the refresh token cookie
    }

    return (
        <AuthContext.Provider value={{isAuthenticated, credential, login, logout}}>
            <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_CLIENT_ID}>
                {children}
            </GoogleOAuthProvider>
        </AuthContext.Provider>
    );
}

export default AuthProvider;
