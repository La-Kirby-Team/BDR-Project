package ch.heigvd.dai;

import ch.heigvd.dai.controllers.*;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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