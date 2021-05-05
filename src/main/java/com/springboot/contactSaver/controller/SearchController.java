package com.springboot.contactSaver.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.contactSaver.dao.ContactRepository;
import com.springboot.contactSaver.dao.UserRepository;
import com.springboot.contactSaver.entities.Contact;
import com.springboot.contactSaver.entities.User;

@RestController
public class SearchController 
{
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contactRepo;
	
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) 
	{
		System.out.println(query);
		
				
		User user = this.userRepo.getUserByUserName(principal.getName());
		System.out.println(user);
		System.out.println(user.getUser_Id());
		
		List<Contact> contacts = this.contactRepo.findByFirstnameContaining(query,user.getUser_Id());
		
		return ResponseEntity.ok(contacts);
	}
}

