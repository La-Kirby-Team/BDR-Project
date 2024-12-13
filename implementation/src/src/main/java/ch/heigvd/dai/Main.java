package ch.heigvd.dai;

import io.javalin.Javalin;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
  static final int port = 8080;
  private static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Javalin app = Javalin.create(config -> {
      config.staticFiles.add("/public");
    });

    app.start(port);

    logger.error("starting");
    logger.warn("starting warn");
    logger.info("starting info");
    logger.debug("starting debug");
    logger.trace("starting trace");
    String host = "localhost";
    int SQLport = 5666;
    String database = "Lab02";
    String username = "postgres";
    String password = "trustno1";

    String url = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s",
            host, SQLport, database, username, password);

    ConnectionPool<PostgreSQLConnection> pool = PostgreSQLConnectionBuilder.createConnectionPool(url);

    Connection connection = pool.connect().get();
    CompletableFuture<QueryResult> future = connection.sendPreparedStatement("select * from article");
    QueryResult queryResult = future.get();
    System.out.println(Arrays.toString(((ArrayRowData) (queryResult.getRows().get(0))).getColumns()));
    System.out.println(Arrays.toString(((ArrayRowData) (queryResult.getRows().get(1))).getColumns()));
    System.out.println(Arrays.toString(((ArrayRowData) (queryResult.getRows().get(2))).getColumns()));




    app.get("/", ctx -> ctx.redirect("/index.html"));
    app.get("/mainMenu", ctx -> ctx.redirect("/mainMenu.html"));
    app.get("/manage-suppliers", ctx -> ctx.result("Managing suppliers..."));
    app.get("/generate-reports", ctx -> ctx.result("Generating reports..."));

  }
}