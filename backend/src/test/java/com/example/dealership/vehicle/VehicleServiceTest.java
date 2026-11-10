package com.example.dealership.vehicle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.dealership.error.ConflictException;
import com.example.dealership.error.NotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    private static final String VIN = "WVWZZZ1KZAW000001";
    private static final String OTHER_VIN = "WAUZZZ8V5KA000002";

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void findAllReturnsMappedVehicles() {
        when(vehicleRepository.findAll(Sort.by("id"))).thenReturn(List.of(vehicle(VIN), vehicle(OTHER_VIN)));

        List<VehicleResponse> responses = vehicleService.findAll();

        assertThat(responses).extracting(VehicleResponse::vin).containsExactly(VIN, OTHER_VIN);
    }

    @Test
    void findByIdThrowsNotFoundWhenVehicleDoesNotExist() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Vehicle with id 99 not found");
    }

    @Test
    void createSavesVehicle() {
        when(vehicleRepository.existsByVin(VIN)).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response = vehicleService.create(request(VIN));

        assertThat(response.vin()).isEqualTo(VIN);
        assertThat(response.make()).isEqualTo("Volkswagen");
        assertThat(response.fuelType()).isEqualTo(FuelType.PETROL);
        assertThat(response.plannedHandoverDate()).isEqualTo(LocalDate.of(2026, 12, 15));
    }

    @Test
    void createThrowsConflictWhenVinAlreadyExists() {
        when(vehicleRepository.existsByVin(VIN)).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.create(request(VIN)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Vehicle with VIN " + VIN + " already exists");
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void updateChangesExistingVehicle() {
        Vehicle vehicle = vehicle(VIN);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.existsByVinAndIdNot(OTHER_VIN, 1L)).thenReturn(false);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        VehicleResponse response = vehicleService.update(1L, request(OTHER_VIN));

        assertThat(response.vin()).isEqualTo(OTHER_VIN);
        assertThat(vehicle.getVin()).isEqualTo(OTHER_VIN);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void updateThrowsConflictWhenVinBelongsToAnotherVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(VIN)));
        when(vehicleRepository.existsByVinAndIdNot(OTHER_VIN, 1L)).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.update(1L, request(OTHER_VIN)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Vehicle with VIN " + OTHER_VIN + " already exists");
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void updateThrowsNotFoundWhenVehicleDoesNotExist() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(99L, request(VIN)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Vehicle with id 99 not found");
    }

    @Test
    void deleteRemovesExistingVehicle() {
        Vehicle vehicle = vehicle(VIN);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        vehicleService.delete(1L);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteThrowsNotFoundWhenVehicleDoesNotExist() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.delete(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Vehicle with id 99 not found");
    }

    private static Vehicle vehicle(String vin) {
        return new Vehicle(vin, "Volkswagen", "Golf", 2025, FuelType.PETROL, LocalDate.of(2026, 12, 15));
    }

    private static VehicleRequest request(String vin) {
        return new VehicleRequest(vin, "Volkswagen", "Golf", 2025, FuelType.PETROL, LocalDate.of(2026, 12, 15));
    }
}
