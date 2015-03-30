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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.common.HybridBinarizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QRScannerActivity extends Activity
        implements SurfaceHolder.Callback, AutoFocusCallback, PreviewCallback {

    public static final String EXPORT_FILEPATH =
        Environment.getExternalStorageDirectory().getPath() + File.separator + "ptcm3.bin";

    private static final int SCAN_INTERVAL = 1000;

    private Camera      mCamera;
    private Camera.Size mCameraSize;
    private int         mCameraId;
    private int         mCameraDeg;

    private View        mViewRoot;
    private SurfaceView mViewPreview;
    private View        mViewFrame;
    private TextView    mTextInfo;

    private float       mPreviewScale;
    private int         mFrameSize;
    private Timer       mTimer;
    private boolean     mScanning;
    private int         mDialogsCount;

    private ArrayList<byte[]>   mResultArray;
    private int                 mResultTotalSize;

    /*-----------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);
        mViewRoot = (View) findViewById(R.id.view_qr_root);
        mViewPreview = (SurfaceView) findViewById(R.id.view_qr_preview);
        mViewFrame = (View) findViewById(R.id.view_qr_frame);
        mTextInfo = (TextView) findViewById(R.id.text_qr_info);

        mViewPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    try {
                        mCamera.autoFocus(QRScannerActivity.this);
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            }
        });
        SurfaceHolder holder = mViewPreview.getHolder();
        holder.addCallback(this);
        clearBufferedResult();
    }

    @Override
    protected void onResume() {
        startScanning();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScanning();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_scanner, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_qr_clear:
            if (mResultArray.size() > 0) {
                OnClickListener listener = new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            clearBufferedResult();
                        }
                    }
                };
                showConfirmationDialog(R.string.menu_qr_clear, getString(R.string.msg_qr_clear),
                        android.R.string.ok, 0, android.R.string.cancel, listener);
            }
            return true;
        case R.id.menu_qr_export:
            if (mResultArray.size() > 0) {
                exportBufferedResults();
            }
            return true;
        }
        return false;
    }

    /*-----------------------------------------------------------------------*/

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            /*  Select camera device  */
            CameraInfo info = new CameraInfo();
            mCameraId = 0;
            for (int i = 0, c = Camera.getNumberOfCameras(); i < c; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                    mCameraId = i;
                    break;
                }
            }
            mCamera = Camera.open(mCameraId);
            if (mCamera == null) {
                Toast.makeText(this, R.string.msg_qr_camera_failed, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            mCamera.setPreviewDisplay(holder);

            /*  Select preview size  */
            Camera.Parameters cp = mCamera.getParameters();
            List<Camera.Size> sizeList = cp.getSupportedPreviewSizes();
            mCameraSize = sizeList.get(0);
            for (Camera.Size s : sizeList) {
                if (s.width * s.height > mCameraSize.width * mCameraSize.height) {
                    mCameraSize = s;
                }
            }
            cp.setPreviewSize(mCameraSize.width, mCameraSize.height);
            mCamera.setParameters(cp);
            try {
                cp.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                mCamera.setParameters(cp);
            } catch (RuntimeException e2) {
                e2.printStackTrace();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        stopScanning();
        if (setupCameraPreview()) {
            startScanning();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopScanning();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Result rawResult = null;
        int size = (int) (mFrameSize / mPreviewScale);
        int srcX = (mCameraSize.width - size) / 2;
        int srcY = (mCameraSize.height - size) / 2;
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                data, mCameraSize.width, mCameraSize.height, srcX, srcY, size, size, false);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader multiFormatReader = new MultiFormatReader();
            try {
                rawResult = multiFormatReader.decode(bitmap);
                appendBufferedResult(rawResult);
            } catch (ReaderException re) {
                // do nothing
            }
        }
        mScanning = false;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            // do nothing
        }
    }

    /*-----------------------------------------------------------------------*/

    private boolean setupCameraPreview() {
        /*  Set camera's orientation  */
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int dispDeg = 0;
        switch (rotation) {
            case Surface.ROTATION_0:   dispDeg = 0;   break;
            case Surface.ROTATION_90:  dispDeg = 90;  break;
            case Surface.ROTATION_180: dispDeg = 180; break;
            case Surface.ROTATION_270: dispDeg = 270; break;
        }
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
            mCameraDeg = (360 + info.orientation - dispDeg) % 360;
        } else {
            mCameraDeg = (720 - info.orientation - dispDeg) % 360;
        }
        mCamera.setDisplayOrientation(mCameraDeg);

        /*  Adjust preview & frame size  */
        float rootWidth = mViewRoot.getWidth();
        float rootHeight = mViewRoot.getHeight();
        float previewWidth, previewHeight;
        if (mCameraDeg % 180 == 90) {
            previewWidth = mCameraSize.height;
            previewHeight = mCameraSize.width;
        } else {
            previewWidth = mCameraSize.width;
            previewHeight = mCameraSize.height;
        }
        mPreviewScale = Math.min(rootWidth / previewWidth, rootHeight / previewHeight);
        int width = (int) (previewWidth * mPreviewScale);
        int height = (int) (previewHeight * mPreviewScale);
        if (width != mViewPreview.getWidth() || height != mViewPreview.getHeight()) {
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(width, height);
            layout.addRule(RelativeLayout.CENTER_IN_PARENT);
            mViewPreview.setLayoutParams(layout);
            mFrameSize = (int) (Math.min(width, height) * 0.875f);
            layout = new RelativeLayout.LayoutParams(mFrameSize, mFrameSize);
            layout.addRule(RelativeLayout.CENTER_IN_PARENT);
            mViewFrame.setLayoutParams(layout);
            return false; // surfaceChanged() will be called again.
        }
        return true;
    }

    private void startScanning() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallbackWithBuffer(this);
                mCamera.startPreview();
            } catch (RuntimeException e) {
                Toast.makeText(this, R.string.msg_qr_camera_failed, Toast.LENGTH_LONG).show();
                finish();
            }
            if (mTimer == null) {
                mTimer = new Timer(true);
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!mScanning && mDialogsCount == 0) {
                            mScanning = true;
                            mCamera.setOneShotPreviewCallback(QRScannerActivity.this);
                        }
                    }
                }, 0, SCAN_INTERVAL);
            }
        }
    }

    private void stopScanning() {
        if (mCamera != null) {
            mCamera.stopPreview();
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    private void clearBufferedResult() {
        if (mResultArray == null) {
            mResultArray = new ArrayList<byte[]>();
        } else {
            mResultArray.clear();
        }
        mResultTotalSize = 0;
        setBufferedResultInfo();
    }

    private void setBufferedResultInfo() {
        mTextInfo.setText(String.format(
                getString(R.string.fmt_qr_info), mResultArray.size(), mResultTotalSize));
    }

    private void appendBufferedResult(Result result) {
        Map<ResultMetadataType,Object> metadata = result.getResultMetadata();
        if (metadata.containsKey(ResultMetadataType.BYTE_SEGMENTS)) {
            @SuppressWarnings("unchecked")
            List<byte[]> byteSegments =
                    (List<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
            final byte[] data = byteSegments.get(0);
            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which != DialogInterface.BUTTON_NEGATIVE) {
                        mResultArray.add(data);
                        mResultTotalSize += data.length;
                        setBufferedResultInfo();
                        if (which == DialogInterface.BUTTON_NEUTRAL) {
                            exportBufferedResults();
                        }
                    }
                }
            };
            String msg = String.format(
                    getString(R.string.fmt_qr_scanned), mResultArray.size() + 1, data.length);
            showConfirmationDialog(
                    R.string.msg_qr_scanned,
                    msg,
                    R.string.label_qr_accept,
                    R.string.label_qr_accept_export,
                    R.string.label_qr_drop,
                    listener);
        }
    }

    private void exportBufferedResults() {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = null;
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        ByteBuffer buf = ByteBuffer.allocate(mResultTotalSize);
                        buf.clear();
                        for (byte[] ary : mResultArray) {
                            buf.put(ary);
                        }
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, new String(buf.array(), "UTF-8"));
                    } else if (which == DialogInterface.BUTTON_NEUTRAL) {
                        File outputFile = new File(EXPORT_FILEPATH);
                        if (outputFile.exists()) {
                            outputFile.delete();
                        }
                        FileOutputStream out = new FileOutputStream(outputFile);
                        for (byte[] ary : mResultArray) {
                            out.write(ary);
                        }
                        out.close();
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("application/octet-stream");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFile));
                    }
                    if (intent != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                getString(R.string.fmt_qr_export_title), Locale.US);
                        intent.putExtra(Intent.EXTRA_SUBJECT, dateFormat.format(new Date()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(QRScannerActivity.this,
                            R.string.msg_qr_export_failed, Toast.LENGTH_LONG).show();
                }
            }
        };
        showConfirmationDialog(
                R.string.menu_qr_export,
                getString(R.string.msg_qr_export),
                R.string.label_qr_export_astext,
                R.string.label_qr_export_asbinary,
                android.R.string.cancel,
                listener);
    }

    private void showConfirmationDialog(int titleId, String msg,
            int posiBtnId, int neuBtnId, int negaBtnId, OnClickListener listener) {
        AlertDialog dlg = new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(titleId)
        .setMessage(msg)
        .create();
        if (posiBtnId != 0) {
            dlg.setButton(DialogInterface.BUTTON_POSITIVE, getString(posiBtnId), listener);
        }
        if (neuBtnId != 0) {
            dlg.setButton(DialogInterface.BUTTON_NEUTRAL, getString(neuBtnId), listener);
        }
        if (negaBtnId != 0) {
            dlg.setButton(DialogInterface.BUTTON_NEGATIVE, getString(negaBtnId), listener);
        }
        dlg.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialogsCount--;
            }
        });
        mDialogsCount++;
        dlg.show();
    }

}
