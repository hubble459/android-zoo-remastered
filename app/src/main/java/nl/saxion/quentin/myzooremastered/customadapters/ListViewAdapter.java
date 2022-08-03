package nl.saxion.quentin.myzooremastered.customadapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.ui.MainActivity;

/**
 * Adapter for Animal Lists
 * in MapActivity.java and HomeFragment.java
 */
public class ListViewAdapter extends ArrayAdapter<Animal> {

    private final Context mContext;
    private final List<Animal> animalList;
    private final int layout;
    private boolean edit;

    public ListViewAdapter(Context context, @NonNull List<Animal> list, boolean edit) {
        super(context, 0, list);
        mContext = context;
        animalList = list;

        this.edit = edit;
        if (edit) layout = R.layout.layout_list_edit_item;
        else layout = R.layout.layout_list_item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.animalImage);
            holder.name = convertView.findViewById(R.id.animalName);
            holder.type = convertView.findViewById(R.id.animalType);
            if (edit) holder.check = convertView.findViewById(R.id.checkButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Animal currentAnimal = animalList.get(position);

        try {
            String type = mContext.getResources().getResourceTypeName(currentAnimal.getImageId());
            if (!type.equals("drawable") && !type.equals("mipmap")) {
                ZooUtils.deleteAll(mContext);
                ((Activity) mContext).finishAndRemoveTask();
            }
        } catch (Resources.NotFoundException e) {
            ZooUtils.deleteAll(mContext);
            ((Activity) mContext).finishAndRemoveTask();
        }

        holder.image.setImageResource(currentAnimal.getImageId());
        holder.name.setText(currentAnimal.getName());
        holder.type.setText(String.format("%s\n%s", currentAnimal.getType(), currentAnimal.getGender()));
        if (edit) {
            if (MainActivity.getEnabledRadioButtons().contains(currentAnimal)) {
                holder.check.setChecked(true);
            } else {
                if (holder.check.getParent() != null)
                    ((RadioGroup) holder.check.getParent()).clearCheck();
            }
        }

        convertView.setTag(R.id.animalItem, currentAnimal);
        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
        TextView type;
        RadioButton check;
    }
}
