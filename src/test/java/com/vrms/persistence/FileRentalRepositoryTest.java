package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;

public class FileRentalRepositoryTest {

    @TempDir
    Path tempDir;

    private FileVehicleRepository vehicleRepository;
    private FileRentalRepository rentalRepository;

    @BeforeEach
    public void setUp() {
        vehicleRepository =
                new FileVehicleRepository(
                        tempDir.resolve("vehicles.txt")
                );

        rentalRepository =
                new FileRentalRepository(
                        tempDir.resolve("rentals.txt"),
                        vehicleRepository
                );
    }

    @Test
    public void saveAndRead_shouldPersistTotalCost() {
        Vehicle vehicle =
                vehicleRepository.findById("V1");

        Rental rental = new Rental(
                "R1",
                vehicle,
                "Ahmad",
                "ahmad@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                RentalStatus.CLOSED
        );

        rental.setTotalCost(180.0);

        rentalRepository.save(rental);

        Rental savedRental =
                rentalRepository.findById("R1");

        assertEquals(
                RentalStatus.CLOSED,
                savedRental.getStatus()
        );

        assertEquals(
                180.0,
                savedRental.getTotalCost(),
                0.001
        );
    }

    @Test
    public void findAll_oldSevenColumnRecord_shouldUseZeroCost()
            throws Exception {

        Path rentalsFile =
                tempDir.resolve("rentals.txt");

        Files.write(
                rentalsFile,
                Arrays.asList(
                        "R2,Sara,sara@example.com,"
                                + "V1,2026-07-01,"
                                + "2026-07-05,ACTIVE"
                ),
                StandardCharsets.UTF_8
        );

        Rental savedRental =
                rentalRepository.findById("R2");

        assertEquals(
                0.0,
                savedRental.getTotalCost(),
                0.001
        );
    }

    @Test
    public void update_shouldReplaceRentalInsteadOfCreatingDuplicate() {
        Vehicle vehicle =
                vehicleRepository.findById("V1");

        Rental rental = new Rental(
                "R3",
                vehicle,
                "Omar",
                "omar@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                RentalStatus.ACTIVE
        );

        rentalRepository.save(rental);

        rental.closeRental();
        rental.setTotalCost(160.0);

        rentalRepository.update(rental);

        assertEquals(
                1,
                rentalRepository.findAll().size()
        );

        assertEquals(
                RentalStatus.CLOSED,
                rentalRepository.findById("R3").getStatus()
        );

        assertEquals(
                160.0,
                rentalRepository.findById("R3").getTotalCost(),
                0.001
        );
    }
}