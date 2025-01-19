package ch.heigvd.dai.controllers;

import ch.heigvd.dai.utils.SQLFileLoader;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class StockController {

    // ConcurrentHashMap pour le cache (stocké par une clé "stock-cache")
    private final ConcurrentHashMap<String, CacheData> stockCache = new ConcurrentHashMap<>();

    // Temps d'expiration du cache en millisecondes (5 minutes)
    private static final long CACHE_EXPIRATION_TIME = 300000;


    public void registerRoutes(Javalin app, Connection connection) {

        String stockQuery = SQLFileLoader.loadSQLFile("sql/stockQuery.sql");
        String lowQTQuery = SQLFileLoader.loadSQLFile("sql/lowQTArticles.sql");

        app.get("/api/stock", ctx -> {
            String cacheKey= "stock-data";

            CacheData cachedData = stockCache.get(cacheKey);

            if(cachedData != null && !isCacheExpired(cachedData)) {
                System.out.println("Utilisation des données mises en cache");
                ctx.json(cachedData.getData());
            }
            else{
                CompletableFuture<QueryResult> future = connection.sendPreparedStatement(stockQuery);
                QueryResult queryResult = future.get();

                String data = queryResult.getRows().stream()
                        .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
                        .toList().toString();


                //Mettre à jour le cache
                stockCache.put(cacheKey, new CacheData(data, System.currentTimeMillis()));
                ctx.json(data);
            }




        });

        app.get("/api/articles-lowQT", ctx -> {
            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(lowQTQuery);
            QueryResult queryResult = future.get();

            ctx.json(queryResult.getRows().stream()
                    .map(row -> Arrays.toString(((ArrayRowData) row).getColumns()))
                    .toList());
        });
    }

    // Méthode pour vérifier si le cache est expiré
    private boolean isCacheExpired(CacheData cachedData) {
        return (System.currentTimeMillis() - cachedData.getTimestamp()) > CACHE_EXPIRATION_TIME;
    }

    // Classe interne pour stocker les données mises en cache et leur timestamp
    private static class CacheData {
        private final String data;
        private final long timestamp;

        public CacheData(String data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }

        public String getData() {
            return data;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
