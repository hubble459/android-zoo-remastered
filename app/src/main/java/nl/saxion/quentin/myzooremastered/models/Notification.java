package nl.saxion.quentin.myzooremastered.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import nl.saxion.quentin.myzooremastered.R;

/**
 * Notification object with a message, icon and a time
 */
public class Notification {
    public static final int DEAD = android.R.drawable.ic_delete;
    public static final int ADD = android.R.drawable.ic_input_add;
    public static final int MAP = R.drawable.ic_compare_arrows_black_24dp;
    private static final String DATE_FORMAT_8 = "yyyy-MM-dd HH:mm:ss";
    private final String message;
    private final int icon;
    private String time;

    /**
     * Constructor
     *
     * @param message message in the notification
     * @param icon    icon next to the message
     */
    public Notification(String message, int icon) {
        this.message = message;
        this.icon = icon;

        // get the current time
        time = getCurrentDate();
    }

    public String getMessage() {
        return message;
    }

    public int getIcon() {
        return icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Get the current date
     *
     * @return current date in String format
     */
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_8, Locale.US);
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    /**
     * Notification to JSONObject
     *
     * @return notificationJSON
     * @throws JSONException exception
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject notificationJSON = new JSONObject();
        notificationJSON.put("message", message);
        notificationJSON.put("time", time);
        notificationJSON.put("icon", icon);
        return notificationJSON;
    }
}
