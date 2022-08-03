package nl.saxion.quentin.myzooremastered.lists;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.models.Notification;
import nl.saxion.quentin.myzooremastered.ui.MainActivity;

public class NotificationList extends ArrayList<Notification> {
    private static final NotificationList instance = new NotificationList();

    public static NotificationList getInstance() {
        return instance;
    }

    /**
     * For demonstration purposes dummy notifications will be added
     * Demo option in Settings [ui -> notification -> SettingsActivity.java]
     */
    public static void demoNotificationList() {
        instance.add(new Notification("Animal Added: Barbie", Notification.ADD));
        instance.add(new Notification("Animal Added: Poetsie", Notification.ADD));
        instance.add(new Notification("Animal Died: God", Notification.DEAD));
        instance.add(new Notification("Animal Added: John", Notification.ADD));
        instance.add(new Notification("Animal Added: UWU", Notification.ADD));
        instance.add(new Notification("Animal Died: Reaper", Notification.DEAD));
    }

    /**
     * Get notifications from JSON file
     */
    public static void getSave() {
        try {
            File file = new File(MainActivity.DIR + "/notificationList.json");
            Scanner sc = new Scanner(file);
            StringBuilder jsonString = new StringBuilder();

            while (sc.hasNextLine()) {
                jsonString.append(sc.nextLine());
            }

            JSONArray notificationsJSON = new JSONArray(jsonString.toString());
            for (int i = 0; i < notificationsJSON.length(); i++) {
                JSONObject notificationJSON = notificationsJSON.getJSONObject(i);
                String message = notificationJSON.getString("message");
                int icon = notificationJSON.getInt("icon");
                String time = notificationJSON.getString("time");
                Notification notification = new Notification(message, icon);
                notification.setTime(time);
                if (!exists(notification))
                    instance.add(notification);
            }
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if a message already exists
     *
     * @param notification notification
     * @return exists
     */
    private static boolean exists(Notification notification) {
        for (Notification n : instance) {
            if (n.getTime().equals(notification.getTime()) && n.getIcon() == notification.getIcon() && n.getMessage().equals(notification.getMessage()))
                return true;
        }
        return false;
    }

    /**
     * Add a notification to the list
     *
     * @param context context
     * @param message The notification message
     * @param icon    Icon for Notification
     */
    public static void addNotification(Context context, String message, int icon) {
        Notification notification = new Notification(message, icon);
        instance.add(notification);
        ZooUtils.saveNotifications(context);
    }
}
