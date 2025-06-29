package com.document.scan.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.document.scan.entity.DocumentEntity;
import com.document.scan.repository.DocumentRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DocumentCleanupService {

    @Autowired
    private DocumentRepository documentRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Kolkata")
    public void deleteOldDocuments() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        List<DocumentEntity> oldDocuments = documentRepository.findByUploadedAtBefore(cutoffDate);
        log.info("Found {} documents older than 7 days", oldDocuments.size());
        documentRepository.deleteAll(oldDocuments);
        log.info("Deleted {} documents and their associated queries", oldDocuments.size());
    }
}