package com.document.scan.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.document.scan.model.TTSRequest;


@RestController
@RequestMapping("/api/tts")
public class AzureTTSController {

    private static final Logger logger = LoggerFactory.getLogger(AzureTTSController.class);

    @Value("${azure.tts.subscription-key}")
    private String subscriptionKey;

    @Value("${azure.tts.region}")
    private String region;

    private final RestTemplate restTemplate;

    public AzureTTSController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateTTS(@Valid @RequestBody TTSRequest request) {
        logger.info("Received TTS request: text={}, language={}", request.getText(), request.getLanguage());
        try {
            String ttsEndpoint = String.format("https://%s.tts.speech.microsoft.com/cognitiveservices/v1", region);
            String ssml = String.format(
                "<speak version='1.0' xml:lang='%s'><voice xml:lang='%s' name='%s'>%s</voice></speak>",
                request.getLanguage(), request.getLanguage(), getVoiceName(request.getLanguage()), request.getText()
            );

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
            headers.set("Content-Type", "application/ssml+xml");
            headers.set("X-Microsoft-OutputFormat", "audio-16khz-128kbitrate-mono-mp3");

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(ssml, headers);
            ResponseEntity<byte[]> response = restTemplate.postForEntity(ttsEndpoint, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("TTS audio generated successfully");
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(response.getBody());
            } else {
                logger.error("Azure TTS API failed with status: {}", response.getStatusCode());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
            }
        } catch (Exception e) {
            logger.error("Exception in generateTTS: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check requested");
        return ResponseEntity.ok("Service is running on port 8081");
    }

    private String getVoiceName(String language) {
        switch (language) {
            case "en-US": return "en-US-JennyNeural";
            case "es-ES": return "es-ES-ElviraNeural";
            case "fr-FR": return "fr-FR-DeniseNeural";
            case "de-DE": return "de-DE-KatjaNeural";
            case "it-IT": return "it-IT-ElsaNeural";
            case "zh-CN": return "zh-CN-XiaoxiaoNeural";
            case "hi-IN": return "hi-IN-AaravNeural";
            case "ja-JP": return "ja-JP-NanamiNeural";
            default: return "en-US-JennyNeural";
        }
    }
}
