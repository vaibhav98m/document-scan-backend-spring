package com.document.scan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class DocumentScanApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentScanApplication.class, args);
	}

}
