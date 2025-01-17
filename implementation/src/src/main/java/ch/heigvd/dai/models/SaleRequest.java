package ch.heigvd.dai.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class SaleRequest {
    public List<String> customDate;
    public List<String> saler;
    public List<String> client;
    public List<String> product;
    public List<Integer> volume;
    public List<String> recipient;
    public List<Double> tauxAlcool;
    public List<Integer> quantity;
    public List<Double> prix;


    @Override
    public String toString() {
        return "SupplyRequest{ +"
                + "date='" + customDate
                + ", vendeur='" + saler
                + ", client='" + client
                + ", product=" + product
                + ", volume=" + volume
                + ", recipient=" + recipient
                + ", tauxAlcohol=" + tauxAlcool
                + ", quantity=" + quantity
                + ", price=" + prix + '\'';

    }
}
