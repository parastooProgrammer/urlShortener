package com.example.urlshortener;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableEncryptableProperties
public class UrlshortenerApplication {

	public static void main(String[] args) {
		System.out.println("ENCRYPTOR PASSWORD: " + System.getProperty("jasypt.encryptor.password"));

		SpringApplication.run(UrlshortenerApplication.class, args);
	}



}
