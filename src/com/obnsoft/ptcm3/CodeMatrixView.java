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

    private static final int[] CODE_ORIGIN_ARRAY = {
        0x0000, 0x0100, 0x0200, 0x0300, 0x0400,         // Latin
        0x2000, 0x2100, 0x2200, 0x2300, 0x2500, 0x2600, // Symbol
        0x3000, 0x4E00, 0xE100, 0xE200, 0xFF00,         // Others
    };
    private static final int[] CODE_RANGE_ARRAY = {
        0x0000, 0x0000, 0x0009, 0x000A, 0x000D, 0x000D, 0x0020, 0x007F,
        0x00A0, 0x017F, 0x0192, 0x0192, 0x01C5, 0x01C6, 0x01F0, 0x01F0,
        0x01F2, 0x01F3, 0x021A, 0x021B, 0x02C7, 0x02C7, 0x02D8, 0x02DC,
        0x037E, 0x037E, 0x0384, 0x038A, 0x038C, 0x038C, 0x038E, 0x03A1,
        0x03A3, 0x03CE, 0x0401, 0x0401, 0x0410, 0x044F, 0x0451, 0x0451,
        0x2013, 0x2014, 0x2018, 0x201E, 0x2020, 0x2021, 0x2026, 0x2026,
        0x2030, 0x2030, 0x2032, 0x2033, 0x2039, 0x203B, 0x20A9, 0x20A9,
        0x20AC, 0x20AC, 0x2116, 0x2116, 0x2122, 0x2122, 0x2190, 0x2193,
        0x21D2, 0x21D2, 0x21D4, 0x21D4, 0x2200, 0x2200, 0x2202, 0x2202,
        0x221A, 0x221A, 0x221E, 0x221E, 0x2234, 0x2235, 0x2282, 0x2283,
        0x2312, 0x2312, 0x2500, 0x2503, 0x250C, 0x250C, 0x250F, 0x2510,
        0x2513, 0x2514, 0x2517, 0x2518, 0x251B, 0x251D, 0x2520, 0x2520,
        0x2523, 0x2525, 0x252B, 0x252C, 0x252F, 0x2530, 0x2533, 0x2534,
        0x2537, 0x2538, 0x253B, 0x253C, 0x253F, 0x253F, 0x2542, 0x2542,
        0x254B, 0x254B, 0x2574, 0x257B, 0x25A0, 0x25A1, 0x25B2, 0x25B3,
        0x25BC, 0x25BD, 0x25C6, 0x25C7, 0x25CB, 0x25CB, 0x25CE, 0x25CF,
        0x2605, 0x2606, 0x2640, 0x2640, 0x2642, 0x2642, 0x2660, 0x2667,
        0x266A, 0x266A, 0x266D, 0x266D, 0x3000, 0x3003, 0x3005, 0x3006,
        0x3008, 0x3012, 0x301C, 0x301D, 0x301F, 0x301F, 0x3041, 0x308F,
        0x3092, 0x3093, 0x309B, 0x309E, 0x30A1, 0x30EF, 0x30F2, 0x30F6,
        0x30FB, 0x30FC, 0x4EDD, 0x4EDD, 0xE100, 0xE17F, 0xE200, 0xE25A,
        0xE260, 0xE2C9, 0xE2E0, 0xE2FF, 0xFF00, 0xFF5E,
    };

    private int     mCodeOrigin = 0x0000;
    private int     mCodeOriginLabelIdx = 0;
    private char[]  mCodeAvailableArray = new char[256];
    private boolean mToSquare;
    private int     mCellSize;

    private Paint   mPaintGrid = new Paint();
    private Paint   mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CodeMatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        String[] items = getResources().getStringArray(R.array.unicode_origin_labels);
        DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mCodeOrigin = CODE_ORIGIN_ARRAY[which];
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
        for (int i = 0; i < mCodeAvailableArray.length; i++) {
            mCodeAvailableArray[i] = 0;
        }
        for (int i = 0; i < CODE_RANGE_ARRAY.length; i += 2) {
            int start = CODE_RANGE_ARRAY[i], end = CODE_RANGE_ARRAY[i + 1];
            if (end >= mCodeOrigin || start <= mCodeOrigin + 255) {
                for (int j = Math.max(0, start - mCodeOrigin);
                        j <= Math.min(255, end - mCodeOrigin); j++) {
                    char c = (char) (mCodeOrigin + j);
                    if (c == 0x0000 || c == 0xFF00) c = ' ';
                    if (c == 0x0009)                c = 0xE209;
                    if (c == 0x000A || c == 0x000D) c = 0x21B5;
                    if (c == 0x007F)                c = 0xFF3C;
                    //if (c == 0x01C5 || c == 0x01C6) c = ' ';
                    //if (c == 0x01F0)                c = ' ';
                    //if (c == 0x01F2 || c == 0x01F3) c = ' ';
                    if (c == 0x021A || c == 0x021B) c = (char) (c - 0x021A + 0x0162);
                    mCodeAvailableArray[j] = c;
                }
            }
        }
    }
}
