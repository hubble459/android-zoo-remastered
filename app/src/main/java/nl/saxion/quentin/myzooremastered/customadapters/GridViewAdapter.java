package nl.saxion.quentin.myzooremastered.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.models.ShopItem;

/**
 * Adapter for shop ui
 * In ShopFragment.java
 */
public class GridViewAdapter extends ArrayAdapter<ShopItem> {
    private static final int resource = R.layout.layout_shop_item_list;
    private final Context mContext;
    private final List<ShopItem> shopItems;

    public GridViewAdapter(@NonNull Context context, @NonNull List<ShopItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.shopItems = objects;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.imageView);
            holder.name = convertView.findViewById(R.id.itemName);
            holder.price = convertView.findViewById(R.id.itemPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShopItem currentItem = shopItems.get(position);

        holder.image.setImageResource(currentItem.getImageId());
        holder.image.setBackgroundColor(currentItem.getBgColor());

        holder.name.setText(currentItem.getItemName());

        holder.price.setText(String.format(mContext.getString(R.string.coin_price), currentItem.getPrice()));

        convertView.setTag(R.id.itemTag, currentItem);
        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
    }
}
