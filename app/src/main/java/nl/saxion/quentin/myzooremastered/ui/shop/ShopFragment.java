package nl.saxion.quentin.myzooremastered.ui.shop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.customadapters.GridViewAdapter;
import nl.saxion.quentin.myzooremastered.customviews.AmountPicker;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Move;
import nl.saxion.quentin.myzooremastered.models.MoveSet;
import nl.saxion.quentin.myzooremastered.models.Notification;
import nl.saxion.quentin.myzooremastered.models.ShopItem;

/**
 * GridList with shop items
 */
public class ShopFragment extends Fragment {

    private static List<ShopItem> shopItems;
    private Menu menu;
    private GridViewAdapter adapter;
    private Spinner spinner;
    private Animal animalToAdd;
    private int priceToPay;
    /**
     * Add to selected region
     */
    private final Dialog.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Subtract pay
            CoinsAndFood.takeCoins(priceToPay);

            // Add animal to region
            String region = (String) spinner.getSelectedItem();
            RegionList.getAnimalList(region).add(animalToAdd);

            // Notify and save
            NotificationList.addNotification(getActivity(), String.format(getString(R.string.added_to_region), animalToAdd.getName(), region), Notification.ADD);
            Toast.makeText(getActivity(), R.string.animal_added, Toast.LENGTH_SHORT).show();
            ZooUtils.saveAll(getActivity());
            refreshCounters();
        }
    };
    /**
     * Buy the selected item
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ShopItem shopItem = (ShopItem) view.getTag(R.id.itemTag);
            if (shopItem.isAnimal()) {
                buyAnimal(shopItem);
            } else {
                buyFood(shopItem);
            }
        }
    };

    /**
     * @return inflated view
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    /**
     * Fill in list with Animal Shop Items
     * And set onClickListeners
     * <p>
     * If the toggle button is pressed the list will change from animal items to food items, and vice versa
     *
     * @param view inflated fragment view
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final GridView gridView = view.findViewById(R.id.grid_view);

        // Set listener
        gridView.setOnItemClickListener(itemClickListener);

        shopItems = shopDataAnimals();
        adapter = new GridViewAdapter(gridView.getContext(), shopItems);
        gridView.setAdapter(adapter);

        // Toggle Button listener
        final ToggleButton tb = view.findViewById(R.id.toggleButton);
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopItems.clear();
                if (!tb.isChecked()) {
                    shopItems.addAll(shopDataAnimals());
                } else {
                    shopItems.addAll(shopDataItems());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Buy food
     *
     * @param shopItem food item
     */
    private void buyFood(final ShopItem shopItem) {
        // Compound control
        final AmountPicker ap = new AmountPicker(getActivity());
        ap.setPrice(shopItem.getPrice());

        // Dialog to choose amount and pay price
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_amount)
                .setView(ap)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int food = ap.getAmount() * shopItem.getPrice();
                        int price = ap.getFinalPrice();
                        if (CoinsAndFood.getCoins() < price) {
                            Toast.makeText(getActivity(), R.string.n_coins, Toast.LENGTH_SHORT).show();
                        } else {
                            CoinsAndFood.takeCoins(price);
                            CoinsAndFood.addFood(food);
                            refreshCounters();
                            Toast.makeText(getActivity(), String.format(getString(R.string.bought_food), food, shopItem.getItemName(), price), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        // Compound control listener for enough money
        ap.setListener(new AmountPicker.OnEnoughMoneyListener() {
            @Override
            public void onEnoughMoney(boolean enough) {
                if (enough)
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }
        });

    }

    /**
     * Buy animal
     *
     * @param shopItem animal item
     */
    private void buyAnimal(final ShopItem shopItem) {
        if (CoinsAndFood.getCoins() < shopItem.getPrice()) {
            Toast.makeText(getActivity(), R.string.n_coins, Toast.LENGTH_SHORT).show();
        } else if (RegionList.getKeys().size() == 0) {
            Toast.makeText(getActivity(), R.string.n_regions, Toast.LENGTH_SHORT).show();
        } else {
            showDialog(shopItem);
        }
    }

    /**
     * @param shopItem animal item
     */
    private void showDialog(final ShopItem shopItem) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_make_animal, new LinearLayout(getActivity()));

        final RadioButton button = view.findViewById(R.id.other);
        final EditText customBox = view.findViewById(R.id.custom_gender);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    customBox.setVisibility(View.VISIBLE);
                else
                    customBox.setVisibility(View.INVISIBLE);
            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.make, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        priceToPay = shopItem.getPrice();
                        makeAnimal(view, shopItem);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        alertDialog.show();

        // Check if all necessary values are filled and enable or disable positive button
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        EditText input = alertDialog.findViewById(R.id.animalName);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean hasLetters = false;
                String text = s.toString().toLowerCase();
                for (int i = 0; i < text.length(); i++) {
                    if (text.charAt(i) >= 'a' && text.charAt(i) <= 'z') {
                        hasLetters = true;
                        break;
                    }
                }
                if (!TextUtils.isEmpty(s) && hasLetters) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
    }

    /**
     * Make animal
     * Get type from shopItem
     * Get name and gender from animalDialog
     * <p>
     * Make animal object
     * Choose region to add animal to
     *
     * @param view     animalDialog
     * @param shopItem animal item
     */
    private void makeAnimal(View view, ShopItem shopItem) {
        String type = shopItem.getItemName();
        String name = ((EditText) view.findViewById(R.id.animalName)).getText().toString();
        RadioButton checkedButton = view.findViewById(((RadioGroup) view.findViewById(R.id.radio_group)).getCheckedRadioButtonId());
        String gender;
        if (checkedButton.getText().toString().equals(getString(R.string.custom))) {
            gender = ((EditText) view.findViewById(R.id.custom_gender)).getText().toString();
            if (gender.equals("")) gender = getString(R.string.non_binary);
        } else if (checkedButton.getText().toString().equals(getString(R.string.random))) {
            int rand = (int) (Math.random() * 2);
            if (rand == 0) gender = getString(R.string.male);
            else gender = getString(R.string.female);
        } else {
            gender = checkedButton.getText().toString();
        }

        int imageId = shopItem.getImageId();

        Animal animal = new Animal(name, imageId, type, gender);
        if (RegionList.doesNotHaveAnimal(animal)) {
            animalToAdd = animal;
            chooseRegion();
        } else {
            Toast.makeText(getActivity(), R.string.double_animal, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pick a region from spinner to add animal to
     */
    private void chooseRegion() {
        spinner = new Spinner(getActivity(), Spinner.MODE_DIALOG);
        spinner.setPaddingRelative(16, 16, 16, 16);

        List<String> regions = new ArrayList<>(RegionList.getInstance().keySet());
        Collections.sort(regions);
        SpinnerAdapter adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, regions);
        spinner.setAdapter(adapter);

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.region)
                .setMessage(R.string.choose_region)
                .setView(spinner)
                .setPositiveButton(R.string.ok, onClickListener)
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    // Shop data animals
    private ArrayList<ShopItem> shopDataAnimals() {
        ArrayList<ShopItem> list = new ArrayList<>();
        list.add(new ShopItem(getString(R.string.bunny), 25, R.mipmap.bunny, Color.YELLOW, true));
        list.add(new ShopItem(getString(R.string.chicken), 25, R.mipmap.chicken, Color.CYAN, true));
        list.add(new ShopItem(getString(R.string.cow), 25, R.mipmap.cow, Color.BLUE, true));
        list.add(new ShopItem(getString(R.string.elephant), 25, R.mipmap.elephant, Color.MAGENTA, true));
        list.add(new ShopItem(getString(R.string.hippo), 25, R.mipmap.hippo, Color.GREEN, true));
        list.add(new ShopItem(getString(R.string.child), 100, R.mipmap.human_child, Color.YELLOW, true));
        list.add(new ShopItem(getString(R.string.monkey), 25, R.mipmap.monkey, Color.BLUE, true));
        list.add(new ShopItem(getString(R.string.pig), 25, R.mipmap.pig, Color.RED, true));
        list.add(new ShopItem(getString(R.string.tiger), 25, R.mipmap.tiger, Color.LTGRAY, true));
        list.add(new ShopItem(getString(R.string.turtle), 50, R.mipmap.turtle, Color.CYAN, true));
        return list;
    }

    // Shop data food
    private ArrayList<ShopItem> shopDataItems() {
        ArrayList<ShopItem> list = new ArrayList<>();
        list.add(new ShopItem(getString(R.string.baby_bones), 10, R.mipmap.bones, Color.WHITE, false));
        list.add(new ShopItem(getString(R.string.meat), 20, R.mipmap.meat, Color.BLUE, false));
        list.add(new ShopItem(getString(R.string.water), 100, R.mipmap.water, Color.GREEN, false));
        list.add(new ShopItem(getString(R.string.bread), 30, R.mipmap.bread, Color.RED, false));
        list.add(new ShopItem(getString(R.string.donut), 50, R.mipmap.donut, Color.CYAN, false));
        list.add(new ShopItem(getString(R.string.pea), 1, R.mipmap.pea, Color.YELLOW, false));
        return list;
    }

    /**
     * Refresh coin and food counters
     * And check for soft lock
     */
    private void refreshCounters() {
        menu.findItem(R.id.coins).setTitle(String.format(getString(R.string.coins_amount), CoinsAndFood.getCoins()));
        menu.findItem(R.id.food).setTitle(String.format(getString(R.string.food_amount), CoinsAndFood.getFood()));
        ZooUtils.saveCounters(requireActivity());
        softLocked();
    }

    /**
     * Food and coin counter
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_shop, menu);
        this.menu = menu;
        refreshCounters();
    }

    /**
     * If no animals and no coins
     * give a SoftLocked animal
     */
    private void softLocked() {
        if (CoinsAndFood.getCoins() < 25 && RegionList.getAnimalList().size() == 0) {
            if (RegionList.getInstance().get("Anti Soft Lock") == null)
                RegionList.getInstance().put("Anti Soft Lock", new ArrayList<Animal>());

            Animal softLocked = new Animal("Soft Locked", R.mipmap.richard, "Richard", "Saviour");
            softLocked.setLevel(2);
            List<Move> moves = new ArrayList<>();
            moves.add(new MoveSet().getMetronome());
            softLocked.setMoveSet(moves);
            RegionList.getInstance().get("Anti Soft Lock").add(softLocked);

            new androidx.appcompat.app.AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.soft_locked)
                    .setMessage(R.string.soft_lock_msg)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }
}