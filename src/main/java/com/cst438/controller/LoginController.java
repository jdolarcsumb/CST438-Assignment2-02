package com.cst438.controller;

import com.cst438.domain.User;
import com.cst438.domain.UserRepository;
import com.cst438.dto.LoginDTO;
import com.cst438.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
public class LoginController {
	
	@Autowired
	TokenService tokenService;

	@Autowired
	UserRepository userRepository;

	@GetMapping("/login")
	public LoginDTO token(Authentication authentication) {
		String name = authentication.getName();
		System.out.println("login authentication "+name);
		User user = userRepository.findByEmail(name);
		String token = tokenService.generateToken(authentication);
		return new LoginDTO(token, user.getType());
	}

	// ADMIN function to create a new user
	@PostMapping("/users")
	@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
	public User createUser(@RequestBody User user) {
		return userRepository.save(user);
	}

	// ADMIN function to update a user
	@PutMapping("/users/{userId}")
	@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
	public User updateUser(@PathVariable("userId") int userId, @RequestBody User user) {
		user.setId(userId);
		return userRepository.save(user);
	}

	// ADMIN function to delete a user
	@DeleteMapping("/users/{userId}")
	@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
	public void deleteUser(@PathVariable("userId") int userId) {
		userRepository.deleteById(userId);
	}

	// Get user details
	@GetMapping("/users/{userId}")
	@PreAuthorize("hasAnyAuthority('SCOPE_ROLE_STUDENT', 'SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_INSTRUCTOR')")
	public User getUserDetails(@PathVariable("userId") int userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id not found");
		}
		return user;
	}

}
