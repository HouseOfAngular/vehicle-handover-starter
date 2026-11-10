package com.example.dealership.vehicle;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VehicleRequest(
        @NotBlank
        @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "must be 17 characters: digits and capital letters except I, O, Q")
        String vin,

        @NotBlank
        @Size(max = 50)
        String make,

        @NotBlank
        @Size(max = 50)
        String model,

        @NotNull
        @ProductionYear
        Integer productionYear,

        @NotNull
        FuelType fuelType,

        LocalDate plannedHandoverDate
) {
}
