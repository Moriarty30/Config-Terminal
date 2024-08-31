package com.superpay.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.superpay.config",
		"com.superpay.config.service",
		"com.superpay.config.repository",
		"com.superpay.config.controller",
		"com.superpay.config.mappers"
})
public class ConfigApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfigApplication.class, args);
	}
}