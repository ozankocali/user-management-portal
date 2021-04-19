package com.ozeeesoftware.usermanagementportal.controller;

import com.ozeeesoftware.usermanagementportal.constant.SecurityConstant;
import com.ozeeesoftware.usermanagementportal.exception.ExceptionHandling;
import com.ozeeesoftware.usermanagementportal.exception.model.EmailExistsException;
import com.ozeeesoftware.usermanagementportal.exception.model.UserNotFoundException;
import com.ozeeesoftware.usermanagementportal.exception.model.UsernameExistsException;
import com.ozeeesoftware.usermanagementportal.model.User;
import com.ozeeesoftware.usermanagementportal.model.UserPrincipal;
import com.ozeeesoftware.usermanagementportal.service.UserService;
import com.ozeeesoftware.usermanagementportal.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/","/api/v1/user"})
public class UserController extends ExceptionHandling {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UsernameExistsException, UserNotFoundException, EmailExistsException {

        User newUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
        return new ResponseEntity<User>(newUser, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user){

        authenticate(user.getUsername(),user.getPassword());

        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal=new UserPrincipal(loginUser);
        HttpHeaders jwtHeader=getJwtHeader(userPrincipal);
        return new ResponseEntity<User>(loginUser,jwtHeader, HttpStatus.OK);

    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add(SecurityConstant.JWT_TOKEN_HEADER,jwtTokenProvider.generateJwtToken(userPrincipal));
        return httpHeaders;

    }

    private void authenticate(String username, String password) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));

    }

}
