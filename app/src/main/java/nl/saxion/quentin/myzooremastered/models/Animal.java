package nl.saxion.quentin.myzooremastered.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import nl.saxion.quentin.myzooremastered.ui.MainActivity;

public class Animal {

    private static int missCount;
    private final String type;
    private final int imageId;
    private String name;
    private String gender;
    private int level;
    private double hp;
    private double maxHP;
    private double defence;
    private double xp;
    private double xpNeeded;
    private boolean willMiss;
    private List<Move> moveSet;

    /**
     * Constructor
     *
     * @param name    name
     * @param imageId image resource
     * @param type    animal type/breed
     * @param gender  gender
     */
    public Animal(String name, int imageId, String type, String gender) {
        this.name = name;
        this.imageId = imageId;
        this.type = type;
        this.gender = gender;
        this.maxHP = 100;
        this.level = 5;
        this.defence = 1;
        this.xpNeeded = 20;
        this.hp = this.maxHP;
        this.willMiss = false;
        this.moveSet = new MoveSet().getFourMoves();
    }

    // Getters

    public List<Move> getMoveSet() {
        return moveSet;
    }

    public void setMoveSet(List<Move> moveSet) {
        this.moveSet = moveSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        double percent = this.level / 200. * level;
        if (level < this.level) {
            percent = 1 - percent;
        } else {
            percent++;
        }

        xpNeeded *= percent;
        maxHP *= percent;
        hp = maxHP;
        this.level = level;
    }

    public double getXp() {
        return xp;
    }

    public double getXpNeeded() {
        return xpNeeded;
    }

    public double getHP() {
        return hp;
    }

    public String getGender() {
        return gender;
    }

    // Setters

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getDefence() {
        return defence;
    }

    public void setDefence(double defence) {
        if (defence < 0.5) defence = 0.5;
        if (defence > 2) defence = 2;
        this.defence = defence;
    }

    public double getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(double maxHP) {
        this.maxHP = maxHP;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    /**
     * Will reset the defence to 1
     */
    public void resetDefence() {
        defence = 1;
    }

    /**
     * Check if animal will miss this round
     *
     * @return miss
     */
    public boolean willMiss() {
        return willMiss;
    }

    /**
     * Animal missed so miss counter will rise
     */
    public void miss() {
        this.willMiss = missCount != 3;
        if (missCount == 3) missCount = 0;
        else missCount++;
    }

    /**
     * Compare animal names
     *
     * @param a animal
     * @return integer
     */
    public int compareTo(Animal a) {
        return name.compareTo(a.getName());
    }

    /**
     * Add an hp amount
     *
     * @param foodAmount amount of food/hp given
     */
    public void addHP(double foodAmount) {
        addHP(foodAmount, true);
    }

    /**
     * Add an hp amount and if hp is higher or equal to max pp will be refilled if ppFill is true
     *
     * @param foodAmount amount of food/hp given
     * @param ppFill     boolean if pp should be filled when hp is full
     */
    public void addHP(double foodAmount, boolean ppFill) {
        if (hp + foodAmount >= maxHP) {
            hp = maxHP;
            if (ppFill) refillPP();
        } else {
            hp += foodAmount;
        }
    }

    /**
     * Refill all pp of the four moves
     */
    public void refillPP() {
        for (Move m : moveSet) {
            m.setLeftPP(m.getMaxPP());
        }
    }

    /**
     * Add xp and level up when needed
     *
     * @param xp xp amount
     */
    public void addXp(double xp) {
        this.xp += xp;
        if (this.xp >= xpNeeded) {
            this.xp = 0;
            levelUp();
        }
    }

    /**
     * Will set level one higher
     * Needed XP will go up
     * Max HP will go up
     * PP will be refilled
     * HP will be filled by half of current hp
     */
    private void levelUp() {
        double percent = level / 200. * ++level + 1;

        if (level > 99) level = 99;
        xpNeeded *= percent;
        maxHP *= percent;
        refillPP();
        addHP((int) (hp / 100 * 50));
    }

    /**
     * Animal to JSONObject
     *
     * @return animalJSON
     * @throws JSONException for if something goes wrong
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject animalJSON = new JSONObject();
        animalJSON.put("name", name);
        animalJSON.put("image", imageId);
        animalJSON.put("type", type);
        animalJSON.put("gender", gender);
        animalJSON.put("hp", hp);
        animalJSON.put("maxHP", maxHP);
        animalJSON.put("level", level);

        JSONArray moveSetJSON = new JSONArray();
        for (Move m : moveSet) {
            moveSetJSON.put(m.toJSON());
        }
        animalJSON.put("moveSet", moveSetJSON);

        return animalJSON;
    }

    /**
     * When an animal list is used in a standard adapter for spinner and the sort like:
     * ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new List<Animal>())
     * Animals will show as "Name the Type"
     * eg. Egbert the Penguin
     *
     * @return String
     */
    @NonNull
    @Override
    public String toString() {
        return String.format(MainActivity.getAnimalToString(), name, type);
    }
}
