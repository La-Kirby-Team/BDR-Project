package ch.heigvd.dai.controllers;

import ch.heigvd.dai.utils.SQLFileLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SellerController {

    public void registerRoutes(Javalin app, Connection connection){
        app.get("/api/vendeur/{id}", ctx -> {
            String vendeurId = ctx.pathParam("id");
            String infoVendeur = SQLFileLoader.loadSQLFile("sql/sellerGetID.sql");

            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(infoVendeur, Arrays.asList(Integer.parseInt(vendeurId)));
            QueryResult queryResult = future.get();

            if (!queryResult.getRows().isEmpty()) {
                ArrayRowData row = (ArrayRowData) queryResult.getRows().get(0);
                Map<String, Object> vendeur = Map.of(
                        "id", row.get(0),
                        "idMagasin", row.get(1),
                        "nom", row.get(2),
                        "salaire", row.get(3),
                        "estActif", row.get(4)
                );

                ctx.json(vendeur);
            } else {
                ctx.status(404).result("Vendeur non trouvé");
            }
        });

        app.post("/api/updateVendeur/{id}", ctx -> {
            String vendeurId = ctx.pathParam("id");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> updatedData = mapper.readValue(ctx.body(), Map.class);

            String ancienNom = (String) updatedData.get("ancienNom");
            String ancienPrenom = (String) updatedData.get("ancienPrenom");
            String nouveauNom = (String) updatedData.get("nom");
            String nouveauPrenom = (String) updatedData.get("prenom");

            if (ancienNom != null && ancienPrenom != null && (!ancienNom.equalsIgnoreCase(nouveauNom) || !ancienPrenom.equalsIgnoreCase(nouveauPrenom))) {
                String ancienFileName = ancienPrenom.toLowerCase() + "_" + ancienNom.toLowerCase() + ".png";
                String ancienFilePath = "src/main/resources/public/avatars/" + ancienFileName;
                Files.deleteIfExists(Paths.get(ancienFilePath));
            }

            String updateQuery = SQLFileLoader.loadSQLFile("sql/sellerUpdate.sql");

            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(
                    updateQuery, Arrays.asList(
                            updatedData.get("idMagasin"),
                            nouveauNom,
                            updatedData.get(
                                    "salaire"),
                            updatedData.get("estActif"),
                            Integer.parseInt(

                                    vendeurId)
                    ));
        });

        app.post("/api/uploadAvatar", ctx -> {
            String vendeurId = ctx.queryParam("id");
            String nomComplet = ctx.queryParam("nom");

            if (vendeurId == null || nomComplet == null) {
                ctx.status(400).result("ID ou nom complet du vendeur manquant");
                return;
            }

            UploadedFile file = ctx.uploadedFile("avatar");
            if (file == null) {
                ctx.status(400).result("Fichier manquant");
                return;
            }

            String contentType = file.contentType();
            if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("image/jpg")) {
                ctx.status(400).result("Format de fichier non pris en charge.");
                return;
            }

            try (InputStream inputStream = file.content()) {
                String fileName = nomComplet.toLowerCase().replace(" ", "_") + ".png";
                String directoryPath = "src/main/resources/public/avatars/";
                String filePath = directoryPath + fileName;

                Files.createDirectories(Paths.get(directoryPath));

                Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

                ctx.status(200).result("Avatar mis à jour avec succès !");
            } catch (Exception e) {
                ctx.status(500).result("Erreur lors de l'upload de l'avatar : " + e.getMessage());
            }
        });


    }
}
