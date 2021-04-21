package com.ozeeesoftware.usermanagementportal.service;

import com.ozeeesoftware.usermanagementportal.exception.model.EmailExistsException;
import com.ozeeesoftware.usermanagementportal.exception.model.EmailNotFoundException;
import com.ozeeesoftware.usermanagementportal.exception.model.UserNotFoundException;
import com.ozeeesoftware.usermanagementportal.exception.model.UsernameExistsException;
import com.ozeeesoftware.usermanagementportal.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws UsernameExistsException, UserNotFoundException, EmailExistsException, MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked,
                    boolean isActive, MultipartFile profileImage) throws UsernameExistsException, UserNotFoundException, EmailExistsException, IOException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked,
                    boolean isActive, MultipartFile profileImage) throws UsernameExistsException, UserNotFoundException, EmailExistsException, IOException;

    void deleteUser(long id);

    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    User updateProfileImage(String username,MultipartFile profileImage) throws UsernameExistsException, UserNotFoundException, EmailExistsException, IOException;

}
