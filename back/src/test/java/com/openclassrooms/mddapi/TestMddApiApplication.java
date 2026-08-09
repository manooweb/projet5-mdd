package com.openclassrooms.mddapi;

import org.springframework.boot.SpringApplication;

public class TestMddApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(MddApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
