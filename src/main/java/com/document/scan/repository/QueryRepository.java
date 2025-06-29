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
    List<QueryEntity> findByDocumentId(String documentId);

    /**
     * Find all queries by conversation ID
     */
    List<QueryEntity> findByConversationId(String conversationId);

    /**
     * Find all queries by created by user
     */
    List<QueryEntity> findByCreatedBy(String createdBy);

    /**
     * Find queries by document ID and conversation ID
     */
    List<QueryEntity> findByDocumentIdAndConversationId(String documentId, String conversationId);

    /**
     * Find queries created after a specific date
     */
    List<QueryEntity> findByCreatedAtAfter(LocalDateTime dateTime);

    /**
     * Find queries created between two dates
     */
    List<QueryEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find queries by document ID ordered by creation date (newest first)
     */
    List<QueryEntity> findByDocumentIdOrderByCreatedAtDesc(String documentId);

    /**
     * Find queries by conversation ID ordered by creation date (oldest first)
     */
    List<QueryEntity> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    /**
     * Count queries by document ID
     */
    long countByDocumentId(String documentId);

    /**
     * Count queries by conversation ID
     */
    long countByConversationId(String conversationId);

    /**
     * Find recent queries by user (last N queries)
     */
    List<QueryEntity> findTop10ByCreatedByOrderByCreatedAtDesc(String createdBy);

    /**
     * Custom query to find queries containing specific text in question
     */
    @Query("SELECT q FROM Query q WHERE q.question LIKE %:searchText%")
    List<QueryEntity> findByQuestionContaining(@Param("searchText") String searchText);

    /**
     * Custom query to find queries containing specific text in answer
     */
    @Query("SELECT q FROM Query q WHERE q.answer LIKE %:searchText%")
    List<QueryEntity> findByAnswerContaining(@Param("searchText") String searchText);

    /**
     * Custom query to find conversation history for a document
     */
    @Query("SELECT q FROM Query q WHERE q.documentId = :documentId AND q.conversationId = :conversationId ORDER BY q.createdAt ASC")
    List<QueryEntity> findConversationHistory(@Param("documentId") String documentId, @Param("conversationId") String conversationId);

    /**
     * Custom query to get query statistics by document
     */
    @Query("SELECT q.documentId, COUNT(q) as queryCount FROM Query q GROUP BY q.documentId")
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