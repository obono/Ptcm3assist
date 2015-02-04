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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    /*-----------------------------------------------------------------------*/

    private class MyAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private Context         mContext;
        private MyApplication   mApp;
        private ProgressDialog  mDlg;
        public MyAsyncTask(Context context, MyApplication app) {
            mContext = context;
            mApp = app;
        }
        @Override
        protected void onPreExecute() {
            mDlg = new ProgressDialog(mContext);
            mDlg.setMessage(mContext.getText(R.string.msg_downloading));
            mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDlg.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            return mApp.downloadResources();
        }
        @Override
        protected void onPostExecute(Boolean result) {
            mDlg.dismiss();
            if (!result) {
                Toast.makeText(mContext, mContext.getText(R.string.msg_download_failed),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /*-----------------------------------------------------------------------*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MyApplication app = (MyApplication) getApplication();
        if (!app.isHaveResources()) {
            (new MyAsyncTask(this, app)).execute();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_about:
            showVersion();
            return true;
        }
        return false;
    }*/

    /*-----------------------------------------------------------------------*/

    public void onClickActivityCommand(View v) {
        startActivity(new Intent(this, CommandListActivity.class));
    }

    public void onClickAbout(View v) {
        showVersion();
    }

    /*-----------------------------------------------------------------------*/

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
