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

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Command {

    /*-----------------------------------------------------------------------*/

    class StringPair {
        public String first;
        public String second;
        public StringPair(Element trElement) {
            Elements children = trElement.children();
            this.first = br2cr(children.get(1));
            this.second = (children.size() > 2) ? br2cr(children.get(2)) : "";
        }
    }

    /*-----------------------------------------------------------------------*/

    private static final String TAG_THEAD = "thead";
    private static final String TAG_TBODY = "tbody";

    private static final String CLASS_FORM = "form";
    private static final String CLASS_PARAMETER = "parameter";
    private static final String CLASS_RETURN = "return";
    private static final String CLASS_SAMPLE = "sample";
    private static final String CLASS_NOTES = "notes";

    private String mIndex;
    private StringPair mExplanation;
    private ArrayList<String> mForms = new ArrayList<String>();
    private ArrayList<StringPair> mParams = new ArrayList<StringPair>();
    private ArrayList<StringPair> mReturns = new ArrayList<StringPair>();
    private ArrayList<StringPair> mSamples = new ArrayList<StringPair>();
    private ArrayList<String> mNotes = new ArrayList<String>();
    private int mCategoryId;

    public Command(Element tableElement, int categoryId) {
        for (Element element1 : tableElement.children()) {
            String tagName = element1.tagName();
            if (tagName.equals(TAG_THEAD)) {
                Element trElement = element1.child(0);
                mIndex = trElement.child(0).text();
                mExplanation = new StringPair(trElement);
            } else if (tagName.equals(TAG_TBODY)) {
                for (Element element2 : element1.children()) {
                    String className = element2.className();
                    if (className.equals(CLASS_FORM)) {
                        Elements children = element2.children();
                        mForms.add(br2cr(element2.child((children.size() == 1) ? 0 : 1)));
                    } else if (className.equals(CLASS_PARAMETER)) {
                        mParams.add(new StringPair(element2));
                    } else if (className.equals(CLASS_RETURN)) {
                        mReturns.add(new StringPair(element2));
                    } else if (className.equals(CLASS_SAMPLE)) {
                        mSamples.add(new StringPair(element2));
                    } else if (className.equals(CLASS_NOTES)) {
                        mNotes.add(br2cr(element2.child(2)));
                    }
                }
            }
        }
        mCategoryId = categoryId;
    }

    public String getIndex() {
        return mIndex;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public View createView(Context context, View rootView, int screenWidth) {
        TextView tv;
        TableLayout tl;

        /* Index */
        tv = (TextView) rootView.findViewById(R.id.textIndex);
        tv.setText(mIndex);

        /* Explanation */
        tv = (TextView) rootView.findViewById(R.id.text_explanation_first);
        tv.setVisibility((mExplanation.first.length() > 0) ? View.VISIBLE : View.GONE);
        tv.setText(mExplanation.first);
        tv = (TextView) rootView.findViewById(R.id.text_explanation_second);
        tv.setVisibility((mExplanation.second.length() > 0) ? View.VISIBLE : View.GONE);
        tv.setText(mExplanation.second);

        /* Forms */
        tv = (TextView) rootView.findViewById(R.id.text_forms_header);
        tl = (TableLayout) rootView.findViewById(R.id.layout_forms);
        setup1ColumnTableLayout(context, tv, tl, mForms);

        /* Parameters */
        tv = (TextView) rootView.findViewById(R.id.text_params_header);
        tl = (TableLayout) rootView.findViewById(R.id.layout_params);
        setup2ColumnsTableLayout(context, tv, tl, screenWidth, true, mParams);

        /* Returns */
        tv = (TextView) rootView.findViewById(R.id.text_returns_header);
        tl = (TableLayout) rootView.findViewById(R.id.layout_returns);
        setup2ColumnsTableLayout(context, tv, tl, screenWidth, false, mReturns);

        /* Samples */
        tv = (TextView) rootView.findViewById(R.id.text_samples_header);
        tl = (TableLayout) rootView.findViewById(R.id.layout_samples);
        setup2ColumnsTableLayout(context, tv, tl, screenWidth, false, mSamples);

        /* Notes */
        tv = (TextView) rootView.findViewById(R.id.text_notes_header);
        tl = (TableLayout) rootView.findViewById(R.id.layout_notes);
        setup1ColumnTableLayout(context, tv, tl, mNotes);

        return rootView;
    }

    /*-----------------------------------------------------------------------*/

    private String br2cr(Element e) {
        e.select("br").append("\\n");
        return e.text().replaceAll("\\\\n", "\n");
    }

    private void setup1ColumnTableLayout(
            Context context, TextView tv, TableLayout tl, ArrayList<String> items) {
        tl.removeAllViews();
        if (items.size() == 0) {
            tv.setVisibility(View.GONE);
            tl.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tl.setVisibility(View.VISIBLE);
            for (String s : items) {
                TableRow row = new TableRow(context);
                row.addView(newTableTextView(context, s, 0));
                tl.addView(row);
            }
        }
    }

    private void setup2ColumnsTableLayout(Context context, TextView tv, TableLayout tl,
            int screenWidth, boolean forceTwo, ArrayList<StringPair> items) {
        tl.removeAllViews();
        if (items.size() == 0) {
            tv.setVisibility(View.GONE);
            tl.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tl.setVisibility(View.VISIBLE);
            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            TableRow.LayoutParams lp2 = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            lp2.span = 2;
            for (StringPair sp : items) {
                TableRow row = new TableRow(context);
                if (!forceTwo && (sp.first.length() == 0 || sp.second.length() == 0)) {
                    String s = (sp.first.length() > 0) ? sp.first : sp.second;
                    row.addView(newTableTextView(context, s, 0), lp2);
                } else {
                    row.addView(newTableTextView(context, sp.first, screenWidth / 4), lp1);
                    row.addView(newTableTextView(context, sp.second, 0), lp1);
                }
                tl.addView(row);
            }
        }
    }

    private TextView newTableTextView(Context context, CharSequence text, int maxWidth) {
        TextView tv = new TextView(context);
        tv.setBackgroundResource(R.drawable.frame);
        tv.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        tv.setText(text);
        if (maxWidth > 0) {
            tv.setMaxWidth(maxWidth);
        }
        return tv;
    }
}
