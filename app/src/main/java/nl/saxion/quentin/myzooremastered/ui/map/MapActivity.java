package nl.saxion.quentin.myzooremastered.ui.map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.SelectedAnimal;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.customadapters.ListViewAdapter;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Notification;
import nl.saxion.quentin.myzooremastered.ui.MainActivity;
import nl.saxion.quentin.myzooremastered.ui.home.AnimalDetails;

/**
 * Show a list of animals in a specific region
 */
public class MapActivity extends AppCompatActivity {
    private String region;

    private ListViewAdapter adapter;
    private ListView regionList;
    private List<Animal> regionAnimals;

    private Menu menu;
    private boolean edit = true;
    /**
     * Same as in HomeFragment
     */
    private final MenuItem.OnMenuItemClickListener menuListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            String itemTitle = item.getTitle().toString();
            if (itemTitle.equals(getString(R.string.add))) {
                MainActivity.setInShop(true);
                finish();
            } else if (itemTitle.equals(getString(R.string.delete))) {
                final int amount = MainActivity.getEnabledRadioButtons().size();
                if (amount != 0) {
                    new AlertDialog.Builder(MapActivity.this)
                            .setTitle(R.string.confirm)
                            .setMessage(String.format(getString(R.string.remove_from_region), amount, MainActivity.getEnabledRadioButtons()))
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MapActivity.this, MainActivity.getEnabledRadioButtons().toString(), Toast.LENGTH_SHORT).show();
                                    for (Animal a : MainActivity.getEnabledRadioButtons()) {
                                        NotificationList.addNotification(MapActivity.this, String.format(getString(R.string.notification_removed_from_region), region, a.getName()), Notification.DEAD);
                                        RegionList.removeAnimal(a);
                                        Log.i("Delete List", "onClick: " + a.getName());
                                    }
                                    MainActivity.clearEnabledRadioButtons();
                                    ZooUtils.saveRegionList(MapActivity.this);
                                    noAnimalCheck();
                                    setEdit(false);
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            } else if (itemTitle.equals(getString(R.string.rename))) {
                final EditText etNewRegionName = new EditText(MapActivity.this);
                etNewRegionName.setSingleLine(true);

                new AlertDialog.Builder(MapActivity.this)
                        .setTitle(R.string.rename)
                        .setView(etNewRegionName)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String regionName = etNewRegionName.getText().toString();
                                if (!regionName.equals("")) {
                                    changeRegionName(regionName);
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            } else {
                if (noAnimalCheck()) {
                    setEdit(edit);
                }
            }
            return true;
        }
    };

    /**
     * Fill in ListView with animals from region
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get region
        Intent intent = getIntent();
        region = intent.getStringExtra(MapFragment.REGION_KEY);

        // Get animals from region
        regionAnimals = RegionList.getAnimalList(region);

        // Set title to region
        setTitle(region);

        // Show a text view with "no animals" if there are no animals in this region
        noAnimalCheck();

        adapter = new ListViewAdapter(this, regionAnimals, false);

        regionList = findViewById(R.id.regionList);
        regionList.setAdapter(adapter);

        // onClick: Open AnimalDetails with details of animal
        regionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedAnimal.setMyAnimal((Animal) view.getTag(R.id.animalItem));

                Intent intent = new Intent(MapActivity.this, AnimalDetails.class);
                startActivity(intent);
            }
        });

        // onLongClick: Switch Region dialog
        regionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switchRegion((Animal) view.getTag(R.id.animalItem));
                return true;
            }
        });
    }

    /**
     * Change region name
     * @param newRegion new region name
     */
    private void changeRegionName(String newRegion) {
        List<Animal> animals = new ArrayList<>(RegionList.getAnimalList(region));
        RegionList.getInstance().remove(region);
        RegionList.getInstance().put(newRegion, animals);
        setTitle(newRegion);

        NotificationList.addNotification(this, String.format(getString(R.string.renamed_region_notification), region, newRegion), Notification.MAP);

        region = newRegion;
        ZooUtils.saveRegionList(this);
    }

    /**
     * Switch an animal to a different region
     *
     * @param animal animal to move
     */
    private void switchRegion(final Animal animal) {
        // Make a list with regions other than the region you are already in
        ArrayList<String> regions = new ArrayList<>();
        for (String region : RegionList.getKeys()) {
            if (!region.equals(this.region)) {
                regions.add(region);
            }
        }

        // if you only have one region, you can't move the animal
        if (regions.size() == 0) {
            Toast.makeText(this, R.string.no_other_regions, Toast.LENGTH_SHORT).show();
            return;
        }

        // Spinner with regions you can choose from
        final Spinner spinner = new Spinner(this);
        spinner.setPaddingRelative(16, 16, 16, 16);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, regions);
        spinner.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle(R.string.switch_region)
                .setView(spinner)
                .setPositiveButton(R.string.switch_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        regionAnimals.remove(animal);
                        String newRegion = (String) spinner.getSelectedItem();
                        RegionList.getAnimalList(newRegion).add(animal);

                        NotificationList.addNotification(MapActivity.this, String.format(getString(R.string.transfer_region), animal.getName(), MapActivity.this.region, newRegion), Notification.MAP);

                        refreshList();
                        ZooUtils.saveRegionList(MapActivity.this);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Check if there are animals in this region
     * Set a TextView message if there are no animals
     *
     * @return true or false
     */
    private boolean noAnimalCheck() {
        if (regionAnimals.size() == 0) {
            ((TextView) findViewById(R.id.regionNote)).setText(R.string.empty_region);
            return false;
        } else {
            ((TextView) findViewById(R.id.regionNote)).setText("");
            return true;
        }
    }

    /**
     * Refresh animal list
     */
    private void refreshList() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    /**
     * Same as in HomeFragment
     */
    public void radioButtonHandler(View view) {
        RadioButton b = (RadioButton) view;
        Animal a = (Animal) ((View) b.getParent().getParent()).getTag(R.id.animalItem);
        if (b.isChecked()) {
            if (MainActivity.getEnabledRadioButtons().contains(a)) {
                ((RadioGroup) b.getParent()).clearCheck();
                MainActivity.getEnabledRadioButtons().remove(a);
            } else {
                MainActivity.getEnabledRadioButtons().add(a);
            }
        }
    }

    /**
     * Same as in HomeFragment
     */
    private void setEdit(boolean b) {
        MainActivity.clearEnabledRadioButtons();

        if (b) {
            menu.findItem(R.id.add).setTitle(R.string.delete);
            menu.findItem(R.id.edit).setTitle(R.string.done);
        } else {
            menu.findItem(R.id.add).setTitle(R.string.add);
            menu.findItem(R.id.edit).setTitle(R.string.edit);
        }

        adapter = new ListViewAdapter(this, regionAnimals, b);
        regionList.setAdapter(adapter);

        edit = !edit;
    }

    /**
     * Same as in HomeFragment
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_region_items, menu);
        menu.findItem(R.id.edit).setOnMenuItemClickListener(menuListener);
        menu.findItem(R.id.add).setOnMenuItemClickListener(menuListener);
        menu.findItem(R.id.rename).setOnMenuItemClickListener(menuListener);
        this.menu = menu;
        return true;
    }

    /**
     * Refresh on resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }
}
