package com.example.backend.security;

import com.example.backend.enums.Role;
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
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final PathPatternRequestMatcher.Builder matcher =
            PathPatternRequestMatcher.withDefaults();
    private static final List<RequestMatcher> PUBLIC_ENDPOINTS = List.of(
            // NEW:
            PathPatternRequestMatcher.withDefaults().matcher("/api/auth/**"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/specializations/**")
    );

    private static final List<RouteRoleRule> ROLE_RULES = List.of(
            new RouteRoleRule(matcher.matcher("/api/admin/**"), Set.of(Role.ADMIN)),
            new RouteRoleRule(matcher.matcher("/api/doctors/**"), Set.of(Role.ADMIN, Role.DOCTOR)),
            new RouteRoleRule(matcher.matcher("/api/patients/**"), Set.of(Role.ADMIN, Role.PATIENT)),
            new RouteRoleRule(matcher.matcher("/api/appointments/**"), Set.of(Role.ADMIN, Role.DOCTOR, Role.PATIENT)),
            new RouteRoleRule(matcher.matcher("/api/schedules/**"), Set.of(Role.ADMIN, Role.DOCTOR))
    );
    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Missing or malformed Authorization header");
            return;
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty() || !jwtService.validateToken(token)) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        Long userId = jwtService.extractUserId(token);
        Role role = jwtService.extractRole(token);

        RouteRoleRule matchedRule = ROLE_RULES.stream()
                .filter(rule -> rule.matches(request))
                .findFirst()
                .orElse(null);

        if (matchedRule != null && !matchedRule.allows(role)) {
            writeError(response, HttpStatus.FORBIDDEN, "Forbidden");
            return;
        }

        request.setAttribute(AuthenticatedUserRequestAttributes.USER_ID, userId);
        request.setAttribute(AuthenticatedUserRequestAttributes.USER_ROLE, role);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }

    private record RouteRoleRule(RequestMatcher matcher, Set<Role> allowedRoles) {

        private boolean matches(HttpServletRequest request) {
            return matcher.matches(request);
        }

        private boolean allows(Role role) {
            return allowedRoles.contains(role);
        }
    }
}
