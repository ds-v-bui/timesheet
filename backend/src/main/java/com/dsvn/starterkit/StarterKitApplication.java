package com.dsvn.starterkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StarterKitApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarterKitApplication.class, args);
    }
}
