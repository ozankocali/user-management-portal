package com.ozeeesoftware.usermanagementportal.service;

import com.ozeeesoftware.usermanagementportal.exception.model.EmailExistsException;
import com.ozeeesoftware.usermanagementportal.exception.model.UserNotFoundException;
import com.ozeeesoftware.usermanagementportal.exception.model.UsernameExistsException;
import com.ozeeesoftware.usermanagementportal.model.User;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws UsernameExistsException, UserNotFoundException, EmailExistsException, MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

}
