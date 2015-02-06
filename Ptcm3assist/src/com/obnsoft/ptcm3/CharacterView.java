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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CharacterView extends View {

    public static class Params {
        int id, srcX, srcY;
        byte width, height, homeX, homeY, attr;
        public Params(int id, int srcX, int srcY) {
            this.id = id;
            this.srcX = srcX;
            this.srcY = srcY;
            this.width = 16;
            this.height = 16;
            this.homeX = 8;
            this.homeY = 8;
            this.attr = 1;
        }
        public Params(int id, int srcX, int srcY, byte width, byte height,
                byte homeX, byte homeY, byte attr) {
            this.id = id;
            this.srcX = srcX;
            this.srcY = srcY;
            this.width = width;
            this.height = height;
            this.homeX = homeX;
            this.homeY = homeY;
            this.attr = attr;
        }
    }

    /*-----------------------------------------------------------------------*/

    private static Paint sPaint;
    private static Bitmap sBitmap;

    private boolean mToAdjust;
    private Params mParams;
    private Matrix mMatrix = new Matrix();
    private RectF mClipRect = new RectF();

    public static void bindBitmap(Bitmap bitmap) {
        sBitmap = bitmap;
        if (bitmap != null) {
            sPaint = new Paint();
            sPaint.setColor(sBitmap.getPixel(0, 0));
        }
    }

    public static void unbindBitmap() {
        if (sBitmap != null) {
            sBitmap.recycle();
        }
        sBitmap = null;
    }

    public CharacterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParams(Params params) {
        mParams = params;
        mToAdjust = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (sBitmap != null) {
            int width = getWidth();
            int height = getHeight();
            if (mToAdjust) {
                setDrawMatrix(width, height);
                mToAdjust = false;
            }
            if (mClipRect.bottom != -1) {
                canvas.drawPaint(sPaint);
                canvas.clipRect(mClipRect);
            }
            canvas.drawBitmap(sBitmap, mMatrix, sPaint);
        }
    }

    /*-----------------------------------------------------------------------*/

    private void setDrawMatrix(int width, int height) {

        /* Translate */
        mMatrix.setTranslate(-mParams.srcX, -mParams.srcY);

        /* Flip */
        float w1 = mParams.width, h1 = mParams.height;
        float w2 = w1 / 2f, h2 = h1 / 2f;
        mMatrix.postScale(((mParams.attr & 8) != 0) ? -1 : 1,
                ((mParams.attr & 16) != 0) ? -1 : 1, w2, h2);

        /* Rotate */
        mMatrix.postRotate(((mParams.attr & 4) != 0) ? 180 : 0, w2, h2);
        if ((mParams.attr & 2) != 0) {
            if (w1 > h1) {
                mMatrix.postRotate(90, h2, h2);
            } else {
                mMatrix.postRotate(90, w2, w2);
                mMatrix.postTranslate(h1 - w1, 0);
            }
            float tmp = w1; w1 = h1; h1 = tmp;
        }

        /* Scale */
        float density = getContext().getResources().getDisplayMetrics().density;
        if (getId() == R.id.chrview_focused) {
            float scale = 4f * density;
            float drawX = width / 2 - mParams.homeX * scale;
            float drawY = height / 2 - mParams.homeY * scale;
            mMatrix.postScale(scale, scale);
            mMatrix.postTranslate(drawX, drawY);
            mClipRect.set(drawX, drawY, drawX + w1 * scale, drawY + h1 * scale);
        } else {
            float scale = 2f * density;
            float drawWidth = Math.min(w1 * scale, width);
            float drawHeight = Math.min(h1 * scale, height);
            mMatrix.postScale(drawWidth / w1, drawHeight / h1);
            if (drawWidth < width || drawHeight < height) {
                mClipRect.set(0f, 0f, drawWidth, drawHeight);
            } else {
                mClipRect.set(-1, -1, -1, -1);
            }
        }
    }
}
