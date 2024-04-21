package com.ThreadWeaver.prototype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WeaverServer {
	public static void main(String[] args) {
		SpringApplication.run(WeaverServer.class, args);
	}
}