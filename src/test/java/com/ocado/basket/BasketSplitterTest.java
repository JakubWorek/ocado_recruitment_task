package com.ocado.basket;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasketSplitterTest {

    @Test
    public void testSplitWithValidConfigAndValidItems(){
        BasketSplitter basketSplitter = new BasketSplitter("src/test/resources/config-1.json");

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> items = new ArrayList<>();
        try {
            items = objectMapper.readValue(new File("src/test/resources/basket-3.json"), new TypeReference<>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, List<String>> deliveryGroups = basketSplitter.split(items);

        Map<String, List<String>> validOutput = new HashMap<>();
        validOutput.put("Courier", List.of("Garden Chair", "Espresso Machine"));
        validOutput.put("Express Delivery", List.of("AA Battery (4 Pcs.)", "Steak (300g)", "Cold Beer (330ml)", "Carrots (1kg)"));

        assertEquals(validOutput.size(), deliveryGroups.size());
        for (Map.Entry<String, List<String>> entry : validOutput.entrySet()) {
            assertTrue(deliveryGroups.containsKey(entry.getKey()));
            assertEquals(entry.getValue().size(), deliveryGroups.get(entry.getKey()).size());
            for (String item : entry.getValue()) {
                assertTrue(deliveryGroups.get(entry.getKey()).contains(item));
            }
        }
    }

    @Test
    public void testSplitWithInvalidConfig(){
        BasketSplitter basketSplitter = new BasketSplitter("src/test/resources/config-2.json");

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> items = new ArrayList<>();
        try {
            items = objectMapper.readValue(new File("src/test/resources/basket-3.json"), new TypeReference<>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> finalItems = items;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            basketSplitter.split(finalItems);
        });
    }

    @Test
    public void testSplitWithNoItems(){
        BasketSplitter basketSplitter = new BasketSplitter("src/test/resources/config-1.json");

        List<String> items = new ArrayList<>();

        Map<String, List<String>> deliveryGroups = basketSplitter.split(items);
        Map<String, List<String>> validOutput = new HashMap<>();

        assertEquals(0, deliveryGroups.size());
        assertTrue(deliveryGroups.isEmpty());
    }
}
