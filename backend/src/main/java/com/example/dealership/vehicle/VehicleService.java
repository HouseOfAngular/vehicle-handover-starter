package com.example.dealership.vehicle;

import java.util.List;

import com.example.dealership.error.ConflictException;
import com.example.dealership.error.NotFoundException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll(Sort.by("id")).stream()
                .map(VehicleMapper::toResponse)
                .toList();
    }

    public VehicleResponse findById(Long id) {
        return VehicleMapper.toResponse(getVehicle(id));
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByVin(request.vin())) {
            throw vinAlreadyExists(request.vin());
        }
        Vehicle vehicle = vehicleRepository.save(VehicleMapper.toEntity(request));
        return VehicleMapper.toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = getVehicle(id);
        if (vehicleRepository.existsByVinAndIdNot(request.vin(), id)) {
            throw vinAlreadyExists(request.vin());
        }
        VehicleMapper.updateEntity(vehicle, request);
        return VehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(Long id) {
        vehicleRepository.delete(getVehicle(id));
    }

    private Vehicle getVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle with id " + id + " not found"));
    }

    private static ConflictException vinAlreadyExists(String vin) {
        return new ConflictException("Vehicle with VIN " + vin + " already exists");
    }
}
