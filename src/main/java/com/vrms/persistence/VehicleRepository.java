package com.vrms.persistence;

import java.util.List;

import com.vrms.domain.Vehicle;

public interface VehicleRepository {

    List<Vehicle> findAll();
}