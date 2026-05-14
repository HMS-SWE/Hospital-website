package com.example.backend.security;

import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // ── Public — no token needed ──────────────────────────────────────────────
    private static final List<RequestMatcher> PUBLIC_ENDPOINTS = List.of(
            new AntPathRequestMatcher("/api/auth/**"),
            new AntPathRequestMatcher("/api/specializations/**", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            // OAuth2 redirect URIs — handled internally by Spring
            new AntPathRequestMatcher("/login/oauth2/**"),
            new AntPathRequestMatcher("/oauth2/**"));

    // ── Role rules — who can access what ─────────────────────────────────────
    private static final List<RouteRoleRule> ROLE_RULES = List.of(
            new RouteRoleRule(new AntPathRequestMatcher("/api/admin/**"),
                    Set.of(Role.ADMIN)),
            new RouteRoleRule(new AntPathRequestMatcher("/api/specializations/**"),
                    Set.of(Role.ADMIN)),
            new RouteRoleRule(new AntPathRequestMatcher("/api/doctors/**"),
                    Set.of(Role.ADMIN, Role.DOCTOR, Role.PATIENT)),
            new RouteRoleRule(new AntPathRequestMatcher("/api/patients/**"),
                    Set.of(Role.ADMIN, Role.PATIENT, Role.DOCTOR)),
            new RouteRoleRule(new AntPathRequestMatcher("/api/appointments/**"),
                    Set.of(Role.ADMIN, Role.DOCTOR, Role.PATIENT)),
            new RouteRoleRule(new AntPathRequestMatcher("/api/appointments/doctor/**"),
                    Set.of(Role.ADMIN, Role.DOCTOR)),
            new RouteRoleRule(new AntPathRequestMatcher("/api/schedules/**"),
                    Set.of(Role.ADMIN, Role.DOCTOR, Role.PATIENT)),
            new RouteRoleRule(
                    new AntPathRequestMatcher("/api/appointments/*/status",
                            HttpMethod.PATCH.name()),
                    Set.of(Role.ADMIN, Role.DOCTOR)),
            new RouteRoleRule(new AntPathRequestMatcher("/api/visits/**"),
                    Set.of(Role.DOCTOR)));

    // ── Sensitive — DB is loaded to check password-change invalidation ────────
    private static final List<RequestMatcher> SENSITIVE_ROUTES = List.of(
            new AntPathRequestMatcher("/api/admin/**"),
            new AntPathRequestMatcher("/api/medical-records/**"),
            new AntPathRequestMatcher("/api/**/profile"));

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(m -> m.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Token presence
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, HttpStatus.UNAUTHORIZED,
                    "Missing or malformed Authorization header");
            return;
        }

        // 2. Token validity
        String token = authHeader.substring(7).trim();
        if (token.isEmpty() || !jwtService.validateToken(token)) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        // 3. Extract claims from token — no DB yet
        Long userId = jwtService.extractUserId(token);
        Role role = jwtService.extractRole(token);

        // 4. Role check — no DB needed
        RouteRoleRule matchedRule = ROLE_RULES.stream()
                .filter(rule -> rule.matches(request))
                .findFirst()
                .orElse(null);

        if (matchedRule != null && !matchedRule.allows(role)) {
            writeError(response, HttpStatus.FORBIDDEN, "Forbidden");
            return;
        }

        // 5. DB call — only for sensitive routes (password-change invalidation)
        User user = null;
        boolean isSensitiveRoute = SENSITIVE_ROUTES.stream()
                .anyMatch(m -> m.matches(request));

        if (isSensitiveRoute) {
            user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                writeError(response, HttpStatus.UNAUTHORIZED, "User not found");
                return;
            }
            if (user.getPasswordChangedAt() != null) {
                LocalDateTime tokenIssuedAt = jwtService.extractIssuedAt(token);
                if (user.getPasswordChangedAt().isAfter(tokenIssuedAt)) {
                    writeError(response, HttpStatus.UNAUTHORIZED,
                            "Password changed — please log in again");
                    return;
                }
            }
        }

        // 6. Stamp request attributes for downstream use
        request.setAttribute(AuthenticatedUserRequestAttributes.USER_ID, userId);
        request.setAttribute(AuthenticatedUserRequestAttributes.USER_ROLE, role);

        // 7. Set auth context
        // Principal is the full User entity on sensitive routes, userId (Long)
        // elsewhere.
        // Downstream code should use request attributes (USER_ID, USER_ROLE) for
        // simplicity, or instanceof-check the principal if the entity is needed:
        // if (authentication.getPrincipal() instanceof User user) { ... }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user != null ? user : userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response,
            HttpStatus status,
            String message) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }

    private record RouteRoleRule(RequestMatcher matcher, Set<Role> allowedRoles) {
        boolean matches(HttpServletRequest request) {
            return matcher.matches(request);
        }

        boolean allows(Role role) {
            return allowedRoles.contains(role);
        }
    }
}