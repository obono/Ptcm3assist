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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CharacterListActivity extends Activity {

    public static final String INTENT_EXT_MODE = "mode";
    public static final int MODE_SPDEF = 1;
    public static final int MODE_BG = 2;

    private int mMode;
    private int mSelectPos = 0;
    private Bitmap mBitmap;
    private Rect mSrcRect = new Rect();
    private Rect mDestRect = new Rect();
    private Paint mPaint = new Paint();

    private MyAdapter mAdapter;
    private GridView mGridView;
    private AbsListView.LayoutParams mGridItemLayout;
    private RelativeLayout.LayoutParams mGridItemTextLayout;

    /*-----------------------------------------------------------------------*/

    public class ChrView extends View {
        private int mId;
        public ChrView(Context context) {
            super(context);
        }
        public void setId(int pos) {
            mId = pos;
        }
        @Override
        public void draw(Canvas canvas) {
            if (mBitmap != null) {
                int sx = mId % 32, sy = mId / 32;
                mSrcRect.set(sx * 16, sy * 16, sx * 16 + 16, sy * 16 + 16);
                canvas.drawBitmap(mBitmap, mSrcRect, mDestRect, null);
                canvas.drawRect(mDestRect.left - 1, mDestRect.top - 1,
                        mDestRect.right, mDestRect.bottom, mPaint);
            }
        }
    }

    private class ViewHolder {
        public ChrView cv;
        public TextView tv;
        public ViewHolder(ChrView cv, TextView tv) {
            this.cv = cv;
            this.tv = tv;
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 1024;
        }
        @Override
        public Object getItem(int pos) {
            return null;
        }
        @Override
        public long getItemId(int pos) {
            return pos;
        }
        @Override
        public View getView(int pos, View itemView, ViewGroup parent) {
            Context context = CharacterListActivity.this;
            ChrView cv;
            TextView tv;
            if (itemView == null) {
                RelativeLayout rl = new RelativeLayout(context);
                rl.setGravity(Gravity.BOTTOM);
                cv = new ChrView(context);
                tv = new TextView(context);
                tv.setGravity(Gravity.CENTER);
                rl.addView(cv);
                rl.addView(tv, mGridItemTextLayout);
                itemView = rl;
                itemView.setTag(new ViewHolder(cv, tv));
            } else {
                ViewHolder holder = (ViewHolder) itemView.getTag();
                cv = holder.cv;
                tv = holder.tv;
            }
            cv.setId(pos);
            tv.setText(String.valueOf(pos));
            itemView.setLayoutParams(mGridItemLayout);
            itemView.setBackgroundColor((pos == mSelectPos) ?
                    Color.argb(128, 255, 255, 0) : Color.TRANSPARENT);
            return itemView;
        }
    }

    /*-----------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_list);

        Intent intent = getIntent();
        if (intent != null) {
            mMode = intent.getIntExtra(INTENT_EXT_MODE, 0);
            MyApplication app = (MyApplication) getApplication();
            switch (mMode) {
            case MODE_SPDEF:
                setTitle(R.string.activity_name_spdef);
                mBitmap = app.getSpriteCharacterImage();
                break;
            case MODE_BG:
                setTitle(R.string.activity_name_bg);
                mBitmap = app.getBgCharacterImage();
                break;
            }
        }

        int chrScale = dp2px(2);
        int itemWidth = 16 * chrScale;
        int itemHeight = 16 * chrScale;
        mDestRect.set(chrScale, chrScale, itemWidth + chrScale, itemHeight + chrScale);

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mGridItemLayout = new AbsListView.LayoutParams(
                itemWidth + chrScale * 2, itemHeight + chrScale * 10);
        mGridItemTextLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        mGridItemTextLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        mAdapter = new MyAdapter();
        mGridView = (GridView) findViewById(R.id.grid_character_list);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                mSelectPos = pos;
                mAdapter.notifyDataSetChanged();
            }
        });
        mGridView.setColumnWidth(itemWidth + chrScale * 2);
        mGridView.setSelection(mSelectPos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    /*-----------------------------------------------------------------------*/

    private int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

}
