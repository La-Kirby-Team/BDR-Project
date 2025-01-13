package ch.heigvd.dai.models;

import java.util.List;

public class SupplyRequest {
    public List<String> product;
    public List<Integer> volume;
    public List<String> recipient;
    public List<String> provider;
    public List<String> EndOfSales;
    public List<String> Peremption;
    public List<Integer> quantity;
    public List<Double> prix;
    public List<Double> tauxAlcool;
    public String dateJour;

    @Override
    public String toString() {
        return "SupplyRequest{" +
                "product=" + product +
                ", volume=" + volume +
                ", recipient=" + recipient +
                ", provider=" + provider +
                ", EndOfSales=" + EndOfSales +
                ", Peremption=" + Peremption +
                ", quantity=" + quantity +
                ", prix=" + prix +
                ", dateJour='" + dateJour + '\'' +
                '}';
    }
}
