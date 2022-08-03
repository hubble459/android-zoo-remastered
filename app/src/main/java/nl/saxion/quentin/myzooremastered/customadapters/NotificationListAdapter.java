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
import nl.saxion.quentin.myzooremastered.models.Notification;

/**
 * Adapter for Notifications
 * In NotificationFragment.java
 */
public class NotificationListAdapter extends ArrayAdapter<Notification> {

    private final Context mContext;
    private final List<Notification> notifications;

    public NotificationListAdapter(Context context, @NonNull List<Notification> list) {
        super(context, 0, list);
        mContext = context;
        notifications = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_notification_list_item, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.messageIcon);
            holder.message = convertView.findViewById(R.id.message);
            holder.time = convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Notification notification = notifications.get(position);

        holder.message.setText(notification.getMessage());
        holder.icon.setImageResource(notification.getIcon());
        holder.time.setText(notification.getTime());

        convertView.setTag(R.id.notification, notification);
        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView message;
        TextView time;
    }
}
