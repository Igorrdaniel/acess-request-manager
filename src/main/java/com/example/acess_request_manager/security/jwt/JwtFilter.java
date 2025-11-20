package com.example.acess_request_manager.security.jwt;

import com.example.acess_request_manager.security.jwt.impl.UserDetailsImpl;
import com.example.acess_request_manager.security.jwt.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtConfig jwtConfig;
  private final UserDetailsServiceImpl userDetailsService;

  public JwtFilter(JwtConfig jwtConfig, UserDetailsServiceImpl userDetailsService) {
    this.jwtConfig = jwtConfig;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String token = authorizationHeader.substring(7);
      String username = jwtConfig.extractUsername(token);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetailsImpl userDetails =
            (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

        if (jwtConfig.isTokenValid(token, userDetails)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
