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

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    function handleLogin() {
        setIsAuthenticated(true);
    };

    return (
        <Layout>
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
                        element={<LoginPage onLogin={handleLogin}/>}
                    />
                )}
            </Routes>
        </Layout>
    );
}

export default App;
