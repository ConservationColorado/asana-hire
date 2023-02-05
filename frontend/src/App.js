import React from 'react';
import {Route, Routes,} from 'react-router-dom';

import SettingsPage from "./pages/SettingsPage";
import HelpPage from "./pages/HelpPage";
import Layout from "./components/ui/Layout";
import HomePage from "./pages/HomePage";
import AllJobsPage from "./pages/AllJobsPage";
import NotFound from "./components/ui/NotFound";
import NewJobPage from "./pages/NewJobPage";

function App() {
    return (
        <Layout>
            <Routes>
                <Route
                    path="/"
                    element={<HomePage/>}
                />
                <Route
                    path="/jobs"
                    element={<AllJobsPage/>}
                />
                <Route
                    path="/jobs/new"
                    element={<NewJobPage/>}
                />
                <Route
                    path="/settings"
                    element={<SettingsPage/>}
                />
                <Route
                    path="/help"
                    element={<HelpPage/>}
                />
                <Route
                    path="*"
                    element={<NotFound/>}
                />
            </Routes>
        </Layout>
    );
}

export default App
