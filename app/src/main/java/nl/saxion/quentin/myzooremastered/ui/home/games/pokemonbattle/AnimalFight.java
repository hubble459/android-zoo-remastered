package nl.saxion.quentin.myzooremastered.ui.home.games.pokemonbattle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.SelectedAnimal;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Move;
import nl.saxion.quentin.myzooremastered.models.MoveSet;
import nl.saxion.quentin.myzooremastered.models.Notification;
import nl.saxion.quentin.myzooremastered.ui.home.AnimalDetails;
import nl.saxion.quentin.myzooremastered.ui.home.GamesActivity;

/**
 * Main screen for pokemon battle
 * Poor commentary because this isn't needed for the app
 * <p>
 * There are two 'screens'
 * There is the animals and their hp bar plus levels
 * And a fragment box with the different actions
 * the four main actions being:
 * - Attack
 * - Pókemon
 * - Bag
 * - Run
 * <p>
 * Attack:
 * Shows you your moves and the possibility to attack with them
 * <p>
 * Pokemon:
 * Shows you information about your animal
 * Including: Name, Level, HP, DEF, XP
 * <p>
 * Bag:
 * Gives you the option to buy potions
 * Potions include: Small HP, Big HP, Small PP, Full PP
 * <p>
 * Run:
 * Gives a three in four chance of success to leave (and return to the GamesActivity)
 */
public class AnimalFight extends AppCompatActivity {
    private static final String MAIN = "main_fragment";
    private static final String OTHER = "other_fragment";
    private Animal myAnimal;
    private Animal opponentAnimal;
    private ProgressBar hpBar;
    private ProgressBar hpBarFoe;
    private int turnCount;
    private double totalDamageDealt;
    private boolean finish;
    private boolean playerTurn;
    private boolean keepTurn;
    private boolean ran;
    private boolean russianRouletteGame;
    private boolean inDialog;

    private Dialog dialog;

    /**
     * Set default values from animals
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_fight);

        resetVars();

        myAnimal = SelectedAnimal.getMyAnimal();
        opponentAnimal = SelectedAnimal.getFoeAnimal();

        // Opponent init
        TextView opponentNameAndLevel = findViewById(R.id.opponentName);
        opponentNameAndLevel.setText(String.format("%s [Level %s]", opponentAnimal.getName(), opponentAnimal.getLevel()));
        ImageView opponentImage = findViewById(R.id.opponentImage);
        opponentImage.setImageResource(opponentAnimal.getImageId());
        hpBarFoe = findViewById(R.id.opponentHPBar);
        hpBarFoe.setMax((int) opponentAnimal.getMaxHP());
        hpBarFoe.setProgress((int) opponentAnimal.getHP());

        // MyAnimal init
        TextView myAnimalNameAndLevel = findViewById(R.id.myAnimalName);
        myAnimalNameAndLevel.setText(String.format("%s [Level %s]", myAnimal.getName(), myAnimal.getLevel()));
        ImageView myAnimalImage = findViewById(R.id.myAnimalImage);
        myAnimalImage.setImageResource(myAnimal.getImageId());
        hpBar = findViewById(R.id.hpBar);
        hpBar.setMax((int) myAnimal.getMaxHP());
        hpBar.setProgress((int) myAnimal.getHP());

        // Fragment init
        getSupportFragmentManager().beginTransaction().replace(R.id.fightFragmentContainer, new MainFightFragment(), MAIN).commit();
    }

    /**
     * Reset all variables to avoid weirdness
     */
    private void resetVars() {
        myAnimal = null;
        opponentAnimal = null;
        hpBar = null;
        hpBarFoe = null;
        turnCount = 0;
        totalDamageDealt = 0;
        finish = false;
        playerTurn = false;
        keepTurn = false;
        ran = false;
        russianRouletteGame = false;
        inDialog = false;
    }

