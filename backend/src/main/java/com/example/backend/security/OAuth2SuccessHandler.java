package com.example.backend.security;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String email = oidcUser.getEmail();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));

            String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
            String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole());
            long expiresIn = (jwtService.extractExpiration(accessToken) - System.currentTimeMillis()) / 1000;

            log.info("OAuth2 login succeeded for user {}", email);

            String redirectUrl = "http://localhost:3000/oauth2/callback"
                    + "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                    + "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8)
                    + "&role=" + URLEncoder.encode(user.getRole().name(), StandardCharsets.UTF_8)
                    + "&expiresIn=" + expiresIn;

            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            log.error("Error in OAuth2 success handler: {}", e.getMessage(), e);
            response.sendRedirect("http://localhost:3000/login?error=Authentication failed");
        }
    }
}
