package nl.saxion.quentin.myzooremastered.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MoveSet {

    /**
     * Moves with their types in a HashMap
     */
    private final HashMap<Integer, List<Move>> hashMap = new HashMap<>();

    /**
     * Constructor
     * <p>
     * Initiate moves
     */
    public MoveSet() {
        if (hashMap.size() == 0) {
            ArrayList<Move> moveList = getMoveList();
            for (Move m : moveList) {
                if (!hashMap.containsKey(m.getMoveType())) {
                    ArrayList<Move> list = new ArrayList<>();
                    list.add(m);
                    hashMap.put(m.getMoveType(), list);
                } else {
                    Objects.requireNonNull(hashMap.get(m.getMoveType())).add(m);
                }
            }
        }
    }

    /**
     * Check if a list has a move by just the name
     *
     * @param moves List with moves
     * @param name  name of move to compare
     * @return boolean has
     */
    private static boolean listHas(ArrayList<Move> moves, String name) {
        for (Move m : moves) {
            if (m.getName().equals(name)) return true;
        }
        return false;
    }

    /**
     * Get one random move
     *
     * @return Move
     */
    public Move getRandomMove() {
        int rand1 = (int) (Math.random() * hashMap.keySet().size());
        int rand2 = (int) (Math.random() * Objects.requireNonNull(hashMap.get(rand1)).size());
        return new Move(Objects.requireNonNull(hashMap.get(rand1)).get(rand2));
    }

    /**
     * Get one random attack move
     * and three random moves
     *
     * @return List with moves
     */
    List<Move> getFourMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        // Make sure you have at least one attack move
        int rand = (int) (Math.random() * Objects.requireNonNull(hashMap.get(Move.ATK)).size());
        Move move = new Move(Objects.requireNonNull(hashMap.get(Move.ATK)).get(rand));
        moves.add(move);

        // get three random moves
        for (int i = 0; i < 3; i++) {
            do {
                rand = (int) (Math.random() * hashMap.keySet().size());
                int rand2 = (int) (Math.random() * Objects.requireNonNull(hashMap.get(rand)).size());
                move = new Move(Objects.requireNonNull(hashMap.get(rand)).get(rand2));
            } while (listHas(moves, move.getName()));
            moves.add(move);
        }

        return moves;
    }

    /**
     * @return List with all possible moves
     */
    private ArrayList<Move> getMoveList() {
        ArrayList<Move> list = new ArrayList<>();
        list.add(new Move("3.1415", Move.LOWER_DEF, 30, 3, "Your intelligence increases! The opponent will be left defenceless.", 0.1));
        list.add(new Move("Boob Flash", Move.LOWER_DEF, 15, 1, "By showing off your nipples, the opponent will be left in a trance.", 0.5));
        list.add(new Move("Bruh Moment", Move.ATK, 8, 10, "Bruh", 0.3));
        list.add(new Move("Cafe KUM", Move.ATK, 7, 10, "Will make the opponent cringe.", 0.1));
        list.add(new Move("Cry", Move.SELF_ATK, 1, -1, "Will do 1 damage to yourself, making the opponent pity you.", 0));
        list.add(new Move("Dig", Move.UNKNOWN, 0, 5, "By digging into the ground, you will be immune to the opponents next three moves", 0.2));
        list.add(new Move("Distract", Move.LOWER_DEF, 5, 10, "Will distract your opponent, resulting in a weaker punch.", 0.2));
        list.add(new Move("Emancipation", Move.LOWER_DEF, 50, 1, "Using this move you will make the opponent lose confidence.", 0));
        list.add(new Move("Fake Surrender", Move.ATK, 30, 6, "Stab the opponent in the back when they think it's over", 0.7));
        list.add(new Move("Feminism", Move.ATK, 10, 5, "Has a 50% chance of being bullshit.", 0.5));
        list.add(new Move("Fornicate", Move.LOWER_DEF, 25, 2, "By fornicating right in front of the opponent, they will freeze in their place.", 0.1));
        list.add(new Move("Hug", Move.LOWER_DEF, 5, -1, "Make opponent lose their will to attack.", 0.2));
        list.add(new Move("Knife", Move.ATK, 20, 5, "Stab a knife in their flesh.", 0.2));
        list.add(new Move("Leer", Move.LOWER_DEF, 5, 5, "Useful against tough, armored Pokémon.", 0.3));
        list.add(new Move("Make a scene", Move.LOWER_DEF, 5, 10, "Will make your opponent self-conscious", 0.6));
        list.add(new Move("Metronome", Move.UNKNOWN, 0, 5, "Will use a random move", 0.1));
        list.add(new Move("Nibble", Move.ATK, 5, 30, "Nibble the opponents hp away.", 0.1));
        list.add(new Move("null", Move.UNKNOWN, 0, 1, "Hidden move! Might delete your opponent...", 0));
        list.add(new Move("Nuzzle", Move.LOWER_DEF, 15, 10, "By pretending to be innocent, their defence will drop.", 0.2));
        list.add(new Move("Pounce", Move.ATK, 7, 10, "Pounce on the opponent.", 0.1));
        list.add(new Move("Rawr x3", Move.DEF, 5, 30, "That's \"I love you\" in dinosaur", 0.1));
        list.add(new Move("Richard", Move.UNKNOWN, 20, -1, "Summon Richard to protect you.", 0.6));
        list.add(new Move("Roll", Move.DEF, 7, 5, "Rolling will make you faster and will increase defence.", 0.2));
        list.add(new Move("Russian Roulette", Move.UNKNOWN, 5, 10, "Has a 1/6 chance of killing either you or the opponent.", 0));
        list.add(new Move("Spook", Move.LOWER_DEF, 5, 7, "Spook the foe.", 0.1));
        list.add(new Move("Striptease", Move.LOWER_DEF, 5, 10, "Will make your opponent lose their will to fight", 0.2));
        list.add(new Move("Suicide", Move.SELF_ATK, 10000, 1, "Will do full damage to yourself, resulting in death. Has a 70% success rate.", 0.3));
        list.add(new Move("Suck", Move.HP_STEAL, 10, 5, "Steal 10 HP of the opponent", 0.4));
        list.add(new Move("Tackle", Move.ATK, 10, 20, "Be a normie and tackle.", 0.1));
        list.add(new Move("Talk it out", Move.UNKNOWN, 0, 1, "Try to use words to make the opponent surrender.", 0.2));
        list.add(new Move("Thunderbolt", Move.ATK, 20, 5, "The copyright infringement might do more damage.", 0.6));
        return list;
    }

    public Move getMetronome() {
        return new Move(hashMap.get(Move.UNKNOWN).get(1));
    }
}
