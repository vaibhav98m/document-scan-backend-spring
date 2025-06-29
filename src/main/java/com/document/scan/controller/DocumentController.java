package com.document.scan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.document.scan.service.DocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Controller", description = "Upload, summarize and analyze documents")
@Validated
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "Upload a document", description = "Uploads a document to the server and returns metadata.")
    public ResponseEntity<ApiResponse<DocumentData>> uploadDocument(
            @RequestParam(value = "document", required = true) @Valid MultipartFile file,
            @Parameter(description = "User ID of the uploader", required = false, schema = @Schema(type = "string")) @Valid @RequestParam(required = true) @NotBlank(message = "userId is required") @NumberFormat(pattern = "^\\\\d{6}$") String userId) {
        if (file.isEmpty()) {
            ApiResponse<DocumentData> errorResponse = new ApiResponse<>("failed", "File is empty", "File upload error",
                    null);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            DocumentData documentData = documentService.uploadDocument(file, userId);
            ApiResponse<DocumentData> response = new ApiResponse<>("success", "Document uploaded", null, documentData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
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
            DocumentSummary summaryData = documentService.generateSummary(documentId);
            return ResponseEntity.ok(new ApiResponse<>("success", "Summary generated.", null, summaryData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("failed", "Failed to generate summary.", e.getMessage(), null));
        }
    }

    @PostMapping("/query")
    @Operation(summary = "Query to a document", description = "Returns a query ans for the asked document")
    public ResponseEntity<ApiResponse<QueryResponse>> queryDocument(@RequestBody @Valid QueryRequest request) {
        try {
            QueryResponse response = documentService.queryDocument(request);
            return ResponseEntity.ok(new ApiResponse<>("success", "Query answered.", null, response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("failed", "Query failed.", e.getMessage(), null));
        }
    }

    @GetMapping("{documentId}/query/history")
    @Operation(summary = "Query to a document", description = "Returns a query ans for the asked document")
    public ResponseEntity<ApiResponse<List<QueryResponse>>> queryHistory(
            @Parameter(description = "Document ID", required = true) @PathVariable @Valid @NotBlank(message = "documentId is required") @NumberFormat String documentId) {
        try {
            List<QueryResponse> response = documentService.queryHistory(Integer.parseInt(documentId));
            return ResponseEntity.ok(new ApiResponse<>("success", "Query history retrieved.", null, response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("failed", "Query retrieval failed.", e.getMessage(), null));
        }
    }

    @GetMapping("/{documentId}/insights")
    @Operation(summary = "Generate document key insights", description = "Returns more detailed key insights of the uploaded document.")
    public ResponseEntity<ApiResponse<DocumentInsights>> generateInsights(@PathVariable String documentId) {
        try {
            DocumentInsights insights = documentService.generateInsights(documentId);
            return ResponseEntity.ok(new ApiResponse<>("success", "Insights generated.", null, insights));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("failed", "Failed to extract insights.", e.getMessage(), null));
        }
    }
}