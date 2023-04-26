import React from 'react';
import {render, fireEvent} from '@testing-library/react';
import GoogleLoginButton from '../components/oauth/GoogleLoginButton';
import {AuthContext} from '../components/oauth/AuthProvider';

describe('GoogleLoginButton', () => {
    test('clicking the button calls the oauthLogin function from AuthContext', () => {
        const oauthLogin = jest.fn();
        const authValue = {
            oauthLogin: oauthLogin,
        };

        const {getByTestId} = render(
            <AuthContext.Provider value={authValue}>
                <GoogleLoginButton/>
            </AuthContext.Provider>
        );

        const googleLoginButton = getByTestId('google-login-button');
        fireEvent.click(googleLoginButton);

        expect(oauthLogin).toHaveBeenCalledWith('google');
    });
});
