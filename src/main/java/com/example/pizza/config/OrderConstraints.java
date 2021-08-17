package com.example.pizza.config;

public class OrderConstraints {
    // Order value constraints
    public static String[] flavor = {"HAWAII", "REGINA", "QUATTRO-FORMAGGI"};
    public static String[] crust = {"THIN"};
    public static String[] size = {"L", "M"};

    // Minimum table number for "takeaway" order
    public static int minTableNumber = 20000;
}
