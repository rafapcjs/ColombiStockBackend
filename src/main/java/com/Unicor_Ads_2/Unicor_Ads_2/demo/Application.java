package com.Unicor_Ads_2.Unicor_Ads_2.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.Unicor_Ads_2")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
