import React, {createContext, useEffect, useState} from "react";
import {getApiPromise, postApiPromise} from '../../utils/PageUtils'
import {API_URL} from "../../config/constants"

export const AuthContext = createContext();

function AuthProvider({children}) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [credential, setCredential] = useState(null);

    useEffect(() => {
        getApiPromise('/user/me')
            .then((response) => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error("User is not authenticated");
                }
            })
            .then((data) => {
                loginSuccess(data);
            })
            .catch((error) => {
                destroySession();
            });
    }, []);

    function oauthLogin(provider) {
        let uri = API_URL + '/oauth2/authorization/' + provider;
        window.location.replace(uri);
    }

    function logout() {
        postApiPromise('/oauth2/logout');
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

    return (
        <AuthContext.Provider value={{isAuthenticated, credential, oauthLogin, logout}}>
            {children}
        </AuthContext.Provider>
    );
}

export default AuthProvider;
