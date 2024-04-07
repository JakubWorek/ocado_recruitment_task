# Recruitment task for Ocado by Jakub Worek

## Description
BasketSplitter is a Java project designed to split basket items into different delivery methods based on a set of predefined delivery options loaded from a JSON configuration file. It employs filtering, validation, permutation generation, and comparison logic to find the best delivery method for the given basket items.

## Instalation (gradle)
Add library to your project dependencies in `build.gradle`  
```gradle
dependencies {
    testImplementation platform('org.junit:junit-bom:5.9.1')
    testImplementation 'org.junit.jupiter:junit-jupiter'

    implementation files('path/to/library/BasketSplitter-1.0-Jakub-Worek.jar')
}
```

## Usage example
Import BaskeSplitter:
```java
import com.ocado.basket.BasketSplitter;
```

Use it in your code:
```java
BasketSplitter basketSplitter = new BasketSplitter("absolute/path/to/config/config-1.json");
List<String> items = new ArrayList<>();

// Add yout items

Map<String, List<String>> result = basketSplitter.split(items);
```

## How it works?
* BasketSplitter class is responsible for splitting the basket items into delivery methods.
* It reads the delivery options from a config file and generates all possible permutations of delivery methods.
* It then finds the best delivery method based on the number of items in each delivery method.
* Code provides functionality to split basket items into different delivery methods based on a set of predefined delivery options loaded from a JSON configuration file.
* It employs filtering, validation, permutation generation, and comparison logic to find the best delivery method for the given basket items.
* This way we are always sure that we found best solution to our problem.

## Dependencies used in project
* Jackson Databind: 
  `BasketSplitter` use `ObjectMapper` form `com.fasterxml.jackson.databind.ObjectMapper`
  to deserialize JSON content of `config file`

## Test cases
* Product in basket is not in config: BasketSplitter throws `IllegalArgumentException`
* No items in basket: BasketSplitter returns empty array