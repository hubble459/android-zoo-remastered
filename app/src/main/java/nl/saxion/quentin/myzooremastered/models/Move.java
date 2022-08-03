package nl.saxion.quentin.myzooremastered.models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A move
 */
public class Move {
    public static final int ATK = 0;
    public static final int DEF = 1;
    public static final int LOWER_DEF = 2;
    public static final int HP_STEAL = 3;
    public static final int SELF_ATK = 4;
    static final int UNKNOWN = 5;
    private final double miss;
    private final int damage;
    private final int moveType;
    private final int maxPP;
    private final String description;
    private int leftPP;
    private String name;

    // Constructors

    Move(Move move) {
        this.name = move.getName();
        this.moveType = move.getMoveType();
        this.damage = move.getDamage();
        this.maxPP = move.getMaxPP();
        this.leftPP = move.getMaxPP();
        this.description = move.getDescription();
        this.miss = move.getMiss();
    }

    public Move(String name, int moveType, int damage, int maxPP, String description, double missChance) {
        this.name = name;
        this.moveType = moveType;
        this.damage = damage;
        this.maxPP = maxPP;
        this.leftPP = maxPP;
        this.description = description;
        this.miss = missChance;
    }

    /**
     * Convert a moveType integer to a String
     *
     * @param moveType moveType integer
     * @return String moveType
     */
    public static String getMoveTypeName(int moveType) {
        switch (moveType) {
            case ATK:
                return "ATTACK";
            case DEF:
                return "DEFENCE";
            case LOWER_DEF:
                return "LOWER DEFENCE";
            case HP_STEAL:
                return "HP STEAL";
            case SELF_ATK:
                return "SELF ATTACK";
            default:
                return "SPECIAL";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoveType() {
        return moveType;
    }

    public int getDamage() {
        return damage;
    }

    public int getMaxPP() {
        return maxPP;
    }

    public int getLeftPP() {
        return leftPP;
    }

    public void setLeftPP(int usableTimes) {
        if (usableTimes > maxPP) usableTimes = maxPP;
        this.leftPP = usableTimes;
    }

    public String getDescription() {
        return description;
    }

    public double getMiss() {
        return miss;
    }

    /**
     * Move to JSON
     *
     * @return JSONObject moveJSON
     * @throws JSONException exception
     */
    JSONObject toJSON() throws JSONException {
        JSONObject moveJSON = new JSONObject();
        moveJSON.put("name", name);
        moveJSON.put("moveType", moveType);
        moveJSON.put("damage", damage);
        moveJSON.put("maxPP", maxPP);
        moveJSON.put("leftPP", leftPP);
        moveJSON.put("description", description);
        moveJSON.put("miss", miss);
        return moveJSON;
    }

    /**
     * Move name [LeftPP / MaxPP]
     * eg. Pounce [3/5]
     *
     * @return String move
     */
    @NonNull
    @Override
    public String toString() {
        String leftPP = String.valueOf(this.leftPP);
        String maxPP = String.valueOf(this.maxPP);
        if (this.maxPP == -1) {
            leftPP = "∞";
            maxPP = "∞";
        }
        return String.format("%s [%s/%s]", name, leftPP, maxPP);
    }
}
