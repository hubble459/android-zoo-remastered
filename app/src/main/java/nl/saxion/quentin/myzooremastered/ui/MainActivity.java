package nl.saxion.quentin.myzooremastered.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Move;
import nl.saxion.quentin.myzooremastered.models.MoveSet;

public class MainActivity extends AppCompatActivity {
    public static File DIR;

    private static boolean inShop = false;

    private static String animalToString;
    /**
     * Used when selecting list items to delete
     * Every item selected will be added to enabledRadioButtons
     */
    private static ArrayList<Animal> enabledRadioButtons = new ArrayList<>();

    /**
     * Get resource R.string.animal_to_string
     *
     * @return resource string
     */
    public static String getAnimalToString() {
        return animalToString;
    }

    /**
     * When set in shop, MainActivity will tell the navigation system to switch to the shop fragment
     *
     * @param inShop boolean
     */
    public static void setInShop(boolean inShop) {
        MainActivity.inShop = inShop;
    }

    /**
     * Get list with selected animals
     *
     * @return list with selected animals
     */
    public static ArrayList<Animal> getEnabledRadioButtons() {
        return enabledRadioButtons;
    }

    /**
     * Clear list
     */
    public static void clearEnabledRadioButtons() {
        enabledRadioButtons.clear();
    }

    /**
     * Initialize all standard values and fetch all saves
     * And setup bottom nav bar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Used in Animal class
        // Animal class cant get String resources so it is instantiated here
        animalToString = getString(R.string.animal_to_string);

        DIR = getFilesDir();

        // Get all saves from json

        File counterSave = new File(getFilesDir(), "counters.json");
        if (counterSave.exists()) CoinsAndFood.getSave();

        File notificationSave = new File(getFilesDir(), "notificationList.json");
        if (notificationSave.exists()) NotificationList.getSave();

        File regionSave = new File(getFilesDir(), "regionList.json");
        if (regionSave.exists()) RegionList.getSave();

        // Start setting the BottomNavigationBar

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_map,
                R.id.navigation_shop,
                R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // If in shop change the fragment to the shop fragment
        // instead of the default home fragment
        // I do this so when you press add in MapActivity you will be sent to the correct fragment and not back to home fragment
        if (inShop) {
            navController.navigate(R.id.navigation_shop);
        }
        inShop = false;

        // Whenever MainActivity gets created all lists will be saved to avoid data loss
        ZooUtils.saveAll(this);
    }

    /**
     * When a radio button is pressed, you cannot deselect it
     * So when pressed this method will deselect or select when needed
     *
     * @param view the RadioButton itself
     */
    public void radioButtonHandler(View view) {
        RadioButton b = (RadioButton) view;
        Animal a = (Animal) ((View) b.getParent().getParent()).getTag(R.id.animalItem);
        if (b.isChecked()) {
            if (enabledRadioButtons.contains(a)) {
                ((RadioGroup) b.getParent()).clearCheck();
                enabledRadioButtons.remove(a);
            } else {
                enabledRadioButtons.add(a);
            }
        }
    }

    /**
     * When you try to exit the app by back presses
     * The lists will be saved to json
     */
    @Override
    public void onBackPressed() {
        ZooUtils.saveAll(this);
        super.onBackPressed();
    }
}
