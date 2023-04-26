import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import LoginPage from '../pages/LoginPage';
import AuthProvider from '../components/oauth/AuthProvider';

describe('LoginPage', () => {
    it('renders without crashing', () => {
        renderWithContext(<LoginPage/>)
    });

    it('displays the page heading', () => {
        const {getByText} = renderWithContext(<LoginPage/>)
        expect(getByText('Job Management Console')).toBeInTheDocument();
    });

    it('displays the page description', () => {
        const {getByText} = renderWithContext(<LoginPage/>)
        expect(getByText('To continue, log with an authorized Google account.')).toBeInTheDocument();
    });

    it('renders the Google login button', () => {
        const {getByTestId} = renderWithContext(<LoginPage/>)
        expect(getByTestId("google-login-button")).toBeInTheDocument();
    });
});

export const renderWithContext = (component) => {
    return render(
        <MemoryRouter>
            <AuthProvider>
                {component}
            </AuthProvider>
        </MemoryRouter>
    );
}
