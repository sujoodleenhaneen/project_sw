package com.vrms.domain;

public class Vehicle {

    private String id;
    private String brand;
    private String model;
    private double dailyRate;
    private VehicleStatus status;

    public Vehicle(String id, String brand, String model, double dailyRate, VehicleStatus status) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
}