package com.vrms.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;

/**
 * Stores and retrieves vehicle information using a text file.
 *
 * <p>Each vehicle is stored on a separate line with its identifier,
 * brand, model, daily rental price, and current status.</p>
 */
public class FileVehicleRepository implements VehicleRepository {

    /**
     * The path of the file used to store vehicle information.
     */
    private final Path filePath;

    /**
     * Creates a vehicle repository using the default vehicles file.
     */
    public FileVehicleRepository() {
        this(Paths.get("data", "vehicles.txt"));
    }

    /**
     * Creates a vehicle repository using the specified file path.
     *
     * @param filePath the path of the vehicles file
     */
    public FileVehicleRepository(Path filePath) {
        this.filePath = filePath;
        createFile();
    }

    /**
     * Creates the vehicles file and its parent directories when they
     * do not already exist.
     *
     * <p>If the file is empty, default vehicle records are added.</p>
     *
     * @throws RuntimeException if the vehicles file cannot be created
     *                          or initialized
     */
    private void createFile() {
        try {
            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            String content = new String(
                    Files.readAllBytes(filePath),
                    StandardCharsets.UTF_8
            ).trim();

            if (content.isEmpty()) {
                Files.write(
                        filePath,
                        Arrays.asList(
                                "V1,Toyota,Corolla,40.0,AVAILABLE",
                                "V2,Kia,Sportage,60.0,RENTED",
                                "V3,Honda,Civic,45.0,AVAILABLE",
                                "V4,Hyundai,Tucson,55.0,RENTED"
                        ),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create vehicles file.",
                    e
            );
        }
    }

    /**
     * Returns all valid vehicles stored in the vehicles file.
     *
     * @return a list containing all stored vehicles
     * @throws RuntimeException if the vehicles file cannot be read
     */
    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();

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

                if (data.length != 5) {
                    continue;
                }

                Vehicle vehicle = new Vehicle(
                        data[0].trim(),
                        data[1].trim(),
                        data[2].trim(),
                        Double.parseDouble(data[3].trim()),
                        VehicleStatus.valueOf(data[4].trim())
                );

                vehicles.add(vehicle);
            }

            return vehicles;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not read vehicles file.",
                    e
            );
        }
    }

    /**
     * Finds a vehicle using its unique identifier.
     *
     * @param id the vehicle identifier
     * @return the matching vehicle, or null if it is not found
     */
    @Override
    public Vehicle findById(String id) {
        for (Vehicle vehicle : findAll()) {
            if (vehicle.getId().equalsIgnoreCase(id)) {
                return vehicle;
            }
        }

        return null;
    }

    /**
     * Saves a new vehicle or updates an existing vehicle with the same ID.
     *
     * @param vehicle the vehicle to save
     * @throws IllegalArgumentException if the vehicle is null
     */
    @Override
    public void save(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException(
                    "Vehicle cannot be null."
            );
        }

        List<Vehicle> vehicles = findAll();
        boolean found = false;

        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId()
                    .equalsIgnoreCase(vehicle.getId())) {

                vehicles.set(i, vehicle);
                found = true;
                break;
            }
        }

        if (!found) {
            vehicles.add(vehicle);
        }

        writeAll(vehicles);
    }

    /**
     * Rewrites the vehicles file using the provided vehicles.
     *
     * @param vehicles the vehicles to write to the file
     * @throws RuntimeException if the vehicles cannot be written
     */
    private void writeAll(List<Vehicle> vehicles) {
        List<String> lines = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            lines.add(
                    vehicle.getId()
                            + "," + vehicle.getBrand()
                            + "," + vehicle.getModel()
                            + "," + vehicle.getPricePerDay()
                            + "," + vehicle.getStatus()
            );
        }

        try {
            Files.write(filePath,lines,StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not save vehicles file.",e
            );
        }
    }
}