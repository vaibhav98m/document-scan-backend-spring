package com.document.scan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.document.scan.entity.DocumentEntity;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer> {

    /**
     * Find documents by name (exact match)
     */
    List<DocumentEntity> findByDocumentName(String documentName);

   
    @Query("SELECT d.uploadedBy, SUM(CAST(d.documentSize AS LONG)) FROM DocumentEntity d GROUP BY d.uploadedBy")
    List<Object[]> getTotalStorageByUser();
}