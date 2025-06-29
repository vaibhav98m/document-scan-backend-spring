package com.document.scan.service;

import com.document.scan.entity.QueryEntity;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGenerationService {

    public byte[] generateQueryPdf(List<QueryEntity> queries) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title
            document.add(new Paragraph("Query Report")
                    .setFontSize(18)
                    .setBold());

            // Create table with 4 columns, set column widths to accommodate large text
            Table table = new Table(UnitValue.createPercentArray(new float[] { 20, 50, 60, 20 }))
                    .useAllAvailableWidth();
            table.addHeaderCell("Document Name");
            table.addHeaderCell("Question");
            table.addHeaderCell("Answer");
            table.addHeaderCell("Created At");

            // Add query data
            for (QueryEntity query : queries) {
                table.addCell(query.getDocument().getDocumentName());
                table.addCell(query.getQuestion());
                table.addCell(query.getAnswer());
                table.addCell(query.getCreatedAt().toString());
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }
}