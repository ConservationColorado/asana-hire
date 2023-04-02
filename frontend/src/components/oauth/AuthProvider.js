import React, {createContext, useEffect, useState} from "react";
import Cookies from 'js-cookie';

export const AuthContext = createContext();

function AuthProvider({children}) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [credential, setCredential] = useState(null);

    useEffect(() => {
        fetch('http://localhost:8080/user/me', {
            method: 'GET',
            credentials: 'include'
        })
            .then((response) => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error("User is not authenticated");
                }
            })
            .then((data) => {
                setIsAuthenticated(true);
                setCredential(data);
            })
            .catch((error) => {
                destroySession();
            });
    }, []);

    function oauthLogin(provider) {
        let uri = "http://localhost:8080/oauth2/authorization/" + provider;
        window.location.href = uri;
    }

    function formLogin() {
        const uri = "http://localhost:8080/login";
    }

    function logout() {
        fetch('http://localhost:8080/oauth2/logout', {
            method: 'POST',
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": getXSRFToken(),
            },
        })
        destroySession();
    }

    function loginSuccess(data) {
        setIsAuthenticated(true);
        setCredential(data);
    }

    function destroySession() {
        setIsAuthenticated(false);
        setCredential(null);
    }

    function getXSRFToken() {
        let csrfToken = Cookies.get('XSRF-TOKEN');
        return csrfToken;
    }

    return (
        <AuthContext.Provider value={{isAuthenticated, getXSRFToken, credential, formLogin, oauthLogin, logout}}>
            {children}
        </AuthContext.Provider>
    );
}

export default AuthProvider;
