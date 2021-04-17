package com.ozeeesoftware.usermanagementportal.constant;

public class SecurityConstant {

    public static final long EXPIRATION_DATE=432000000;
    public static final String TOKEN_PREFIX="Bearer ";
    public static final String JWT_TOKEN_HEADER="Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED="Token cannot be verified";
    public static final String OZEEE_SOFTWARE_LLC="Ozeee Software,LLC";
    public static final String OZEEE_SOFTWARE_ADMINISTRATION="User Management Portal";
    public static final String AUTHORITIES="Authorities";
    public static final String FORBIDDEN_MESSAGE="You need to log in to access this page";
    public static final String ACCESS_DENIED="You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD="OPTIONS";
    public static final String[] PUBLIC_URLS={"/api/v1/user/login","/api/v1/user/register","/api/v1/user/resetpassword/**","/api/v1/user/image/**"};

}
