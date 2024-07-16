package com.coherentsolutions.pot.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class NotificationsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationsMicroserviceApplication.class, args);
	}
}
