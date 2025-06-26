package com.document.scan.utility;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.document.scan.model.OCRResult;
import com.document.scan.model.WordConfidence;

import jakarta.annotation.PostConstruct;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;

@Component
public class OCRService {

	private Tesseract tesseract;

	@Value("${app.tesseract.datapath}")
	private String tessDataPath;

	@PostConstruct
	public void init() {
		tesseract = new Tesseract();
		tesseract.setDatapath(tessDataPath);
		tesseract.setLanguage("eng"); // Default to English
		tesseract.setPageSegMode(6);
		tesseract.setOcrEngineMode(1);
		// Set additional configuration variables
		tesseract.setVariable("tessedit_char_whitelist",
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,!?:;-()[]{}\"' ");
		tesseract.setVariable("preserve_interword_spaces", "1");
	}

	public String extractText(BufferedImage file) throws TesseractException, IOException {
		// Convert MultipartFile to BufferedImage
		BufferedImage image = preprocessImage(file);

		// Preprocess image for better OCR results
		BufferedImage processedImage = preprocessImage(image);

		// Try multiple approaches if first fails
		String result = tesseract.doOCR(processedImage);

		if (result == null || result.trim().isEmpty()) {
			// Try with different preprocessing
			processedImage = enhancedPreprocessing(image);
			result = tesseract.doOCR(processedImage);
		}

		if (result == null || result.trim().isEmpty()) {
			// Try with different page segmentation mode
			tesseract.setPageSegMode(3); // Fully automatic page segmentation
			result = tesseract.doOCR(processedImage);
			tesseract.setPageSegMode(6); // Reset to default
		}

		return result;
	}

	private BufferedImage enhancedPreprocessing(BufferedImage original) {
		// 1. Scale up if image is too small
		BufferedImage scaled = scaleImage(original);

		// 2. Convert to grayscale
		BufferedImage grayscale = convertToGrayscale(scaled);

		// 3. Apply contrast enhancement
		BufferedImage contrasted = enhanceContrast(grayscale, 1.5f);

		// 4. Apply noise reduction
		BufferedImage denoised = reduceNoise(contrasted);

		// 5. Apply sharpening
		BufferedImage sharpened = sharpenImage(denoised);

		// 6. Binarize (convert to black and white)
		BufferedImage binary = binarizeImage(sharpened);

		return binary;
	}

	private BufferedImage scaleImage(BufferedImage original) {
		int minSize = 600; // Minimum size for better OCR

		if (original.getWidth() < minSize || original.getHeight() < minSize) {
			double scale = Math.max((double) minSize / original.getWidth(), (double) minSize / original.getHeight());

			int newWidth = (int) (original.getWidth() * scale);
			int newHeight = (int) (original.getHeight() * scale);

			BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = scaled.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
			g2d.dispose();

			return scaled;
		}

		return original;
	}

	private BufferedImage convertToGrayscale(BufferedImage original) {
		BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = grayscale.createGraphics();
		g2d.drawImage(original, 0, 0, null);
		g2d.dispose();
		return grayscale;
	}

