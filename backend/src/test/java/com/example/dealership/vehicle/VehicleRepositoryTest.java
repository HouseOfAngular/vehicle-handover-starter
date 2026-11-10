package com.example.dealership.vehicle;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class VehicleRepositoryTest {

    private static final String VIN = "WF0XXXGCDX0000004";
    private static final String OTHER_VIN = "WF0XXXGCDX0000005";

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByVinAndIdNotFindsVinOfAnotherVehicle() {
        Vehicle first = vehicleRepository.save(
                new Vehicle(VIN, "Ford", "Focus", 2026, FuelType.ELECTRIC, null));
        Vehicle second = vehicleRepository.save(
                new Vehicle(OTHER_VIN, "Ford", "Puma", 2025, FuelType.HYBRID, null));
        entityManager.flush();

        assertThat(vehicleRepository.existsByVinAndIdNot(VIN, second.getId())).isTrue();
        assertThat(vehicleRepository.existsByVinAndIdNot(VIN, first.getId())).isFalse();
        assertThat(vehicleRepository.existsByVin("WF0XXXGCDX0000006")).isFalse();
    }

    @Test
    void saveStoresVehicleThatCanBeReadBack() {
        Vehicle saved = vehicleRepository.save(
                new Vehicle(VIN, "Ford", "Focus", 2026, FuelType.ELECTRIC, LocalDate.of(2027, 3, 1)));
        entityManager.flush();
        entityManager.clear();

        Vehicle found = vehicleRepository.findById(saved.getId()).orElseThrow();

        assertThat(found).isNotSameAs(saved);
        assertThat(found.getVin()).isEqualTo(VIN);
        assertThat(found.getMake()).isEqualTo("Ford");
        assertThat(found.getModel()).isEqualTo("Focus");
        assertThat(found.getProductionYear()).isEqualTo(2026);
        assertThat(found.getFuelType()).isEqualTo(FuelType.ELECTRIC);
        assertThat(found.getPlannedHandoverDate()).isEqualTo(LocalDate.of(2027, 3, 1));
        assertThat(vehicleRepository.existsByVin(VIN)).isTrue();
        assertThat(vehicleRepository.existsByVinAndIdNot(VIN, saved.getId())).isFalse();
    }
}
