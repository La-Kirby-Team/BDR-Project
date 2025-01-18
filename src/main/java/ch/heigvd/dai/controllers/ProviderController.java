package ch.heigvd.dai.controllers;

import ch.heigvd.dai.models.AddProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static ch.heigvd.dai.Main.logger;

public class ProviderController {

    public void registerRoutes(Javalin app, Connection connection){
        app.get("/api/providers", ctx -> {
            String providerQuery = "SELECT id, nom, adresse, numeroTelephone FROM Fournisseur ORDER BY nom;";
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(providerQuery);
            QueryResult queryResult = future.get();
            ctx.json(queryResult.getRows().stream().map(row -> {
                ArrayRowData rowData = (ArrayRowData) row;
                return Map.of(
                        "id", rowData.get(0),
                        "nom", rowData.get(1),
                        "adresse", rowData.get(2),
                        "numeroTelephone", rowData.get(3)
                );
            }).toList());
        });

        app.post("/api/providers", ctx -> {

            logger.info("Requête reçue avec le corps suivant : {}", ctx.body());

            ObjectMapper objectMapper = new ObjectMapper();


            AddProvider newProvider = objectMapper.readValue(ctx.body(), AddProvider.class);


            try{

                String providerAlreadyExist = """
                        SELECT id FROM fournisseur WHERE nom = ?
                                                  AND adresse = ?
                                                  AND numeroTelephone = ?
                                                  ;
                        """;
                String insertNewProvider = """
                        INSERT INTO fournisseur (nom, adresse, numeroTelephone)
                        VALUES (?, ?, ?);
                        """;

                String name = newProvider.name;
                String adresse = newProvider.address;
                String phoneNumber = newProvider.phone;

                //Vérification de si le fournisseur existe déjà
                CompletableFuture<QueryResult> checkProviderAlreadyExist = connection.sendPreparedStatement(
                        providerAlreadyExist, Arrays.asList(name, adresse, phoneNumber)
                );

                QueryResult CheckProviderResult = checkProviderAlreadyExist.get();

                if(!CheckProviderResult.getRows().isEmpty()){
                    ctx.status(400).json(Map.of("error", "Un fournisseur avec des informations identiques existe déjà."));
                    return;
                }

                //Insérer un nouveau fournisseur
                connection.sendPreparedStatement(
                        insertNewProvider, Arrays.asList(name, adresse, phoneNumber)
                );


                ctx.json(Map.of("message", "Nouveau fournisseur enregistré avec succès !"));
                ctx.status(200).json(Map.of("message", "Succès"));



            }catch(Exception e){
                logger.error("Erreur d'insertion :", e);
                ctx.status(500).json(Map.of("error", "Erreur lors de l'enregistrement"));
            }

        });

        app.delete("/api/providers/{id}", ctx -> {  // ✅ Fix: Define ID in the route
            try {
                String providerId = ctx.pathParam("id"); // Retrieve the ID from the URL
                String deleteProviderQuery = "DELETE FROM Fournisseur WHERE id = ?";

                CompletableFuture<QueryResult> future = connection.sendPreparedStatement(deleteProviderQuery, List.of(Integer.parseInt(providerId)));
                QueryResult queryResult = future.get();

                if (queryResult.getRowsAffected() > 0) {
                    ctx.status(200).result("Fournisseur supprimé avec succès");
                } else {
                    ctx.status(404).result("Fournisseur non trouvé");
                }
            } catch (Exception e) {
                ctx.status(500).result("Erreur serveur: " + e.getMessage());
            }
        });

    }
}
