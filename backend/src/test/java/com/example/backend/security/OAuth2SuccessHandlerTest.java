package com.example.backend.security;

import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OidcUser oidcUser;

    @InjectMocks
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Test
    void onAuthenticationSuccess_shouldRedirectWithTokensRoleAndExpiry_whenUserExists() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .role(Role.PATIENT)
                .build();

        long fakeExpiry = System.currentTimeMillis() + 3_600_000L;

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(1L, Role.PATIENT)).thenReturn("mock-access-token");
        when(jwtService.generateRefreshToken(1L, Role.PATIENT)).thenReturn("mock-refresh-token");
        when(jwtService.extractExpiration("mock-access-token")).thenReturn(fakeExpiry);

        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect(startsWith(
                "http://localhost:3000/oauth2/callback?accessToken=mock-access-token" +
                        "&refreshToken=mock-refresh-token" +
                        "&role=PATIENT" +
                        "&expiresIn="
        ));
    }

    @Test
    void onAuthenticationSuccess_shouldRedirectWithError_whenUserNotFound() throws Exception {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("notfound@gmail.com");
        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("http://localhost:3000/login?error=Authentication failed");
    }
}
