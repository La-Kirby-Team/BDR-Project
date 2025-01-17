package ch.heigvd.dai;

import ch.heigvd.dai.controllers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    static final int port = 8080;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Javalin app = Javalin.create(config -> config.staticFiles.add("/public"));

        app.start(port);

        logger.error("starting");
        logger.warn("starting warn");
        logger.info("starting info");
        logger.debug("starting debug");
        logger.trace("starting trace");
        String host = "localhost";
        int SQLport = 5666;
        String database = "bdr_project";
        String username = "postgres";
        String password = "trustno1";

        String url = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s",
                host, SQLport, database, username, password);

        ConnectionPool<PostgreSQLConnection> pool = PostgreSQLConnectionBuilder.createConnectionPool(url);

        Connection connection = pool.connect().get();




        app.get("/api/vendeur/{id}", ctx -> {
            String vendeurId = ctx.pathParam("id");
            String infoVendeur = "SELECT id, idMagasin, nom, salaire, estActif FROM Vendeur WHERE id = ?";

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

                ObjectMapper mapper = new ObjectMapper();
                ctx.json(vendeur);
            } else {
                ctx.status(404).result("Vendeur non trouvé");
            }
        });

        app.get("/api/magasins", ctx -> {
            String idMagasin = "SELECT DISTINCT id, nom FROM Magasin";
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(idMagasin);
            QueryResult queryResult = future.get();

            ObjectMapper mapper = new ObjectMapper();
            ctx.json(queryResult.getRows().stream()
                    .map(row -> Map.of(
                            "id", row.get(0),
                            "nom", row.get(1)
                    ))
                    .toList());
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

            String updateQuery = """
                    UPDATE Vendeur
                    SET idMagasin = ?, nom = ?, salaire = ?, estActif = ?
                    WHERE id = ?
                    """;

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

        ProviderController providerController = new ProviderController();
        providerController.registerRoutes(app, connection);

        // Initialize OrderController
        OrderController orderController = new OrderController();
        orderController.registerRoutes(app, connection);


        // Initialize the StockController
        StockController stockController = new StockController();
        stockController.registerRoutes(app, connection);


      // Initialiser le SupplyController
        SupplyController supplyController = new SupplyController();
        supplyController.registerRoutes(app, connection);

      //Inialiser le AddProviderController
      AddProviderController appProviderController = new AddProviderController();
      appProviderController.registerRoutes(app, connection);


        app.get("/", ctx -> ctx.redirect("html/index.html"));
        app.get("/mainMenu", ctx -> ctx.redirect("html/mainMenu.html"));
        app.get("/orders", ctx -> ctx.redirect("html/supply.html"));
        app.get("/stockView", ctx -> ctx.redirect("html/stockView.html"));
        app.get("/add-provider", ctx -> ctx.redirect("html/addProvider.html"));
        app.get("/providerView", ctx -> ctx.redirect("html/providerView.html"));


    }
}