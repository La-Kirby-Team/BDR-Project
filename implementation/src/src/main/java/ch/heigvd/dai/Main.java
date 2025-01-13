package ch.heigvd.dai;

import io.javalin.Javalin;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.javalin.http.UploadedFile;

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

    public static void main(String[] args) throws ExecutionException, InterruptedException {
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

        String query = """
                SELECT\s
                    p.nom AS produit,
                    COALESCE(SUM(CASE\s
                        WHEN ms.id IN (SELECT idMouvementStock FROM Approvisionnement) THEN ms.quantite
                        WHEN ms.id IN (SELECT idMouvementStock FROM Vente) THEN -ms.quantite
                        ELSE 0
                    END), 0) AS quantite_totale
                FROM\s
                    Produit p
                LEFT JOIN\s
                    Article a ON p.id = a.idProduit
                LEFT JOIN\s
                    MouvementStock ms ON a.idProduit = ms.idProduit\s
                        AND a.volume = ms.volume\s
                        AND a.recipient = ms.recipient
                GROUP BY\s
                    p.nom
                ORDER BY\s
                    p.nom;
                """;

        app.get("/api/articles", ctx -> {
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(query);
            QueryResult queryResult = future.get();

            // Convert result to JSON
            ObjectMapper mapper = new ObjectMapper();
            ctx.json(queryResult.getRows().stream()
                    .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
                    .toList());
        });

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
            String idMagasin = "SELECT id, nom FROM Magasin";
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(idMagasin);
            QueryResult queryResult = future.get();

            // Convert result to JSON
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

            // Supprimez l'ancienne image si le nom ou prénom change
            if (!ancienNom.equalsIgnoreCase(nouveauNom) || !ancienPrenom.equalsIgnoreCase(nouveauPrenom)) {
                String ancienFileName = ancienPrenom.toLowerCase() + "_" + ancienNom.toLowerCase() + ".png";
                String ancienFilePath = "src/main/resources/public/avatars/" + ancienFileName;
                Files.deleteIfExists(Paths.get(ancienFilePath));
            }

            String updateQuery = """
            UPDATE Vendeur
            SET idMagasin = ?, nom = ?, salaire = ?, estActif = ?
            WHERE id = ?
            """;

            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(updateQuery, Arrays.asList(
                    updatedData.get("idMagasin"),
                    nouveauNom,
                    updatedData.get("salaire"),
                    updatedData.get("estActif"),
                    Integer.parseInt(vendeurId)
            ));

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

        app.post("/api/uploadAvatar", ctx -> {
            String vendeurId = ctx.queryParam("id");
            String nom = ctx.queryParam("nom");
            String prenom = ctx.queryParam("prenom");

            if (vendeurId == null || nom == null || prenom == null) {
                ctx.status(400).result("ID, nom ou prénom du vendeur manquant");
                return;
            }

            UploadedFile file = ctx.uploadedFile("avatar");
            if (file == null) {
                ctx.status(400).result("Fichier manquant");
                return;
            }

            try (InputStream inputStream = file.content()) {
                // Formatez le nom du fichier en utilisant le nom et prénom
                String fileName = prenom.toLowerCase() + "_" + nom.toLowerCase() + ".png";
                String directoryPath = "public/avatars/";
                String filePath = directoryPath + fileName;

                // Créez le répertoire si nécessaire
                Files.createDirectories(Paths.get(directoryPath));

                // Enregistrez le fichier
                Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Fichier enregistré à : " + filePath);

                ctx.status(200).result("Avatar mis à jour avec succès !");
            } catch (Exception e) {
                ctx.status(500).result("Erreur lors de l'upload de l'avatar : " + e.getMessage());
            }
        });


        app.get("/", ctx -> ctx.redirect("/index.html"));
        app.get("/mainMenu", ctx -> ctx.redirect("/mainMenu.html"));
        app.get("/manage-suppliers", ctx -> ctx.result("/supply.html"));
        app.get("/generate-reports", ctx -> ctx.result("Generating reports..."));
        //app.get("/jsp", ctx -> ctx.result("profil.html"));

    }
}