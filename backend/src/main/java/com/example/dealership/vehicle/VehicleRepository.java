package com.example.dealership.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByVin(String vin);

    boolean existsByVinAndIdNot(String vin, Long id);
}
