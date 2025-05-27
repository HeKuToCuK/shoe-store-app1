package com.shoeshop;

import jakarta.annotation.PostConstruct;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class ShoeStoreApplication {
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void checkConnection() {
		try {
			System.out.println("Testing database connection...");
			Connection connection = dataSource.getConnection();
			System.out.println("Connection successful! URL: " + connection.getMetaData().getURL());
			connection.close();
		} catch (SQLException e) {
			System.err.println("Failed to connect to database:");
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(ShoeStoreApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}
}
