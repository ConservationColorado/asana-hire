import React from "react";
import {render, screen, fireEvent} from "@testing-library/react";
import AllJobsPage from "../pages/AllJobsPage";
import {renderWithContext} from "./LoginPage.test"

describe("AllJobsPage", () => {

    test("should render job dropdown and breadcrumb", async () => {
        renderWithContext(<AllJobsPage/>);
        expect(screen.getByText(/View jobs/i)).toBeInTheDocument();
        expect(screen.getByText(/Home/i)).toBeInTheDocument();
    });

});
