package ch.heigvd.dai.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SQLFileLoader {
    private static final Logger logger = LoggerFactory.getLogger(SQLFileLoader.class);

    public static String loadSQLFile(String path) {
        String fullPath = "public/" + path; // Chemin correct dans le JAR
        try (InputStream inputStream = SQLFileLoader.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (inputStream == null) {
                logger.error("Fichier SQL introuvable : " + fullPath);
                return "";
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du fichier SQL: " + fullPath, e);
            return "";
        }
    }
}
