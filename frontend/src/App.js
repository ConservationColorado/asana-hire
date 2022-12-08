import React from 'react';
import {
    Routes,
    Route,
} from 'react-router-dom';

import AllJobsPage from "./pages/AllJobs";
import MainNavigation from "./components/layout/MainNavigation";
import JobPage from "./pages/JobPage";
import ManageJobsPage from "./pages/ManageJobsPage";
import SettingsPage from "./pages/SettingsPage";
import HelpPage from "./pages/HelpPage";

function App() {
    return (
        <div>
            <MainNavigation/>
            <Routes>
                <Route
                    path='/'
                    element={<AllJobsPage/>}
                />
                <Route
                    path='/jobs'
                    element={<JobPage/>}
                />
                <Route
                    path='/manage'
                    element={<ManageJobsPage/>}
                />
                <Route
                    path='/settings'
                    element={<SettingsPage/>}
                />
                <Route
                    path='/help'
                    element={<HelpPage/>}
                />
            </Routes>
        </div>
    );
}

export default App
