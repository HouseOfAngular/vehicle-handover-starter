package com.example.dealership.vehicle;

import java.time.Year;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductionYearValidator implements ConstraintValidator<ProductionYear, Integer> {

    static final int MIN_YEAR = 1990;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value >= MIN_YEAR && value <= Year.now().getValue() + 1;
    }
}
