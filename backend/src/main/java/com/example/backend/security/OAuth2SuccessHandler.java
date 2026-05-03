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

            String token = jwtService.generateToken(user.getId(), user.getRole());
            log.info("JWT generated successfully for user: {}", email);

            response.sendRedirect("http://localhost:3000/oauth2/callback?token=" + token
                + "&name=" + oidcUser.getFullName()
                + "&image=" + oidcUser.getPicture()
            );

        } catch (Exception e) {
            log.error("Error in OAuth2 success handler: {}", e.getMessage());
            response.sendRedirect("http://localhost:3000/login?error=Authentication failed");
        }
    }
}