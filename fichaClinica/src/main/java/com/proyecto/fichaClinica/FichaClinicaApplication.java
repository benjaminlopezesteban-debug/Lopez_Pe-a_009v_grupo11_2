package com.proyecto.fichaClinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class FichaClinicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FichaClinicaApplication.class, args);
	}

}
