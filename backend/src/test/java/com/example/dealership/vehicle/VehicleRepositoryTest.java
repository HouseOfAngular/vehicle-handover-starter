package com.example.dealership.vehicle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;

@DataJpaTest
class VehicleRepositoryTest {

    private static final String VIN = "WF0XXXGCDX0000004";

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void migrationsCreateSchemaWithSeededVehicles() {
        assertThat(vehicleRepository.findAll(Sort.by("id")))
                .extracting(Vehicle::getVin, Vehicle::getFuelType)
                .containsExactly(
                        tuple("WVWZZZ1KZAW000001", FuelType.PETROL),
                        tuple("WAUZZZ8V5KA000002", FuelType.DIESEL),
                        tuple("TMBJJ7NE8L0000003", FuelType.HYBRID));
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
