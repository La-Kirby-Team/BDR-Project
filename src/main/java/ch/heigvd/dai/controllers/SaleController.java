package ch.heigvd.dai.controllers;

import ch.heigvd.dai.models.SaleRequest;
import ch.heigvd.dai.models.SupplyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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

            ObjectMapper objectMapper = new ObjectMapper();

            SaleRequest request = objectMapper.readValue(ctx.body(), SaleRequest.class);

            logger.info(request.toString());

            logger.info("Données reçues : " + request);

            try{



            } catch (Exception e) {
                logger.error("Erreur d'insertion :", e);
                ctx.status(500).json(Map.of("error", "Erreur lors de l'enregistrement"));
            }


            ctx.status(200).json(Map.of("message", "Succès"));

        });


    }
}
