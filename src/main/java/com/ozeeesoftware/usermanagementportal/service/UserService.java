package com.ozeeesoftware.usermanagementportal.service;

import com.ozeeesoftware.usermanagementportal.exception.model.*;
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
                    boolean isActive, MultipartFile profileImage) throws UsernameExistsException, UserNotFoundException, EmailExistsException, IOException, NotAnImageFileException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked,
                    boolean isActive, MultipartFile profileImage) throws UsernameExistsException, UserNotFoundException, EmailExistsException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    User updateProfileImage(String username,MultipartFile profileImage) throws UsernameExistsException, UserNotFoundException, EmailExistsException, IOException, NotAnImageFileException;

}
