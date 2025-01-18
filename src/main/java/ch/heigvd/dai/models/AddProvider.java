package ch.heigvd.dai.models;

public class AddProvider {
    public String name;
    public String address;
    public String phone;


    @Override
    public String toString() {
        return "name: " + name +
                ", address: " + address +
                ", phone: " + phone;
    }
}


