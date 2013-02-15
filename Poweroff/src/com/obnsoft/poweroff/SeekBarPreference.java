/*
 * Copyright (C) 2013 OBN-soft
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

package com.obnsoft.poweroff;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {

    private int mValue;
    private int mMinimum = 0;
    private int mMaximum = 100;

    private SeekBar mSeekBar;
    private TextView mTextView;

    /*-----------------------------------------------------------------------*/

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.seekbarpref);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, 50);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mValue = getPersistedInt(mValue);
        } else {
            mValue = (Integer) defaultValue;
            persistInt(mValue);
        }
    }

    @Override
    protected void onBindView(View view) {
        mTextView = (TextView) view.findViewById(R.id.seekbarpref_text);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbarpref_seekbar);
        if (mSeekBar != null) {
            mSeekBar.setMax(mMaximum - mMinimum);
            mSeekBar.setProgress(mValue - mMinimum);
            mSeekBar.setOnSeekBarChangeListener(this);
            onProgressChanged(mSeekBar, mValue - mMinimum, false);
        }
        super.onBindView(view);
    }

    /*-----------------------------------------------------------------------*/

    @Override
    public void onStartTrackingTouch(SeekBar seekbar) {
        // Do nothing
    }

    @Override
    public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
        if (mTextView != null) {
            mTextView.setText(String.valueOf(progress + mMinimum));
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekbar) {
        int value = seekbar.getProgress() + mMinimum;
        if (callChangeListener(value)) {
            mValue = value;
            persistInt(mValue);
        }
    }

    /*-----------------------------------------------------------------------*/

    public void setMinMax(int min, int max) {
        if (min < max) {
            mMinimum = min;
            mMaximum = max;
            if (mSeekBar != null) {
                mSeekBar.setMax(mMaximum - mMinimum);
            }
        }
    }
}
