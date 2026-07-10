package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class MainTest {

    private final java.io.InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void main_whenUserChoosesExit_shouldCloseProgram() {
        String input = "2\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Program closed"));
    }

    @Test
    public void main_whenUserChoosesInvalidOptionBeforeLogin_shouldShowInvalidChoice() {
        String input = "9\n2\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Invalid choice"));
        assertTrue(output.contains("Program closed"));
    }

    @Test
    public void main_whenUsernameIsEmpty_shouldShowUsernameCannotBeEmpty() {
        String input = "1\n   \n2\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Username cannot be empty"));
    }

    @Test
    public void main_whenUsernameDoesNotExist_shouldShowUsernameNotFound() {
        String input = "1\nwrongUser\n2\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Username not found"));
    }

    @Test
    public void main_whenPasswordIsEmpty_shouldShowPasswordCannotBeEmpty() {
        String input = "1\nadmin\n   \n2\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Password cannot be empty"));
    }

    @Test
    public void main_whenPasswordIsIncorrect_shouldShowIncorrectPassword() {
        String input = "1\nadmin\n1111\n2\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Incorrect password"));
    }

    @Test
    public void main_whenLoginSuccessfulAndViewVehicles_shouldDisplayAvailableVehicles() {
        String input = "1\nadmin\n1234\n1\n3\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Login successful"));
        assertTrue(output.contains("Available vehicles"));
        assertTrue(output.contains("Toyota"));
        assertTrue(output.contains("Honda"));
        assertTrue(output.contains("Program closed"));
    }

    @Test
    public void main_whenLoginSuccessfulAndLogout_shouldLogoutSuccessfully() {
        String input = "1\nadmin\n1234\n2\n2\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Login successful"));
        assertTrue(output.contains("Logout successful"));
        assertTrue(output.contains("Program closed"));
    }

    @Test
    public void main_whenUserChoosesInvalidOptionAfterLogin_shouldShowInvalidChoice() {
        String input = "1\nadmin\n1234\n9\n3\n";
        String output = runMainWithInput(input);

        assertTrue(output.contains("Login successful"));
        assertTrue(output.contains("Invalid choice"));
        assertTrue(output.contains("Program closed"));
    }

    private String runMainWithInput(String input) {
        ByteArrayInputStream testInput = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();

        System.setIn(testInput);
        System.setOut(new PrintStream(testOutput));

        Main.main(new String[]{});

        return testOutput.toString();
    }
}