package com.leverx.blog.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.leverx.blog.model.User;
import com.leverx.blog.model.UserDetailsImpl;
import com.leverx.blog.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        try {
            String token = (String) request.getSession().getAttribute("token");
            if (token == null || !token.startsWith(JwtProperties.TOKEN_PREFIX)) {
                chain.doFilter(request, response);
                return;
            }
            Authentication authentication = getUsernamePasswordAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (
                TokenExpiredException e) {
            response.sendRedirect("/logout");
        }
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        response.sendRedirect("/login");
    }

    private Authentication getUsernamePasswordAuthentication(HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("token");
        if (token != null) {
            String userName = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()))
                    .build()
                    .verify(token.replace(JwtProperties.TOKEN_PREFIX, ""))
                    .getSubject();

            if (userName != null) {
                User user = userRepository.findByEmail(userName);
                UserDetailsImpl principal = new UserDetailsImpl(user);
                return new UsernamePasswordAuthenticationToken(userName, null, principal.getAuthorities());
            }
            return null;
        }
        return null;
    }
}
