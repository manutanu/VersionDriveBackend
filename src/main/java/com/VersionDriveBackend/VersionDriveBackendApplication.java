/*
* VersionDriveBackend
* This class Bootstraps whole application
*
* 1.0
*
* @authored by Mritunjay Yadav
*/


package com.VersionDriveBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories({"com.VersionDriveBackend.repository"})
@ComponentScan({"com.VersionDriveBackend"})
@EnableJpaAuditing(setDates = true)
@EnableAsync
public class VersionDriveBackendApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(VersionDriveBackendApplication.class, args);
	}	

}
