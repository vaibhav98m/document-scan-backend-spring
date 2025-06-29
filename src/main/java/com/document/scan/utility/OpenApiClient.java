package com.document.scan.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.document.scan.model.AiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class OpenApiClient {

	@Value("${openapi.url}")
	private String openApiUrl;

	@Value("${spring.ai.openai.api-key}")
	private String openApiToken;

	private final RestTemplate restTemplate = new RestTemplate();

	public String queryDocument(String prompt) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(openApiToken);

		Map<String, Object> requestBody = Map.of("model", "llama-3.3-70b-versatile", "messages",
				new Object[] { Map.of("role", "user", "content", prompt) }, "temperature", 0.9);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> restResponse = restTemplate.postForEntity(openApiUrl, request, String.class);

		if (restResponse.getStatusCode() == HttpStatus.OK && restResponse.getBody() != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			AiResponse response = objectMapper.readValue(restResponse.getBody(), AiResponse.class);
			String content = response.getChoices().get(0).getMessage().getContent();

			return content;
		}
		return "Error: Unable to fetch response from OpenAPI.";
	}
}
