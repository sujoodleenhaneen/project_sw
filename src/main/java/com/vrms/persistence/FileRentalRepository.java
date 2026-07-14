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
        this(Paths.get("data", "rentals.txt"), new FileVehicleRepository());
    }

    public FileRentalRepository(Path filePath) {
        this(filePath, new FileVehicleRepository());
    }

    public FileRentalRepository(VehicleRepository vehicleRepository) {
        this(Paths.get("data", "rentals.txt"), vehicleRepository);
    }

    public FileRentalRepository(Path filePath, VehicleRepository vehicleRepository) {
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
        } catch (IOException e) {
            throw new RuntimeException("Could not create rentals file.", e);
        }
    }

    public void save(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }

        List<Rental> rentals = findAll();
        boolean found = false;

        for (int i = 0; i < rentals.size(); i++) {
        	if (rentals.get(i).getRentalId().equals(rental.getRentalId())) {
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

    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length != 7) {
                    continue;
                }

                Vehicle vehicle = vehicleRepository.findById(data[3]);

                if (vehicle == null) {
                    continue;
                }

                Rental rental = new Rental(
                        data[0],
                        vehicle,
                        data[1],
                        data[2],
                        LocalDate.parse(data[4]),
                        LocalDate.parse(data[5]),
                        RentalStatus.valueOf(data[6])
                );

                rentals.add(rental);
            }

            return rentals;
        } catch (IOException e) {
            throw new RuntimeException("Could not read rentals file.", e);
        }
    }

    public Rental findById(String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            return null;
        }

        for (Rental rental : findAll()) {
            if (rental.getRentalId().equals(rentalId)) {
                return rental;
            }
        }

        return null;
    }

    public void update(Rental rental) {
        save(rental);
    }

    private void writeAll(List<Rental> rentals) {
        List<String> lines = new ArrayList<>();

        for (Rental rental : rentals) {
            String line = rental.getRentalId()
                    + "," + rental.getCustomerName()
                    + "," + rental.getCustomerEmail()
                    + "," + rental.getVehicle().getId()
                    + "," + rental.getStartDate()
                    + "," + rental.getEndDate()
                    + "," + rental.getStatus();

            lines.add(line);
        }

        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save rentals file.", e);
        }
    }
}