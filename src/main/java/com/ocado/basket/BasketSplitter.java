package com.ocado.basket;

import java.util.*;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
/* ... */

public class BasketSplitter {
    /* ... */
    private Map<String, List<String>> deliveryOptions;
    public BasketSplitter(String absolutePathToConfigFile) {
        // Load the delivery options from the config file
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            deliveryOptions = objectMapper.readValue(new File(absolutePathToConfigFile), new TypeReference<Map<String, List<String>>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Map<String, List<String>> split(List<String> items) {
        Map<String, List<String>> deliveryGroups = new HashMap<>();
        Map<String, Integer> deliveryCounts = new HashMap<>();

        // Sortuj przedmioty według liczby dostępnych metod dostawy
        items.sort((item1, item2) -> deliveryOptions.get(item1).size() - deliveryOptions.get(item2).size());

        for (String item : items) {
            List<String> deliveryMethods = deliveryOptions.get(item);
            if (deliveryMethods != null) {
                String bestMethod = null;
                int maxCount = -1;
                for (String method : deliveryMethods) {
                    int count = deliveryCounts.getOrDefault(method, 0);
                    if (count > maxCount) {
                        maxCount = count;
                        bestMethod = method;
                    }
                }
                if (bestMethod != null) {
                    if (!deliveryGroups.containsKey(bestMethod)) {
                        deliveryGroups.put(bestMethod, new ArrayList<>());
                    }
                    deliveryGroups.get(bestMethod).add(item);
                    deliveryCounts.put(bestMethod, maxCount + 1);
                }
            }
        }

        return deliveryGroups;
    }

    public void printDeliveryOptions() {
        for (Map.Entry<String, List<String>> entry : deliveryOptions.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    /* ... */
}
