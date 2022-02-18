package com.waes.phillips.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PhillipsProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhillipsProductApplication.class, args);
	}

}