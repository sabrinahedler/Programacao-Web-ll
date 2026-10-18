package br_com_savepoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SavepointprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SavepointprojectApplication.class, args);
	}

}
