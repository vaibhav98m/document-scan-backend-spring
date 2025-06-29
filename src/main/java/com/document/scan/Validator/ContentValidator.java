package com.document.scan.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import com.document.scan.annotation.ContentValidation;

public class ContentValidator implements ConstraintValidator<ContentValidation, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true; // handle @NotNull separately if needed
        // Jsoup.clean with Safelist.none() removes all HTML tags
        return Jsoup.isValid(value, Safelist.basic());
    }
}