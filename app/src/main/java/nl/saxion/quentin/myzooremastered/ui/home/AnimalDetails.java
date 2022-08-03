package nl.saxion.quentin.myzooremastered.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.SelectedAnimal;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Notification;

/**
 * Detail Screen of an Animal
 */
public class AnimalDetails extends AppCompatActivity {
    public static final int DEAD_ANIMAL = 9;

    private Animal animal;
    private Menu menu;

    /**
     * Inflate layout and get animal from SelectedAnimal
     * Fill in all values on layout with animal properties
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        animal = SelectedAnimal.getMyAnimal();

        refreshValues();
    }

    /**
     * Fill in values on R.layout.activity_animal_detail
     */
    private void refreshValues() {
        ((TextView) findViewById(R.id.animalName2)).setText(animal.getName());
        ((TextView) findViewById(R.id.animalType2)).setText(animal.getType());
        ((TextView) findViewById(R.id.animalGender2)).setText(animal.getGender());
        ((TextView) findViewById(R.id.animalLevel2)).setText(String.format(getString(R.string.level), animal.getLevel()));
        ((TextView) findViewById(R.id.animalHP2)).setText(String.format(getString(R.string.hp2), animal.getHP(), animal.getMaxHP()));
        ((TextView) findViewById(R.id.animalXP2)).setText(String.format(getString(R.string.xp), animal.getXp(), animal.getXpNeeded()));
        ((ImageView) findViewById(R.id.animalImage2)).setImageResource(animal.getImageId());
        ZooUtils.saveRegionList(this);
        if (menu != null) refreshCounters();
    }

    /**
     * Kill this animal (this.animal)
     *
     * @param view kill button
     */
    public void killAnimal(View view) {
        // Confirm delete with an AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm)
                .setMessage(String.format(getString(R.string.confirm_kill), animal.getName()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    /**
                     * Add a notification to NotificationList
                     * Remove animal from RegionList
                     * Show a Toast Message as confirmation
                     * Save RegionList
                     * Stop activity, because a dead animal does not have details
                     *
                     * @param dialog the AlertDialog
                     * @param which positive button or negative button, in this case, it's always positive button
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationList.addNotification(AnimalDetails.this, String.format(getString(R.string.notification_animal_killed), animal.getName()), Notification.DEAD);
                        RegionList.removeAnimal(animal);
                        Toast.makeText(AnimalDetails.this, String.format(getString(R.string.kill_deleted), animal.getName()), Toast.LENGTH_SHORT).show();
                        ZooUtils.saveRegionList(AnimalDetails.this);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Feed animal button
     * Will feed the selected animal 10 food if you have enough
     *
     * @param view feed button
     */
    public void feedAnimal(View view) {
        // Check if enough food
        if (CoinsAndFood.getFood() < 10) {
            Toast.makeText(this, R.string.not_enough_food, Toast.LENGTH_SHORT).show();
        } else {
            // Check if animal hp is filled
            if (animal.getHP() >= animal.getMaxHP()) {
                Toast.makeText(this, String.format(getString(R.string.filled_hp), animal.getName()), Toast.LENGTH_SHORT).show();
                animal.refillPP();
            } else {
                // Take 10 food from food counter
                CoinsAndFood.takeFood(10);

                // Add 10 HP/Food
                animal.addHP(10);
                if (animal.getHP() == animal.getMaxHP()) {
                    Toast.makeText(this, R.string.refilled_pp, Toast.LENGTH_SHORT).show();
                }

                // Refresh values
                refreshValues();
            }
        }
    }

    /**
     * Show dialog for changing the animal's gender
     *
     * @param view gender change button
     */
    public void genderChangeAnimal(View view) {
        // Inflate genderDialog
        final View genderDialog = getLayoutInflater().inflate(R.layout.dialog_gender_change, new ConstraintLayout(this));

        final EditText editTextCustom = genderDialog.findViewById(R.id.customEditText);
        final RadioGroup rg = genderDialog.findViewById(R.id.radioGroup);
        // Preselect the gender your animal is currently;
        if (animal.getGender().equals(getString(R.string.male))) {
            rg.check(R.id.radioButton1);
        } else if (animal.getGender().equals(getString(R.string.female))) {
            rg.check(R.id.radioButton2);
        } else {
            rg.check(R.id.radioButton3);
            editTextCustom.setHint(animal.getGender());
            editTextCustom.setVisibility(View.VISIBLE);
        }

        // If custom is selected, show customEditText
        ((RadioButton) genderDialog.findViewById(R.id.radioButton3)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextCustom.setVisibility(View.VISIBLE);
                } else {
                    editTextCustom.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Build Dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.gender)
                .setView(genderDialog) // <-- added the genderDialog
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioButton rb = genderDialog.findViewById(rg.getCheckedRadioButtonId());
                        // if gender is already what you selected nothing happened
                        if (rb.getText().toString().equals(animal.getGender())) return;
                        String newGender;

                        // Get newGender
                        if (!rb.getText().toString().equals(getString(R.string.custom))) {
                            newGender = rb.getText().toString();
                        } else {
                            if (!editTextCustom.getText().toString().equals("")) {
                                newGender = editTextCustom.getText().toString();
                            } else return; // If custom is empty return
                        }

                        // Set the newGender
                        animal.setGender(newGender);

                        // Refresh all values
                        refreshValues();
                    }
                })
                .setNegativeButton(R.string.cancel, null).show();
    }

    /**
     * Rename the animal
     *
     * @param view rename button
     */
    public void renameAnimal(View view) {
        final EditText edittext = new EditText(this);
        edittext.setSingleLine(true);

        new AlertDialog.Builder(this)
                .setTitle(R.string.rename)
                .setMessage(R.string.new_name)
                .setView(edittext)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // if new name is empty do nothing
                        String newName = edittext.getText().toString();
                        if (!newName.equals("")) {
                            animal.setName(newName);
                            refreshValues();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Show game activity to play with animal
     * Raise level and gain coins with games
     *
     * @param view play button
     */
    public void playWithAnimal(View view) {
        Intent intent = new Intent(this, GamesActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * Show a list with moves and details when clicked
     *
     * @param view show moves button
     */
    public void showMoves(View view) {
        Intent intent = new Intent(this, MovesActivity.class);
        startActivity(intent);
    }

    /**
     * Refresh counters in menu bar
     */
    private void refreshCounters() {
        menu.findItem(R.id.coins).setTitle(String.format(getString(R.string.coins_amount), CoinsAndFood.getCoins()));
        menu.findItem(R.id.food).setTitle(String.format(getString(R.string.food_amount), CoinsAndFood.getFood()));
        ZooUtils.saveCounters(this);
    }

    /**
     * Make on optionsMenu with a coin and food counter\
     * Clicking these will do nothing
     *
     * @param menu menu
     * @return consume click
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_shop, menu);
        this.menu = menu;
        refreshCounters();
        return true;
    }

    /**
     * refresh counters when resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshValues();
    }

    /**
     * If animal dies in game stop the activity
     *
     * @param requestCode 0
     * @param resultCode  DEAD_ANIMAL or different
     * @param data        null
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DEAD_ANIMAL) {
            finish();
        }
    }
}
