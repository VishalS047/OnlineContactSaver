package com.springboot.contactSaver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.springboot.contactSaver.dao.UserRepository;
import com.springboot.contactSaver.entities.User;

public class CustomUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//		fetching user from database

		User user = userRepo.getUserByUserName(username);

		if (user == null) {
			throw new UsernameNotFoundException("User with this Email not found in the database.");
		}

		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		return customUserDetails;
	}

}
