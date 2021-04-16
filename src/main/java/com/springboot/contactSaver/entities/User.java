package com.springboot.contactSaver.entities;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "User_Table")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int user_Id;
	@NotBlank(message = "Name field cannot be blank.")
	@Size(min = 3, max = 50, message = "Name must be between 3-50 characters.")
	private String user_Name;
	@Column(unique = true)
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid Email!!")
	@NotBlank(message = "Email field cannpt be blank")
	private String user_Email;
	@NotBlank(message = "Passcode cannot be blank!!")
	@Size(min = 3, message = "Passcode should be of 3-15 characters.")
	private String user_Passcode;
	private String user_Role;
	private boolean user_Status;
	private String user_ImageUrl;
	@Column(length = 500)
	private String user_Description;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
	private List<Contact> contacts = new ArrayList<Contact>();

	@Override
	public String toString() {
		return "User [user_Id=" + user_Id + ", user_Name=" + user_Name + ", user_Passcode=" + user_Passcode
				+ ", user_Email=" + user_Email + ", user_Role=" + user_Role + ", user_Status=" + user_Status
				+ ", user_ImageUrl=" + user_ImageUrl + ", user_Description=" + user_Description + ", contacts="
				+ contacts + "]";
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public User(int user_Id, String user_Name, String user_Passcode, String user_Email, String user_Role,
			boolean user_Status, String user_ImageUrl, String user_Description, List<Contact> contacts) {
		super();
		this.user_Id = user_Id;
		this.user_Name = user_Name;
		this.user_Passcode = user_Passcode;
		this.user_Email = user_Email;
		this.user_Role = user_Role;
		this.user_Status = user_Status;
		this.user_ImageUrl = user_ImageUrl;
		this.user_Description = user_Description;
		this.contacts = contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(int user_Id) {
		this.user_Id = user_Id;
	}

	public String getUser_Name() {
		return user_Name;
	}

	public void setUser_Name(String user_Name) {
		this.user_Name = user_Name;
	}

	public String getUser_Passcode() {
		return user_Passcode;
	}

	public void setUser_Passcode(String user_Passcode) {
		this.user_Passcode = user_Passcode;
	}

	public String getUser_Email() {
		return user_Email;
	}

	public void setUser_Email(String user_Email) {
		this.user_Email = user_Email;
	}

	public String getUser_Role() {
		return user_Role;
	}

	public void setUser_Role(String user_Role) {
		this.user_Role = user_Role;
	}

	public boolean isUser_Status() {
		return user_Status;
	}

	public void setUser_Status(boolean user_Status) {
		this.user_Status = user_Status;
	}

	public String getUser_ImageUrl() {
		return user_ImageUrl;
	}

	public void setUser_ImageUrl(String user_ImageUrl) {
		this.user_ImageUrl = user_ImageUrl;
	}

	public String getUser_Description() {
		return user_Description;
	}

	public void setUser_Description(String user_Description) {
		this.user_Description = user_Description;
	}

}
