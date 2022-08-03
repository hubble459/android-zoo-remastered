package nl.saxion.quentin.myzooremastered;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Notification;

import static android.content.Context.MODE_PRIVATE;
import static nl.saxion.quentin.myzooremastered.ui.MainActivity.DIR;

/**
 * Utilities for the MyZoo application
 * (saving utils)
 */
public class ZooUtils {
    private static final String TAG = "ZooUtils";

    public static void saveAll(Context context) {
        saveRegionList(context);
        saveNotifications(context);
        saveCounters(context);
    }

    /**
     * Save all notifications from NotificationList in a json file named notificationList.json
     *
     * @param context needed for getting file dir and fileOutputStream
     */
    public static void saveNotifications(Context context) {
        try {
            JSONArray notificationJSON = new JSONArray();
            for (Notification n : NotificationList.getInstance()) {
                notificationJSON.put(n.toJSON());
            }

            String name = "notificationList.json";
            File path = new File(context.getFilesDir(), name);
            boolean deleted = true;
            if (path.exists()) {
                deleted = context.deleteFile(name);
            }
            if (deleted) {
                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(name, MODE_PRIVATE));
                out.write(notificationJSON.toString());
                out.close();
                Log.i(TAG, "Notification List Saved to: " + path.getAbsolutePath());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Notification List Failed to Save");
        }
    }

    /**
     * Save all regions with animals from RegionList in a json file named regionList.json
     *
     * @param context needed for getting file dir and fileOutputStream
     */
    public static void saveRegionList(Context context) {
        try {
            JSONArray regionsJSON = new JSONArray();
            for (String key : RegionList.getInstance().keySet()) {
                JSONObject jsonRegion = new JSONObject();
                jsonRegion.put("region", key);
                JSONArray animalList = new JSONArray();
                for (Animal animal : RegionList.getAnimalList(key)) {
                    animalList.put(animal.toJSON());
                }
                jsonRegion.put("animals", animalList);
                regionsJSON.put(jsonRegion);
            }

            String name = "regionList.json";
            File path = new File(context.getFilesDir(), name);
            boolean deleted = true;
            if (path.exists()) {
                deleted = context.deleteFile(name);
            }
            if (deleted) {
                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(name, MODE_PRIVATE));
                out.write(regionsJSON.toString());
                out.close();
                Log.i(TAG, "Region List Saved to: " + path.getAbsolutePath());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Region List Failed to Save");
        }
    }

    /**
     * Save money and food counters in json
     *
     * @param context context
     */
    public static void saveCounters(Context context) {
        try {
            JSONObject counters = new JSONObject();
            counters.put("coins", CoinsAndFood.getCoins());
            counters.put("food", CoinsAndFood.getFood());

            String name = "counters.json";
            File path = new File(context.getFilesDir(), name);
            boolean deleted = true;
            if (path.exists()) {
                deleted = context.deleteFile(name);
            }
            if (deleted) {
                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(name, MODE_PRIVATE));
                out.write(counters.toString());
                out.close();
                Log.i(TAG, "Counters Saved to: " + path.getAbsolutePath());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Counters Failed to Save");
        }
    }

    public static void deleteAll(Context context) {
        String[] files = {"counters.json", "notificationList.json", "regionList.json"};
        for (String fileName : files) {
            File file = new File(context.getFilesDir(), fileName);
            if (file.exists()) {
                if (!file.delete()) {
                    Log.e(TAG, "deleteAll: Failed to delete " + fileName);
                    Log.e(TAG, "deleteAll: Trying again");
                    if (!context.deleteFile(fileName)) {
                        Log.e(TAG, "deleteAll: Failed again");
                    }
                }
            }
        }

        CoinsAndFood.getInstance().put("coins", 100);
        CoinsAndFood.getInstance().put("food", 50);
        NotificationList.getInstance().clear();
        RegionList.clearLists();
    }
}
