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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class CharacterListActivity extends Activity {

    public static final String INTENT_EXT_MODE = "mode";
    public static final int MODE_SPDEF = 1;
    public static final int MODE_BG = 2;

    /*-----------------------------------------------------------------------*/

    private class MyItem {
        int id, srcX, srcY, width, height, homeX, homeY, attr;
        public MyItem(int id, int srcX, int srcY, int width, int height,
                int homeX, int homeY, int attr) {
            this.id = id;
            this.srcX = srcX;
            this.srcY = srcY;
            this.width = width;
            this.height = height;
            this.homeX = homeX;
            this.homeY = homeY;
            this.attr = attr;
        }
        public MyItem(int id, String cvs) {
            String[] ary = cvs.split(",");
            Integer.parseInt(ary[0]);
            this.id = id;
            this.srcX = Integer.parseInt(ary[0]);
            this.srcY = Integer.parseInt(ary[1]);
            this.width = Integer.parseInt(ary[2]);
            this.height = Integer.parseInt(ary[3]);
            this.homeX = Integer.parseInt(ary[4]);
            this.homeY = Integer.parseInt(ary[5]);
            this.attr = Integer.parseInt(ary[6]);
        }
    }

    private class MyViewHolder {
        public CharacterView cv;
        public TextView tv;
    }

    private class MyAdapter extends ArrayAdapter<MyItem> {
        public MyAdapter(Context context, ArrayList<MyItem> myItems) {
            super(context, 0, myItems);
        }
        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            MyViewHolder holder;
            if (itemView == null) {
                itemView = View.inflate(getContext(), R.layout.character_list_item, null);
                holder = new MyViewHolder();
                holder.cv = (CharacterView) itemView.findViewById(R.id.chrview_listitem);
                holder.tv = (TextView) itemView.findViewById(R.id.text_listitem_id);
                itemView.setTag(holder);
            } else {
                holder = (MyViewHolder) itemView.getTag();
            }
            MyItem myItem = (MyItem) getItem(position);
            holder.cv.setParams(myItem.srcX, myItem.srcY, myItem.width, myItem.height,
                    myItem.homeX, myItem.homeY, myItem.attr);
            holder.tv.setText(String.valueOf(myItem.id));
            itemView.setBackgroundColor((position == mSelectPos) ?
                    Color.argb(128, 255, 255, 0) : Color.TRANSPARENT);
            return itemView;
        }
    }

    /*-----------------------------------------------------------------------*/

    private int mMode;
    private int mSelectPos = 0;

    private MyAdapter mAdapter;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_list);

        ArrayList<MyItem> myItems = new ArrayList<MyItem>();
        Intent intent = getIntent();
        if (intent != null) {
            mMode = intent.getIntExtra(INTENT_EXT_MODE, 0);
            MyApplication app = (MyApplication) getApplication();
            switch (mMode) {
            case MODE_SPDEF:
                setTitle(R.string.activity_name_spdef);
                CharacterView.bindBitmap(app.getSpriteCharacterImage());
                try {
                    InputStream in = getResources().openRawResource(R.raw.spdef);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    int i = 0;
                    String str;
                    while((str = reader.readLine()) != null) {
                        myItems.add(new MyItem(i++, str));
                        if (i == 1482) i = 2048;
                        if (i == 3529) i = 4095;
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MODE_BG:
                setTitle(R.string.activity_name_bg);
                CharacterView.bindBitmap(app.getBgCharacterImage());
                for (int i = 0; i < 1024; i++) {
                    myItems.add(new MyItem(i, i % 32 * 16, i / 32 * 16, 16, 16, 0, 0, 1));
                }
                break;
            }
        }

        mAdapter = new MyAdapter(this, myItems);
        mGridView = (GridView) findViewById(R.id.grid_character_list);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                mSelectPos = pos;
                mAdapter.notifyDataSetChanged();
            }
        });
        mGridView.setSelection(mSelectPos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CharacterView.unbindBitmap();
    }

}
