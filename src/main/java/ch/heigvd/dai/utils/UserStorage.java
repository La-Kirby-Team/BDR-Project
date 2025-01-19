package ch.heigvd.dai.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserStorage {
    private static final String APP_NAME = "winventory";
    private static final Path STORAGE_DIR = Paths.get(System.getProperty("user.home"), ".config", APP_NAME);
    private static final Path FILE_PATH = STORAGE_DIR.resolve("users.txt");

    /**
     * Ensures that the storage directory and file exist.
     */
    private static void ensureFileExists() throws IOException {
//        System.out.println("STORAGE_DIR: " + STORAGE_DIR);
        if (!Files.exists(STORAGE_DIR)) {
            Files.createDirectories(STORAGE_DIR);
        }
        if (!Files.exists(FILE_PATH)) {

            // Copy from resources if available
            try (InputStream is = UserStorage.class.getResourceAsStream("/users.txt")) {
                if (is != null) {
                    Files.copy(is, FILE_PATH);
                } else {
                    Files.createFile(FILE_PATH);
                }
            }
        }
    }

    /**
     * Saves a user with a hashed password to the users file.
     */
    public static void saveUser(String username, String hashedPassword) throws IOException {
        ensureFileExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH.toFile(), true))) {
            writer.write(username + ":" + hashedPassword);
            writer.newLine();
        }
    }

    /**
     * Loads users as a raw string.
     */
    public static String loadUsersString() {
        try {
            ensureFileExists();
            return Files.lines(FILE_PATH, StandardCharsets.UTF_8)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Loads users into a Map<Username, HashedPassword>.
     */
    public static Map<String, String> loadUsers() throws IOException {
        ensureFileExists();
        Map<String, String> users = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        }
        return users;
    }
}
