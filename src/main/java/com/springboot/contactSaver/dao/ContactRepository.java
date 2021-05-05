package com.springboot.contactSaver.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.springboot.contactSaver.entities.Contact;
import com.springboot.contactSaver.entities.User;

public interface ContactRepository  extends JpaRepository<Contact, Integer>{
	
	@Query("from Contact as c where c.user.user_Id=:userId")
	public Page<Contact> findContactByUserId(@Param("userId")int userId,Pageable pePageable);
	
	@Query("from Contact as c where c.contact_Id=:contactId")
	public Contact findContactDetailByUserId(@Param("contactId") int userId);
	
	@Query(value =  "select contact_name from contact_table where contact_name LIKE '%:searchTerm%' and user_id =:userId  ", nativeQuery = true)
	public List<Contact> findByFirstnameContaining(@Param("searchTerm") String searchTerm, @Param("userId") int userId);
}
 