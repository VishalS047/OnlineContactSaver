package com.springboot.contactSaver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Contact_Table")
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int contact_Id;
	@NotBlank(message = "Contact Name cannot be blank.")
	@Size(min = 3, max = 20, message = "Name should strictly be within 3-20 characters.")
	private String contact_Name;
	@NotBlank(message = "NickName cannot be blank")
	@Size(min = 3, max = 15, message = "Nick name should be composed of 3-15 characters.")
	private String contact_NickName;
	@Column(unique = true)
	@NotBlank(message = "Email Id cannot be blank.")
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid Email!!")
	private String contact_EmailID;
	@NotBlank(message = "Phone number field cannot be blank.")
	private String contact_PhoneNo;
	private String contact_Profession;
	@Column(length = 5000)
	private String contact_Description;
	private String contact_ImageUrl;
//	(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@ManyToOne
	private User user;

//	@Override
//	public String toString() {
//		return "Contact [contact_Id=" + contact_Id + ", contact_Name=" + contact_Name + ", contact_NickName="
//				+ contact_NickName + ", contact_EmailID=" + contact_EmailID + ", contact_PhoneNo=" + contact_PhoneNo
//				+ ", contact_Profession=" + contact_Profession + ", contact_Description=" + contact_Description
//				+ ", contact_ImageUrl=" + contact_ImageUrl + ", user=" + user + "]";
//	}

	public User getUser() {
		return user;
	}

	public Contact(int contact_Id, String contact_Name, String contact_NickName, String contact_EmailID,
			String contact_PhoneNo, String contact_Profession, String contact_Description, String contact_ImageUrl,
			User user) {
		super();
		this.contact_Id = contact_Id;
		this.contact_Name = contact_Name;
		this.contact_NickName = contact_NickName;
		this.contact_EmailID = contact_EmailID;
		this.contact_PhoneNo = contact_PhoneNo;
		this.contact_Profession = contact_Profession;
		this.contact_Description = contact_Description;
		this.contact_ImageUrl = contact_ImageUrl;
		this.user = user;
	}

	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getContact_Id() {
		return contact_Id;
	}

	public void setContact_Id(int contact_Id) {
		this.contact_Id = contact_Id;
	}

	public String getContact_Name() {
		return contact_Name;
	}

	public void setContact_Name(String contact_Name) {
		this.contact_Name = contact_Name;
	}

	public String getContact_NickName() {
		return contact_NickName;
	}

	public void setContact_NickName(String contact_NickName) {
		this.contact_NickName = contact_NickName;
	}

	public String getContact_EmailID() {
		return contact_EmailID;
	}

	public void setContact_EmailID(String contact_EmailID) {
		this.contact_EmailID = contact_EmailID;
	}

	public String getContact_PhoneNo() {
		return contact_PhoneNo;
	}

	public void setContact_PhoneNo(String contact_PhoneNo) {
		this.contact_PhoneNo = contact_PhoneNo;
	}

	public String getContact_Profession() {
		return contact_Profession;
	}

	public void setContact_Profession(String contact_Profession) {
		this.contact_Profession = contact_Profession;
	}

	public String getContact_Description() {
		return contact_Description;
	}

	public void setContact_Description(String contact_Description) {
		this.contact_Description = contact_Description;
	}

	public String getContact_ImageUrl() {
		return contact_ImageUrl;
	}

	public void setContact_ImageUrl(String contact_ImageUrl) {
		this.contact_ImageUrl = contact_ImageUrl;
	}

}
