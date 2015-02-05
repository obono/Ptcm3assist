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

    private static float sSize;
    private static Paint sPaint;
    private static Bitmap sBitmap;

    private Matrix mMatrix;
    private RectF mClipRect;
    private int mHomeX;
    private int mHomeY;

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
        if (sSize == 0) {
            sSize = (int) (32f * context.getResources().getDisplayMetrics().density);
        }
    }

    public void setParams(int srcX, int srcY, int width, int height,
            int homeX, int homeY, int attr) {
        /* Translate */
        mMatrix = new Matrix();
        mMatrix.postTranslate(-srcX, -srcY);

        /* Flip */
        float w2 = width / 2f, h2 = height / 2f;
        mMatrix.postScale(((attr & 8) != 0) ? -1 : 1, ((attr & 16) != 0) ? -1 : 1, w2, h2);

        /* Rotate */
        mMatrix.postRotate(((attr & 4) != 0) ? 180 : 0, w2, h2);
        if ((attr & 2) != 0) {
            if (width > height) {
                mMatrix.postRotate(90, h2, h2);
            } else {
                mMatrix.postRotate(90, w2, w2);
                mMatrix.postTranslate(height - width, 0);
            }
            int tmp = width; width = height; height = tmp;
        }

        /* Scale */
        float scaleX = Math.min(sSize / 16, sSize / width);
        float scaleY = Math.min(sSize / 16, sSize / height);
        mMatrix.postScale(scaleX, scaleY);
        if (width * scaleX < sSize || height * scaleY < sSize) {
            mClipRect = new RectF(0f, 0f, width * scaleX, height * scaleY);
        } else {
            mClipRect = null;
        }

        mHomeX = homeX;
        mHomeY = homeY;
    }

    @Override
    public void draw(Canvas canvas) {
        if (sBitmap != null) {
            if (mClipRect != null) {
                canvas.drawRect(0, 0, sSize, sSize, sPaint);
                canvas.clipRect(mClipRect);
            }
            canvas.drawBitmap(sBitmap, mMatrix, null);
        }
    }

}
