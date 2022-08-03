package nl.saxion.quentin.myzooremastered.lists;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Move;
import nl.saxion.quentin.myzooremastered.ui.MainActivity;

public class RegionList extends HashMap<String, List<Animal>> {
    private static final String TAG = "RegionList";

    private static final RegionList instance = new RegionList();

    public static RegionList getInstance() {
        sort();
        return instance;
    }

    /**
     * Remove all animals
     */
    public static void clearLists() {
        instance.clear();
    }

    /**
     * Remove an animal from any region
     *
     * @param a animal
     */
    public static void removeAnimal(Animal a) {
        for (List<Animal> animals : instance.values()) {
            if (animals.remove(a)) break;
        }
    }

    /**
     * Get all animals from all regions
     *
     * @return list with all animals
     */
    public static List<Animal> getAnimalList() {
        sort();
        List<Animal> allAnimals = new ArrayList<>();
        for (List<Animal> animals : instance.values()) {
            allAnimals.addAll(animals);
        }
        Collections.sort(allAnimals, new Comparator<Animal>() {
            @Override
            public int compare(Animal o1, Animal o2) {
                return o1.compareTo(o2);
            }
        });
        return allAnimals;
    }

    /**
     * Get all animals from a certain region
     *
     * @param regionKey region
     * @return list with all animals from that region
     */
    public static List<Animal> getAnimalList(String regionKey) {
        if (instance.containsKey(regionKey)) {
            return instance.get(regionKey);
        }
        return new ArrayList<>();
    }

    /**
     * Regions for demonstration purposes
     */
    public static void demoRegionList() {
        if (!instance.containsKey("Ocean"))
            instance.put("Ocean", new ArrayList<Animal>());
        if (!instance.containsKey("Arctic"))
            instance.put("Arctic", new ArrayList<Animal>());
        if (!instance.containsKey("Jungle"))
            instance.put("Jungle", new ArrayList<Animal>());
        if (!instance.containsKey("Hell"))
            instance.put("Hell", new ArrayList<Animal>());
        if (!instance.containsKey("Forest"))
            instance.put("Forest", new ArrayList<Animal>());
        if (!instance.containsKey("Desert"))
            instance.put("Desert", new ArrayList<Animal>());
        if (!instance.containsKey("City"))
            instance.put("City", new ArrayList<Animal>());
        if (!instance.containsKey("Island"))
            instance.put("Island", new ArrayList<Animal>());
    }

    /**
     * Animals for demonstration purposes
     */
    public static void demoAnimalList() {
        ArrayList<Animal> demoAnimals = new ArrayList<>();
        Animal a0 = new Animal("Berry", R.mipmap.bunny, "Bunny", "Male");
        Animal a1 = new Animal("Chuckle", R.mipmap.chicken, "Chicken", "Male");
        Animal a2 = new Animal("Betsy", R.mipmap.cow, "Cow", "Female");
        Animal a3 = new Animal("Trunk", R.mipmap.elephant, "Elephant", "Non-Binary");
        Animal a4 = new Animal("Moto Moto", R.mipmap.hippo, "Hippo", "Alpha Male");
        Animal a5 = new Animal("Child", R.mipmap.human_child, "Human Child", "Female");
        Animal a6 = new Animal("Saru", R.mipmap.monkey, "Monkey", "Male");
        Animal a7 = new Animal("Ur mum", R.mipmap.pig, "Pig", "Female");
        Animal a8 = new Animal("Tigre", R.mipmap.tiger, "Tiger", "Male");
        Animal a9 = new Animal("God", R.mipmap.turtle, "Turtle", "Androgynous");
        demoAnimals.add(a0);
        demoAnimals.add(a1);
        demoAnimals.add(a2);
        demoAnimals.add(a3);
        demoAnimals.add(a4);
        demoAnimals.add(a5);
        demoAnimals.add(a6);
        demoAnimals.add(a7);
        demoAnimals.add(a8);
        demoAnimals.add(a9);

        addToRandomRegion(demoAnimals);
    }

    /**
     * Add a list of animals to random regions
     *
     * @param animals list of animals
     */
    private static void addToRandomRegion(List<Animal> animals) {
        for (Animal a : animals) {
            addToRandomRegion(a, false);
        }
        sort();
    }

