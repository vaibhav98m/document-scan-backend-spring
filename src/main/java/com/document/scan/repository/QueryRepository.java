package com.document.scan.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.document.scan.entity.QueryEntity;

@Repository
public interface QueryRepository extends JpaRepository<QueryEntity, Integer> {

    /**
     * Find all queries by document ID
     */
    List<QueryEntity> findByDocument_DocumentId(Integer documentId);

    List<QueryEntity> findByDocumentUploadedById(Integer userId);

    /**
     * Custom query to find conversation history for a document
     */
    @Query("SELECT q FROM QueryEntity q WHERE q.document.documentId = :documentId AND q.conversationId = :conversationId ORDER BY q.createdAt ASC")
    List<QueryEntity> findConversationHistory(@Param("documentId") Integer documentId,
            @Param("conversationId") String conversationId);

    /**
     * Custom query to get query statistics by document
     */
    @Query("SELECT q.document.documentId, COUNT(q) as queryCount FROM QueryEntity q GROUP BY q.document.documentId")
    List<Object[]> getQueryStatsByDocument();

    /**
     * Delete queries older than specified date
     */
    void deleteByCreatedAtBefore(LocalDateTime dateTime);

    /**
     * Check if conversation exists
     */
    boolean existsByConversationId(String conversationId);

    /**
     * Find latest query in a conversation
     */
    QueryEntity findTopByConversationIdOrderByCreatedAtDesc(String conversationId);
}