	private BufferedImage enhanceContrast(BufferedImage image, float contrast) {
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				int gray = rgb & 0xFF;

				// Apply contrast enhancement
				int newGray = (int) (((gray - 128) * contrast) + 128);
				newGray = Math.max(0, Math.min(255, newGray));

				int newRgb = (newGray << 16) | (newGray << 8) | newGray;
				result.setRGB(x, y, newRgb);
			}
		}

		return result;
	}

	private BufferedImage binarizeImage(BufferedImage image) {
		// Otsu's thresholding for automatic binarization
		int[] histogram = new int[256];

		// Calculate histogram
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int gray = image.getRGB(x, y) & 0xFF;
				histogram[gray]++;
			}
		}

		// Find optimal threshold using Otsu's method
		int threshold = findOtsuThreshold(histogram, image.getWidth() * image.getHeight());

		// Apply threshold
		BufferedImage binary = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int gray = image.getRGB(x, y) & 0xFF;
				int newColor = gray > threshold ? 0xFFFFFF : 0x000000;
				binary.setRGB(x, y, newColor);
			}
		}

		return binary;
	}

	private int findOtsuThreshold(int[] histogram, int totalPixels) {
		float sum = 0;
		for (int i = 0; i < 256; i++) {
			sum += i * histogram[i];
		}

		float sumB = 0;
		int wB = 0;
		int wF = 0;
		float varMax = 0;
		int threshold = 0;

		for (int i = 0; i < 256; i++) {
			wB += histogram[i];
			if (wB == 0)
				continue;

			wF = totalPixels - wB;
			if (wF == 0)
				break;

			sumB += (float) (i * histogram[i]);

			float mB = sumB / wB;
			float mF = (sum - sumB) / wF;

			float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

			if (varBetween > varMax) {
				varMax = varBetween;
				threshold = i;
			}
		}

		return threshold;
	}

	private BufferedImage sharpenImage(BufferedImage image) {
		float[] sharpenKernel = { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0.0f };

		BufferedImageOp sharpenOp = new ConvolveOp(new Kernel(3, 3, sharpenKernel), ConvolveOp.EDGE_NO_OP, null);

		return sharpenOp.filter(image, null);
	}

	private BufferedImage reduceNoise(BufferedImage image) {
		// Gaussian blur for noise reduction
		float[] blurKernel = { 1 / 16f, 2 / 16f, 1 / 16f, 2 / 16f, 4 / 16f, 2 / 16f, 1 / 16f, 2 / 16f, 1 / 16f };

		BufferedImageOp blurOp = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, null);

		return blurOp.filter(image, null);
	}

	public OCRResult extractTextWithConfidence(BufferedImage file) throws TesseractException, IOException {

		BufferedImage processedImage = preprocessImage(file);

		// Get results with word-level confidence
		List<Word> words = tesseract.getWords(processedImage, 1);

		StringBuilder fullText = new StringBuilder();
		List<WordConfidence> wordConfidences = new ArrayList<>();

		for (Word word : words) {
			fullText.append(word.getText()).append(" ");
			wordConfidences.add(new WordConfidence(word.getText(), word.getConfidence()));
		}

		return new OCRResult(fullText.toString().trim(), wordConfidences);
	}

	private BufferedImage preprocessImage(BufferedImage original) {
		// Convert to grayscale
		BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = grayscale.createGraphics();
		g2d.drawImage(original, 0, 0, null);
		g2d.dispose();

		// Scale up for better recognition (if image is small)
		if (original.getWidth() < 300 || original.getHeight() < 300) {
			int newWidth = original.getWidth() * 2;
			int newHeight = original.getHeight() * 2;

			BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g2dScaled = scaled.createGraphics();
			g2dScaled.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2dScaled.drawImage(grayscale, 0, 0, newWidth, newHeight, null);
			g2dScaled.dispose();

			return scaled;
		}

		return grayscale;
	}

	// Advanced preprocessing methods
	public BufferedImage enhanceImage(BufferedImage original) {
		BufferedImage enhanced = new BufferedImage(original.getWidth(), original.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);

		// Convert to grayscale first
		Graphics2D g2d = enhanced.createGraphics();
		g2d.drawImage(original, 0, 0, null);
		g2d.dispose();

		// Apply contrast enhancement
		enhanced = adjustContrast(enhanced, 1.2f);

		// Apply noise reduction
		enhanced = reduceNoise(enhanced);

		return enhanced;
	}

	private BufferedImage adjustContrast(BufferedImage image, float contrast) {
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				int gray = (rgb >> 16) & 0xFF;

				// Apply contrast
				int newGray = (int) (((gray - 128) * contrast) + 128);
				newGray = Math.max(0, Math.min(255, newGray));

				int newRgb = (newGray << 16) | (newGray << 8) | newGray;
				result.setRGB(x, y, newRgb);
			}
		}

		return result;
	}

}