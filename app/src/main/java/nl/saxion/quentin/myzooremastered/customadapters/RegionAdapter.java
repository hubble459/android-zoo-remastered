package nl.saxion.quentin.myzooremastered.customadapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import nl.saxion.quentin.myzooremastered.lists.RegionList;

/**
 * Adapter for Region List
 * Shown in MapFragment.java
 */
public class RegionAdapter extends ArrayAdapter<String> {
    private List<String> mRegions;

    public RegionAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        mRegions = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        int max = Math.max(RegionList.getAnimalList().size(), 1);

        int size = RegionList.getAnimalList(mRegions.get(position)).size();
        int alpha = 150 / max * size;
        view.setBackgroundColor(Color.argb(alpha, 255, 100, 200));

        return view;
    }
}
