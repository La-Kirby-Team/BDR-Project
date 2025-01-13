package ch.heigvd.dai.controllers;

import ch.heigvd.dai.models.SupplyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.pool.ConnectionPool;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jasync.sql.db.QueryResult;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SupplyController {
    private static final Logger logger = LoggerFactory.getLogger(SupplyController.class);
    private final ConnectionPool<?> pool;

    public SupplyController(ConnectionPool<?> pool) {
        this.pool = pool;
    }

    public void registerRoutes(Javalin app, Connection connection) {
        app.post("/api/add-supply", ctx -> {
            ObjectMapper objectMapper = new ObjectMapper();
            SupplyRequest request = objectMapper.readValue(ctx.body(), SupplyRequest.class);

            logger.info("Données reçues : " + request);

            try {
                // Vérification si l'article existe avec le même prix
                String checkArticleQuery = """
                    SELECT idProduit FROM Article 
                    WHERE idProduit = (SELECT idProduit FROM Produit WHERE nom = ?) 
                    AND volume = ? 
                    AND recipient = ?::typeRecipient
                    AND datePeremption = ? 
                    AND dateFinDeVente = ? 
                    AND prix != ?;
                    """;

                String insertProductQuery = """
                    INSERT INTO Produit (idProvenance, nom, tauxAlcool) 
                    VALUES (1, ?, ?) RETURNING idProduit;
                    """;

                String insertArticleQuery = """
                    INSERT INTO Article (idProduit, volume, recipient, prix, datePeremption, dateFinDeVente) 
                    VALUES (?, ?, ?::typeRecipient, ?, ?, ?);
                    """;

                String insertMouvementStockQuery = """
                    INSERT INTO MouvementStock (idMagasin, idProduit, volume, recipient, quantite) 
                    VALUES (?, ?, ?, ?::typeRecipient, ?) RETURNING id;
                    """;

                String insertApprovisionnementQuery = """
                    INSERT INTO Approvisionnement (idMouvementStock, dateCommande) 
                    VALUES (?, ?);
                    """;

                String insertApprovisionnementFournisseurQuery = """
                    INSERT INTO Approvisionnement_Fournisseur (idMouvementStock, idFournisseur) 
                    VALUES (?, ?);
                    """;

                for (int i = 0; i < request.product.size(); i++) {
                    String productName = request.product.get(i);
                    int productVolume = request.volume.get(i);
                    String productRecipient = request.recipient.get(i);
                    String productProvider = "1";
                    String productEndOfSales = request.EndOfSales.get(i);
                    String productPeremption = request.Peremption.get(i);
                    int productQuantity = request.quantity.get(i);
                    double productPrice = request.prix.get(i);
                    double tauxAlcool = request.tauxAlcool.get(i); // Nouveau champ

                    // Vérification si l'article avec un prix différent existe déjà
                    CompletableFuture<QueryResult> checkArticleFuture = connection.sendPreparedStatement(
                            checkArticleQuery, Arrays.asList(
                                    productName, productVolume, productRecipient, productPeremption, productEndOfSales, productPrice
                            )
                    );

                    QueryResult checkArticleResult = checkArticleFuture.get();

                    if (!checkArticleResult.getRows().isEmpty()) {
                        ctx.status(400).json(Map.of("error", "Un article avec des informations identiques existe déjà, mais avec un prix différent."));
                        return;
                    }

                    // Insérer un nouveau produit si inexistant
                    CompletableFuture<QueryResult> productFuture = connection.sendPreparedStatement(insertProductQuery,
                            Arrays.asList(1, productName, tauxAlcool));

                    QueryResult productResult = productFuture.get();
                    int idProduit = (int) productResult.getRows().get(0).get(0);

                    // Insérer un nouvel article
                    connection.sendPreparedStatement(insertArticleQuery,
                            Arrays.asList(idProduit, productVolume, productRecipient, productPrice, productPeremption, productEndOfSales));

                    // Insérer le mouvement de stock
                    CompletableFuture<QueryResult> mouvementStockFuture = connection.sendPreparedStatement(
                            insertMouvementStockQuery, Arrays.asList(1, idProduit, productVolume, productRecipient, productQuantity));

                    QueryResult mouvementStockResult = mouvementStockFuture.get();
                    int idMouvementStock = (int) mouvementStockResult.getRows().get(0).get(0);

                    // Insérer l'approvisionnement
                    connection.sendPreparedStatement(insertApprovisionnementQuery,
                            Arrays.asList(idMouvementStock, request.dateJour));

                    // Associer à un fournisseur
                    connection.sendPreparedStatement(insertApprovisionnementFournisseurQuery,
                            Arrays.asList(idMouvementStock, 1));
                }

                ctx.json(Map.of("message", "Approvisionnement enregistré avec succès !"));
            } catch (Exception e) {
                logger.error("Erreur d'insertion :", e);
                ctx.status(500).json(Map.of("error", "Erreur lors de l'enregistrement"));
            }
        });
    }
}
