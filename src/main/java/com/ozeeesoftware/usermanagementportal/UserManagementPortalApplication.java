package com.ozeeesoftware.usermanagementportal;

import com.ozeeesoftware.usermanagementportal.constant.FileConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

import static com.ozeeesoftware.usermanagementportal.constant.FileConstant.*;

@SpringBootApplication
public class UserManagementPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagementPortalApplication.class, args);
		new File(USER_FOLDER).mkdirs();
	}


	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
