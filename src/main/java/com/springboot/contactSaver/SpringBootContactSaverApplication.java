package com.springboot.contactSaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class SpringBootContactSaverApplication{

	public static void main(String[] args) {
		SpringApplication.run(SpringBootContactSaverApplication.class, args);
	}
}