package com.springboot.contactSaver.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.contactSaver.dao.ContactRepository;
import com.springboot.contactSaver.dao.UserRepository;
import com.springboot.contactSaver.entities.Contact;
import com.springboot.contactSaver.entities.User;
import com.springboot.contactSaver.helper.Message;

@Controller
@RequestMapping(path = "/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;

	/* method to add common data to response */
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		System.out.println("---------------------------------------------------------");
		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);
		System.out.println("User's Name is: " + user.getUser_Name());
		model.addAttribute("user", user);
	}

//	Dash Board Home
	@RequestMapping(path = "/dash")
	public String dashBoard(Model model, Principal principal) {
		model.addAttribute("dashboard", "User dashboard");
		return "normal_user/user_dashBoard";
	}

	@GetMapping(path = "/addContact")
	public String openAddContactForm(Model model) {

		model.addAttribute("addContact", "Add Contact Form");
		model.addAttribute("contact", new Contact());

		return "normal_user/add_contact_form";
	}

	@PostMapping(path = "/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result, Model model,
			Principal principal, @RequestParam("contact_ImageUrl") MultipartFile file, HttpSession session) {

		try {
			if (result.hasErrors()) {
				System.out.println("ERROR: " + result.toString());
				System.out.println("RESULT HAS ERRORS");
				model.addAttribute("contact", contact);
				/* return "normal_user/add_contact_form"; */
			}

			String name = principal.getName();
			User user = this.userRepo.getUserByUserName(name);

			if (file.isEmpty()) {
				contact.setContact_ImageUrl("contact.png");

			} else {
				contact.setContact_ImageUrl(file.getOriginalFilename());
				File file2 = new ClassPathResource("static/image").getFile();
				System.out.println(file2);
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("message", new Message("Image Successfully uploaded", "alert-success"));
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepo.save(user);
			System.out.println("Contact: " + contact);
			System.out.println("Successfully added to database.");

			model.addAttribute("contact", new Contact());
			session.setAttribute("message",
					new Message("Congrats! you are successfully registered!!", "alert-success"));
			Thread.sleep(2000);
			return "normal_user/add_contact_form";

		} catch (Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				model.addAttribute("contact", contact);
				session.setAttribute("message",
						new Message(
								"This Email is already registered with us. Please register with different Email ID.",
								"alert-danger"));
				e.printStackTrace();
				System.out.println("Email ID is already present in DB.");
				return "normal_user/add_contact_form";
			}
			e.printStackTrace();

			model.addAttribute("contact", contact);

			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-danger"));

			return "normal_user/add_contact_form";
		}
	}

	@RequestMapping(path = "/viewcontacts/{pageno}", method = RequestMethod.GET)
	public String showContacts(@PathVariable("pageno") int page, Model model, Principal principal) {
		model.addAttribute("title", "Show user Contacts");
		String name = principal.getName();
		User user = this.userRepo.getUserByUserName(name);
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepo.findContactByUserId(user.getUser_Id(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);

		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal_user/show_contacts";
	}

	@GetMapping(path = "/contact/{cId}")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		System.out.println("contact ID is: " + cId);
		Contact contact = this.contactRepo.findContactDetailByUserId(cId);
		System.out.println(contact);
		String name = principal.getName();
		User user = this.userRepo.getUserByUserName(name);

		/* System.out.println(user); */

		if (user.getUser_Id() == contact.getUser().getUser_Id()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getContact_Name());
		}

		return "normal_user/showcontactdetails";
	}

	@RequestMapping(path = "/deleteContact/{cId}", method = RequestMethod.GET)
	public String deleteContact(@PathVariable("cId") Integer id, Model model, Principal principal,
			HttpSession session) {
		Contact contact = this.contactRepo.findContactDetailByUserId(id);
		String name = principal.getName();
		this.contactRepo.delete(contact);
		User user = this.userRepo.getUserByUserName(name);
		if (user.getUser_Id() == contact.getUser().getUser_Id()) {
			this.contactRepo.delete(contact);
			session.setAttribute("message", new Message("Contact Successfully deleted!", "alert-success"));
		}

		return "redirect:/user/viewcontacts/0";
	}
}
