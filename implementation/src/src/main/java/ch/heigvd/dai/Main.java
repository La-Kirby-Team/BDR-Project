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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    String lowQTQuery = Files.readString(Path.of("src/main/resources/public/sql/lowQTArticles.sql"), StandardCharsets.UTF_8);

    app.get("/api/articles-lowQT", ctx -> {
      CompletableFuture<QueryResult> future = connection.sendPreparedStatement(lowQTQuery);
      QueryResult queryResult = future.get();

      // Convert result to JSON
      ObjectMapper mapper = new ObjectMapper();
      ctx.json(queryResult.getRows().stream()
              .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
              .toList());
    });

    String waitingOrders = Files.readString(Path.of("src/main/resources/public/sql/waitingOrders.sql"), StandardCharsets.UTF_8);

    app.get("/api/orders-waiting", ctx -> {
      try {
        CompletableFuture<QueryResult> future = connection.sendPreparedStatement(waitingOrders);
        QueryResult queryResult = future.get();

        // Convert result to JSON
        ObjectMapper mapper = new ObjectMapper();
        ctx.json(queryResult.getRows().stream()
                .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
                .toList());
      } catch (Exception e) {
        // Log the error and return a 500 status
        e.printStackTrace();
        ctx.status(500).result("Server Error: " + e.getMessage());
      }
    });



    app.post("/api/orders-confirm", ctx -> {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> requestData = mapper.readValue(ctx.body(), Map.class);

      int mouvementStockId = Integer.parseInt(requestData.get("id").toString());
      String receivedDate = requestData.get("date").toString();
      int receivedQuantity = Integer.parseInt(requestData.get("quantite").toString());

      String updateQuery = """
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
    });










    app.get("/", ctx -> ctx.redirect("html/index.html"));
    app.get("/mainMenu", ctx -> ctx.redirect("html/mainMenu.html"));
    app.get("/manage-suppliers", ctx -> ctx.result("html/supply.html"));
    app.get("/generate-reports", ctx -> ctx.result("Generating reports..."));

  }
}