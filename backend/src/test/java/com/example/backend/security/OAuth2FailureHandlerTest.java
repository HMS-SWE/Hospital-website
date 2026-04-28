package com.example.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2FailureHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;

    @InjectMocks
    private OAuth2FailureHandler oAuth2FailureHandler;

    @Test
    void onAuthenticationFailure_shouldRedirectToFrontendWithError() throws Exception {
        when(exception.getMessage()).thenReturn("access_denied");

        oAuth2FailureHandler.onAuthenticationFailure(request, response, exception);

        verify(response).sendRedirect("http://localhost:3000/login?error=access_denied");
    }

    @Test
    void onAuthenticationFailure_shouldRedirectWithInvalidToken() throws Exception {
        when(exception.getMessage()).thenReturn("invalid_token");

        oAuth2FailureHandler.onAuthenticationFailure(request, response, exception);

        verify(response).sendRedirect("http://localhost:3000/login?error=invalid_token");
    }

    @Test
    void onAuthenticationFailure_shouldRedirectWithExpiredSession() throws Exception {
        when(exception.getMessage()).thenReturn("session_expired");

        oAuth2FailureHandler.onAuthenticationFailure(request, response, exception);

        verify(response).sendRedirect("http://localhost:3000/login?error=session_expired");
    }
}