package nl.saxion.quentin.myzooremastered.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Collections;
import java.util.Comparator;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.customadapters.NotificationListAdapter;
import nl.saxion.quentin.myzooremastered.lists.NotificationList;
import nl.saxion.quentin.myzooremastered.models.Notification;

/**
 * Show list with notifications
 */
public class NotificationsFragment extends Fragment {
    private NotificationListAdapter adapter;

    /**
     * @return inflated view
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    /**
     * Fill in ListView with notifications
     *
     * @param view inflated fragment view
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        ListView listView = view.findViewById(R.id.notificationList);

        sortList();
        adapter = new NotificationListAdapter(getActivity(), NotificationList.getInstance());

        final SwipeRefreshLayout srl = view.findViewById(R.id.pullToRefreshNotifications);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sortList();
                adapter.notifyDataSetChanged();
                srl.setRefreshing(false);
            }
        });

        listView.setAdapter(adapter);
    }

    /**
     * Sort list on time
     */
    private void sortList() {
        Collections.sort(NotificationList.getInstance(), new Comparator<Notification>() {
            @Override
            public int compare(Notification o1, Notification o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        ZooUtils.saveNotifications(getActivity());
    }

    /**
     * Refresh on resume
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    /**
     * Clear:
     * Clear all notifications
     * <p>
     * Graph:
     * Start GraphActivity with a custom GraphView
     * <p>
     * Settings:
     * Open SettingsActivity
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.notification_menu, menu);

        menu.findItem(R.id.clear).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NotificationList.getInstance().clear();
                adapter.notifyDataSetChanged();
                ZooUtils.saveNotifications(getActivity());
                return true;
            }
        });

        menu.findItem(R.id.graph).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), PopulationGraph.class);
                startActivity(intent);
                return true;
            }
        });

        menu.findItem(R.id.settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }
}
