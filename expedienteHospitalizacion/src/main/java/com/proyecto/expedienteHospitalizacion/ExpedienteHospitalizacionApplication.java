package com.proyecto.expedienteHospitalizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ExpedienteHospitalizacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpedienteHospitalizacionApplication.class, args);
	}

}
