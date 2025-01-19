package ch.heigvd.dai.controllers;

import ch.heigvd.dai.utils.AuthService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String SESSION_COOKIE_NAME = "session_token";


    public void registerRoutes(Javalin app) {
        app.put("/api/register", this::registerUser);
        app.post("/api/login", this::loginUser);
        app.post("/api/logout", this::logoutUser);
    }

    private void registerUser(Context ctx) throws IOException {
        Map<String, String> requestBody = ctx.bodyAsClass(Map.class);
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            ctx.status(400).json(Map.of("message", "Username and password are required"));
            return;
        }

        boolean registered = AuthService.registerUser(username, password);

        if (registered) {
            ctx.status(201).json(Map.of("message", "User registered successfully"));
        } else {
            ctx.status(409).json(Map.of("message", "Username already exists"));
        }
    }

    private void loginUser(Context ctx) {
        try {
            Map requestBody = ctx.bodyAsClass(Map.class);
            String username = (String) requestBody.get("username");
            String password = (String) requestBody.get("password");

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Username and password are required"));
                return;
            }

            boolean authenticated = AuthService.authenticateUser(username, password);

            if (authenticated) {
                String sessionToken = UUID.randomUUID().toString();

                // Set cookie (expires in 1 day)
                ctx.cookie(SESSION_COOKIE_NAME, sessionToken, 86400);

                logger.info("✅ User '{}' logged in successfully. Session: {}", username, sessionToken);
                ctx.status(200).json(Map.of("message", "Login successful"));
            } else {
                logger.warn("⚠️ Login failed for user '{}'", username);
                ctx.status(401).json(Map.of("message", "Invalid username or password"));
            }
        } catch (Exception e) {
            logger.error("❌ Error during login attempt: {}", e.getMessage());
            ctx.status(500).json(Map.of("message", "Internal server error"));
        }
    }

    private void logoutUser(Context ctx) {
        // Invalidate the session by deleting the cookie
        ctx.removeCookie(SESSION_COOKIE_NAME);
        logger.info("✅ User logged out and session invalidated.");
        ctx.status(200).json(Map.of("message", "Logged out successfully"));
    }
}
