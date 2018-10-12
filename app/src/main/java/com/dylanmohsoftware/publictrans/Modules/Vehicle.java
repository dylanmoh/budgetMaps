package com.dylanmohsoftware.publictrans.Modules;

public class Vehicle {
    public String icon;
    public String type;
    public String short_name;
    public int num_stops;
    public double price;

    public Vehicle(String icon, String type, String short_name, int num_stops) {
        this.icon = icon;
        this.type = type;
        this.short_name = short_name;
        this.num_stops = num_stops;
        if (this.short_name.equals("walking") || this.short_name.equals("Utah Valley Express") || this.short_name.equals("Salt Lake")) {
            this.price = 0.00;
        }
        else if (this.short_name.equals("Frontrunner")) {
            this.price = 2.50 + (0.60 * this.num_stops);
        }
        else {
            this.price = 2.50;
        }
    }
}
