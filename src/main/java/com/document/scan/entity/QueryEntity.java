package com.document.scan.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "query")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    @Column(name = "question", nullable = false, length = 1800)
    private String question;

    @Column(name = "answer", nullable = false, length = 9000)
    private String answer;

    @Column(name = "conversation_id", nullable = false, unique = true, length = 150)
    private String conversationId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}