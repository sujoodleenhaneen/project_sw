package com.vrms.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;

public class FileRentalRepository implements RentalRepository {

    private final Path filePath;
    private final VehicleRepository vehicleRepository;

    public FileRentalRepository() {
        this(
                Paths.get("data", "rentals.txt"),
                new FileVehicleRepository()
        );
    }

    public FileRentalRepository(Path filePath) {
        this(filePath, new FileVehicleRepository());
    }

    public FileRentalRepository(
            VehicleRepository vehicleRepository) {

        this(
                Paths.get("data", "rentals.txt"),
                vehicleRepository
        );
    }

    public FileRentalRepository(
            Path filePath,
            VehicleRepository vehicleRepository) {

        if (filePath == null) {
            throw new IllegalArgumentException(
                    "File path cannot be null."
            );
        }

        if (vehicleRepository == null) {
            throw new IllegalArgumentException(
                    "Vehicle repository cannot be null."
            );
        }

        this.filePath = filePath;
        this.vehicleRepository = vehicleRepository;

        createFile();
    }

    private void createFile() {
        try {
            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not create rentals file.",
                    exception
            );
        }
    }

    @Override
    public void save(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException(
                    "Rental cannot be null."
            );
        }

        List<Rental> rentals = findAll();
        boolean found = false;

        for (int i = 0; i < rentals.size(); i++) {
            Rental savedRental = rentals.get(i);

            if (savedRental.getRentalId()
                    .equalsIgnoreCase(rental.getRentalId())) {

                rentals.set(i, rental);
                found = true;
                break;
            }
        }

        if (!found) {
            rentals.add(rental);
        }

        writeAll(rentals);
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(
                    filePath,
                    StandardCharsets.UTF_8
            );

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length != 7 && data.length != 8) {
                    continue;
                }

                Vehicle vehicle = vehicleRepository.findById(
                        data[3].trim()
                );

                if (vehicle == null) {
                    continue;
                }

                double totalCost = 0.0;

                if (data.length == 8
                        && !data[7].trim().isEmpty()) {

                    totalCost = Double.parseDouble(
                            data[7].trim()
                    );
                }

                Rental rental = new Rental(
                        data[0].trim(),
                        vehicle,
                        data[1].trim(),
                        data[2].trim(),
                        LocalDate.parse(data[4].trim()),
                        LocalDate.parse(data[5].trim()),
                        RentalStatus.valueOf(data[6].trim()),
                        totalCost
                );

                rentals.add(rental);
            }

            return rentals;

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not read rentals file.",
                    exception
            );
        }
    }

    @Override
    public Rental findById(String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            return null;
        }

        for (Rental rental : findAll()) {
            if (rental.getRentalId()
                    .equalsIgnoreCase(rentalId)) {

                return rental;
            }
        }

        return null;
    }

    @Override
    public void update(Rental rental) {
        save(rental);
    }

    private void writeAll(List<Rental> rentals) {
        List<String> lines = new ArrayList<>();

        for (Rental rental : rentals) {
            String line =
                    rental.getRentalId()
                            + "," + rental.getCustomerName()
                            + "," + rental.getCustomerEmail()
                            + "," + rental.getVehicle().getId()
                            + "," + rental.getStartDate()
                            + "," + rental.getEndDate()
                            + "," + rental.getStatus()
                            + "," + rental.getTotalCost();

            lines.add(line);
        }

        try {
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not save rentals file.",
                    exception
            );
        }
    }
}