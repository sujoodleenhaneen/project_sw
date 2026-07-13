package com.vrms.persistence;

import java.util.List;

import com.vrms.domain.Rental;

/**
 * Defines the operations needed to store and retrieve rental records.
 */
public interface RentalRepository {

    /**
     * Saves a rental record.
     *
     * @param rental the rental record to save
     */
    void save(Rental rental);

    /**
     * Returns all rental records.
     *
     * @return list of rental records
     */
    List<Rental> findAll();
}