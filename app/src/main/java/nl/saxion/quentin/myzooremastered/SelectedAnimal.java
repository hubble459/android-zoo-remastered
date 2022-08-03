package nl.saxion.quentin.myzooremastered;

import nl.saxion.quentin.myzooremastered.models.Animal;

/**
 * Easier and a more reliable way for passing an animal to a new activity
 * because list are constantly sorted and removed so positions change and animals disappear
 * <p>
 * FoeAnimal is used for the battle system
 */
public class SelectedAnimal {
    private static Animal myAnimal;
    private static Animal foeAnimal;

    public static Animal getMyAnimal() {
        return myAnimal;
    }

    public static void setMyAnimal(Animal myAnimal) {
        SelectedAnimal.myAnimal = myAnimal;
    }

    public static Animal getFoeAnimal() {
        return foeAnimal;
    }

    public static void setFoeAnimal(Animal foeAnimal) {
        SelectedAnimal.foeAnimal = foeAnimal;
    }
}
