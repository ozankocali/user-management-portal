package com.ozeeesoftware.usermanagementportal.service;

import com.ozeeesoftware.usermanagementportal.constant.Role;
import com.ozeeesoftware.usermanagementportal.constant.UserImplConstant;
import com.ozeeesoftware.usermanagementportal.exception.model.EmailExistsException;
import com.ozeeesoftware.usermanagementportal.exception.model.UserNotFoundException;
import com.ozeeesoftware.usermanagementportal.exception.model.UsernameExistsException;
import com.ozeeesoftware.usermanagementportal.model.User;
import com.ozeeesoftware.usermanagementportal.model.UserPrincipal;
import com.ozeeesoftware.usermanagementportal.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ozeeesoftware.usermanagementportal.constant.Role.ROLE_USER;
import static com.ozeeesoftware.usermanagementportal.constant.UserImplConstant.*;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger LOGGER= LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private LoginAttemptService loginAttemptService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepository.findUserByUsername(username);
        if(user==null){
            LOGGER.error(NO_USER_FOUND_BY_USERNAME+username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME+username);
        }else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal=new UserPrincipal(user);
            LOGGER.info("Returning found user by username: "+username);
            return userPrincipal;
        }
    }

    private void validateLoginAttempt(User user){

        if (user.isNotLocked()){
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())){
                user.setNotLocked(false);

            }else {
                user.setNotLocked(true);
            }
        }else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }

    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws UsernameExistsException, UserNotFoundException, EmailExistsException {
        validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);

        User user=new User();
        user.setUserId(generateUserId());
        String password=generatePassword();
        String encodedPassword=encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        userRepository.save(user);
        LOGGER.info("New user password: "+password);
        return user;
    }

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_PROILE_IMAGE_PATH).toUriString();
    }

    private String encodePassword(String password) {

        return bCryptPasswordEncoder.encode(password);

    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {

        return RandomStringUtils.randomNumeric(10);

    }

    private User validateNewUsernameAndEmail(String currentUsername,String newUsername,String newEmail) throws EmailExistsException, UsernameExistsException, UserNotFoundException {

        User userByNewUsername=findUserByUsername(newUsername);
        User userByNewEmail=findUserByEmail(newEmail);

        if(StringUtils.isNotBlank(currentUsername)){
            User currentUser=findUserByUsername(currentUsername);
            if(currentUser==null){
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME +currentUsername);
            }
            if (userByNewUsername!=null && !currentUser.getId().equals(userByNewUsername.getId())){
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail!=null && !currentUser.getId().equals(userByNewEmail.getId())){
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        }else {
            if (userByNewUsername!=null ){
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail!=null ){
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }


    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
