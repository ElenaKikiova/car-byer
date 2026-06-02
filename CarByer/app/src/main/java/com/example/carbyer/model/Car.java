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

    public Car() {}

    public Car(int id, String brand, String model,
               int productionYear, String imageURL,
               int kilometers, int price) {

        this.id = id;
        this.brand = brand;
        this.model = model;
        this.productionYear = productionYear;
        this.imageURL = imageURL;
        this.kilometers = kilometers;
        this.price = price;
    }

    public static List<Car> listFromJson(JSONArray arr) {
        List<Car> list = new ArrayList<>();

        if (arr == null) return list;

        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;

            Car c = new Car();

            c.id = o.optInt("_id");
            c.brand = o.optString("brand");
            c.model = o.optString("model");
            c.productionYear = o.optInt("productionYear");
            c.imageURL = o.optString("imageURL");
            c.kilometers = o.optInt("kilometers");

            c.price = o.optInt("price", 0);

            list.add(c);
        }

        return list;
    }
}