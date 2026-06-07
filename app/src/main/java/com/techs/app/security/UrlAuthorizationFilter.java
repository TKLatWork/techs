package com.techs.app.security;

import com.techs.domain.userauth.service.AuthorizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class UrlAuthorizationFilter extends OncePerRequestFilter {

    private final AuthorizationService authorizationService;

    public UrlAuthorizationFilter(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = (String) auth.getPrincipal();
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String action = "GET".equalsIgnoreCase(method) ? "READ" : "WRITE";
        String rightName = action.toLowerCase() + ":" + path;

        if (authorizationService.hasRight(userId, rightName)) {
            filterChain.doFilter(request, response);
            return;
        }

        String baseRightName = action.toLowerCase() + ":" + getBasePath(path);
        if (authorizationService.hasRight(userId, baseRightName)) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getBasePath(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            return "/" + parts[1] + "/" + parts[2];
        }
        return path;
    }
}
