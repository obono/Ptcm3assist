/*
 * Copyright (C) 2013 OBN-soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.obnsoft.poweroff;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.TextView;

public class MyActivity extends PreferenceActivity {

    public static final String PREFKEY_SERVICE = "service";
    public static final String PREFKEY_TILT = "tilt";
    public static final String PREFKEY_COUNT = "count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        Preference prefService = (Preference) findPreference(PREFKEY_SERVICE);
        prefService.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object newValue) {
                Intent intent = new Intent(MyActivity.this, MyService.class);
                if ((Boolean) newValue) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
                return true;
            }
        });

        SeekBarPreference prefTilt = (SeekBarPreference) findPreference(PREFKEY_TILT);
        prefTilt.setMinMax(3, 12);
        SeekBarPreference prefCount = (SeekBarPreference) findPreference(PREFKEY_COUNT);
        prefCount.setMinMax(3, 30);

        TextView textView = new TextView(this);
        textView.setText(String.format(getString(R.string.msg_license), getVersion()));
        getListView().addFooterView(textView);
    }

    private String getVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
