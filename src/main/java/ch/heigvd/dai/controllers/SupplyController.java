package ch.heigvd.dai.controllers;

import ch.heigvd.dai.models.SupplyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jasync.sql.db.QueryResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SupplyController {
    private static final Logger logger = LoggerFactory.getLogger(SupplyController.class);

    public SupplyController() {
    }

    public void registerRoutes(Javalin app, Connection connection) {

      app.post("/api/add-supply", ctx -> {

          logger.info("Requête reçue avec le corps suivant : {}", ctx.body());

          ObjectMapper objectMapper = new ObjectMapper();


          SupplyRequest request = objectMapper.readValue(ctx.body(), SupplyRequest.class);


          try {
              // Vérification si l'article existe avec le même prix
              /*

               */
              String checkArticleQuery = """
                        SELECT idProduit FROM Article
                        WHERE idProduit = ?
                            AND recipient = ?::typeRecipient
                            AND volume = ?
                            AND datePeremption = ?
                            AND dateFinDeVente = ?
                            AND prix != ?;
                        """;

              String checkProductExist = """
                      SELECT idProduit FROM Produit WHERE nom = ?
                                                      AND tauxAlcool = ?;
                      """;
              String checkArticleExist = """
                      SELECT idProduit FROM Article
                        WHERE idProduit = ?
                            AND recipient = ?::typeRecipient
                            AND volume = ?
                            AND datePeremption = ?
                            AND dateFinDeVente = ?
                            AND prix = ?;
                     """;

              String insertProductQuery = """
                        INSERT INTO Produit (idProvenance, nom, tauxAlcool)
                        VALUES (?, ?, ?) RETURNING idProduit;
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
              String findFournisseurQuery = """
                        SELECT id FROM Fournisseur
                        WHERE nom = ?;
                        """;

              String insertApprovisionnementFournisseurQuery = """
                        INSERT INTO Approvisionnement_Fournisseur (idMouvementStock, idFournisseur)
                        VALUES (?, ?);
                        """;
                String customDate = request.customDate.getFirst();
              for (int i = 0; i < request.product.size(); i++) {
                  String productName = request.product.get(i);
                  int productVolume = request.volume.get(i);
                  String productRecipient = request.recipient.get(i);
                  String productProvider = request.provider.get(i);
                  String productEndOfSales = request.EndOfSales.get(i);
                  String productPeremption = request.Peremption.get(i);
                  double tauxAlcool = request.tauxAlcool.get(i);
                  int productQuantity = request.quantity.get(i);
                  double productPrice = request.prix.get(i);


                  int idProduit;
                  //Regarde si le produit existe déjà
                  CompletableFuture<QueryResult> checkProductFuture = connection.sendPreparedStatement(checkProductExist, Arrays.asList(productName, tauxAlcool));

                  if(checkProductFuture.get().getRows().isEmpty()) {
                      // Insérer un nouveau produit si inexistant
                      CompletableFuture<QueryResult> productFuture = connection.sendPreparedStatement(insertProductQuery,
                              Arrays.asList(1, productName, tauxAlcool));

                      QueryResult productResult = productFuture.get();

                      if (productResult.getRows().getFirst().getFirst() != null) {
                          idProduit = (int) productResult.getRows().getFirst().getFirst();
                      } else {
                          throw new Exception("IdProduit manquant");
                      }
                  }
                  else{
                      //Récuper l'id du produit
                      idProduit = (int) checkProductFuture.get().getRows().getFirst().getFirst();
                  }

                  // Vérification si l'article avec un prix différent existe déjà
                  CompletableFuture<QueryResult> checkArticleFuture = connection.sendPreparedStatement(
                          checkArticleQuery, Arrays.asList(
                                  idProduit, productRecipient, productVolume, productPeremption, productEndOfSales, productPrice
                          )
                  );

                  QueryResult checkArticleResult = checkArticleFuture.get();

                  if (!checkArticleResult.getRows().isEmpty()) {
                      ctx.status(400).json(Map.of("error", "Un article avec des informations identiques existe déjà, mais avec un prix différent."));
                      return;
                  }

                  //Vérification si l'article existe déjà
                  CompletableFuture<QueryResult> checkArticle = connection.sendPreparedStatement(checkArticleExist,
                          Arrays.asList(idProduit, productRecipient, productVolume, productPeremption, productEndOfSales, productPrice));

                  if(checkArticle.get().getRows().isEmpty()) {
                      // Insérer un nouvel article
                      connection.sendPreparedStatement(insertArticleQuery,
                              Arrays.asList(idProduit, productVolume, productRecipient, productPrice, productPeremption, productEndOfSales));
                  }

                  // Insérer le mouvement de stock
                  CompletableFuture<QueryResult> mouvementStockFuture = connection.sendPreparedStatement(
                          insertMouvementStockQuery, Arrays.asList(1, idProduit, productVolume, productRecipient, productQuantity));

                  QueryResult mouvementStockResult = mouvementStockFuture.get();

                  int idMouvementStock;
                  if (mouvementStockResult.getRows().getFirst().getFirst() != null) {
                      idMouvementStock = (int) mouvementStockResult.getRows().getFirst().getFirst();
                  } else {
                      throw new Exception("IdMouvementStock manquant");
                  }

                  // Insérer l'approvisionnement
                  connection.sendPreparedStatement(insertApprovisionnementQuery,
                          Arrays.asList(idMouvementStock, customDate));


                  CompletableFuture<QueryResult> findFIdFournisseur = connection.sendPreparedStatement(
                          findFournisseurQuery, Collections.singletonList(productProvider));


                  QueryResult findFournisseurResult = findFIdFournisseur.get();

                  if (findFournisseurResult.getRows().isEmpty()) {
                      ctx.status(400).json(Map.of("message", "Fournisseur inconnu"));
                      return;

                      //TODO
                      //throw new Exception("Fournisseur inconnu");

                     // connection.sendPreparedStatement(insertApprovisionnementFournisseurQuery,
                            //  Arrays.asList(idMouvementStock, null));

                  } else {
                      // Associer à un fournisseur
                      connection.sendPreparedStatement(insertApprovisionnementFournisseurQuery,
                              Arrays.asList(idMouvementStock, findFournisseurResult.getRows().getFirst().getFirst()));
                  }

              }

              ctx.json(Map.of("message", "Approvisionnement enregistré avec succès !"));
              ctx.status(200).json(Map.of("message", "OK, enregistrement effectué"));



          } catch (Exception e) {
              logger.error("Erreur d'insertion :", e);
              ctx.status(500).json(Map.of("error", "Erreur lors de l'enregistrement"));
          }


      });

    }
}
