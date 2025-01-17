package ch.heigvd.dai.controllers;

import ch.heigvd.dai.models.AddProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AddProviderController {
    private static final Logger logger = LoggerFactory.getLogger(AddProvider.class);

    public AddProviderController() {}
    public void registerRoutes(Javalin app, Connection connection) {

        app.post("/api/add-provider", ctx -> {

        logger.info("Requête reçue avec le corps suivant : {}", ctx.body());

            ObjectMapper objectMapper = new ObjectMapper();


            AddProvider newProvider = objectMapper.readValue(ctx.body(), AddProvider.class);


            try{

                String providerAlreadyExist = """
                        SELECT id FROM fournisseur WHERE nom = ?
                                                  AND adresse = ?
                                                  AND numeroTelephone = ?
                                                  ;
                        """;
                String insertNewProvider = """
                        INSERT INTO fournisseur (nom, adresse, numeroTelephone)
                        VALUES (?, ?, ?);
                        """;

                String name = newProvider.name;
                String adresse = newProvider.address;
                String phoneNumber = newProvider.phone;

                //Vérification de si le fournisseur existe déjà
                CompletableFuture<QueryResult> checkProviderAlreadyExist = connection.sendPreparedStatement(
                        providerAlreadyExist, Arrays.asList(name, adresse, phoneNumber)
                );

                QueryResult CheckProviderResult = checkProviderAlreadyExist.get();

                if(!CheckProviderResult.getRows().isEmpty()){
                    ctx.status(400).json(Map.of("error", "Un fournisseur avec des informations identiques existe déjà."));
                    return;
                }

                //Insérer un nouveau fournisseur
                connection.sendPreparedStatement(
                        insertNewProvider, Arrays.asList(name, adresse, phoneNumber)
                    );


            ctx.json(Map.of("message", "Nouveau fournisseur enregistré avec succès !"));
            ctx.status(200).json(Map.of("message", "Succès"));



            }catch(Exception e){
                logger.error("Erreur d'insertion :", e);
                ctx.status(500).json(Map.of("error", "Erreur lors de l'enregistrement"));
            }

        });
    }
}
