package com.document.scan.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sourceforge.tess4j.TesseractException;

@Component
public class DocumentParserUtil {

	@Autowired
	OCRService ocrService;

	private static final String UPLOAD_DIR = "upload/";

	public String extractTextByDocId(String docId) throws IOException {
		File uploadFolder = new File(UPLOAD_DIR);
		if (!uploadFolder.exists())
			throw new FileNotFoundException("Upload directory not found");

		File[] matchingFiles = uploadFolder.listFiles((dir, name) -> name.startsWith(docId + "_"));
		if (matchingFiles == null || matchingFiles.length == 0) {
			throw new FileNotFoundException("Document with ID " + docId + " not found");
		}

		File file = matchingFiles[0];
		String contentType = Files.probeContentType(file.toPath());
		if (contentType == null)
			throw new IllegalArgumentException("Unknown file type for " + file.getName());

		try (InputStream is = new FileInputStream(file)) {
			return extractTextFromStream(is, contentType);
		}
	}

	private String extractTextFromStream(InputStream is, String contentType) throws IOException {
		String text;

		switch (contentType) {
		case "text/plain":
			text = new String(is.readAllBytes());
			break;

		case "application/pdf":
			try (PDDocument document = PDDocument.load(is)) {
				text = new PDFTextStripper().getText(document);
			}
			break;

		case "application/msword":
			try (HWPFDocument doc = new HWPFDocument(is)) {
				WordExtractor extractor = new WordExtractor(doc);
				text = extractor.getText();
			}
			break;

		case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
			try (XWPFDocument docx = new XWPFDocument(is)) {
				XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
				text = extractor.getText();
			}
			break;

		case "application/vnd.ms-excel":
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
			try (Workbook workbook = contentType.contains("openxml") ? new XSSFWorkbook(is)
					: WorkbookFactory.create(is)) {
				StringBuilder sb = new StringBuilder();
				for (Sheet sheet : workbook) {
					for (Row row : sheet) {
						for (Cell cell : row) {
							sb.append(cell.toString()).append(" ");
						}
					}
				}
				text = sb.toString();
			}
			break;

		case "image/jpeg":
		case "image/png":
			BufferedImage image = ImageIO.read(is);
			try {
				text = ocrService.extractText(image);
			} catch (TesseractException | IOException e) {
				e.printStackTrace();
			}
			text = "Failed";
			break;

		default:
			throw new IllegalArgumentException("Unsupported file type: " + contentType);
		}

		return text;
	}
}
