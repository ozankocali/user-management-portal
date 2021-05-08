package com.ozeeesoftware.usermanagementportal.controller;

import com.ozeeesoftware.usermanagementportal.constant.FileConstant;
import com.ozeeesoftware.usermanagementportal.constant.SecurityConstant;
import com.ozeeesoftware.usermanagementportal.exception.ExceptionHandling;
import com.ozeeesoftware.usermanagementportal.exception.model.*;
import com.ozeeesoftware.usermanagementportal.model.HttpResponse;
import com.ozeeesoftware.usermanagementportal.model.User;
import com.ozeeesoftware.usermanagementportal.model.UserPrincipal;
import com.ozeeesoftware.usermanagementportal.service.UserService;
import com.ozeeesoftware.usermanagementportal.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
    public ResponseEntity<User> register(@RequestBody User user) throws UsernameExistsException, UserNotFoundException, EmailExistsException, MessagingException {

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

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName")String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage",required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException {

        User newUser=userService.addNewUser(firstName,lastName,username,email,role,Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<User>(newUser,HttpStatus.OK);

    }

    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam("currentUsername")String currentUsername,
                                       @RequestParam("firstName")String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("role") String role,
                                       @RequestParam("isActive") String isActive,
                                       @RequestParam("isNonLocked") String isNonLocked,
                                       @RequestParam(value = "profileImage",required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException {

        User updatedUser=userService.updateUser(currentUsername,firstName,lastName,username,email,role,Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<User>(updatedUser,HttpStatus.OK);

    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable("username") String username){

        User existingUser=userService.findUserByUsername(username);

        return new ResponseEntity<User>(existingUser,HttpStatus.OK);

    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers(){

        List<User> users=userService.getUsers();

        return new ResponseEntity<List<User>>(users,HttpStatus.OK);

    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailNotFoundException {

        userService.resetPassword(email);


        return response(HttpStatus.OK,"An email with a new password was sent to: "+email);

    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(HttpStatus.OK,"User deleted successfully");
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username")String username,
                                                   @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException {

        User updatedUser=userService.updateProfileImage(username,profileImage);
        return new ResponseEntity<User>(updatedUser,HttpStatus.OK);

    }

    @GetMapping(path = "/image/{username}/{fileName}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username,@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(FileConstant.USER_FOLDER+username+FileConstant.FORWARD_SLASH+fileName));
    }

    @GetMapping(path = "/image/profile/{username}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getDefaultProfileImage(@PathVariable("username") String username) throws MalformedURLException,IOException {
        URL url=new URL(FileConstant.TEMP_PROFILE_IMAGE_BASE_URL+username);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        try (InputStream inputStream=url.openStream()){
            int bytesRead;
            byte[] chunk=new byte[1024];
            while ((bytesRead=inputStream.read(chunk))>0){
                byteArrayOutputStream.write(chunk,0,bytesRead);
            }

        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<HttpResponse>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
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
