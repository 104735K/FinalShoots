package com.Shoots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.Shoots", "provider"})
public class ShootsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShootsApplication.class, args);
	}

}
