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

import java.util.ArrayList;
import java.util.Comparator;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CommandListActivity extends ListActivity {

    /*-----------------------------------------------------------------------*/

    private class MyItem {
        long    id;
        String  text;
        int     categoryId;
        public MyItem(long id, String text, int categoryId) {
            this.id = id;
            this.text = text;
            this.categoryId = categoryId;
        }
    }

    private class MyViewHolder {
        TextView tv;
    }

    private class MyAdapter extends ArrayAdapter<MyItem> {
        public MyAdapter(Context context, ArrayList<MyItem> myItems) {
            super(context, 0, myItems);
        }
        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            MyViewHolder holder;
            if (itemView == null) {
                itemView = View.inflate(getContext(), R.layout.command_list_item, null);
                holder = new MyViewHolder();
                holder.tv = (TextView) itemView.findViewById(R.id.text_listitem_index);
                itemView.setTag(holder);
            } else {
                holder = (MyViewHolder) itemView.getTag();
            }
            MyItem myItem = (MyItem) getItem(position);
            holder.tv.setText(myItem.text);
            boolean isSection = (myItem.categoryId >= 0);
            holder.tv.setTextAppearance(getContext(), isSection ?
                    android.R.style.TextAppearance_Large : android.R.style.TextAppearance_Small);
            holder.tv.setBackgroundColor(isSection ? Color.TRANSPARENT : Color.rgb(48, 80, 64));
            if (isSection) {
                holder.tv.setTextColor(Color.WHITE);
            }
            return itemView;
        }
        @Override
        public long getItemId(int position) {
            return ((MyItem) getItem(position)).id;
        }
        @Override
        public boolean isEnabled(int position) {
            return (((MyItem) getItem(position)).categoryId >= 0);
        }
    }

    /*-----------------------------------------------------------------------*/

    private enum SortType { UNKNOWN, CATEGORY, ALPHABET }
    private SortType mSortType = SortType.UNKNOWN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.command_list);
        setListAdapter(new MyAdapter(this, new ArrayList<MyItem>()));
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(CommandListActivity.this, CommandDetailActivity.class);
                intent.putExtra(CommandDetailActivity.INTENT_EXT_INDEX, (int) id);
                startActivity(intent);
            }
        });

        final MyApplication app = (MyApplication) getApplication();
        if (app.getCommandList() == null) {
            MyAsyncTaskWithDialog.ITask task = new MyAsyncTaskWithDialog.ITask() {
                @Override
                public Boolean task() {
                    return app.buildCommandList();
                }
                @Override
                public void post(Boolean result) {
                    if (result) {
                        sortListItems(SortType.CATEGORY);
                    } else {
                        Toast.makeText(CommandListActivity.this,
                                CommandListActivity.this.getText(R.string.msg_buildlist_failed),
                                Toast.LENGTH_LONG).show();
                    }
                }
            };
            MyAsyncTaskWithDialog.execute(this, R.string.msg_buildlist, task);
        } else {
            sortListItems(SortType.CATEGORY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.command_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_sort_category).setVisible(mSortType != SortType.CATEGORY);
        menu.findItem(R.id.menu_sort_alphabet).setVisible(mSortType != SortType.ALPHABET);
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_sort_category:
            sortListItems(SortType.CATEGORY);
            return true;
        case R.id.menu_sort_alphabet:
            sortListItems(SortType.ALPHABET);
            return true;
        }
        return false;
    }

    /*-----------------------------------------------------------------------*/

    private void sortListItems(SortType sortType) {
        if (mSortType == sortType) {
            return;
        }

        /* Remove section headers */
        MyAdapter adapter = (MyAdapter) getListAdapter();
        MyApplication app = (MyApplication) getApplication();
        boolean isInitial = (adapter.getCount() == 0);
        if (isInitial) {
            ArrayList<Command> commands = app.getCommandList();
            for (int i = 0, c = commands.size(); i < c; i++) {
                Command command = commands.get(i);
                adapter.add(new MyItem(i, command.getIndex(), command.getCategoryId()));
            }
        } else {
            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                MyItem item = adapter.getItem(i);
                if (item.id == -1) {
                    adapter.remove(item);
                }
            }
        }

        /* Sort and insert section headers */
        switch (sortType) {
        case CATEGORY: // By categories
            if (!isInitial) {
                adapter.sort(new Comparator<MyItem>() {
                    @Override
                    public int compare(MyItem a, MyItem b) {
                        return (int) (a.id - b.id);
                    }
                });
            }
            ArrayList<String> categories = app.getCategoryList();
            if (categories != null && categories.size() > 0) {
                int categoryId = categories.size() - 1;
                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                    if (i == 0 || adapter.getItem(i - 1).categoryId < categoryId) {
                        adapter.insert(new MyItem(-1, categories.get(categoryId), -1), i);
                        categoryId--;
                    }
                }
            }
            break;
        case ALPHABET: // By alphabet
            adapter.sort(new Comparator<MyItem>() {
                @Override
                public int compare(MyItem a, MyItem b) {
                    return a.text.compareTo(b.text);
                }
            });
            String startChar = null;
            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                if (startChar == null) {
                    startChar = adapter.getItem(i).text.substring(0, 1);
                }
                if (i == 0 || !adapter.getItem(i - 1).text.startsWith(startChar)) {
                    adapter.insert(new MyItem(-1, startChar, -1), i);
                    startChar = null;
                }
            }
            break;
        default: // Otherwise
            break;
        }
        adapter.notifyDataSetChanged();
        getListView().setSelection(0);
        mSortType = sortType;
    }

}
