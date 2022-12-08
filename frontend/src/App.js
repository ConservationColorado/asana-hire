import React from 'react';
import {
    Routes,
    Route,
} from 'react-router-dom';

import AllJobsPage from "./pages/AllJobs";
import NewJobPage from "./pages/NewJob";
import FavoritesPage from "./pages/Favorites";
import MainNavigation from "./components/layout/MainNavigation";

function App() {
    return (
        <div>
            <MainNavigation/>
            <Routes>
                <Route path='/' element={<AllJobsPage/>}/>
                <Route path='/new' element={<NewJobPage/>}/>
                <Route path='/favorites' element={<FavoritesPage/>}/>
            </Routes>
        </div>
    );
}

export default App
