package com.vrms.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import com.vrms.domain.Manager;

public class FileManagerRepository implements ManagerRepository {

    private final Path filePath;

    public FileManagerRepository() {
        this(Paths.get("data", "managers.txt"));
    }

    public FileManagerRepository(Path filePath) {
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

            if (Files.size(filePath) == 0) {
                Files.write(filePath, Arrays.asList("admin,1234"), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create managers file.", e);
        }
    }

    public void save(Manager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null.");
        }

        if (findByUsername(manager.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String line = manager.getUsername() + "," + manager.getPassword();

        try {
            Files.write(filePath, Arrays.asList(line), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Could not save manager.", e);
        }
    }

    public Manager findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length == 2 && data[0].equals(username)) {
                    return new Manager(data[0], data[1]);
                }
            }

            return null;
        } catch (IOException e) {
            throw new RuntimeException("Could not read managers file.", e);
        }
    }
}