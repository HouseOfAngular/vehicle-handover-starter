package com.example.dealership.vehicle;

public final class VehicleMapper {

    private VehicleMapper() {
    }

    public static Vehicle toEntity(VehicleRequest request) {
        return new Vehicle(
                request.vin(),
                request.make(),
                request.model(),
                request.productionYear(),
                request.fuelType(),
                request.plannedHandoverDate());
    }

    public static void updateEntity(Vehicle vehicle, VehicleRequest request) {
        vehicle.setVin(request.vin());
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setProductionYear(request.productionYear());
        vehicle.setFuelType(request.fuelType());
        vehicle.setPlannedHandoverDate(request.plannedHandoverDate());
    }

    public static VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getVin(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getProductionYear(),
                vehicle.getFuelType(),
                vehicle.getPlannedHandoverDate());
    }
}
