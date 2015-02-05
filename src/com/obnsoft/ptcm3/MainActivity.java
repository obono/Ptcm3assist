/*
 * Copyright (C) 2015 OBN-soft
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

package com.obnsoft.ptcm3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final MyApplication app = (MyApplication) getApplication();
        if (!app.isHaveResources()) {
            startDownloadTask();
        }
    }

    /*-----------------------------------------------------------------------*/

    public void onClickShowCommandList(View v) {
        startActivity(new Intent(this, CommandListActivity.class));
    }

    public void onClickShowSpdefList(View v) {
        Intent intent = new Intent(this, CharacterListActivity.class);
        intent.putExtra(CharacterListActivity.INTENT_EXT_MODE, CharacterListActivity.MODE_SPDEF);
        startActivity(intent);
    }

    public void onClickShowBgList(View v) {
        Intent intent = new Intent(this, CharacterListActivity.class);
        intent.putExtra(CharacterListActivity.INTENT_EXT_MODE, CharacterListActivity.MODE_BG);
        startActivity(intent);
    }

    public void onClickGoToWebSite(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyApplication.URL_BASE)));
    }

    public void onClickReloadResource(View v) {
        startDownloadTask();
    }

    public void onClickAbout(View v) {
        showVersion();
    }

    /*-----------------------------------------------------------------------*/

    private void startDownloadTask() {
        MyAsyncTaskWithDialog.ITask task = new MyAsyncTaskWithDialog.ITask() {
            @Override
            public Boolean task() {
                MyApplication app = (MyApplication) getApplication();
                return app.downloadResources();
            }
            @Override
            public void post(Boolean result) {
                if (!result) {
                    Toast.makeText(MainActivity.this,
                            MainActivity.this.getText(R.string.msg_download_failed),
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        MyAsyncTaskWithDialog.execute(this, R.string.msg_downloading, task);
    }

    private void showVersion() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View aboutView = inflater.inflate(R.layout.about, new ScrollView(this));
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            TextView textView = (TextView) aboutView.findViewById(R.id.text_about_version);
            textView.setText("Version " + packageInfo.versionName);

            StringBuilder buf = new StringBuilder();
            InputStream in = getResources().openRawResource(R.raw.license);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String str;
            while((str = reader.readLine()) != null) {
                buf.append(str).append('\n');
            }
            textView = (TextView) aboutView.findViewById(R.id.text_about_message);
            textView.setText(buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.menu_about)
                .setView(aboutView)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}
