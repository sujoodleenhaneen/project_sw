package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the console-based Main class using simulated user input.
 */
public class MainTest {

    /**
     * Original system input stream.
     */
    private final InputStream originalIn = System.in;

    /**
     * Original system output stream.
     */
    private final PrintStream originalOut = System.out;

    /**
     * Restores the original input and output streams after every test.
     */
    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Verifies that choosing Exit before login closes the program.
     */
    @Test
    public void main_whenUserChoosesExit_shouldCloseProgram() {
        String input = "2\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Program closed"));
    }

    /**
     * Verifies that an invalid option before login displays an error.
     */
    @Test
    public void main_whenUserChoosesInvalidOptionBeforeLogin_shouldShowInvalidChoice() {
        String input = "9\n2\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Invalid choice"));
        assertTrue(output.contains("Program closed"));
    }

    /**
     * Verifies that an empty username displays a validation message.
     */
    @Test
    public void main_whenUsernameIsEmpty_shouldShowUsernameCannotBeEmpty() {
        String input =
                "1\n"
                + "   \n"
                + "admin\n"
                + "1234\n"
                + "4\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Username cannot be empty"));
        assertTrue(output.contains("Program closed"));
    }

    /**
     * Verifies that an unknown username displays an error message.
     */
    @Test
    public void main_whenUsernameDoesNotExist_shouldShowUsernameNotFound() {
        String input =
                "1\n"
                + "wrongUser\n"
                + "admin\n"
                + "1234\n"
                + "4\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Username not found"));
        assertTrue(output.contains("Program closed"));
    }

    /**
     * Verifies that an empty password displays a validation message.
     */
    @Test
    public void main_whenPasswordIsEmpty_shouldShowPasswordCannotBeEmpty() {
        String input =
                "1\n"
                + "admin\n"
                + "   \n"
                + "1234\n"
                + "4\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Password cannot be empty"));
        assertTrue(output.contains("Program closed"));
    }

    /**
     * Verifies that an incorrect password displays an error message.
     */
    @Test
    public void main_whenPasswordIsIncorrect_shouldShowIncorrectPassword() {
        String input =
                "1\n"
                + "admin\n"
                + "1111\n"
                + "2\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Incorrect password"));
        assertTrue(output.contains("Program closed"));
    }

    /**
     * Verifies that available vehicles are displayed after login.
     */
    @Test
    public void main_whenLoginSuccessfulAndViewVehicles_shouldDisplayAvailableVehicles() {
        String input =
                "1\n"
                + "admin\n"
                + "1234\n"
                + "1\n"
                + "4\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Login successful"));
        assertTrue(output.contains("Available vehicles"));
        assertTrue(output.contains("Toyota"));
        assertTrue(output.contains("Honda"));
        assertTrue(output.contains("Program closed"));
    }

    /**
     * Verifies that the manager can log out successfully.
     */
    @Test
    public void main_whenLoginSuccessfulAndLogout_shouldLogoutSuccessfully() {
        String input =
                "1\n"
                + "admin\n"
                + "1234\n"
                + "3\n"
                + "2\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Login successful"));
        assertTrue(output.contains("Logout successful"));
        assertTrue(output.contains("Program closed"));
    }

    /**
     * Verifies that an invalid option after login displays an error.
     */
    @Test
    public void main_whenUserChoosesInvalidOptionAfterLogin_shouldShowInvalidChoice() {
        String input =
                "1\n"
                + "admin\n"
                + "1234\n"
                + "9\n"
                + "4\n";

        String output = runMainWithInput(input);

        assertTrue(output.contains("Login successful"));
        assertTrue(output.contains("Invalid choice"));
        assertTrue(output.contains("Program closed"));
    }

    /**
     * Runs Main using simulated console input and captures its output.
     *
     * @param input simulated user input
     * @return captured console output
     */
    private String runMainWithInput(String input) {
        ByteArrayInputStream testInput =
                new ByteArrayInputStream(input.getBytes());

        ByteArrayOutputStream testOutput =
                new ByteArrayOutputStream();

        System.setIn(testInput);
        System.setOut(new PrintStream(testOutput));

        Main.main(new String[]{});

        return testOutput.toString();
    }
}