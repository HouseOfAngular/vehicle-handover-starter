package com.example.dealership.vehicle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ProductionYearValidator.class)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductionYear {

    String message() default "must be between 1990 and next year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
