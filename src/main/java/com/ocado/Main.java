package com.ocado;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocado.basket.BasketSplitter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        BasketSplitter basketSplitter = new BasketSplitter("src/main/resources/config-1.json");
        basketSplitter.printDeliveryOptions();
        ObjectMapper objectMapper = new ObjectMapper();


        List<String> items = new ArrayList<>();
        Map<String, List<String>> deliveryGroups = new HashMap<>();

        try {
            items = objectMapper.readValue(new File("src/main/resources/basket-3.json"), new TypeReference<List<String>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        // print items
        for (String item : items) {
            System.out.println(item);
        }
        System.out.println("Splitting the basket...");
        // split the basket
        deliveryGroups = basketSplitter.split(items);
        // print the delivery groups
        for (Map.Entry<String, List<String>> entry : deliveryGroups.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}