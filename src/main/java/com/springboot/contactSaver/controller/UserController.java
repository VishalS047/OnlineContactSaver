package com.springboot.contactSaver.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.springboot.contactSaver.dao.ContactRepository;
import com.springboot.contactSaver.dao.MyOrderRepo;
import com.springboot.contactSaver.dao.UserRepository;
import com.springboot.contactSaver.entities.Contact;
import com.springboot.contactSaver.entities.OrderDetails;
import com.springboot.contactSaver.entities.User;
import com.springboot.contactSaver.helper.Message;


@Controller
@RequestMapping(path = "/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@Autowired
	private MyOrderRepo myOrderRepo;
	
	/* method to add common data to response */
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		System.out.println("---------------------------------------------------------");
		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);
//		System.out.println("User's Name is: " + user.getUser_Name());
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
			Thread.sleep(1000);
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

	@RequestMapping(path = "/updateContact/{cId}", method = RequestMethod.GET)
	public String updateContact(@PathVariable("cId") Integer id, Model model, Principal principal,
			HttpSession session) {
//		System.out.println("Contact update form");
		model.addAttribute("title", "Update Contact Form");
		Contact contact = this.contactRepo.findContactDetailByUserId(id);
		model.addAttribute("contact", contact);
		return "normal_user/updateform";
	}

	@RequestMapping(path = "/process-update", method = RequestMethod.POST)
	public String processUpdate(@ModelAttribute Contact contact, BindingResult result, Model model, Principal principal,
			@RequestParam("contact_ImageUrl") MultipartFile file, @RequestParam("contact_Id") Integer id,
			HttpSession session) {
		
			Contact oldContactDetail = this.contactRepo.findContactDetailByUserId(contact.getContact_Id());
			
//			System.out.println(oldContactDetail);
		try {
			if (!file.isEmpty()) {
//				System.out.println("File is not empty");
				
//				delete old contact
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file3 = new File(deleteFile, oldContactDetail.getContact_ImageUrl());
				file3.delete();
				
//				update new contact
				File file2 = new ClassPathResource("static/image").getFile();
//				System.out.println(file2);
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
//				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setContact_ImageUrl(file.getOriginalFilename());
				
			} else {
				contact.setContact_ImageUrl(oldContactDetail.getContact_ImageUrl());
			}
			
			User user = this.userRepo.getUserByUserName(principal.getName());
			
			contact.setUser(user);
			
			this.contactRepo.save(contact);
			
			session.setAttribute("message", new Message("Details successfully uploaded","alert-success"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Contact Name: " + contact.getContact_Name());
		System.out.println("Contact Id: " + contact.getContact_Id());

		return "redirect:/user/viewcontacts/"+0;
	}
	
	@RequestMapping(path = "/profile", method = RequestMethod.GET)
	public String profile(Model model) {
		model.addAttribute("title", "User Profile");
		return "normal_user/profile";
	}
	
	@RequestMapping(path = "/settings" , method = RequestMethod.GET)
	public String settings() {
		
		return "normal_user/settings";
	}
	@RequestMapping(path = "/change-password", method = RequestMethod.POST)
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,Principal principal,Model model,HttpSession session) {
		
		
		String userName = principal.getName();
		User currentUser = this.userRepo.getUserByUserName(userName);
		
		if(bcryptPasswordEncoder.matches(oldPassword, currentUser.getUser_Passcode())) {
			
			System.out.println("oldPASSWORD "+oldPassword);
			System.out.println("oldPASSWORD "+newPassword);
			
			currentUser.setUser_Passcode(this.bcryptPasswordEncoder.encode(newPassword));
			this.userRepo.save(currentUser);
			session.setAttribute("message", new Message("Password successfully changed!","alert-success"));
		} 
		else {
			session.setAttribute("message", new Message("Old password does not match!","alert-danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/dash";
	}
	
//	creating order for payment
	@PostMapping(path = "/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws Exception {
//		System.out.println("Order successfully placed");
		
		int amt = Integer.parseInt(data.get("amount").toString());
		RazorpayClient client = new RazorpayClient("rzp_test_VxtfatH6wU2Kx4","s9oLmhRIK9XPMxE5luPyHNk1");
		System.out.println(data); 
		
		JSONObject obj = new JSONObject();
		obj.put("amount", amt*100);
		obj.put("currency", "INR");
		obj.put("receipt", "txn_1694512");
		
//		creating new order

		Order order = client.Orders.create(obj);
		System.out.println(order);
		
		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);
		
		
		
		OrderDetails orderDetails = new OrderDetails();
		
		orderDetails.setAmount(order.get("amount")+"");
		orderDetails.setOrderId(order.get("id"));
		orderDetails.setPaymentId(null);
		orderDetails.setStatus("created");
		orderDetails.setUser(user);
		orderDetails.setReceipt(order.get("receipt"));
		
		this.myOrderRepo.save(orderDetails);
		
		return order.toString();
	}
	
	@PostMapping(path="/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {
		
		OrderDetails myOrder = this.myOrderRepo.findByOrderId(data.get("order_id").toString());
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		myOrder.setPaymentId(data.get("payment_Id").toString());
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		myOrder.setStatus(data.get("status").toString());
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		System.out.println("askcascasuvavbasv asovhasvoashivaoshvsao");
		this.myOrderRepo.save(myOrder);
		
		return ResponseEntity.ok(Map.of("msg", "updated"));
	}
}

