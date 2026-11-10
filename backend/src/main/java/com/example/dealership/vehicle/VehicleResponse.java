package com.example.dealership.vehicle;

import java.time.LocalDate;

public record VehicleResponse(
        Long id,
        String vin,
        String make,
        String model,
        Integer productionYear,
        LocalDate plannedHandoverDate
) {
}
