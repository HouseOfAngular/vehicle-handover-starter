package com.example.dealership.vehicle;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 17)
    private String vin;

    @Column(nullable = false, length = 50)
    private String make;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false)
    private Integer productionYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuelType fuelType;

    private LocalDate plannedHandoverDate;

    protected Vehicle() {
    }

    public Vehicle(String vin, String make, String model, Integer productionYear, FuelType fuelType,
            LocalDate plannedHandoverDate) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.productionYear = productionYear;
        this.fuelType = fuelType;
        this.plannedHandoverDate = plannedHandoverDate;
    }

    public Long getId() {
        return id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public LocalDate getPlannedHandoverDate() {
        return plannedHandoverDate;
    }

    public void setPlannedHandoverDate(LocalDate plannedHandoverDate) {
        this.plannedHandoverDate = plannedHandoverDate;
    }
}
