package ch.heigvd.dai.models;

import java.util.List;

public class SupplyRequest {
    public List<String> customDate;
    public List<String> product;
    public List<Integer> volume;
    public List<String> recipient;
    public List<String> provider;
    public List<String> EndOfSales;
    public List<String> Peremption;
    public List<Double> tauxAlcool;
    public List<Integer> quantity;
    public List<Double> prix;


    @Override
    public String toString() {
        return "SupplyRequest{" +
                "dateJour='" + customDate +
                ", product=" + product +
                ", volume=" + volume +
                ", recipient=" + recipient +
                ", provider=" + provider +
                ", EndOfSales=" + EndOfSales +
                ", Peremption=" + Peremption +
                ", tauxAlcool=" + tauxAlcool +
                ", quantity=" + quantity +
                ", prix=" + prix + '\'' +
                '}';
    }
}
