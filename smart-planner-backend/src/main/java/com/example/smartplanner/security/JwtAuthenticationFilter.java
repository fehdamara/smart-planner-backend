package com.example.smartplanner.security;

import com.example.smartplanner.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserPrincipalService principalService;
    private final TokenBlacklistRepository blacklistRepo;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserPrincipalService principalService,
                                   TokenBlacklistRepository blacklistRepo) {
        this.jwtService = jwtService;
        this.principalService = principalService;
        this.blacklistRepo = blacklistRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();

        try {
            Claims claims = jwtService.parse(token).getBody();

            // Only access tokens are used for endpoint authentication
            if (!jwtService.isAccessToken(claims)) {
                chain.doFilter(request, response);
                return;
            }

            // Check blacklist
            String jti = claims.getId();
            if (jti != null && blacklistRepo.existsByTokenJti(jti)) {
                chain.doFilter(request, response);
                return;
            }

            String email = claims.get("email", String.class);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = principalService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception ignored) {
            // Invalid token: no authentication set
        }

        chain.doFilter(request, response);
    }
}
