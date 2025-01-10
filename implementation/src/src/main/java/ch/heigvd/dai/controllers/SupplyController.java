package ch.heigvd.dai.controllers;

import ch.heigvd.dai.models.SupplyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.pool.ConnectionPool;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

public class SupplyController {
    private static final Logger logger = LoggerFactory.getLogger(SupplyController.class);
    private final ConnectionPool<?> pool;

    public SupplyController(ConnectionPool<?> pool) {
        this.pool = pool;
    }

    public void registerRoutes(Javalin app) {
        app.post("/api/add-supply", ctx -> {
            ObjectMapper objectMapper = new ObjectMapper();
            SupplyRequest request = objectMapper.readValue(ctx.body(), SupplyRequest.class);

            logger.info("Données reçues : " + request);

            try {
                String insertQuery = """
                        INSERT INTO Approvisionnement 
                        (produit, volume, recipient, fournisseur, date_fin_vente, date_peremption, quantite, prix, date_approvisionnement)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                        INSERT INTO PRODUIT(nom, tauxAlcool) VALUES (produit, )
                        """;

                Connection connection = pool.connect().get();

                for (int i = 0; i < request.product.size(); i++) {
                    connection.sendPreparedStatement(insertQuery, Arrays.asList(
                            request.product.get(i),
                            request.volume.get(i),
                            request.recipient.get(i),
                            request.provider.get(i),
                            request.EndOfSales.get(i),
                            request.Peremption.get(i),
                            request.quantity.get(i),
                            request.prix.get(i),
                            request.dateJour
                    ));
                }

                ctx.json(Map.of("message", "Approvisionnement enregistré avec succès !"));
            } catch (Exception e) {
                logger.error("Erreur d'insertion :", e);
                ctx.status(500).json(Map.of("error", "Erreur lors de l'enregistrement"));
            }
        });
    }
}
