package com.vrms.persistence;

import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;

/**
 * Stores rental records temporarily in memory.
 */
public class InMemoryRentalRepository implements RentalRepository {

    /**
     * List containing all stored rental records.
     */
    private final List<Rental> rentals;

    /**
     * Creates an empty in-memory rental repository.
     */
    public InMemoryRentalRepository() {
        rentals = new ArrayList<>();
    }

    /**
     * Saves a rental record in memory.
     *
     * @param rental the rental record to save
     */
    @Override
    public void save(Rental rental) {
        rentals.add(rental);
    }

    /**
     * Returns a copy of all stored rental records.
     *
     * @return list containing all stored rentals
     */
    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals);
    }
}