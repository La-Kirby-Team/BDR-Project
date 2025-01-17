package ch.heigvd.dai.controllers;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import io.javalin.Javalin;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProviderController {

    public void registerRoutes(Javalin app, Connection connection) throws ExecutionException, InterruptedException {
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
    }
}
