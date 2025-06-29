package com.document.scan.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.document.scan.Validator.ContentValidator;

@Documented
@Constraint(validatedBy = ContentValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentValidation {
    String message() default "No harmful HTML tags allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}