package com.document.scan.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.document.scan.model.ApiResponse;
import com.document.scan.model.DocumentData;
import com.document.scan.model.DocumentInsights;
import com.document.scan.model.DocumentSummary;
import com.document.scan.model.QueryRequest;
import com.document.scan.model.QueryResponse;
import com.document.scan.utility.DocumentParserUtil;
import com.document.scan.utility.OpenApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Controller", description = "Upload, summarize and analyze documents")
public class DocumentController {

	@Autowired
	private DocumentParserUtil parserUtil;

	private static final String UPLOAD_DIR = "upload/";

	@Autowired
	private OpenApiClient openApiClient;

	@PostMapping("/upload")
	@Operation(summary = "Upload a document", description = "Uploads a document to the server and returns metadata.")
	public ResponseEntity<ApiResponse<DocumentData>> uploadDocument(@RequestParam("document") MultipartFile file) {
		if (file.isEmpty()) {
			ApiResponse<DocumentData> errorResponse = new ApiResponse<>("failed", "File is empty", "File upload error",
					null);
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		try {
			String docId = UUID.randomUUID().toString();
			String originalFilename = file.getOriginalFilename();
			String newFileName = docId + "_" + originalFilename;
			Path uploadPath = Paths.get(UPLOAD_DIR);
			Files.createDirectories(uploadPath);
			Path filePath = uploadPath.resolve(newFileName);
			file.transferTo(filePath);

			DocumentData doc = new DocumentData();
			doc.setId(docId);
			doc.setFileName(originalFilename);
			doc.setFileSize(file.getSize());
			doc.setFileType(file.getContentType());
			doc.setUploadedAt(new Date());

			ApiResponse<DocumentData> response = new ApiResponse<>("success", "Document uploaded", null, doc);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (IOException e) {
			ApiResponse<DocumentData> errorResponse = new ApiResponse<>("failed", "File upload failed", e.getMessage(),
					null);
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{documentId}/summary")
	@Operation(summary = "Generate document summary", description = "Returns a summary and key insights of the uploaded document.")
	public ResponseEntity<ApiResponse<DocumentSummary>> generateSummary(
			@PathVariable(name = "documentId") String documentId) {
		try {
			String summary = openApiClient.queryDocument(
					"Summarize this document as well as extract key insights in the provided Strict JSON format always: "
							+ "|DSVR|{\"summary\":\"summary of doc\",\"wordCount\":\"word Count in keyinsights and summry | 0\",\"readingTime\":\"reading time in minute number | 0\",\"keyInsights\":[\"Insight-1\", ..... , \"Insight-n\"]}|DSVR| : "
							+ parserUtil.extractTextByDocId(documentId));

			String[] summarySplit = summary.split("\\|DSVR\\|");
			JSONObject jsonResponse = null;
			List<String> keyInsights = new ArrayList<>();
			
			Integer wordCount=0;
			Integer readingTimeInMins = 0;

			System.out.println("------------>" + parserUtil.extractTextByDocId(documentId));

			for (String jsonString : summarySplit) {

				jsonString.replace("\n", "");
				try {
					jsonResponse = new JSONObject(jsonString);
					JSONArray keyInsgt = jsonResponse.getJSONArray("keyInsights");
					summary = jsonResponse.getString("summary");
					
					wordCount = jsonResponse.getInt("wordCount");
					readingTimeInMins = jsonResponse.getInt("readingTime");
					
					for (int i = 0; i < keyInsgt.length(); i++) {
						keyInsights.add(keyInsgt.getString(i));
					}
				} catch (JSONException | NumberFormatException ex) {
					System.out.println("========>" + ex.getMessage());
					continue;
				}
			}

			DocumentSummary summaryData = new DocumentSummary(UUID.randomUUID().toString(), documentId, summary,
					Date.from(Instant.now()), keyInsights);
			
			summaryData.setWordCount(wordCount);
			summaryData.setReadingTime(readingTimeInMins);
			

			return ResponseEntity.ok(new ApiResponse<>("success", "Summary generated.", null, summaryData));

		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse<>("failed", "Failed to generate summary.", e.getMessage(), null));
		}
	}

	@PostMapping("/query")
	@Operation(summary = "Query to a document", description = "Returns a query ans for the asked document")
	public ResponseEntity<ApiResponse<QueryResponse>> queryDocument(@RequestBody QueryRequest request) {
		try {
			String answer = openApiClient.queryDocument("Q: " + request.getQuestion() + "\nA (based on document): "
					+ parserUtil.extractTextByDocId(request.getDocumentId()));
			QueryResponse response = new QueryResponse(UUID.randomUUID().toString(), request.getDocumentId(),
					request.getQuestion(), answer, Date.from(Instant.now()), Math.random() // Simulated confidence
			);

			return ResponseEntity.ok(new ApiResponse<>("success", "Query answered.", null, response));

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ApiResponse<>("failed", "Query failed.", e.getMessage(), null));
		}
	}

	@GetMapping("/{documentId}/insights")
	@Operation(summary = "Generate document key insights", description = "Returns more detailed key insights of the uploaded document.")
	public ResponseEntity<ApiResponse<DocumentInsights>> generateInsights(
			@PathVariable("documentId") String documentId) {

		try {

			String keyInsights = "You are an AI assistant tasked with analyzing a document and extracting all meaningful pieces of information in a structured and organized manner.\r\n"
					+ "\r\n"
					+ "For each important item in the document (e.g., a decision, a risk, a milestone, a legal point, a key person, a financial value, a process, or other insights), create an entry with the following properties:\r\n"
					+ "\r\n" + "id: a unique string identifier (e.g., insight-001)\r\n" + "\r\n"
					+ "type: one of the following types:\r\n"
					+ "\"decision\" | \"data\" | \"date\" | \"person\" | \"location\" | \"amount\" | \"risk\" | \"opportunity\" | \"action\" | \"milestone\" | \"metric\" | \"compliance\" | \"process\" | \"technology\" | \"financial\" | \"legal\" | \"strategic\" | \"operational\" | \"other\"\r\n"
					+ "\r\n"
					+ "priority: \"high\", \"medium\", or \"low\" based on its importance in the context of the document.\r\n"
					+ "\r\n" + "content: a short, informative statement summarizing the key point.\r\n" + "\r\n"
					+ "relevance: a number from 0 to 1 reflecting the significance of the insight in the document.\r\n"
					+ "\r\n" + "context (optional): a short phrase or sentence giving context to the insight.\r\n"
					+ "\r\n" + "category (optional): a broader theme or classification for grouping insights.\r\n"
					+ "\r\n" + "tags (optional): list of related tags that improve searchability and filtering.\r\n"
					+ "\r\n" + "Return the result as a JSON object in the following format:\r\n" + "\r\n" + "json\r\n"
					+ "Copy\r\n" + "Edit\r\n" + "{\r\n" + "  \"insights\": [\r\n" + "    {\r\n"
					+ "      \"id\": \"string\",\r\n" + "      \"type\": \"decision | data | ...\", \r\n"
					+ "      \"priority\": \"high | medium | low\",\r\n" + "      \"content\": \"string\",\r\n"
					+ "      \"relevance\": number,\r\n" + "      \"context\": \"string (optional)\",\r\n"
					+ "      \"category\": \"string (optional)\",\r\n" + "      \"tags\": [\"string\"]\r\n"
					+ "    }\r\n" + "  ]\r\n" + "}\r\n"
					+ "Be concise yet precise. Focus on capturing the most relevant points from the document.\r\n"
					+ "Only return the JSON — no extra commentary or explanation: ";
			String rawInsights = openApiClient.queryDocument(keyInsights + parserUtil.extractTextByDocId(documentId));

			String cleanedJson = rawInsights.replaceAll("(?s)```json\\s*", "") // remove ```json
					.replaceAll("(?s)```", "") // remove closing ```
					.trim();

			DocumentInsights insightest = new ObjectMapper().readValue(cleanedJson, DocumentInsights.class);
			insightest.setDocumentId(documentId);
			insightest.setGeneratedAt(Date.from(Instant.now()));
			insightest.setId(UUID.randomUUID().toString());

			return ResponseEntity.ok(new ApiResponse<>("success", "Insights generated.", null, insightest));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse<>("failed", "Failed to extract insights.", e.getMessage(), null));
		}
	}

}
