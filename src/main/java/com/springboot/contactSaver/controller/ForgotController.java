package com.springboot.contactSaver.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.contactSaver.Service.EmailService;
import com.springboot.contactSaver.dao.UserRepository;
import com.springboot.contactSaver.entities.User;
import com.springboot.contactSaver.helper.Message;
import com.sun.mail.handlers.message_rfc822;

@Controller
public class ForgotController {

	Random random = new Random(123456);

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

//	email id form open handler
	@RequestMapping(path = "/forgot", method = RequestMethod.GET)
	public String openEmailForm() {

		return "forgot_email_form";
	}

	@RequestMapping(path = "/sendOTP", method = RequestMethod.POST)
	public String sendotp(@RequestParam("email") String email, HttpSession session) {
//		System.out.println(email);

		int otp = random.nextInt(98765);
		String subject = "OTP from Online Contact Book";

		String message = "" + "<div style='border:1px solid black; padding 20px;'>" + "<h1>"
				+ "Your One Time Password is: " + "<bold>" + otp + "</bold>" + "</h1>" + "</div>";

		String to = email;

//		System.out.println(otp);

		boolean isOTP = this.emailService.sendEmail(subject, message, to);

		if (isOTP) {
//			System.out.println("Full Stack Developer");
			session.setAttribute("OTP", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		} else {
			session.setAttribute("message", new Message("OTP is incorrect!!", "alert-danger"));
//			System.out.println("Noob Developer");
			return "forgot_email_form";
		}
	}

	// verify OTP
	@RequestMapping(path = "/verify_otp", method = RequestMethod.POST)
	public String checkOtp(@RequestParam("otp")int otp, HttpSession session) {
		
		int myOTP = (int)session.getAttribute("OTP");

		String myEmail = (String) session.getAttribute("email"); 
		
		System.out.println("OTP is: "+ myOTP+ " and Email is: "+myEmail);
		
		System.out.println("myOTP: "+myOTP);
		
		System.out.println("OTP: "+otp);
		
		if(myOTP == otp) {
			
			User user = this.userRepository.getUserByUserName(myEmail);
			System.out.println(user);
			
			if(user == null) {
				
				session.setAttribute("message", new Message("No user exists with this email!","alert-danger"));
				return "forgot_email_form";
				
			}else {
				
				return "password_change_form";
			}
		} else {
			session.setAttribute("message", new Message("You have entered wrong OTP!!","alert-danger"));
			return "verify_otp";
		}
	}
	
	@PostMapping(path="/change_password")
	public String changePassword(@RequestParam("password") String password, HttpSession session) {
		String myEmail = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(myEmail);
		
		user.setUser_Passcode(this.bCryptPasswordEncoder.encode(password));
		
		this.userRepository.save(user);
		
		
		
		return "redirect:/signin?change=password changed successfully..";
	}
}
