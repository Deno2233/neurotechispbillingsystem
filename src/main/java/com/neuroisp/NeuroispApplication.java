package com.neuroisp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NeuroispApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeuroispApplication.class, args);
    }

}
