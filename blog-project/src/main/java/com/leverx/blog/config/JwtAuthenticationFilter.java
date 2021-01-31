package com.leverx.blog.config;

import com.auth0.jwt.JWT;
import com.leverx.blog.model.User;
import com.leverx.blog.model.UserDetailsImpl;
import com.leverx.blog.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = userRepository.findByEmail(username);
        if (null == user) {
            throw new AuthenticationServiceException("failed to authenticate");
        }
        if (!user.isActive()) {
            throw new AuthenticationServiceException("inactive");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
        Authentication auth = authenticationManager.authenticate(authenticationToken);
        return auth;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        String url;
        if(failed.getMessage().equals("inactive")){
            User user = userRepository.findByEmail(request.getParameter("username"));
            url = (user.getCreatedAt().isBefore(LocalDate.now()))
                    ? "/activate/" + user.getEmail()
                    : "/login?inactive=true";
        }else{
            url = "/login?error=true";
        }
        response.sendRedirect(url);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {

        UserDetailsImpl principal = (UserDetailsImpl) authResult.getPrincipal();
        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .sign(HMAC512(JwtProperties.SECRET.getBytes()));

        request.getSession().setAttribute("token", JwtProperties.TOKEN_PREFIX + token);
        response.sendRedirect("/articles");
    }

}
