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
    public static final int MODE_UNKNOWN = 0;
    public static final int MODE_SPDEF = 1;
    public static final int MODE_BG = 2;

    /*-----------------------------------------------------------------------*/

    private class MyViewHolder {
        CharacterView cv;
        TextView tv;
    }

    private class MyAdapter extends ArrayAdapter<CharacterView.Params> {
        public MyAdapter(Context context, ArrayList<CharacterView.Params> aryParams) {
            super(context, 0, aryParams);
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
            CharacterView.Params params = (CharacterView.Params) getItem(position);
            holder.cv.setParams(params);
            holder.tv.setText(String.valueOf(params.id));
            holder.tv.setBackgroundColor((position == mSelectPos) ?
                    Color.argb(128, 64, 64, 255) : Color.TRANSPARENT);
            return itemView;
        }
    }

    /*-----------------------------------------------------------------------*/

    private static final String STATEKEY_POSITION = "position";

    private int mSelectPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_list);

        Intent intent = getIntent();
        final int mode;
        if (intent != null) {
            mode = intent.getIntExtra(INTENT_EXT_MODE, MODE_UNKNOWN);
            MyApplication app = (MyApplication) getApplication();
            switch (mode) {
            case MODE_SPDEF:
                setTitle(R.string.activity_name_spdef);
                CharacterView.bindBitmap(app.getSpriteCharacterImage());
                break;
            case MODE_BG:
                setTitle(R.string.activity_name_bg);
                CharacterView.bindBitmap(app.getBgCharacterImage());
                findViewById(R.id.shape_cross).setVisibility(View.INVISIBLE);
                break;
            }
        } else {
            mode = MODE_UNKNOWN;
        }

        final MyAdapter adapter = new MyAdapter(this, generateAdapterItems(mode));
        final CharacterView characterView = (CharacterView) findViewById(R.id.chrview_focused);
        final TextView textView = (TextView) findViewById(R.id.text_chrinfo);
        final GridView gridView = (GridView) findViewById(R.id.grid_character_list);
        OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                mSelectPos = pos;
                CharacterView.Params params = adapter.getItem(mSelectPos);
                textView.setText(generateCharacterInfo(mode, params));
                characterView.setParams(params);
                characterView.invalidate();
                adapter.notifyDataSetChanged();
            }
        };
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(listener);

        if (savedInstanceState != null) {
            mSelectPos = savedInstanceState.getInt(STATEKEY_POSITION);
        }
        listener.onItemClick(null, null, mSelectPos, 0);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATEKEY_POSITION, mSelectPos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CharacterView.unbindBitmap();
    }

    /*-----------------------------------------------------------------------*/

    private ArrayList<CharacterView.Params> generateAdapterItems(int mode) {
        ArrayList<CharacterView.Params> aryParams = new ArrayList<CharacterView.Params>();
        switch (mode) {
        case MODE_SPDEF:
            try {
                InputStream in = getResources().openRawResource(R.raw.spdef);
                int i = 0;
                byte[] buf = new byte[8];
                while(in.read(buf) != -1) {
                    aryParams.add(new CharacterView.Params(i++, buf[0] * 8, buf[1] * 8,
                            buf[2], buf[3], buf[4], buf[5], buf[6]));
                    if (i == 1482) i = 2048;
                    if (i == 3529) i = 4095;
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        case MODE_BG:
            for (int i = 0; i < 1024; i++) {
                aryParams.add(new CharacterView.Params(i, i % 32 * 16, i / 32 * 16));
            }
            break;
        }
        return aryParams;
    }

    private String generateCharacterInfo(int mode, CharacterView.Params params) {
        switch (mode) {
        case MODE_SPDEF:
            return String.format(getString(R.string.fmt_chrinfo_spdef),
                    params.id, params.srcX, params.srcY, params.width, params.height,
                    params.homeX, params.homeY, params.attr);
        case MODE_BG:
            return String.format(getString(R.string.fmt_chrinfo_bg),
                    params.id, params.srcX, params.srcY);
        }
        return null;
    }
}