    /**
     * PokeStats
     *
     * @param view button
     */
    public void showPokeStats(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fightFragmentContainer, new PokeStatFragment()).commit();
    }

    /**
     * Bag
     *
     * @param view button
     */
    public void openBag(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fightFragmentContainer, new BagFragment(), OTHER).commit();
    }

    /**
     * Run
     *
     * @param view button
     */
    public void tryToRun(View view) {
        String message;
        finish = false;

        int rand = (int) (Math.random() * 3);
        if (rand == 0) {
            message = getString(R.string.failed_run);
        } else {
            message = getString(R.string.success_run);
            finish = true;
            ran = true;
        }

        showMessage(message);
    }

    /**
     * Show a message in the fragment container
     *
     * @param msg text to show
     */
    private void showMessage(String msg) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fightFragmentContainer, new FightMessageFragment(msg), OTHER).commit();
    }

    /**
     * attack is the on click handler for the attack button on the main panel
     * if you somehow ran out of pp for all your moves, you will automatically use struggle
     *
     * @param view the attack button
     */
    public void attack(View view) {
        if (struggleCheck(myAnimal, opponentAnimal, hpBar, hpBarFoe)) {
            String message = String.format(getString(R.string.struggle_move), myAnimal.getName(), myAnimal.getName());
            showMessage(message);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fightFragmentContainer, new FightAttackFragment(), OTHER).commit();
        }
    }

    /**
     * After showing a message, the app will wait for a click to continue
     * If finish == true the fight activity will stop
     *
     * @param view = context of message panel
     */
    public void onClickMessageHandler(View view) {
        ZooUtils.saveRegionList(this);
        if (finish) {
            if (ran) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.confirm_run)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ran = false;
                                finish = false;
                            }
                        }).show();
            } else {
                finish();
            }
        } else {
            if (inDialog) {
                showMessage(doDialog(playerTurn ? myAnimal : opponentAnimal));
            } else if (playerTurn || keepTurn) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fightFragmentContainer, new MainFightFragment(), MAIN).commit();
                turnCount++;
                playerTurn = false;
                keepTurn = false;
            } else {
                doOpponentTurn();
                checkDead();
                playerTurn = true;
            }
        }
    }

    /**
     * Do damage to foe
     *
     * @param view button in move details in attack screen
     */
    public void attackFoe(View view) {
        Move move = (Move) view.getTag();
        String message = attackMethod(move, myAnimal, opponentAnimal, hpBar, hpBarFoe);
        showMessage(message);
    }

    /**
     * Method of attack
     * Is used by both the player and the opponent
     *
     * @param move     The move to attack with
     * @param a        friendly animal
     * @param b        enemy animal
     * @param hpBar    friendly hp bar
     * @param hpBarFoe enemy hp bar
     * @return string for a message
     */
    private String attackMethod(Move move, Animal a, Animal b, ProgressBar hpBar, ProgressBar hpBarFoe) {
        String message;
        boolean special = false;
        if (russianRouletteGame) message = russianRoulette(opponentAnimal, hpBarFoe);
        else {
            if (a.willMiss()) {
                message = String.format(getString(R.string.missed), move.getName());
                a.miss();
            } else {
                if (move.getLeftPP() == 0) {
                    message = getString(R.string.no_pp);
                    keepTurn = true;
                } else {
                    boolean missed = false;

                    if ((move.getMiss() * 10) >= (int) (Math.random() * 10) + 1) {
                        missed = true;
                    }
                    if (move.getMiss() == 0) missed = false;

                    if (move.getMaxPP() != -1)
                        move.setLeftPP(move.getLeftPP() - 1);

                    switch (move.getMoveType()) {
                        case Move.ATK:
                            if (missed) {
                                message = String.format(getString(R.string.missed), move.getName());
                            } else {
                                doDamage(b, move.getDamage(), hpBarFoe, Move.ATK);
                                double damageDone = Math.round(move.getDamage() / b.getDefence() * 100) / 100.0;
                                message = String.format(getString(R.string.hit), move.getName(), b.getName(), damageDone);
                            }
                            break;
                        case Move.DEF:
                            if (missed) {
                                message = String.format(getString(R.string.failed), move.getName());
                            } else {
                                increaseDef(a, move.getDamage());
                                message = String.format(getString(R.string.def_raised), move.getName(), a.getName());
                            }
                            break;
                        case Move.LOWER_DEF:
                            if (missed) {
                                message = String.format(getString(R.string.failed), move.getName());
                            } else {
                                decreaseDef(b, move.getDamage());
                                message = String.format(getString(R.string.def_lowered), move.getName(), b.getName(), (b.getGender().equals(getString(R.string.male)) ? "his" : "her"));
                            }
                            break;
                        case Move.HP_STEAL:
                            if (missed) {
                                message = String.format(getString(R.string.failed), move.getName());
                            } else {
                                doDamage(b, move.getDamage(), hpBarFoe, Move.HP_STEAL);
                                increaseHP(a, move.getDamage(), hpBar);
                                message = String.format(getString(R.string.took_hp), move.getName(), a.getName(), move.getDamage());
                            }
                            break;
                        case Move.SELF_ATK:
                            if (missed) {
                                message = String.format(getString(R.string.lucky_fail), move.getName());
                            } else {
                                doDamage(a, move.getDamage(), hpBar, Move.SELF_ATK);
                                message = String.format(getString(R.string.hurt_itself), move.getName(), a.getName());
                            }
                            break;
                        default:
                            // Move.SPECIAL
                            if (missed) {
                                message = String.format(getString(R.string.not_work), move.getName());
                            } else {
                                message = specialAttack(move, a, b, hpBar, hpBarFoe);
                                special = true;
                            }
                    }
                }
            }
        }
        if (special) {
            return message;
        } else {
            return a.getName() + ":\n\n" + message;
        }
    }

    /**
     * Check if anyone died and set finish to true if so
     * If myAnimal died, the animal is removed from the app
     * <p>
     * If myAnimal won, xp and coins will be given
     */
    private void checkDead() {
        Animal dead = null;
        Animal alive = null;

        if (myAnimal.getHP() <= 0) {
            dead = myAnimal;
            alive = opponentAnimal;
            finish = true;
        } else if (opponentAnimal.getHP() <= 0) {
            dead = opponentAnimal;
            alive = myAnimal;
            finish = true;
        }

        if (finish) {
            if (dead == null) return;
            TextView finishMessage = findViewById(R.id.finishMessage);
            finishMessage.setVisibility(View.VISIBLE);
            finishMessage.setText(String.format(getString(R.string.died_in_battle), dead.getName()));

            String msg = String.format(getString(R.string.brutal_murder), alive.getName(), dead.getName());
            showMessage(msg);
            if (dead == myAnimal) {
                NotificationList.addNotification(this, String.format(getString(R.string.notification_battle_death), dead.getName()), Notification.DEAD);
                RegionList.removeAnimal(dead);
                setResult(AnimalDetails.DEAD_ANIMAL);
            } else {
                double xp = totalDamageDealt / turnCount;

                myAnimal.resetDefence();
                totalDamageDealt = 0;
                turnCount = 0;

                Intent data = new Intent();
                data.putExtra("coins", (int) (xp * 1.5));
                data.putExtra("xp", xp);
                setResult(GamesActivity.COINS_AND_EXPERIENCE, data);
            }

        }
    }

    /**
     * Do damage
     *
     * @param a      animal to do damage to
     * @param damage amount of damage
     * @param hpBar  hp bar of opponent
     */
    private void doDamage(Animal a, int damage, ProgressBar hpBar, int type) {
        double damageDone = damage / a.getDefence();
        a.setHp(a.getHP() - damageDone);
        hpBar.setProgress((int) a.getHP());
        if (a == opponentAnimal && type != Move.SELF_ATK) {
            totalDamageDealt += damageDone;
        }
    }

    /**
     * do damage but opposite
     */
    private void increaseHP(Animal a, int hp, ProgressBar hpBar) {
        if (a.getHP() >= a.getMaxHP() || a.getHP() + hp >= a.getMaxHP()) return;
        a.setHp(a.getHP() + hp);
        hpBar.setProgress((int) a.getHP());
    }

    /**
     * Increase defence
     */
    private void increaseDef(Animal a, int percent) {
        a.setDefence(a.getDefence() * (percent / 100.0 + 1));
    }

    /**
     * Decrease defence
     */
    private void decreaseDef(Animal b, int percent) {
        b.setDefence(b.getDefence() * (1 - percent / 100.0));
    }

    /**
     * Special attacks
     *
     * @param move     special move
     * @param a        friendly Animal
     * @param b        foe Animal
     * @param hpBar    friendly hp bar
     * @param hpBarFoe foe hp bar
     * @return String message
     */
    private String specialAttack(Move move, Animal a, Animal b, ProgressBar hpBar, ProgressBar hpBarFoe) {
        boolean isMetronome = false;
        String message;
        switch (move.getName()) {
            case "Dig":
                message = "You dug in the ground!\nThe opponent can't hit you for 2 turns";
                opponentAnimal.miss();
                break;

            case "Metronome":
                isMetronome = true;
                Move metronome;
                do {
                    metronome = new MoveSet().getRandomMove();
                } while (metronome.getName().equals("Metronome"));
                message = attackMethod(metronome, a, b, hpBar, hpBarFoe);
                break;

            case "null":
                opponentAnimal = null;
                message = "Now the game's broken...";
                break;

            case "Richard":
                message = String.format(getString(R.string.protecting_richard), a.getName());
                increaseDef(a, move.getDamage());
                if (a == myAnimal)
                    findViewById(R.id.richard).setVisibility(View.VISIBLE);
                else
                    findViewById(R.id.richardFoe).setVisibility(View.VISIBLE);
                break;

            case "Russian Roulette":
                message = russianRoulette(a, hpBar);
                break;

            case "Talk it out":
                if (a != myAnimal)
                    message = String.format(getString(R.string.missed), move.getName());
                else {
                    inDialog = true;
                    dialog = new Dialog();
                    message = doDialog(a);
                }
                break;

            default:
                message = "OWO!?";
        }
        if (!inDialog && !isMetronome) {
            message = a.getName() + ":\n\n" + message;
        }
        return message;
    }

    /**
     * This method is for the "Talk it Out" special move
     * With this a deep conversation will take place between the two animals
     *
     * @param a friendly Animal
     * @return String with message
     */
    private String doDialog(Animal a) {
        String message = dialog.nextLine();

        String peek = dialog.peek();
        if (peek.equals("switch")) {
            dialog.nextLine();
            playerTurn = !playerTurn;
        } else {
            playerTurn = true;
        }

        if (message.equals("decision")) {
            int rand = new Random().nextInt(2);
            if (rand == 0) {
                dialog.nextLine();
            } else {
                // 50% chance to get the opponent to join your zoo
                RegionList.addToRandomRegion(opponentAnimal, false);
                TextView finishMessage = findViewById(R.id.finishMessage);
                finishMessage.setVisibility(View.VISIBLE);
                finishMessage.setText(String.format(getString(R.string.opponent_joined), opponentAnimal.getName()));
                NotificationList.addNotification(this, String.format(getString(R.string.opponent_joined), opponentAnimal.getName()), Notification.ADD);
                finish = true;
            }
            message = dialog.nextLine();

            inDialog = false;
            playerTurn = false;
        }

        return a.getName() + ":\n\n" + message;
    }

    /**
     * Russian Roulette special move
     * 6 rounds of shooting with a 1/6 chance od death
     *
     * @param a     animal shooting
     * @param hpBar hp bar of animal shooting
     * @return message
     */
    private String russianRoulette(Animal a, ProgressBar hpBar) {
        String msg;
        int rand = new Random().nextInt(6);
        if (rand == 0) {
            doDamage(a, (int) a.getHP(), hpBar, Move.ATK);
            TextView finishMessage = findViewById(R.id.finishMessage);
            finishMessage.setVisibility(View.VISIBLE);
            finishMessage.setText(String.format(getString(R.string.died_in_battle), a.getName()));
            NotificationList.addNotification(this, String.format(getString(R.string.notification_battle_death), a.getName()), Notification.DEAD);
            msg = String.format(getString(R.string.bang), a.getName());
            if (a == myAnimal) {
                RegionList.removeAnimal(a);
                setResult(AnimalDetails.DEAD_ANIMAL);
            } else {
                Intent data = new Intent();
                data.putExtra("coins", 35);
                data.putExtra("xp", 20);
                setResult(GamesActivity.COINS_AND_EXPERIENCE);
            }
            finish = true;
        } else {
            msg = String.format(getString(R.string.faulty_gun), a.getName());
        }
        russianRouletteGame = !russianRouletteGame;
        return msg;
    }

    /**
     * Opponent's turn
     */
    private void doOpponentTurn() {
        if (struggleCheck(opponentAnimal, myAnimal, hpBarFoe, hpBar)) {
            String message = String.format(getString(R.string.struggle_move), opponentAnimal.getName(), opponentAnimal.getName());
            showMessage(message);
        } else {
            // Get random move
            Move move;
            do {
                int rand = (int) (Math.random() * 4);
                move = opponentAnimal.getMoveSet().get(rand);
            } while (move.getLeftPP() == 0);

            String message = attackMethod(move, opponentAnimal, myAnimal, hpBarFoe, hpBar);

            showMessage(message);
        }
    }

    /**
     * If animal runs out of pp, animal will use struggle
     *
     * @param a        animal to check pp
     * @param b        opponent
     * @param hpBar    animal hp
     * @param hpBarFoe opponent hp
     * @return boolean struggling
     */
    private boolean struggleCheck(Animal a, Animal b, ProgressBar hpBar, ProgressBar hpBarFoe) {
        int ppCount = 0;
        for (Move m : a.getMoveSet()) {
            ppCount += m.getLeftPP();
        }

        if (ppCount <= 0) {
            doDamage(b, 5, hpBarFoe, Move.ATK);
            doDamage(a, 7, hpBar, Move.SELF_ATK);
            return true;
        }
        return false;
    }

    // Bag item 0
    public void smallPotion(View view) {
        int hpAmount = 20;
        int[] prices = new int[]{10, 20};
        addHPPotion(hpAmount, prices);
    }

    // Bag item 1
    public void bigPotion(View view) {
        int hpAmount = 50;
        int[] prices = new int[]{25, 50};
        addHPPotion(hpAmount, prices);
    }

    // Bag item 2
    public void smallPP(View view) {
        selectMoveAndAddPP(false);
    }

    // Bag item 3
    public void fullPP(View view) {
        selectMoveAndAddPP(true);
    }

    /**
     * Buy a pp bag item for either coins or food
     *
     * @param full full pp or 2 pp
     */
    private void selectMoveAndAddPP(final boolean full) {
        final int price = full ? 50 : 20;

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.choose_move));
        alertDialog.setMessage(String.format(getString(R.string.price), price));

        ArrayAdapter<Move> adapter = new ArrayAdapter<>(alertDialog.getContext(), android.R.layout.simple_list_item_1, myAnimal.getMoveSet());
        final Spinner spinner = new Spinner(alertDialog.getContext());
        spinner.setPaddingRelative(16, 16, 16, 16);
        spinner.setAdapter(adapter);

        alertDialog.setView(spinner);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CoinsAndFood.getCoins() < price) {
                    Toast.makeText(AnimalFight.this, R.string.n_coins, Toast.LENGTH_SHORT).show();
                } else {
                    Move move = (Move) spinner.getSelectedItem();
                    if (full) {
                        buyPPProduct(move, move.getMaxPP());
                    } else {
                        buyPPProduct(move, move.getLeftPP() + 2);
                    }
                }
            }
        });

        alertDialog.show();
    }

    private void buyPPProduct(Move move, int ppAmount) {
        String msg = String.format(getString(R.string.pp_added), ppAmount - move.getLeftPP(), move.getName());
        move.setLeftPP(ppAmount);
        showMessage(msg);
    }

    private void buyHPProduct(int result, double hpAmount, int[] prices) {
        String resultKey = result == 0 ? "coins" : "food";
        int amount = Objects.requireNonNull(CoinsAndFood.getInstance().get(resultKey));
        if (amount > prices[result]) {
            CoinsAndFood.getInstance().put(resultKey, amount - prices[result]);
            myAnimal.addHP(hpAmount, false);
            hpBar.setProgress((int) myAnimal.getHP());
            String msg = String.format(getString(R.string.hp_potion_used), (int) hpAmount);
            showMessage(msg);
        }
    }

    /**
     * Buy an HP Potion
     *
     * @param hpAmount amount of hp
     * @param prices   coin and food prices
     */
    private void addHPPotion(final double hpAmount, final int[] prices) {
        final int COINS = 0;
        final int FOOD = 1;
        int coins = CoinsAndFood.getCoins();
        int food = CoinsAndFood.getFood();
        new AlertDialog.Builder(AnimalFight.this)
                .setTitle(R.string.buy)
                .setMessage(String.format(getString(R.string.choose_payment), coins, food))
                .setNeutralButton(R.string.cancel, null)
                .setNegativeButton(String.format(getString(R.string.food_price), prices[COINS]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buyHPProduct(COINS, hpAmount, prices);
                    }
                })
                .setPositiveButton(String.format(getString(R.string.coin_price), prices[FOOD]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buyHPProduct(FOOD, hpAmount, prices);
                    }
                })
                .show();
    }

    /**
     * When back pressed try to run, or go back a fragment screen
     */
    @Override
    public void onBackPressed() {
        if (inDialog) return;
        if (finish) super.onBackPressed();
        if (getSupportFragmentManager().findFragmentByTag(OTHER) != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fightFragmentContainer, new MainFightFragment(), MAIN).commit();
        } else {
            tryToRun(null);
        }
    }

    /**
     * Dialog class for "Talk it Out"
     */
    private static class Dialog {
        private static Queue<String> talkingDialog;

        Dialog() {
            Queue<String> dialog = new ArrayDeque<>();
            dialog.add("Hey... Can we talk?");
            dialog.add("I know we're battling right now, but..");
            dialog.add("switch");
            dialog.add("Yes?");
            dialog.add("switch");
            dialog.add("Do you want to continue this meaningless fight?");
            dialog.add("We are just being used by Richard. He doesn't even care if we live or die.");
            dialog.add("So I was hoping we could bury the battle axe and become friends..");
            dialog.add("What do you say?");
            dialog.add("switch");
            dialog.add("decision");
            dialog.add("I love you.");
            dialog.add("Shut up!");
            talkingDialog = dialog;
        }

        String nextLine() {
            return talkingDialog.poll();
        }

        String peek() {
            return talkingDialog.peek();
        }
    }
}
