package com.vrms.persistence;

import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;

public class InMemoryRentalRepository implements RentalRepository {

    private List<Rental> rentals;

    public InMemoryRentalRepository() {
        rentals = new ArrayList<>();
    }

    @Override
    public void save(Rental rental) {
        rentals.add(rental);
    }

    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals);
    }
}