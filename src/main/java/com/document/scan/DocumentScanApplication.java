package com.document.scan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component("com.document.scan.service")
@EntityScan("com.document.scan.entity")
@EnableJpaRepositories("com.document.scan.repository")
@EnableScheduling
public class DocumentScanApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentScanApplication.class, args);
	}

}
