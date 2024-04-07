package com.ocado;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocado.basket.BasketSplitter;

public class Main {

    public static void main(String[] args) {
        BasketSplitter basketSplitter = new BasketSplitter("D:/DEV/GIT/ocado/src/main/resources/config-1.json");

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> items = new ArrayList<>();
        try {
            items = objectMapper.readValue(new File("src/main/resources/basket-3.json"), new TypeReference<>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Map<String, List<String>> deliveryGroups;
            deliveryGroups = basketSplitter.split(items);
            for (Map.Entry<String, List<String>> entry : deliveryGroups.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }
}