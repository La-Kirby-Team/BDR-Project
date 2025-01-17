package ch.heigvd.dai.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StockController {
    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void registerRoutes(Javalin app, Connection connection) throws IOException {

        String stockQuery = Files.readString(Path.of("src/main/resources/public/sql/stockQuery.sql"), StandardCharsets.UTF_8);
        app.get("/api/stock", ctx -> {
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(stockQuery);
            QueryResult queryResult = future.get();

            ctx.json(queryResult.getRows().stream()
                    .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
                    .toList());
        });

        String lowQTQuery = Files.readString(Path.of("src/main/resources/public/sql/lowQTArticles.sql"), StandardCharsets.UTF_8);
        app.get("/api/articles-lowQT", ctx -> {
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(lowQTQuery);
            QueryResult queryResult = future.get();

            ctx.json(queryResult.getRows().stream()
                    .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
                    .toList());
        });

        app.post("/api/update-stock", ctx -> {
            Map requestData = objectMapper.readValue(ctx.body(), Map.class);
            int mouvementStockId = Integer.parseInt(requestData.get("id").toString());
            int updatedQuantity = Integer.parseInt(requestData.get("quantity").toString());

            // Retrieve the movement type
            String getTypeQuery = """
                SELECT 
                    CASE 
                        WHEN EXISTS (SELECT 1 FROM Vente v WHERE v.idMouvementStock = ms.id) THEN 'vente'
                        ELSE 'approvisionnement'
                    END AS Type
                FROM MouvementStock ms
                WHERE ms.id = ?
            """;
            CompletableFuture<QueryResult> futureType = connection.sendPreparedStatement(getTypeQuery, List.of(mouvementStockId));
            QueryResult typeResult = futureType.get();

            if (!typeResult.getRows().isEmpty()) {
                String movementType = (String) ((ArrayRowData) typeResult.getRows().getFirst()).getFirst();
                logger.info("\n " + updatedQuantity + " " + movementType + "\n");

                // Ensure correct sign
                if ("vente".equals(movementType) && updatedQuantity > 0) {
                    updatedQuantity = -Math.abs(updatedQuantity);
                }
                if ("approvisionnement".equals(movementType) && updatedQuantity < 0) {
                    updatedQuantity = Math.abs(updatedQuantity);
                }
            }

            // Update the database
            String updateQuery = "UPDATE MouvementStock SET quantite = ? WHERE id = ?";
            CompletableFuture<QueryResult> futureUpdate = connection.sendPreparedStatement(updateQuery, Arrays.asList(
                    updatedQuantity, mouvementStockId
            ));

            logger.info("Updated stock with id " + mouvementStockId + " to " + updatedQuantity);

            futureUpdate.thenAccept(queryResult -> {
                if (queryResult.getRowsAffected() > 0) {
                    ctx.status(200).json(Map.of("message", "Stock mis à jour avec succès"));
                } else {
                    ctx.status(404).json(Map.of("error", "MouvementStock non trouvé"));
                }
            }).exceptionally(e -> {
                ctx.status(500).json(Map.of("error", "Erreur interne : " + e.getMessage()));
                return null;
            });
        });



    }
}
