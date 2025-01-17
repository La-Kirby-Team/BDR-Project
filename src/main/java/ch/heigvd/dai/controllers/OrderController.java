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

public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void registerRoutes(Javalin app, Connection connection) throws IOException {

        // Fetch all waiting orders
        String waitingOrdersQuery = Files.readString(Path.of("src/main/resources/public/sql/waitingOrders.sql"), StandardCharsets.UTF_8);
        app.get("/api/orders-waiting", ctx -> {
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(waitingOrdersQuery);
            QueryResult queryResult = future.get();

            ctx.json(queryResult.getRows().stream()
                    .map(row -> Map.of(
                            "id", ((ArrayRowData) row).get(0),
                            "product", ((ArrayRowData) row).get(1),
                            "quantity", ((ArrayRowData) row).get(2),
                            "date", ((ArrayRowData) row).get(3),
                            "status", ((ArrayRowData) row).get(4)
                    ))
                    .toList());
        });

        // Confirm an order (change status to 'confirmed' and update stock)
        app.post("/api/orders-confirm", ctx -> {
            Map<String, Object> requestData = objectMapper.readValue(ctx.body(), Map.class);

            int orderId = Integer.parseInt(requestData.get("id").toString());
            String receivedDate = requestData.get("date").toString();
            int receivedQuantity = Integer.parseInt(requestData.get("quantite").toString());

            logger.info("Confirming order ID: {} with quantity {}", orderId, receivedQuantity);

            // Update the order status to confirmed
            String updateOrderQuery = """
                UPDATE Commandes
                SET status = 'confirmed', received_date = ?
                WHERE id = ?
            """;

            CompletableFuture<QueryResult> futureOrderUpdate = connection.sendPreparedStatement(updateOrderQuery, List.of(receivedDate, orderId));

            // Update the stock quantity based on the received order
            String updateStockQuery = """
                UPDATE Stock
                SET quantity = quantity + ?
                WHERE id = (
                    SELECT product_id FROM Commandes WHERE id = ?
                )
            """;

            CompletableFuture<QueryResult> futureStockUpdate = connection.sendPreparedStatement(updateStockQuery, Arrays.asList(receivedQuantity, orderId));

            // Handle responses
            futureOrderUpdate.thenAccept(orderResult -> {
                if (orderResult.getRowsAffected() > 0) {
                    futureStockUpdate.thenAccept(stockResult -> {
                        if (stockResult.getRowsAffected() > 0) {
                            ctx.status(200).json(Map.of("message", "Commande confirmée et stock mis à jour avec succès"));
                        } else {
                            ctx.status(404).json(Map.of("error", "Stock non trouvé pour cette commande"));
                        }
                    }).exceptionally(e -> {
                        ctx.status(500).json(Map.of("error", "Erreur mise à jour du stock : " + e.getMessage()));
                        return null;
                    });
                } else {
                    ctx.status(404).json(Map.of("error", "Commande non trouvée"));
                }
            }).exceptionally(e -> {
                ctx.status(500).json(Map.of("error", "Erreur lors de la confirmation de la commande : " + e.getMessage()));
                return null;
            });
        });
    }
}
