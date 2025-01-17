package ch.heigvd.dai.controllers;

import com.github.jasync.sql.db.Connection;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SaleController {
    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);


    public SaleController() {
    }

    public void registerRoutes(Javalin app, Connection connection) {

        app.post("/api/add-sale", ctx -> {

            logger.info("Requête reçue avec le corps suivant : {}", ctx.body());

            ctx.status(200).json(Map.of("message", "Succès"));

        });


    }
}
