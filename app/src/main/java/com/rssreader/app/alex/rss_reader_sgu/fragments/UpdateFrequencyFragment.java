package com.rssreader.app.alex.rss_reader_sgu.fragments;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.rssreader.app.alex.rss_reader_sgu.R;
import com.rssreader.app.alex.rss_reader_sgu.service.RefreshService;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Alex on 29.07.2017.
 */

public class UpdateFrequencyFragment extends Fragment {
    private RadioButton radioBtn2;
    private RadioButton radioBtn3;
    private RadioButton radioBtn4;
    private RadioButton radioBtn5;
    private Button saveBtn;

    private long frequency;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_frequency_fragment, container, false);

        radioBtn2 = (RadioButton) view.findViewById(R.id.radioButton2);
        radioBtn3 = (RadioButton) view.findViewById(R.id.radioButton3);
        radioBtn4 = (RadioButton) view.findViewById(R.id.radioButton4);
        radioBtn5 = (RadioButton) view.findViewById(R.id.radioButton5);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);

        init();

        return view;
    }


    private void init() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        int switched = (int) sharedPreferences.getLong("frequency", 900_000);
        switch (switched) {
            case 900_000:
                radioBtn2.setChecked(true);
                break;
            case 1800_000:
                radioBtn3.setChecked(true);
                break;
            case 3600_000:
                radioBtn4.setChecked(true);
                break;
            case -1:
                radioBtn5.setChecked(true);
            default:
                break;
        }

        frequency = 900_000;

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning(RefreshService.class)) {
                    getActivity().stopService(new Intent(getActivity(), RefreshService.class));
                }
                getActivity().getPreferences(Context.MODE_PRIVATE)
                        .edit()
                        .putLong("frequency", frequency)
                        .apply();
                getFragmentManager().popBackStack();
                getActivity().startService(new Intent(getActivity(), RefreshService.class));
            }
        });



        radioBtn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setFrequency(900_000);
                    Log.d("RadioBtnTag", "onCheckedChanged: " + "15 minutes");
                }
            }
        });

        radioBtn3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setFrequency(1800_000);
                    Log.d("RadioBtnTag", "onCheckedChanged: " + "30 minutes");
                }
            }
        });

        radioBtn4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setFrequency(3600_000);
                    Log.d("RadioBtnTag", "onCheckedChanged: " + "1 hour");
                }
            }
        });

        radioBtn5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setFrequency(-1);
                    Log.d("RadioBtnTag", "onCheckedChanged: " + "off");
                }
            }
        });
    }

    private void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
