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

import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class CodeMatrixView extends View implements OnClickListener {

    private static byte[] sCodeMap;

    private int     mCodeOrigin = 0x0000;
    private int     mCodeOriginLabelIdx = 0;
    private char[]  mCodeAvailableArray = new char[256];
    private boolean mToSquare;
    private int     mCellSize;

    private Paint   mPaintGrid = new Paint();
    private Paint   mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CodeMatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (sCodeMap == null) {
            try {
                sCodeMap = new byte[8192];
                InputStream in = getResources().openRawResource(R.raw.codes);
                in.read(sCodeMap);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                sCodeMap = null;
            }
        }
        setOnClickListener(this);
        setDrawingCacheEnabled(true);
        mPaintGrid.setColor(Color.GRAY);
        setupCodeMatrix();
    }

    public void setTypeface(Typeface typeface) {
        if (typeface != null) {
            mPaintText.setTypeface(typeface);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getWidth();
        int height = getHeight();
        mToSquare = (width * 2 < height * 3);
        if (mToSquare) {
            mCellSize = Math.min(width / 19, height / 18);
        } else {
            mCellSize = Math.min(width / 35, height / 10);
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int rows = (mToSquare) ? 16 : 8;
        int cols = (mToSquare) ? 16 : 32;
        canvas.translate((getWidth() - mCellSize * (cols - 1)) / 2,
                getHeight() - mCellSize * (rows + 1));
        int fontSize = mCellSize * 3 / 4;
        int baseOffs = mCellSize / 2 + fontSize * 7 / 16;

        /*  Grid & Indexes  */
        mPaintText.setColor(Color.GRAY);
        mPaintText.setTextSize(fontSize / 2);
        float w = mPaintText.measureText("XX");
        int pr = cols * mCellSize;
        for (int i = 0; i <= rows; i++) {
            canvas.drawLine(0, i * mCellSize, pr, i * mCellSize, mPaintGrid);
            if (i < rows) {
                canvas.drawText(String.format("%04X", mCodeOrigin + i * cols),
                        -mCellSize - w, i * mCellSize + baseOffs, mPaintText);
            }
        }
        int pb = rows * mCellSize;
        for (int i = 0; i <= cols; i++) {
            canvas.drawLine(i * mCellSize, 0, i * mCellSize, pb, mPaintGrid);
            if (i < cols) {
                canvas.drawText(String.format("%2X", i),
                        i * mCellSize + (mCellSize - w) / 2, -mCellSize + baseOffs, mPaintText);
            }
        }

        /*  Glyphs  */
        mPaintText.setTextSize(fontSize);
        w = mPaintText.measureText("X");
        for (int i = 0; i < rows; i++) {
            mPaintText.setColor((mCodeOrigin == 0 && i == 0) ? Color.CYAN : Color.WHITE);
            for (int j = 0; j < cols; j++) {
                char c = mCodeAvailableArray[i * cols + j];
                if (c == '\0') {
                    canvas.drawLine((j + 1) * mCellSize, i * mCellSize,
                            j * mCellSize, (i + 1) * mCellSize, mPaintGrid);
                } else {
                    canvas.drawText(String.valueOf(c), j * mCellSize + (mCellSize - w) / 2,
                            i * mCellSize + baseOffs, mPaintText);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        final String[] items = getResources().getStringArray(R.array.unicode_origin_labels);
        DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mCodeOrigin = Integer.parseInt(items[which].substring(0, 2), 16) << 8;
                mCodeOriginLabelIdx = which;
                dialog.dismiss();
                setupCodeMatrix();
                invalidate();
            }
        };
        new AlertDialog.Builder(getContext())
                .setSingleChoiceItems(items, mCodeOriginLabelIdx, l)
                .show();
    }

    /*-----------------------------------------------------------------------*/

    private void setupCodeMatrix() {
        char code = (char) mCodeOrigin;
        for (int i = 0; i < mCodeAvailableArray.length; i++, code++) {
            if (sCodeMap != null && (sCodeMap[code >> 3] & (1 << code % 8)) != 0) {
                char showCode = code;
                if (code == 0x0000 || code == 0xFF00) showCode = ' ';
                if (code == 0x0009)                   showCode = 0xE209;
                if (code == 0x000A || code == 0x000D) showCode = 0x21B5;
                if (code == 0x007F)                   showCode = 0xFF3C;
                //if (code == 0x01C5 || code == 0x01C6) showCode = ' ';
                //if (code == 0x01F0)                   showCode = ' ';
                //if (code == 0x01F2 || code == 0x01F3) showCode = ' ';
                if (code == 0x021A || code == 0x021B) showCode = (char) (code - 0x021A + 0x0162);
                mCodeAvailableArray[i] = showCode;
            } else {
                mCodeAvailableArray[i] = 0;
            }
        }
    }
}
