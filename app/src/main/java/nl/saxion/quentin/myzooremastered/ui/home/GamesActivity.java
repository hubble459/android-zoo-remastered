package nl.saxion.quentin.myzooremastered.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.SelectedAnimal;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.ui.home.games.FourInARowActivity;
import nl.saxion.quentin.myzooremastered.ui.home.games.HangmanActivity;
import nl.saxion.quentin.myzooremastered.ui.home.games.HigherOrLowerActivity;
import nl.saxion.quentin.myzooremastered.ui.home.games.TicTacToeActivity;
import nl.saxion.quentin.myzooremastered.ui.home.games.WordSnakeActivity;
import nl.saxion.quentin.myzooremastered.ui.home.games.pokemonbattle.AnimalFight;

/**
 * List of games to play
 * <p>
 * Method names speak for themselves
 */
public class GamesActivity extends AppCompatActivity {
    public static final int COINS_AND_EXPERIENCE = 10;

    private Animal animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        animal = SelectedAnimal.getMyAnimal();
    }

    public void startHangman(View view) {
        selectLanguageForActivity(HangmanActivity.class);
    }

    public void startWordSnake(View view) {
        selectLanguageForActivity(WordSnakeActivity.class);
    }

    private void selectLanguageForActivity(final Class<?> cls) {
        SelectLanguage selectLanguage = new SelectLanguage(this);
        selectLanguage.show();
        selectLanguage.setOnLanguageSelectedListener(new SelectLanguage.OnLanguageSelected() {
            @Override
            public void languageSelected(int language) {
                Intent intent = new Intent(GamesActivity.this, cls);
                intent.putExtra("language", language);
                startActivityForResult(intent, 0);
            }
        });
    }

    public void startTicTacToe(View view) {
        Intent intent = new Intent(this, TicTacToeActivity.class);
        startActivityForResult(intent, 0);
    }

    public void startFourInARow(View view) {
        Intent intent = new Intent(this, FourInARowActivity.class);
        startActivityForResult(intent, 0);
    }

    public void startHigherOrLower(View view) {
        Intent intent = new Intent(this, HigherOrLowerActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * Start Pokemon ripoff
     * Confirmation dialog for fighting
     *
     * @param view pokemon button
     */
    public void startPokemon(View view) {
        // Refill pp if hp 100
        if (animal.getHP() == animal.getMaxHP()) animal.refillPP();

        // Get Foe
        final Animal animal = getRandomAnimal();
        animal.addHP(animal.getMaxHP()); // Fill foe's HP

        new AlertDialog.Builder(this)
                .setTitle(R.string.battle)
                .setMessage(String.format(getString(R.string.fight_against), animal))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedAnimal.setMyAnimal(GamesActivity.this.animal);
                        SelectedAnimal.setFoeAnimal(animal);

                        Intent intent = new Intent(GamesActivity.this, AnimalFight.class);
                        startActivityForResult(intent, 0);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

    }

    /**
     * Get a random Animal
     *
     * @return Animal
     */
    private Animal getRandomAnimal() {
        // Random name
        List<String> names = new ArrayList<>();
        names.add("Gary");
        names.add("Princess");
        names.add("Fluffy");
        names.add("Jack");
        names.add("Sun");
        names.add("Dad");
        names.add("Richard");
        names.add("Maarten");
        names.add("Dobby");
        names.add("Poopoo");
        names.add("Charles");
        names.add("Human");
        names.add("Mom");

        String name = names.get(new Random().nextInt(names.size()));

        // Random image and type
        List<Integer> images = new ArrayList<>();
        images.add(R.mipmap.bunny);
        images.add(R.mipmap.chicken);
        images.add(R.mipmap.cow);
        images.add(R.mipmap.elephant);
        images.add(R.mipmap.hippo);
        images.add(R.mipmap.human_child);
        images.add(R.mipmap.monkey);
        images.add(R.mipmap.pig);
        images.add(R.mipmap.tiger);
        images.add(R.mipmap.turtle);

        List<String> types = new ArrayList<>();
        types.add(getString(R.string.bunny));
        types.add(getString(R.string.chicken));
        types.add(getString(R.string.cow));
        types.add(getString(R.string.elephant));
        types.add(getString(R.string.hippo));
        types.add(getString(R.string.child));
        types.add(getString(R.string.monkey));
        types.add(getString(R.string.pig));
        types.add(getString(R.string.tiger));
        types.add(getString(R.string.turtle));

        int rand = new Random().nextInt(images.size());
        int imageId = images.get(rand);
        String type = types.get(rand);

        // Random gender
        String gender = new Random().nextInt(2) == 0 ? getString(R.string.male) : getString(R.string.female);

        Animal animal = new Animal(name, imageId, type, gender);

        // Random level
        // 1 lower; the same; 1 higher
        int randLevel = new Random().nextInt(3);
        animal.setLevel(new Random().nextInt(2) == 0 ? this.animal.getLevel() - randLevel : this.animal.getLevel() + randLevel);

        return animal;
    }

    /**
     * If animal died finish activity and return with result DEAD_ANIMAL
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZooUtils.saveRegionList(this);
        if (resultCode == AnimalDetails.DEAD_ANIMAL) {
            setResult(AnimalDetails.DEAD_ANIMAL);
            finish();
        } else if (resultCode == COINS_AND_EXPERIENCE && data != null) {
            int coins = data.getIntExtra("coins", 0);
            double xp = data.getDoubleExtra("xp", 0);

            CoinsAndFood.addCoins(coins);
            animal.addXp(xp);

            showGain(coins, xp);
        }
    }

    private void showGain(int coins, double xp) {
        new AlertDialog.Builder(this)
                .setMessage(String.format(getString(R.string.gain_message), coins, xp))
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private static class SelectLanguage {
        private AlertDialog selectLanguageDialog;
        private OnLanguageSelected onLanguageSelectedListener;

        SelectLanguage(Context context) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        onLanguageSelectedListener.languageSelected(R.raw.words_english);
                    } else {
                        onLanguageSelectedListener.languageSelected(R.raw.words_dutch);
                    }
                }
            };

            selectLanguageDialog = new AlertDialog.Builder(context)
                    .setTitle("Language")
                    .setMessage("Choose Language")
                    .setPositiveButton("English", listener)
                    .setNegativeButton("Dutch", listener)
                    .setNeutralButton("Cancel", null)
                    .create();
        }

        void show() {
            selectLanguageDialog.show();
        }

        void setOnLanguageSelectedListener(OnLanguageSelected onLanguageSelectedListener) {
            this.onLanguageSelectedListener = onLanguageSelectedListener;
        }

        private interface OnLanguageSelected {
            void languageSelected(int language);
        }

    }
}
