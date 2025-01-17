package ch.heigvd.dai.controllers;

import ch.heigvd.dai.models.SaleRequest;
import ch.heigvd.dai.models.SupplyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SaleController {
    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);


    public SaleController() {
    }

    public void registerRoutes(Javalin app, Connection connection) {

        app.post("/api/add-sale", ctx -> {

            logger.info("Requête reçue avec le corps suivant : {}", ctx.body());

            ObjectMapper objectMapper = new ObjectMapper();

            SaleRequest request = objectMapper.readValue(ctx.body(), SaleRequest.class);

            logger.info(request.toString());

            logger.info("Données reçues : " + request);

            try {

                String checkProductQuantity = """
                        SELECT idProduit FROM Article WHERE idProduit = (SELECT idProduit FROM Produit WHERE nom = ?
                                                                                             AND recipient = ?::typeRecipient
                                                                                             AND volume = ?
                                                                                             AND tauxAlcool = ?)
                                                                AND prix = ?;
                        """;
                String insertMouvementStockQuery = """
                        INSERT INTO MouvementStock (idMagasin, idProduit, volume, recipient, quantite, date)
                        VALUES (?, ?, ?, ?::typeRecipient, ?, ?) RETURNING id;
                        """;

                String checkClient = """
                        SELECT id FROM Client WHERE email = ?;
                        """;

                String checkSaler = """
                       SELECT id FROM Vendeur WHERE nom = ?;
                       """;

                String insertSale = """
                        INSERT INTO Vente (idMouvementStock, idVendeur, idClient)
                        VALUES (?, ?, ?);
                        """;

                String customDate = request.customDate.getFirst();
                String vendeur = request.saler.getFirst();
                String client = request.client.getFirst();

                for(int i = 0; i <request.product.size();++i){
                    String product = request.product.get(i);
                    Integer volume = request.volume.get(i);
                    String recipient = request.recipient.get(i);
                    Double tauxAlcool = request.tauxAlcool.get(i);
                    Integer quantity = request.quantity.get(i);
                    Double prix = request.prix.get(i);

                    CompletableFuture<QueryResult> checkClientOfSale = connection.sendPreparedStatement(checkClient,
                            Collections.singletonList(client));

                    if(checkClientOfSale.get().getRows().isEmpty()){
                        ctx.status(400).json(Map.of("error", "Aucun client trouvé, ajouter un client avant de procéder à une vente"));
                        return;
                    }

                    int idClient;
                    if(checkClientOfSale.get().getRows().getFirst().getFirst() != null){
                        idClient = (int) checkClientOfSale.get().getRows().getFirst().getFirst();
                    }
                    else {
                        throw new Exception("idClient manquant");
                    }

                    CompletableFuture<QueryResult> checkSalerOfSale = connection.sendPreparedStatement(checkSaler,
                            Collections.singletonList(vendeur));

                    if(checkSalerOfSale.get().getRows().isEmpty()){
                        ctx.status(400).json(Map.of("error", "Aucun vendeur trouvé, embaucher un nouveau vendeur portant ce nom"));
                        return;
                    }
                    int idVendeur;
                    if(checkSalerOfSale.get().getRows().getFirst().getFirst() != null){
                        idVendeur = (int) checkSalerOfSale.get().getRows().getFirst().getFirst();
                    }
                    else{
                        throw new Exception("IdVendeur manquant");
                    }


                    CompletableFuture<QueryResult> checkStock = connection.sendPreparedStatement(checkProductQuantity,
                            Arrays.asList(product, recipient, volume, tauxAlcool, prix)
                    );

                    if(checkStock.get().getRows().isEmpty()){
                        ctx.status(400).json(Map.of("error", "Aucun produit n'ayant ces caractéristiques"));
                        return;
                    }
                    int idProduit;
                    if (checkStock.get().getRows().getFirst().getFirst() != null) {
                        idProduit = (int) checkStock.get().getRows().getFirst().getFirst();
                    } else {
                        throw new Exception("IdProduit manquant");
                    }

                    CompletableFuture<QueryResult> mouvementStockFuture = connection.sendPreparedStatement(insertMouvementStockQuery,
                            Arrays.asList(1, idProduit, volume, recipient, quantity, customDate));

                    QueryResult mouvementStockResult = mouvementStockFuture.get();

                    int idMouvementStock;
                    if (mouvementStockResult.getRows().getFirst().getFirst() != null) {
                        idMouvementStock = (int) mouvementStockResult.getRows().getFirst().getFirst();
                    } else {
                        throw new Exception("IdMouvementStock manquant");
                    }

                    connection.sendPreparedStatement(insertSale, Arrays.asList(idMouvementStock, idVendeur, idClient));

                    ctx.json(Map.of("message", "Approvisionnement enregistré avec succès !"));
                    ctx.status(200).json(Map.of("message", "OK, enregistrement effectué"));



                }

            } catch (Exception e) {
                logger.error("Erreur d'insertion :", e);
                ctx.status(500).json(Map.of("error", "Erreur lors de l'enregistrement"));
            }


            ctx.status(200).json(Map.of("message", "Succès"));

        });


    }
}
