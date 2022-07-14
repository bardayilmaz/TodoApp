package com.bulentyilmaz.todoapp;

import com.bulentyilmaz.todoapp.entity.Role;
import com.bulentyilmaz.todoapp.entity.User;
import com.bulentyilmaz.todoapp.repository.UserRepository;
import com.bulentyilmaz.todoapp.service.AuthService;
import com.bulentyilmaz.todoapp.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TodoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoAppApplication.class, args);
	}

//	@Bean
//	CommandLineRunner run(UserRepository userRepository) { // data sql
//		return args ->{
//			User u = new User();
//			//u.setId(new Long(1));
//			u.setFirstName("Bulent");
//			u.setLastName("Yilmaz");
//			u.setEmail("admin@todo.com");
//			u.setPasswordHash("$2a$10$5k99tvpc.Vi6fua8d9GOyOA2iyIDqgR.HQa1hHn1pZ9ajvdWlt3Um");
//			u.setRole(Role.ADMIN);
//			userRepository.save(u);
//		};
//	}

}
