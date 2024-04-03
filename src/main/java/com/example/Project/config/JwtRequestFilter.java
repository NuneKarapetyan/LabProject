package com.example.Project.config;

import com.example.Project.repo.TokenBlackListRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter
{

    private final JwtUserDetailService jwtUserDetailsService;

    private final JwtTokUtil jwtTokenUtil;

    private final TokenBlackListRepository blackListRepository;

    @Autowired
    public JwtRequestFilter(
        JwtUserDetailService jwtUserDetailsService,
        JwtTokUtil jwtTokenUtil, TokenBlackListRepository blackListRepository
    )
    {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.blackListRepository = blackListRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException
    {

        String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer "))
        {
            jwtToken = requestTokenHeader.substring(7);
            System.out.println(jwtToken);
            System.out.println(jwtToken);
            System.out.println(jwtToken);
            if (blackListRepository.existsByToken(jwtToken))
            {
                System.out.println("session is deleted");
                throw new ServletException("session was deleted please login again");
            }

            try
            {
                username = jwtTokenUtil.extractUsername(jwtToken);
            }
            catch (IllegalArgumentException e)
            {
                System.out.println("Unable to get JWT Token");
            }
            catch (ExpiredJwtException e)
            {
                System.out.println("JWT Token has expired");
            }
        } else
        {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        //Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {

            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // if token is valid configure Spring Security to manually set authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails))
            {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}

