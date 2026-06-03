package com.example.carbyer.model;


public class Dealer {

    public int id;

    public String name;
    public String address;
    public String city;
    public String workingHours;

    public Car[] cars;

    public Dealer(int id, String name, String address, String city,
               String workingHours) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.workingHours = workingHours;
    }

}
