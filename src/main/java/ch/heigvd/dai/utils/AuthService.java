package ch.heigvd.dai.utils;

import java.io.IOException;
import java.util.Map;

public class AuthService {
    public static boolean registerUser(String username, String password) throws IOException {
        Map<String, String> users = UserStorage.loadUsers();
        if (users.containsKey(username)) {
            return false; // User already exists
        }
        String hashedPassword = PasswordUtils.hashPassword(password);
        UserStorage.saveUser(username, hashedPassword);
        return true;
    }

    public static boolean authenticateUser(String username, String password) throws IOException {
        Map<String, String> users = UserStorage.loadUsers();
        String hashedPassword = users.get(username);
        if (hashedPassword == null) {
            return false; // User not found
        }
        return PasswordUtils.verifyPassword(password, hashedPassword);
    }
}