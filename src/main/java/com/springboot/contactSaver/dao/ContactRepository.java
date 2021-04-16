package com.springboot.contactSaver.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.contactSaver.entities.Contact;

public interface ContactRepository  extends JpaRepository<Contact, Integer>{
	
	@Query("from Contact as c where c.user.user_Id=:userId")
	public Page<Contact> findContactByUserId(@Param("userId")int userId,Pageable pePageable);
	
	@Query("from Contact as c where c.contact_Id=:contactId")
	public Contact findContactDetailByUserId(@Param("contactId") int userId);
}
