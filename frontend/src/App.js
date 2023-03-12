import React, {useState} from "react";
import {Route, Routes} from 'react-router-dom';

import SettingsPage from "./pages/SettingsPage";
import HelpPage from "./pages/HelpPage";
import Layout from "./components/ui/Layout";
import HomePage from "./pages/HomePage";
import AllJobsPage from "./pages/AllJobsPage";
import NotFound from "./components/ui/NotFound";
import NewJobPage from "./pages/NewJobPage";
import LoginPage from "./pages/LoginPage";
import {GoogleOAuthProvider} from '@react-oauth/google';
import Notification from "./components/ui/Notification"

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [token, setToken] = useState("");

    function handleLoginSuccess(credentialResponse) {
        setIsAuthenticated(true);
        setToken(credentialResponse);
    }

    function handleLoginError() {
        return <Notification message="Couldn't authenticate you! Please try again."/>
    }

    return (
        <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_CLIENT_ID}>
            <Layout isAuthenticated={isAuthenticated}>
                <Routes>
                    {isAuthenticated ? (
                        <>
                            <Route path="/" element={<HomePage/>}/>
                            <Route path="/jobs" element={<AllJobsPage/>}/>
                            <Route path="/jobs/new" element={<NewJobPage/>}/>
                            <Route path="/settings" element={<SettingsPage/>}/>
                            <Route path="/help" element={<HelpPage/>}/>
                            <Route path="*" element={<NotFound/>}/>
                        </>
                    ) : (
                        <Route
                            path="/*"
                            element={<LoginPage onSuccess={handleLoginSuccess} onError={handleLoginError}/>}
                        />
                    )}
                </Routes>
            </Layout>
        </GoogleOAuthProvider>
    );
}

export default App;
