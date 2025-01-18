package ch.heigvd.dai.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import io.javalin.Javalin;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ShopController {

    public void registerRoutes(Javalin app, Connection connection){
        app.get("/api/magasins", ctx -> {
            String idMagasin = "SELECT DISTINCT id, nom FROM Magasin";
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(idMagasin);
            QueryResult queryResult = future.get();

            ctx.json(queryResult.getRows().stream()
                    .map(row -> Map.of(
                            "id", row.get(0),
                            "nom", row.get(1)
                    ))
                    .toList());
        });
    }
}
