package com.document.scan.service;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.document.scan.model.DocumentData;
import com.document.scan.model.DocumentInsights;
import com.document.scan.model.DocumentSummary;
import com.document.scan.model.QueryRequest;
import com.document.scan.model.QueryResponse;
import com.document.scan.utility.DocumentParserUtil;
import com.document.scan.utility.OpenApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DocumentService {

    @Autowired
    private DocumentParserUtil parserUtil;

    @Autowired
    private OpenApiClient openApiClient;

    private static final String UPLOAD_DIR = "upload/";

    /**
     * Uploads a document and returns document metadata
     */
    public DocumentData uploadDocument(MultipartFile file) throws IOException {
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

        return doc;
    }

    /**
     * Generates a summary and key insights for the document
     */
    public DocumentSummary generateSummary(String documentId) throws Exception {
        String promptTemplate = "Summarize this document as well as extract key insights in the provided Strict JSON format always: "
                + "|DSVR|{\"summary\":\"summary of doc\",\"wordCount\":\"word Count in keyinsights and summry | 0\",\"readingTime\":\"reading time in minute number | 0\",\"keyInsights\":[\"Insight-1\", ..... , \"Insight-n\"]}|DSVR| : ";
        
        String documentText = parserUtil.extractTextByDocId(documentId);
        String summary = openApiClient.queryDocument(promptTemplate + documentText);

        String[] summarySplit = summary.split("\\|DSVR\\|");
        JSONObject jsonResponse = null;
        List<String> keyInsights = new ArrayList<>();
        
        Integer wordCount = 0;
        Integer readingTimeInMins = 0;
        String summaryText = "";

        for (String jsonString : summarySplit) {
            jsonString = jsonString.replace("\n", "");
            try {
                jsonResponse = new JSONObject(jsonString);
                JSONArray keyInsgt = jsonResponse.getJSONArray("keyInsights");
                summaryText = jsonResponse.getString("summary");
                
                wordCount = jsonResponse.getInt("wordCount");
                readingTimeInMins = jsonResponse.getInt("readingTime");
                
                for (int i = 0; i < keyInsgt.length(); i++) {
                    keyInsights.add(keyInsgt.getString(i));
                }
                break; // Exit loop once we find valid JSON
            } catch (JSONException | NumberFormatException ex) {
                System.out.println("JSON parsing error: " + ex.getMessage());
                continue;
            }
        }

        DocumentSummary summaryData = new DocumentSummary(
            UUID.randomUUID().toString(), 
            documentId, 
            summaryText,
            Date.from(Instant.now()), 
            keyInsights
        );
        
        summaryData.setWordCount(wordCount);
        summaryData.setReadingTime(readingTimeInMins);
        
        return summaryData;
    }

    /**
     * Processes a query against a document and returns the answer
     */
    public QueryResponse queryDocument(QueryRequest request) throws Exception {
        String promptTemplate = "Q: " + request.getQuestion() + "\nA (based on document): ";
        String documentText = parserUtil.extractTextByDocId(request.getDocumentId());
        String answer = openApiClient.queryDocument(promptTemplate + documentText);
        
        QueryResponse response = new QueryResponse(
            UUID.randomUUID().toString(), 
            request.getDocumentId(),
            request.getQuestion(), 
            answer, 
            Date.from(Instant.now()), 
            Math.random() // Simulated confidence
        );

        return response;
    }

    /**
     * Generates detailed insights for the document
     */
    public DocumentInsights generateInsights(String documentId) throws Exception {
        String insightsPrompt = buildInsightsPrompt();
        String documentText = parserUtil.extractTextByDocId(documentId);
        String rawInsights = openApiClient.queryDocument(insightsPrompt + documentText);

        String cleanedJson = cleanJsonResponse(rawInsights);
        
        DocumentInsights insights = new ObjectMapper().readValue(cleanedJson, DocumentInsights.class);
        insights.setDocumentId(documentId);
        insights.setGeneratedAt(Date.from(Instant.now()));
        insights.setId(UUID.randomUUID().toString());

        return insights;
    }

    /**
     * Builds the comprehensive insights prompt
     */
    private String buildInsightsPrompt() {
        return "You are an AI assistant tasked with analyzing a document and extracting all meaningful pieces of information in a structured and organized manner.\r\n"
                + "\r\n"
                + "For each important item in the document (e.g., a decision, a risk, a milestone, a legal point, a key person, a financial value, a process, or other insights), create an entry with the following properties:\r\n"
                + "\r\n" 
                + "id: a unique string identifier (e.g., insight-001)\r\n" 
                + "\r\n"
                + "type: one of the following types:\r\n"
                + "\"decision\" | \"data\" | \"date\" | \"person\" | \"location\" | \"amount\" | \"risk\" | \"opportunity\" | \"action\" | \"milestone\" | \"metric\" | \"compliance\" | \"process\" | \"technology\" | \"financial\" | \"legal\" | \"strategic\" | \"operational\" | \"other\"\r\n"
                + "\r\n"
                + "priority: \"high\", \"medium\", or \"low\" based on its importance in the context of the document.\r\n"
                + "\r\n" 
                + "content: a short, informative statement summarizing the key point.\r\n" 
                + "\r\n"
                + "relevance: a number from 0 to 1 reflecting the significance of the insight in the document.\r\n"
                + "\r\n" 
                + "context (optional): a short phrase or sentence giving context to the insight.\r\n"
                + "\r\n" 
                + "category (optional): a broader theme or classification for grouping insights.\r\n"
                + "\r\n" 
                + "tags (optional): list of related tags that improve searchability and filtering.\r\n"
                + "\r\n" 
                + "Return the result as a JSON object in the following format:\r\n" 
                + "\r\n" 
                + "json\r\n"
                + "Copy\r\n" 
                + "Edit\r\n" 
                + "{\r\n" 
                + "  \"insights\": [\r\n" 
                + "    {\r\n"
                + "      \"id\": \"string\",\r\n" 
                + "      \"type\": \"decision | data | ...\", \r\n"
                + "      \"priority\": \"high | medium | low\",\r\n" 
                + "      \"content\": \"string\",\r\n"
                + "      \"relevance\": number,\r\n" 
                + "      \"context\": \"string (optional)\",\r\n"
                + "      \"category\": \"string (optional)\",\r\n" 
                + "      \"tags\": [\"string\"]\r\n"
                + "    }\r\n" 
                + "  ]\r\n" 
                + "}\r\n"
                + "Be concise yet precise. Focus on capturing the most relevant points from the document.\r\n"
                + "Only return the JSON — no extra commentary or explanation: ";
    }

    /**
     * Cleans the JSON response by removing markdown formatting
     */
    private String cleanJsonResponse(String rawJson) {
        return rawJson.replaceAll("(?s)```json\\s*", "") // remove ```json
                .replaceAll("(?s)```", "") // remove closing ```
                .trim();
    }
}