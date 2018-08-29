package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BootApplication.class, args);
	}

    @Override
    public void run(String... args) {
	    System.out.println("\n\nHello world from BootApplication !!!!!!!!!!!!!!!!!\n\n");
    }
}
