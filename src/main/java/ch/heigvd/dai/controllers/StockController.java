package ch.heigvd.dai.controllers;

import ch.heigvd.dai.utils.SQLFileLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class StockController {
    public void registerRoutes(Javalin app, Connection connection) {

        String stockQuery = SQLFileLoader.loadSQLFile("sql/stockQuery.sql");
        String lowQTQuery = SQLFileLoader.loadSQLFile("sql/lowQTArticles.sql");

        app.get("/api/stock", ctx -> {
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(stockQuery);
            QueryResult queryResult = future.get();

            ctx.json(queryResult.getRows().stream()
                    .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
                    .toList());
        });

        app.get("/api/articles-lowQT", ctx -> {
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(lowQTQuery);
            QueryResult queryResult = future.get();

            ctx.json(queryResult.getRows().stream()
                    .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
                    .toList());
        });
    }
}
