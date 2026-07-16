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

import com.vrms.domain.Car;
import com.vrms.domain.ElectricVehicle;
import com.vrms.domain.Motorcycle;
import com.vrms.domain.Truck;
import com.vrms.domain.Van;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.domain.VehicleType;

public class FileVehicleRepository
        implements VehicleRepository {

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

            String content = new String(
                    Files.readAllBytes(filePath),
                    StandardCharsets.UTF_8
            ).trim();

            if (content.isEmpty()) {
                Files.write(
                        filePath,
                        Arrays.asList(
                                "V1,CAR,Toyota,Corolla,40.0,AVAILABLE",
                                "V2,MOTORCYCLE,Honda,CBR,35.0,AVAILABLE",
                                "V3,VAN,Ford,Transit,70.0,AVAILABLE",
                                "V4,TRUCK,Volvo,FH,120.0,AVAILABLE",
                                "V5,ELECTRIC_VEHICLE,Tesla,Model3,90.0,AVAILABLE"
                        ),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not create vehicles file.",
                    exception
            );
        }
    }

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

                String[] data = line.split(",");

                Vehicle vehicle;

                if (data.length == 5) {
                    vehicle = createVehicle(
                            VehicleType.CAR,
                            data[0].trim(),
                            data[1].trim(),
                            data[2].trim(),
                            Double.parseDouble(data[3].trim()),
                            VehicleStatus.valueOf(
                                    data[4].trim()
                            )
                    );

                } else if (data.length == 6) {
                    vehicle = createVehicle(
                            parseType(data[1]),
                            data[0].trim(),
                            data[2].trim(),
                            data[3].trim(),
                            Double.parseDouble(data[4].trim()),
                            VehicleStatus.valueOf(
                                    data[5].trim()
                            )
                    );

                } else {
                    continue;
                }

                vehicles.add(vehicle);
            }

            return vehicles;

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not read vehicles file.",
                    exception
            );
        }
    }

    private VehicleType parseType(String value) {
        String type = value.trim().toUpperCase();

        if ("ELECTRIC".equals(type)) {
            return VehicleType.ELECTRIC_VEHICLE;
        }

        return VehicleType.valueOf(type);
    }

    private Vehicle createVehicle(
            VehicleType type,
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        switch (type) {
            case MOTORCYCLE:
                return new Motorcycle(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );

            case VAN:
                return new Van(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );

            case TRUCK:
                return new Truck(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );

            case ELECTRIC_VEHICLE:
                return new ElectricVehicle(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );

            case CAR:
            default:
                return new Car(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );
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
        if (vehicle == null) {
            throw new IllegalArgumentException(
                    "Vehicle cannot be null."
            );
        }

        List<Vehicle> vehicles = findAll();
        boolean found = false;

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle savedVehicle = vehicles.get(i);

            if (savedVehicle.getId().equalsIgnoreCase(
                    vehicle.getId())) {

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
                            + "," + vehicle.getType()
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

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not save vehicles file.",
                    exception
            );
        }
    }
}