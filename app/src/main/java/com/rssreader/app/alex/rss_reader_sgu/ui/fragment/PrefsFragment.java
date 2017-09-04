package com.rssreader.app.alex.rss_reader_sgu.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rssreader.app.alex.rss_reader_sgu.R;
import com.rssreader.app.alex.rss_reader_sgu.location.Location;

public class PrefsFragment extends Fragment {

    private Switch wifiOnlySwitch;
    private Switch notificationSwitch;
    private Switch locationSwitch;
    private TextView frequencyTV;
    private LinearLayout linearLayout;
    private Location location;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getFragmentManager().getBackStackEntryCount() == 1) {
            getFragmentManager().popBackStack();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.prefs_fragment, container, false);

        wifiOnlySwitch = (Switch) v.findViewById(R.id.use_mobile_data);
        notificationSwitch = (Switch) v.findViewById(R.id.notifications_sw);
        locationSwitch = (Switch) v.findViewById(R.id.location_sw);
        linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout);
        frequencyTV = (TextView) v.findViewById(R.id.frequencyTV);

        init();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    private void init() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        int frequency = (int) prefs.getLong("frequency", 900_000);

        switch (frequency) {
            case 900_000:
                frequencyTV.setText("Every 15 minutes");
                break;
            case 1800_000:
                frequencyTV.setText("Every 30 minutes");
                break;
            case 3600_000:
                frequencyTV.setText("Every hour");
                break;
            case -1:
                frequencyTV.setText("Manually");
                break;
            default:
                break;
        }

        wifiOnlySwitch.setChecked(prefs.getBoolean("useMobileData", false));
        wifiOnlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onWiFiSwitched(isChecked);
            }
        });

        notificationSwitch.setChecked(prefs.getBoolean("notifications", true));
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onNotificationsSwitched(isChecked);
            }
        });

        locationSwitch.setChecked(false);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onLocationSwitched(isChecked);
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isResumed()) {
                    Listener l = (Listener) getActivity();
                    l.onUpdateFrequencyClicked();
                }
            }
        });
    }

    public interface Listener {
        void onUpdateFrequencyClicked();
    }

    private void onLocationSwitched(boolean isChecked) {
        location = new Location(getActivity());
        if (isChecked) {
            if (!location.isProviderEnabled()) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle("Настройки геолокации");
                ad.setMessage("Для использования данной функции, необходимо включить геолокацию, " +
                        "перейти в меню настроек геолокации?");
                ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                    }
                });
                ad.setCancelable(true);
                ad.show();
            }
            if (location.isProviderEnabled()) {
                location.requestLocationUpdates();
            }
        } else {
            location.removeUpdates();
        }
    }


    private void onWiFiSwitched(boolean checked) {
        getActivity().getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putBoolean("useMobileData", checked)
                .apply();
    }

    private void onNotificationsSwitched(boolean checked) {
        getActivity().getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putBoolean("notifications", checked)
                .apply();
    }
}
