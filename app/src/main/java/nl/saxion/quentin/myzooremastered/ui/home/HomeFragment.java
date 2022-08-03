package nl.saxion.quentin.myzooremastered.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Objects;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.SelectedAnimal;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.customadapters.ListViewAdapter;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Notification;
import nl.saxion.quentin.myzooremastered.ui.MainActivity;

/**
 * Shows a list of all animals you have
 * App starts of on this screen
 */
public class HomeFragment extends Fragment {

    private ListViewAdapter adapter;
    private ListView listView;
    private TextView tvNoAnimals;
    private List<Animal> animalList;
    private Menu menu;

    private boolean edit = true;
    /**
     * Menu click listener
     */
    private final MenuItem.OnMenuItemClickListener menuListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            String itemTitle = item.getTitle().toString();
            if (itemTitle.equals(getString(R.string.add))) {
                // Change fragment to shop fragment to add/buy a new animal
                NavController navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_shop);
            } else if (itemTitle.equals(getString(R.string.delete))) {
                // Delete selected animals
                final int amount = MainActivity.getEnabledRadioButtons().size();
                // If none selected do nothing
                if (amount != 0) {
                    // Ask for confirmation
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.confirm)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(String.format(getString(R.string.kill_multiple), amount, MainActivity.getEnabledRadioButtons()))
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Remove all selected animals
                                    for (Animal a : MainActivity.getEnabledRadioButtons()) {
                                        NotificationList.addNotification(getActivity(), String.format(getString(R.string.notification_animal_killed), a.getName()), Notification.DEAD);
                                        RegionList.removeAnimal(a);
                                    }
                                    Toast.makeText(getActivity(), String.format(getString(R.string.killed_multiple), amount), Toast.LENGTH_SHORT).show();
                                    MainActivity.clearEnabledRadioButtons();
                                    ZooUtils.saveRegionList(getActivity());
                                    setEdit(false);
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            } else {
                // Change list adapter to edit list adapter
                setEdit(edit);
            }
            return true;
        }
    };

    /**
     * Inflate view
     *
     * @return inflated view
     */
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Fill in the listView with all animals
     * Add onItemClick; when clicked opens AnimalDetails
     *
     * @param view fragment view
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        listView = view.findViewById(R.id.listView);

        animalList = RegionList.getAnimalList();

        tvNoAnimals = view.findViewById(R.id.noAnimals);
        checkEmpty();

        adapter = new ListViewAdapter(getActivity(), animalList, false);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedAnimal.setMyAnimal((Animal) view.getTag(R.id.animalItem));

                Intent intent = new Intent(getActivity(), AnimalDetails.class);
                startActivityForResult(intent, 0);
            }
        });

        // Swipe up to refresh
        // Not really necessary but fun and satisfying
        final SwipeRefreshLayout srl = view.findViewById(R.id.pullToRefresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                srl.setRefreshing(false);
            }
        });
    }

    /**
     * If list with animals to show is empty, a message with the text "You don't have any animals" will show
     */
    private void checkEmpty() {
        if (animalList.isEmpty()) {
            tvNoAnimals.setVisibility(View.VISIBLE);
        } else {
            tvNoAnimals.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Refresh the list by removing and re-adding
     * Because notifyDataSetChanged alone does not work
     */
    private void refreshList() {
        animalList.clear();
        animalList.addAll(RegionList.getAnimalList());
        if (adapter != null) adapter.notifyDataSetChanged();
        checkEmpty();
    }

    /**
     * Two buttons in the menu bar
     * Either Add and Edit
     * or Delete and Done
     *
     * @param menu         menu
     * @param menuInflater menuInflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.toolbar_animal_items, menu);
        menu.findItem(R.id.edit).setOnMenuItemClickListener(menuListener);
        menu.findItem(R.id.add).setOnMenuItemClickListener(menuListener);
        this.menu = menu;
    }

    /**
     * Change the adapter layout
     *
     * @param b edit
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

        adapter = new ListViewAdapter(getActivity(), RegionList.getAnimalList(), b);
        listView.setAdapter(adapter);

        edit = !edit;
    }

    /**
     * When resumed refresh list
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    /**
     * When an animal died while playing show a message
     *
     * @param requestCode 0
     * @param resultCode  DEAD_ANIMAL or different
     * @param data        null
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AnimalDetails.DEAD_ANIMAL) {
            new AlertDialog.Builder(requireActivity())
                    .setMessage(R.string.died_while_playing)
                    .setPositiveButton(R.string.ok, null)
                    .show();

        }
    }
}
