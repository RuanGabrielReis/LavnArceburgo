package com.example.lavnarceburgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class LavnarceburgoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LavnarceburgoApplication.class, args);
	}


}
