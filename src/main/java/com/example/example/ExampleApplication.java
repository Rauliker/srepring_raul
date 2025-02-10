package com.example.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleApplication {
	private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);

	public static void main(String[] args) {

		logger.info("Método exampleMethod ejecutado");
		logger.debug("Debugging exampleMethod");
		try {

			logger.info("Iniciando la aplicación Spring Boot");
			SpringApplication.run(ExampleApplication.class, args);
			logger.info("Aplicación Spring Boot iniciada");
		} catch (Exception e) {
			logger.error("Error en exampleMethod: {}", e.getMessage());
		}
	}

}
