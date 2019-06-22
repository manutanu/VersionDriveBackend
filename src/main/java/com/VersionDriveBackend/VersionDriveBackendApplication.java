package com.VersionDriveBackend;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableJpaRepositories({"com.VersionDriveBackend.repository"})
@ComponentScan({"com.VersionDriveBackend"})
@EnableJpaAuditing
@CrossOrigin("http://localhost:4200")
public class VersionDriveBackendApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(VersionDriveBackendApplication.class, args);
	}	

}
