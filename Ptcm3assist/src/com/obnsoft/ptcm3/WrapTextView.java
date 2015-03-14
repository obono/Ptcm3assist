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
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class WrapTextView extends TextView {

    /*-----------------------------------------------------------------------*/

    private class WrapTextViewFilter implements InputFilter {
        private final TextView view;

        public WrapTextViewFilter(TextView view) {
            this.view = view;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                Spanned dest, int dstart, int dend) {
            TextPaint paint = view.getPaint();
            int w = view.getWidth();
            int wpl = view.getCompoundPaddingLeft();
            int wpr = view.getCompoundPaddingRight();
            int width = w - wpl - wpr;

            SpannableStringBuilder result = new SpannableStringBuilder();
            for (int index = start; index < end; index++) {

                if (Layout.getDesiredWidth(source, start, index + 1, paint) > width) {
                    result.append(source.subSequence(start, index));
                    result.append("\n");
                    start = index;

                } else if (source.charAt(index) == '\n') {
                    result.append(source.subSequence(start, index));
                    start = index;
                }
            }

            if (start < end) {
                result.append(source.subSequence(start, end));
            }
            return result;
        }
    }

    /*-----------------------------------------------------------------------*/

    private CharSequence mOrgText = "";
    private BufferType mOrgBufferType = BufferType.NORMAL;

    public WrapTextView(Context context) {
        super(context);
        setFilters(new InputFilter[] { new WrapTextViewFilter(this) });
    }

    public WrapTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFilters(new InputFilter[] { new WrapTextViewFilter(this) });
    }

    public WrapTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFilters(new InputFilter[] { new WrapTextViewFilter(this) });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        setText(mOrgText, mOrgBufferType);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mOrgText = text;
        mOrgBufferType = type;
        super.setText(text, type);
    }

    @Override
    public CharSequence getText() {
        return mOrgText;
    }

    @Override
    public int length() {
        return mOrgText.length();
    }
}