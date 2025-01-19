package ch.heigvd.dai;

import ch.heigvd.dai.controllers.*;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    static final int port = 8080;
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Javalin app = Javalin.create(config -> config.staticFiles.add(staticFile -> staticFile.directory = "/public"));

        app.start(port);

        logger.error("starting");
        logger.warn("starting warn");
        logger.info("starting info");
        logger.debug("starting debug");
        logger.trace("starting trace");
        String host = "localhost"; // "localhost"; if you are running the database locally or "db" if you are running the database in a docker container
        int SQLport = 5432;
        String database = "winventory";
        String username = "postgres";
        String password = "trustno1";

        String url = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s",
                host, SQLport, database, username, password);

        ConnectionPool<PostgreSQLConnection> pool = PostgreSQLConnectionBuilder.createConnectionPool(url);

        Connection connection = pool.connect().get();


        // Initialize the ShopController
        ShopController shopController = new ShopController();
        shopController.registerRoutes(app, connection);

        // Initialize the SellerController
        SellerController sellerController = new SellerController();
        sellerController.registerRoutes(app, connection);

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

        SaleController saleController = new SaleController();
        saleController.registerRoutes(app, connection);

        app.get("/", ctx -> ctx.redirect("/html/index.html"));
        app.get("/mainMenu", ctx -> ctx.redirect("/html/mainMenu.html"));
        app.get("/orders", ctx -> ctx.redirect("/html/supply.html"));
        app.get("/stockView", ctx -> ctx.redirect("/html/stockView.html"));
        app.get("/add-provider", ctx -> ctx.redirect("/html/addProvider.html"));
        app.get("/providerView", ctx -> ctx.redirect("/html/providerView.html"));




    }
}