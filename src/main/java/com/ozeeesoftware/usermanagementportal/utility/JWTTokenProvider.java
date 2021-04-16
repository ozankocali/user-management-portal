package com.ozeeesoftware.usermanagementportal.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ozeeesoftware.usermanagementportal.constant.SecurityConstant;
import com.ozeeesoftware.usermanagementportal.model.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generateJwtToken(UserPrincipal userPrincipal){
        String[] claims=getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(SecurityConstant.OZEEE_SOFTWARE_LLC).withAudience(SecurityConstant.OZEEE_SOFTWARE_ADMINISTRATION)
                .withIssuedAt(new Date()).withSubject(userPrincipal.getUsername()).withArrayClaim(SecurityConstant.AUTHORITIES,claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+SecurityConstant.EXPIRATION_DATE)).sign(Algorithm.HMAC512(secret.getBytes()));
    }


    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims=getClaimsFromToken(token);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }


    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(username,null,authorities);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }

    public boolean isTokenValid(String username,String token){
        JWTVerifier jwtVerifier=getJWTVerifier();
        return StringUtils .isNotEmpty(username)&& !isTokenExpired(jwtVerifier,token);
    }

    public String getSubject(String token){
        JWTVerifier jwtVerifier=getJWTVerifier();
        return jwtVerifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier jwtVerifier, String token) {

        Date expiration=jwtVerifier.verify(token).getExpiresAt();
        return expiration.before(new Date());

    }


    private String[] getClaimsFromToken(String token) {
        JWTVerifier jwtVerifier= getJWTVerifier();
        return jwtVerifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier jwtVerifier;
        try {
            Algorithm algorithm=Algorithm.HMAC512(secret);
            jwtVerifier=JWT.require(algorithm).withIssuer(SecurityConstant.OZEEE_SOFTWARE_LLC).build();
        }catch (JWTVerificationException exception){

            throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);

        }
        return jwtVerifier;
    }


    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {

        List<String> authorities=new ArrayList<>();
        for(GrantedAuthority grantedAuthority:userPrincipal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }

}
