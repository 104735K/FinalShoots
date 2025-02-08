package com.shoots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.Shoots", "provider"})
public class ShootsPersonalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShootsPersonalApplication.class, args);
    }

}
