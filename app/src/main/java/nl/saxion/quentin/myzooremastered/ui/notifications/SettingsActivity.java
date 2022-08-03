package nl.saxion.quentin.myzooremastered.ui.notifications;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.lists.RegionList;

/**
 * Settings Screen
 * <p>
 * Contains information about app
 * Has a demo button for demonstration purposes
 * Has a clear save button for if clearing all saves
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /**
     * Button Demo Mode
     *
     * @param view button
     */
    public void demoMode(View view) {
        // Confirmation Dialog
        new AlertDialog.Builder(this)
                .setMessage(R.string.demo_mode_confirmation)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CoinsAndFood.getInstance().put("coins", 999999);
                        CoinsAndFood.getInstance().put("food", 999999);
                        RegionList.demoRegionList();
                        RegionList.demoAnimalList();
                        NotificationList.demoNotificationList();
                        ZooUtils.saveAll(SettingsActivity.this);

                        Toast.makeText(SettingsActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    /**
     * Clear all saves
     *
     * @param view Clear Button
     */
    public void clearSave(View view) {
        // Confirmation Dialog
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_clear)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ZooUtils.deleteAll(SettingsActivity.this);

                        Toast.makeText(SettingsActivity.this, "Save Cleared", Toast.LENGTH_SHORT).show();

                        ZooUtils.saveAll(SettingsActivity.this);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}
