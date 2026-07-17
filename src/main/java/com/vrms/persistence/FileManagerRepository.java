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

/**
 * Stores and retrieves manager information using a text file.
 *
 * <p>Each manager is stored on a separate line using the format
 * username,password.</p>
 */
public class FileManagerRepository implements ManagerRepository {

    /**
     * The path of the file used to store manager information.
     */
    private final Path filePath;

    /**
     * Creates a manager repository that uses the default managers file.
     */
    public FileManagerRepository() {
        this(Paths.get("data", "managers.txt"));
    }

    /**
     * Creates a manager repository using the specified file path.
     *
     * @param filePath the path of the managers file
     */
    public FileManagerRepository(Path filePath) {
        this.filePath = filePath;
        createFile();
    }

    /**
     * Creates the managers file and its parent directories when they
     * do not already exist.
     *
     * <p>If the file is empty, default manager credentials are added.</p>
     *
     * @throws RuntimeException if the file cannot be created or initialized
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

            if (Files.size(filePath) == 0) {
                Files.write(filePath,Arrays.asList("admin,1234"),StandardCharsets.UTF_8,StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create managers file.",e);
        }
    }

    /**
     * Saves a new manager in the managers file.
     *
     * @param manager the manager to save
     * @throws IllegalArgumentException if the manager is null or the username
     *                                  already exists
     * @throws RuntimeException if the manager cannot be written to the file
     */
    public void save(Manager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null.");
        }

        if (findByUsername(manager.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String line =manager.getUsername() + "," + manager.getPassword();

        try {
            Files.write(filePath,Arrays.asList(line),StandardCharsets.UTF_8,StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Could not save manager.",e);
        }
    }

    /**
     * Finds a manager using the provided username.
     *
     * @param username the username to search for
     * @return the matching manager, or null if the username is invalid
     *         or no manager is found
     * @throws RuntimeException if the managers file cannot be read
     */
    @Override
    public Manager findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        try {
            List<String> lines =Files.readAllLines(filePath,StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length == 2
                        && data[0].equals(username)) {
                    return new Manager(data[0], data[1]);
                }
            }

            return null;
        } catch (IOException e) {
            throw new RuntimeException("Could not read managers file.",e);
        }
    }
}