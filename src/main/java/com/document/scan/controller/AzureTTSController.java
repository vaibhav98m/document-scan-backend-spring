package com.document.scan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.document.scan.model.TTSRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tts")
@Tag(name = "Text-to-Speech", description = "Azure Cognitive Services Text-to-Speech API")
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

    @Operation(summary = "Generate Text-to-Speech Audio", description = "Generates an audio response (MP3) using Azure Cognitive Services TTS based on the input text and language.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audio generated successfully", content = @Content(mediaType = "audio/wav")),
    // @ApiResponse(responseCode = "500", description = "Internal server error",
    // content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateTTS(
            @Parameter(description = "TTS request with text and language", required = true) @Valid @RequestBody TTSRequest request) {

        logger.info("Received TTS request: text={}, language={}", request.getText(), request.getLanguage());
        try {
            String ttsEndpoint = String.format("https://%s.tts.speech.microsoft.com/cognitiveservices/v1", region);
            String ssml = String.format(
                    "<speak version='1.0' xml:lang='%s'><voice xml:lang='%s' name='%s'>%s</voice></speak>",
                    request.getLanguage(), request.getLanguage(), getVoiceName(request.getLanguage()),
                    request.getText());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
            headers.set("Content-Type", "application/ssml+xml");
            headers.set("X-Microsoft-OutputFormat", "audio-16khz-128kbitrate-mono-mp3");

            HttpEntity<String> entity = new HttpEntity<>(ssml, headers);
            ResponseEntity<byte[]> response = restTemplate.postForEntity(ttsEndpoint, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("TTS audio generated successfully");
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .body(response.getBody());
            } else {
                logger.error("Azure TTS API failed with status: {}", response.getStatusCode());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception e) {
            logger.error("Exception in generateTTS: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Health Check", description = "Returns service status.")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check requested");
        return ResponseEntity.ok("Service is running on port 8081");
    }

    private String getVoiceName(String language) {
        switch (language) {
            case "en-US":
                return "en-US-JennyNeural";
            case "es-ES":
                return "es-ES-ElviraNeural";
            case "fr-FR":
                return "fr-FR-DeniseNeural";
            case "de-DE":
                return "de-DE-KatjaNeural";
            case "it-IT":
                return "it-IT-ElsaNeural";
            case "zh-CN":
                return "zh-CN-XiaoxiaoNeural";
            case "hi-IN":
                return "hi-IN-AaravNeural";
            case "ja-JP":
                return "ja-JP-NanamiNeural";
            default:
                return "en-US-JennyNeural";
        }
    }
}
