package com.example.backend.security;

import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldReturn401_whenAuthorizationHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/admin/dashboard");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing or malformed Authorization header"));

        verifyNoInteractions(jwtService, userRepository);
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_shouldReturn401_whenTokenInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/admin/dashboard");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken("invalid-token")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid or expired token"));

        verify(jwtService).validateToken("invalid-token");
        verifyNoInteractions(userRepository);
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_shouldReturn401_whenTokenIsRefreshToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/admin/dashboard");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer refresh-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken("refresh-token")).thenReturn(true);
        when(jwtService.isAccessToken("refresh-token")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid access token"));

        verify(jwtService).validateToken("refresh-token");
        verify(jwtService).isAccessToken("refresh-token");
        verifyNoInteractions(userRepository);
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_shouldReturn401_whenUserNotFound() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/doctors/profile");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.isAccessToken("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn(10L);
        when(jwtService.extractRole("valid-token")).thenReturn(Role.DOCTOR);
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("User not found"));

        verify(userRepository).findById(10L);
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_shouldAuthenticateAndContinue_whenTokenValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/patients/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.isAccessToken("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn(25L);
        when(jwtService.extractRole("valid-token")).thenReturn(Role.PATIENT);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertEquals(25L, request.getAttribute(AuthenticatedUserRequestAttributes.USER_ID));
        assertEquals(Role.PATIENT, request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE));

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertInstanceOf(
                UsernamePasswordAuthenticationToken.class,
                SecurityContextHolder.getContext().getAuthentication()
        );

        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertEquals(25L, auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldNotFilter_shouldReturnTrue_forSwaggerEndpoint() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setServletPath("/swagger-ui/index.html");

        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        assertTrue(result);
    }

    @Test
    void doFilter_shouldSkipJwtValidation_forPublicAuthEndpoint() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/api/auth/login");

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userRepository);
    }
}
