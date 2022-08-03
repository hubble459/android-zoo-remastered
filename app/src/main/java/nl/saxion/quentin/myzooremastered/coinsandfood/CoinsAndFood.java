package nl.saxion.quentin.myzooremastered.coinsandfood;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import nl.saxion.quentin.myzooremastered.ui.MainActivity;

/**
 * Hashmap for saving and using the coin and food counters
 */
public class CoinsAndFood extends HashMap<String, Integer> {
    private static CoinsAndFood instance = new CoinsAndFood();

    private CoinsAndFood() {
        this.put("coins", 100);
        this.put("food", 50);
    }

    public static CoinsAndFood getInstance() {
        return instance;
    }

    /**
     * Get saved counters from json and fill them in
     */
    public static void getSave() {
        try {
            Scanner sc = new Scanner(new File(MainActivity.DIR + "/counters.json"));
            StringBuilder jsonString = new StringBuilder();

            while (sc.hasNextLine()) jsonString.append(sc.nextLine());

            JSONObject countersJSON = new JSONObject(jsonString.toString());
            instance.put("coins", countersJSON.getInt("coins"));
            instance.put("food", countersJSON.getInt("food"));
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the amount of coins
     *
     * @return #coins
     */
    public static int getCoins() {
        return instance.get("coins");
    }

    /**
     * Get the amount of food
     *
     * @return #food
     */
    public static int getFood() {
        return instance.get("food");
    }

    /**
     * Subtract an amount of coins
     *
     * @param amount amount
     */
    public static void takeCoins(int amount) {
        int coins = getCoins() - amount;
        instance.put("coins", coins);
    }

    /**
     * Add an amount of coins
     *
     * @param amount amount
     */
    public static void addCoins(int amount) {
        int coins = getCoins() + amount;
        instance.put("coins", coins);
    }

    /**
     * Subtract an amount of food
     *
     * @param amount amount
     */
    public static void takeFood(int amount) {
        int food = getFood() - amount;
        instance.put("food", food);
    }

    /**
     * Add an amount of food
     *
     * @param amount amount
     */
    public static void addFood(int amount) {
        int food = getFood() + amount;
        instance.put("food", food);
    }
}
