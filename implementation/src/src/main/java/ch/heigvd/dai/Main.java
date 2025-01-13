package ch.heigvd.dai;

import ch.heigvd.dai.controllers.SupplyController;
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

import java.util.Arrays;
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


    // Initialiser le SupplyController
    SupplyController supplyController = new SupplyController(pool);
    supplyController.registerRoutes(app, connection);


    app.get("/", ctx -> ctx.redirect("/index.html"));
    app.get("/mainMenu", ctx -> ctx.redirect("/mainMenu.html"));
    app.get("/manage-suppliers", ctx -> ctx.redirect("/supply.html"));

    app.get("/generate-reports", ctx -> ctx.result("Generating reports..."));

  }
}