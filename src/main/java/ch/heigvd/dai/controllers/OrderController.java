package ch.heigvd.dai.controllers;

import ch.heigvd.dai.utils.SQLFileLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OrderController {
    public void registerRoutes(Javalin app, Connection connection) {

        String waitingOrdersQuery = SQLFileLoader.loadSQLFile("sql/waitingOrders.sql");

        app.get("/api/orders-waiting", ctx -> {
            try {
                CompletableFuture<QueryResult> future = connection.sendPreparedStatement(waitingOrdersQuery);
                QueryResult queryResult = future.get();

                ctx.json(queryResult.getRows().stream()
                        .map(row -> {
                            ArrayRowData rowData = (ArrayRowData) row;
                            return Map.of(
                                    "produit", rowData.get(0),
                                    "quantite", rowData.get(1),
                                    "dateCommande", rowData.get(2).toString(),
                                    "joursDepuisCommande", rowData.get(3),
                                    "mouvementStockId", rowData.get(4)
                            );
                        })
                        .toList());
            } catch (Exception e) {
                ctx.status(500).json(Map.of("error", "Erreur serveur: " + e.getMessage()));
            }
        });

        app.put("/api/orders-confirm", ctx -> {
            ObjectMapper mapper = new ObjectMapper();
            Map requestData = mapper.readValue(ctx.body(), Map.class);

            int mouvementStockId = Integer.parseInt(requestData.get("id").toString());
            String receivedDate = requestData.get("date").toString();
            int receivedQuantity = Integer.parseInt(requestData.get("quantite").toString());

            String updateQuery =
                    """
                            UPDATE MouvementStock
                            SET date = ?, quantite = ?
                            WHERE id = ?
                            """;


            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(updateQuery, Arrays.asList(
                    receivedDate,
                    receivedQuantity,
                    mouvementStockId
            ));

            future.thenAccept(queryResult -> {
                if (queryResult.getRowsAffected() > 0) {
                    ctx.status(200).result("Commande mise à jour avec succès");
                } else {
                    ctx.status(404).result("MouvementStock non trouvé");
                }
            }).exceptionally(e -> {
                ctx.status(500).result("Erreur interne : " + e.getMessage());
                return null;
            });

            future.thenAccept(queryResult -> {
                if (queryResult.getRowsAffected() > 0) {
                    ctx.status(200).result("Mise à jour réussie");
                } else {
                    ctx.status(404).result("Vendeur non trouvé");
                }
            }).exceptionally(e -> {
                ctx.status(500).result("Erreur interne : " + e.getMessage());
                return null;
            });
        });
    }
}
