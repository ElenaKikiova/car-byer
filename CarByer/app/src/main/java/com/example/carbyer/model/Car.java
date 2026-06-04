package com.example.carbyer.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Car {

    public int id;
    public String brand;
    public String model;
    public int productionYear;
    public String imageURL;
    public int kilometers;
    public int price;

    public String engine;

    public boolean automatic;

    public Dealer dealer;

    public Car(int id, String brand, String model,
               int productionYear, String imageURL,
               int kilometers, int price, Dealer dealer) {

        this.id = id;
        this.brand = brand;
        this.model = model;
        this.productionYear = productionYear;
        this.imageURL = imageURL;
        this.kilometers = kilometers;
        this.price = price;
        this.dealer = dealer;
    }


    public Car(int id, String brand, String model,
               int productionYear, String imageURL,
               int kilometers, int price, Dealer dealer, String engine, boolean automatic) {

        this.id = id;
        this.brand = brand;
        this.model = model;
        this.productionYear = productionYear;
        this.imageURL = imageURL;
        this.kilometers = kilometers;
        this.price = price;
        this.dealer = dealer;
        this.engine = engine;
        this.automatic = automatic;
    }


}