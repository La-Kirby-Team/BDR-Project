package ch.heigvd.dai.models;

import java.util.List;

public class SaleRequest {
    List<String> date;
    List<String> vendeur;
    List<String> client;
    List<String> product;
    List<Integer> volume;
    List<String> recipient;
    List<Double> tauxAlcohol;
    List<Integer> quantity;
    List<Double> price;



    @Override
    public String toString() {
        return "SupplyRequest{ +"
                + "date='" + date
                + ", vendeur='" + vendeur
                + ", client='" + client
                + ", product=" + product
                + ", volume=" + volume
                + ", recipient=" + recipient
                + ", tauxAlcohol=" + tauxAlcohol
                + ", quantity=" + quantity
                + ", price=" + price + '\'';

    }
}
