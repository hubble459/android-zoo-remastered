package nl.saxion.quentin.myzooremastered.ui.map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.customadapters.RegionAdapter;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Notification;

/**
 * Show a list with regions
 * Regions can contain animals
 * When a region is clicked MapActivity will show the list of animals in that region
 */
public class MapFragment extends Fragment {
    static final String REGION_KEY = "region_key";

    private List<String> regions;
    private RegionAdapter adapter;
    private TextView tvNoRegions;

    /**
     * Inflate view
     *
     * @return inflated view
     */
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    /**
     * List will show items with a color gradient dependant on the amount of animals in that region
     * So a more dense color when there's more animals in it
     *
     * @param view fragment view
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        tvNoRegions = view.findViewById(R.id.noRegions);
        refreshRegions();

        ListView list = view.findViewById(R.id.regionList);
        adapter = new RegionAdapter(requireActivity(), regions);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                String region = regions.get(position);
                intent.putExtra(REGION_KEY, region);
                startActivity(intent);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteRegion(position);
                return true;
            }
        });
    }

    /**
     * Delete a region and all animals in it
     *
     * @param position position of region in list
     */
    private void deleteRegion(final int position) {
        String finalKey = regions.get(position);

        final List<Animal> animalList = RegionList.getAnimalList(finalKey);
        String confirmationMessage = String.format(getString(R.string.confirm_remove_region), regions.get(position), animalList.size(), animalList);

        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.delete)
                .setMessage(confirmationMessage)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String region = regions.get(position);
                        RegionList.getInstance().remove(region);
                        for (Animal a : animalList) {
                            NotificationList.addNotification(getActivity(), String.format(getString(R.string.notification_removed_from_region), region, a.getName()), Notification.DEAD);
                        }
                        animalList.clear();

                        Toast.makeText(getActivity(), String.format(getString(R.string.removed_region), region), Toast.LENGTH_SHORT).show();
                        NotificationList.addNotification(getActivity(), String.format(getString(R.string.region_deleted), region), Notification.DEAD);

                        refreshRegions();
                        ZooUtils.saveRegionList(getActivity());
                    }
                })
                .show();
    }

    /**
     * Add a region
     */
    private void addRegion() {
        final EditText editText = new EditText(getActivity());
        editText.setHint(R.string.name);
        editText.setPaddingRelative(16, 32, 32, 32);
        editText.setSingleLine(true);

        final AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.add_region)
                .setView(editText)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String regionName = editText.getText().toString();

                        RegionList.getInstance().put(regionName, new ArrayList<Animal>());

                        String note = String.format(getString(R.string.region_added), regionName);
                        NotificationList.addNotification(getActivity(), note, Notification.ADD);

                        refreshRegions();
                        ZooUtils.saveRegionList(getActivity());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
    }

    /**
     * Refresh the list
     */
    private void refreshRegions() {
        regions = RegionList.getKeys();
        Collections.sort(regions);

        if (adapter != null) {
            adapter.clear();
            adapter.addAll(regions);
            adapter.notifyDataSetChanged();
        }
        checkEmpty();
    }

    /**
     * If list with regions to show is empty, a message with the text "You don't have any regions" will show
     */
    private void checkEmpty() {
        if (regions.isEmpty()) {
            tvNoRegions.setVisibility(View.VISIBLE);
        } else {
            tvNoRegions.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Add button
     *
     * @param menu     menu
     * @param inflater inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_just_add, menu);
        menu.findItem(R.id.add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addRegion();
                return true;
            }
        });
    }

    /**
     * Refresh on resume
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshRegions();
    }
}
