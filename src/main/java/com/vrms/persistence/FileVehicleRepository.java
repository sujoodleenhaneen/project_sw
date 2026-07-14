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

public class FileVehicleRepository implements VehicleRepository {

    private final Path filePath;

    public FileVehicleRepository() {
        this(Paths.get("data", "vehicles.txt"));
    }

    public FileVehicleRepository(Path filePath) {
        this.filePath = filePath;
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

            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8).trim();

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
            throw new RuntimeException("Could not create vehicles file.", e);
        }
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

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
            throw new RuntimeException("Could not read vehicles file.", e);
        }
    }

    @Override
    public Vehicle findById(String id) {
        for (Vehicle vehicle : findAll()) {
            if (vehicle.getId().equalsIgnoreCase(id)) {
                return vehicle;
            }
        }

        return null;
    }

    @Override
    public void save(Vehicle vehicle) {
        List<Vehicle> vehicles = findAll();
        boolean found = false;

        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equalsIgnoreCase(vehicle.getId())) {
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
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not save vehicles file.", e);
        }
    }
}