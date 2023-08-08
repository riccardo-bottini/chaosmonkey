package com.riccardobottini.chaosmonkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChaosmonkeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChaosmonkeyApplication.class, args);
	}

}
