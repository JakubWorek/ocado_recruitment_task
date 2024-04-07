package com.ocado.basket;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/* BasketSplitter class is responsible for splitting the basket items into delivery methods.
 * It reads the delivery options from a config file and generates all possible permutations of delivery methods.
 * It then finds the best delivery method based on the number of items in each delivery method.
 * This code provides functionality to split basket items into different delivery methods
 * based on a set of predefined delivery options loaded from a JSON configuration file.
 * It employs filtering, validation, permutation generation, and comparison logic
 * to find the best delivery method for the given basket items.
 */
public class BasketSplitter{

    private Map<String, List<String>> deliveryOptions;

    /* Constructor to initialize the BasketSplitter with the absolute path to the configuration file.
     * It reads the delivery options from the JSON file specified by the absolute path provided and stores them in a map.
     */
    public BasketSplitter(String absolutePathToConfigFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.deliveryOptions = objectMapper.readValue(new File(absolutePathToConfigFile), new TypeReference<>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Method to split the basket items into delivery methods based on the predefined delivery options.
     * It filters the delivery options based on the basket items, generates all possible permutations of delivery methods,
     * and finds the best delivery method based on the number of items in each delivery method.
     * It returns a map of delivery methods with the corresponding basket items assigned to each method.
     */
    public Map<String, List<String>> split(List<String> basketItems) {
        if (deliveryOptions == null) {
            return new HashMap<>();
        }

        Map<String, List<String>> filteredConfig = filterConfigByBasketItems(basketItems);
        validateFilteredConfig(filteredConfig, basketItems);

        Set<String> deliveryMethods = getUniqueDeliveryMethods(filteredConfig);

        List<List<String>> permutations = generatePermutations(new ArrayList<>(deliveryMethods));

        return findBestDeliveryMethod(filteredConfig, permutations);
    }

    /* Helper method to validate the filtered configuration based on the basket items.
     * It checks if all basket items have corresponding delivery options in the filtered configuration.
     * If any item does not have a corresponding delivery option, it throws an IllegalArgumentException.
     */
    private void validateFilteredConfig(Map<String, List<String>> filteredConfig, List<String> basketItems) {
        basketItems.stream()
                .filter(item -> !filteredConfig.containsKey(item))
                .findAny()
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Item not available: " + item);
                });
    }

    /* Helper method to filter the delivery options based on the basket items.
     * It returns a map of delivery options that are applicable to the basket items.
     */
    private Map<String, List<String>> filterConfigByBasketItems(List<String> basketItems) {
        return deliveryOptions.entrySet()
                .stream()
                .filter(entry -> basketItems.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())));
    }

    /* Helper method to get the unique delivery methods from the filtered configuration.
     * It returns a set of unique delivery methods available for the basket items.
     */
    private Set<String> getUniqueDeliveryMethods(Map<String, List<String>> itemsMapWithDeliveryMethods) {
        return itemsMapWithDeliveryMethods.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    /* Helper method to generate all possible permutations of delivery methods.
     * It returns a list of lists where each list represents a permutation of delivery methods.
     */
    private List<List<String>> generatePermutations(List<String> deliveryMethodsList){
        if (deliveryMethodsList == null || deliveryMethodsList.isEmpty()) {
            return new ArrayList<>();
        }

        return generatePermutationsRecursion(deliveryMethodsList)
                .collect(Collectors.toList());
    }

    /* Helper method to generate permutations of delivery methods recursively.
     * It returns a stream of lists where each list represents a permutation of delivery methods.
     */
    private Stream<List<String>> generatePermutationsRecursion(List<String> deliveryMethodsList) {
        if (deliveryMethodsList.isEmpty()) {
            return Stream.of(new ArrayList<>());
        }

        return deliveryMethodsList.stream()
                .flatMap(item -> {
                    List<String> remainingItems = new ArrayList<>(deliveryMethodsList);
                    remainingItems.remove(item);
                    return generatePermutationsRecursion(remainingItems)
                            .map(permutation -> {
                                List<String> newPermutation = new ArrayList<>(permutation);
                                newPermutation.add(item);
                                return newPermutation;
                            });
                });
    }

    /* Helper method to find the best delivery method based on the number of items in each delivery method.
     * It returns a map of delivery methods with the corresponding basket items assigned to each method.
     */
    private Map<String, List<String>> findBestDeliveryMethod(Map<String, List<String>> filteredConfig, List<List<String>> permutations) {
        return permutations.stream()
                .map(permutation -> createMethodMap(permutation, new HashMap<>(filteredConfig)))
                .reduce(new HashMap<>(), (bestMethod, currentMethod) ->
                        bestMethod.isEmpty() || isBetterMethod(currentMethod, bestMethod) ? currentMethod : bestMethod);
    }

    /* Helper method to create a map of delivery methods with the corresponding basket items based on a permutation.
     * It returns a map where each key represents a delivery method and the value is a list of basket items assigned to that method.
     */
    private Map<String, List<String>> createMethodMap(List<String> permutation, Map<String, List<String>> remainingItems) {
        Map<String, List<String>> currentMethod = new HashMap<>();
        permutation.forEach(method -> remainingItems.entrySet().removeIf(entry -> {
            if (entry.getValue().contains(method)) {
                currentMethod.computeIfAbsent(method, k -> new ArrayList<>()).add(entry.getKey());
                return true;
            }
            return false;
        }));
        return currentMethod;
    }

    /* Helper method to check if the current delivery method is better than the best delivery method.
     * It compares the number of delivery methods and the total number of items in each method.
     * It returns true if the current method is better than the best method, false otherwise.
     */
    private boolean isBetterMethod(Map<String, List<String>> currentMethod, Map<String, List<String>> bestMethod) {
        return currentMethod.size() < bestMethod.size()
                || ( currentMethod.size() == bestMethod.size()
                        && getTotalItemCount(currentMethod) > getTotalItemCount(bestMethod) );
    }

    /* Helper method to get the total number of items in a delivery method.
     * It returns the total count of items in the delivery method.
     */
    private int getTotalItemCount(Map<String, List<String>> method) {
        return method.values()
                .stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);
    }
}