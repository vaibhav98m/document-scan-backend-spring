package com.document.scan.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import com.document.scan.entity.UserEntity;
import com.document.scan.exception.CustomExcetion;
import com.document.scan.model.UserRequest;
import com.document.scan.model.ValidationResult;
import com.document.scan.repository.UserRepository;
import com.document.scan.utility.EmailDomainValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailDomainValidator domainValidator;

	public UserEntity onboardUser(UserRequest request) {

		ValidationResult result = domainValidator.validateEmail(request.getEmail());
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("============>" + mapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		if (result != null && StringUtils.isNotBlank(result.getError())) {
			throw new CustomExcetion(result.getError(), "024", HttpStatusCode.valueOf(404));
		}

		if (result.getMxRecords().get(0).equalsIgnoreCase("0 .")) {
			throw new CustomExcetion("Domain Does not receive mails, please privde vaild email.", "025",
					HttpStatusCode.valueOf(404));
		}

		// Check if user already exists
		return userRepository.findByEmail(request.getEmail()).orElseGet(() -> {
			UserEntity user = new UserEntity();
			user.setName(request.getFirstname() + " " + request.getLastname());
			user.setEmail(request.getEmail());
			return userRepository.save(user);
		});
	}
}