    /**
     * Used by addToRandomRegion to get a random Region
     *
     * @return a random region from keySet
     */
    private static String getRandomRegion() {
        String randomRegion = null;

        int rand = new Random().nextInt(instance.keySet().size());
        int counter = 0;
        for (String key : instance.keySet()) {
            if (counter == rand) {
                randomRegion = key;
            }
            counter++;
        }
        return randomRegion;
    }

    /**
     * Add one animal to a random region
     * Will make a list with one animal
     *
     * @param animal to add
     */
    public static void addToRandomRegion(Animal animal, boolean preventDoubles) {
        String region = getRandomRegion();

        if (doesNotHaveAnimal(animal) || !preventDoubles) {
            getAnimalList(region).add(animal);
        }
    }

    /**
     * Check if RegionList has an animal
     *
     * @param animal to check
     * @return has
     */
    public static boolean doesNotHaveAnimal(Animal animal) {
        for (Animal a : getAnimalList()) {
            if (a.getName().equals(animal.getName()) &&
                    a.getGender().equals(animal.getGender()) &&
                    a.getType().equals(animal.getType())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sort all lists in HashMap
     */
    private static void sort() {
        for (String key : instance.keySet()) {
            Collections.sort(Objects.requireNonNull(instance.get(key)), new Comparator<Animal>() {
                @Override
                public int compare(Animal o1, Animal o2) {
                    return o1.compareTo(o2);
                }
            });
        }
    }

    /**
     * Get all keys in List<String> form
     * <p>
     * instance.keySet() to List
     *
     * @return List with keys
     */
    public static List<String> getKeys() {
        return new ArrayList<>(instance.keySet());
    }

    /**
     * Get JSON with regions and animals and fill them in the list
     */
    public static void getSave() {
        Log.d(TAG, "getSave: Getting regions");
        try {
            File file = new File(MainActivity.DIR + "/regionList.json");
            Scanner sc = new Scanner(file);
            StringBuilder jsonString = new StringBuilder();

            while (sc.hasNextLine()) {
                jsonString.append(sc.nextLine());
            }

            JSONArray regionsJSON = new JSONArray(jsonString.toString());
            Log.i(TAG, "getSave: Array Length: " + regionsJSON.length());
            for (int i = 0; i < regionsJSON.length(); i++) {
                JSONObject regionJSON = regionsJSON.getJSONObject(i);
                String region = regionJSON.getString("region");

                if (instance.get(region) == null) {
                    instance.put(region, new ArrayList<Animal>());
                }

                JSONArray animalsJSON = regionJSON.getJSONArray("animals");
                for (int j = 0; j < animalsJSON.length(); j++) {
                    JSONObject animalJSON = animalsJSON.getJSONObject(j);
                    Animal animal = getAnimalFromJSONObject(animalJSON);

                    if (doesNotHaveAnimal(animal)) {
                        getAnimalList(region).add(animal);
                    }
                }
            }
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Turn an JSONObject with Animal properties to an Animal Object
     *
     * @param animalJSON JSONObject
     * @return Animal object
     * @throws JSONException if error reading JSONException will be thrown
     */
    private static Animal getAnimalFromJSONObject(JSONObject animalJSON) throws JSONException {
        String name = animalJSON.getString("name");
        String type = animalJSON.getString("type");
        String gender = animalJSON.getString("gender");
        int image = animalJSON.getInt("image");
        int level = animalJSON.getInt("level");
        int hp = animalJSON.getInt("hp");
        int maxHP = animalJSON.getInt("maxHP");

        List<Move> moveSet = new ArrayList<>();
        JSONArray moveSetJSON = animalJSON.getJSONArray("moveSet");
        for (int j = 0; j < moveSetJSON.length(); j++) {
            JSONObject moveJSON = moveSetJSON.getJSONObject(j);
            String moveName = moveJSON.getString("name");
            String moveDescription = moveJSON.getString("description");
            int moveType = moveJSON.getInt("moveType");
            int moveDamage = moveJSON.getInt("damage");
            int moveMaxPP = moveJSON.getInt("maxPP");
            int moveLeftPP = moveJSON.getInt("leftPP");
            int moveMiss = moveJSON.getInt("miss");

            Move move = new Move(moveName, moveType, moveDamage, moveMaxPP, moveDescription, moveMiss);
            move.setLeftPP(moveLeftPP);
            moveSet.add(move);
        }
        Animal animal = new Animal(name, image, type, gender);
        animal.setLevel(level);
        animal.setMaxHP(maxHP);
        animal.setHp(hp);
        animal.setMoveSet(moveSet);
        return animal;
    }
}
