package com.ozeeesoftware.usermanagementportal.filter;

import com.ozeeesoftware.usermanagementportal.constant.SecurityConstant;
import com.ozeeesoftware.usermanagementportal.utility.JWTTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.ozeeesoftware.usermanagementportal.constant.SecurityConstant.*;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JWTTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if(httpServletRequest.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)){
            httpServletResponse.setStatus(HttpStatus.OK.value());
        }else{
            String authorizationHeader=httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if(authorizationHeader==null||!authorizationHeader.startsWith(TOKEN_PREFIX)){
                filterChain.doFilter(httpServletRequest,httpServletResponse);
                return;
            }
            String token=authorizationHeader.substring(TOKEN_PREFIX.length());
            String username=jwtTokenProvider.getSubject(token);
            if(jwtTokenProvider.isTokenValid(username,token)&& SecurityContextHolder.getContext().getAuthentication()==null){
                List<GrantedAuthority> authorities=jwtTokenProvider.getAuthorities(token);
                Authentication authentication=jwtTokenProvider.getAuthentication(username,authorities,httpServletRequest);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
