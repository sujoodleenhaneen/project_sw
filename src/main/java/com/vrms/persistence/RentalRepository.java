package com.vrms.persistence;

import java.util.List;

import com.vrms.domain.Rental;

public interface RentalRepository {

    void save(Rental rental);

    List<Rental> findAll();

    Rental findById(String rentalId);

    void update(Rental rental);
}