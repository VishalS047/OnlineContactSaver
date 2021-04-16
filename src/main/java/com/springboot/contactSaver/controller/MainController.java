package com.springboot.contactSaver.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.springboot.contactSaver.dao.UserRepository;
import com.springboot.contactSaver.entities.Contact;
import com.springboot.contactSaver.entities.User;
import com.springboot.contactSaver.helper.Message;

@Controller
public class MainController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepo;

	@GetMapping(path = "/test")
	@ResponseBody
	public String check() {
		User user = new User();
		System.out.println("Its time to work");
		user.setUser_Name("Vishal");
		user.setUser_Email("iamvishal047@gmail.com");
		user.setUser_Role("ADMIN");
		user.setUser_Passcode("P@55c0de");
		Contact contact = new Contact();
		user.getContacts().add(contact);

		userRepo.save(user);

		System.out.println("User: " + user + "successfully saved...");

		return "working";
	}

	@GetMapping(path = "/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Online Contact Saver");
		return "home";
	}

	@GetMapping(path = "/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Online Contact Saver");
		return "about";
	}

	@RequestMapping(path = "/signup")
	public String signUp(Model model) {
		model.addAttribute("title", "Sign Up - Online Contact Saver");
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping(path = "/registered", method = RequestMethod.POST)
	public String userRegistration(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean status, Model model,
			HttpSession session) {
		try {
			if (!status) {
				System.out.println("You have not agreed to the terms and conditions");
				throw new Exception("You have not agreed to the terms and conditions");
			}

			if (result.hasErrors()) {
				System.out.println("ERROR: " + result.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			System.out.println(status);
			System.out.println(user);

			user.setUser_Role("ROLE_USER");
			user.setUser_Status(true);
			user.setUser_ImageUrl("default.png");
			user.setUser_Passcode(passwordEncoder.encode(user.getUser_Passcode()));

			User savedUser = userRepo.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("message",
					new Message("Congrats! you are successfully registered!!", "alert-success"));
			Thread.sleep(1000);
			return "signup";

		} catch (Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				model.addAttribute("user", user);
				session.setAttribute("message",
						new Message(
								"This Email is already registered with us. Please register with different Email ID.",
								"alert-danger"));
				return "signup";
			}
			e.printStackTrace();

			model.addAttribute("user", user);

			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-danger"));

			return "signup";
		}
	}

	@GetMapping(path = "/signin")
	public String login(Model model) {
		model.addAttribute("title", "Login - Online Contact Saver");
		return "signin";
	}
}
