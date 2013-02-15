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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    private static final String TAG = "TiltPowerOff";
    private static final String PKGNAME_SU = "com.noshufou.android.su";
    private static final String EXENAME_POWERKEY = "powerkey";
    private static final String CMD_CHMOD = "/system/bin/chmod 755 ";
    private static final String CMD_SU_SH = "/system/bin/su";
    private static final int THRESHOLD_YAXIS = 6;
    private static final int THRESHOLD_COUNT = 10;

    private int mCounter;
    private int mCounterMax;
    private int mTiltEdge;
    private boolean mEdgeFlag;

    private Process mSuProcess;
    private SensorManager mSsManager;
    private SensorEventListener mSsListener;

    /*-----------------------------------------------------------------------*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        if (!appIsInstalled(PKGNAME_SU)) {
            Log.e(TAG, getString(R.string.msg_no_su));
            Toast.makeText(this, R.string.msg_no_su, Toast.LENGTH_LONG).show();
            stopSelf();
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCounterMax = prefs.getInt(MyActivity.PREFKEY_COUNT, THRESHOLD_COUNT);
        mTiltEdge = prefs.getInt(MyActivity.PREFKEY_TILT, THRESHOLD_YAXIS);

        /*  Sensor modules  */
        mSsManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSsListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Do nothing
            }
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float val = event.values[1];
                    if (val >= 0f) {
                        mEdgeFlag = false;
                        mCounter = 0;
                    } else if (mCounter == 0) {
                        if (val <= -mTiltEdge) {
                            mCounter = 1;
                        }
                    } else if (!mEdgeFlag && ++mCounter >= mCounterMax) {
                        Log.d(TAG, "power off");
                        executeCommand(getBinaryPath(EXENAME_POWERKEY));
                        mEdgeFlag = true;
                    }
                }
            }
        };
        startSensing();

        /*  Broadcast receiver  */
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    startSensing();
                }
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    stopSensing();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        try {
            registerReceiver(receiver, filter);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        /*  Superuser process  */
        extractBinary(R.raw.powerkey, EXENAME_POWERKEY);
        try {
            mSuProcess = Runtime.getRuntime().exec(CMD_SU_SH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart()");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        stopSensing();
        if (mSuProcess != null) {
            mSuProcess.destroy();
            mSuProcess = null;
        }
        super.onDestroy();
    }

    /*-----------------------------------------------------------------------*/

    private boolean appIsInstalled(String PackageName) {
        try {
            getPackageManager().getApplicationInfo(PackageName, 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void startSensing() {
        if (mSsManager != null) {
            List<Sensor> sensors = mSsManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensors.size() > 0) {
                //Log.d(TAG, "start sensing");
                mSsManager.registerListener(
                        mSsListener, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    private void stopSensing() {
        if (mSsManager != null) {
            //Log.d(TAG, "stop sensing");
            mSsManager.unregisterListener(mSsListener);
        }
    }

    private void extractBinary(int resid, String fname) {
        String path = getBinaryPath(fname);
        if (new File(path).exists()) {
            return;
        }
        try {
            OutputStream os = new FileOutputStream(path);
            byte[] buf = new byte[1024];
            InputStream is = getResources().openRawResource(resid);
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            is.close();
            os.close();
            Process process = Runtime.getRuntime().exec(CMD_CHMOD.concat(path));
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBinaryPath(String fname) {
        return getFilesDir().getAbsolutePath().concat(File.separator).concat(fname);
    }

    private void executeCommand(String cmd) {
        if (mSuProcess != null) {
            try {
                OutputStream os = mSuProcess.getOutputStream();
                os.write(cmd.concat("\n").getBytes());
            } catch (Exception e) {
                Log.e(TAG, "Failed to execute command");
                e.printStackTrace();
                stopSelf();
            }
        }
    }

}
