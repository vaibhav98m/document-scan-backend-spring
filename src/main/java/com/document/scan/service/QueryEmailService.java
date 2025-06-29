package com.document.scan.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.document.scan.entity.QueryEntity;
import com.document.scan.entity.UserEntity;
import com.document.scan.repository.QueryRepository;
import com.document.scan.repository.UserRepository;

@Service
public class QueryEmailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private EmailService emailService;

    @Transactional(readOnly = true)
    public void sendQueriesToUser(Integer userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        UserEntity user = userOptional.get();
        List<QueryEntity> queries = queryRepository.findByDocumentUploadedById(userId);

        if (queries.isEmpty()) {
            throw new IllegalStateException("No queries found for user: " + user.getEmail());
        }

        // Generate PDF
        byte[] pdfBytes = pdfGenerationService.generateQueryPdf(queries);

        // Send email
        String subject = "Your Query Report";
        String body = "Dear " + user.getName()
                + ",\n\nAttached is the report of your queries.\n\nBest regards,\nDocument Scan Team";
        emailService.sendQueryPdfEmail(user.getEmail(), subject, body, pdfBytes);
    }
}