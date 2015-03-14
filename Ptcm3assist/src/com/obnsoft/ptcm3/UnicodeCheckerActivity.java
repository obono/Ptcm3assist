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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UnicodeCheckerActivity extends Activity {

    private static final int FOCUS_FGCOL = Color.rgb(192, 192, 255);
    private static final int FOCUS_BGCOL = Color.rgb(64, 64, 255);
    private static final int MODE_10COMMA = 0;
    private static final int MODE_16COMMA = 1;
    private static final int MODE_16FIX = 2;
    private static final String[] ENCODE_FORMAT_ARY = new String[] { "%d", "&H%X", "%04X" };

    private int mEncodeMode = MODE_16COMMA;
    private int mTextLen = 0;
    private int mFocusPos = 0;

    private EditText    mEditTextSource;
    private TextView    mTextDetail;
    private TextView    mTextEncoded;
    private Button      mBtnPrev;
    private Button      mBtnNext;

    private Editable    mEditableSource;
    private Editable    mEditableEncoded = new SpannableStringBuilder();
    private ArrayList<Integer>  mEncodedDelimitAry = new ArrayList<Integer>();
    private SimpleDateFormat    mDateFormat =
            new SimpleDateFormat("'文字コード情報('yyMMdd'-'HHmmss')'", Locale.US);

    /*----------------------------------------------------------------------*/

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (mFocusPos >= start + count) {
                mFocusPos += after - count;
            } else if (after < count && mFocusPos >= start + after) {
                mFocusPos = start + after - 1;
            }
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Do nothing
        }
        @Override
        public void afterTextChanged(Editable s) {
            mEditableSource = s;
            mTextLen = s.length();
            if (mFocusPos > mTextLen - 1) {
                mFocusPos = mTextLen - 1;
            }
            if (mFocusPos < 0) {
                mFocusPos = 0;
            }
            setupEncodedTextView();
            updateViews();
        }
    };

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
    };

    private CharacterStyle mEmphasis = new CharacterStyle() {
        @Override
        public void updateDrawState(TextPaint tp) {
            int c = tp.bgColor;
            int a1 = Color.alpha(c);
            int a2 = Color.alpha(FOCUS_BGCOL);
            int a = a1 + a2;
            int r = (Color.red(c)  *a1 + Color.red(FOCUS_BGCOL)  *a2) / a;
            int g = (Color.green(c)*a1 + Color.green(FOCUS_BGCOL)*a2) / a;
            int b = (Color.blue(c) *a1 + Color.blue(FOCUS_BGCOL) *a2) / a;
            tp.bgColor = Color.rgb(r, g, b);
            tp.setColor(FOCUS_FGCOL);
        }
    };

    /*----------------------------------------------------------------------*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unicode_checker);

        mTextDetail = (TextView) findViewById(R.id.text_unicode_detail);
        mTextEncoded = (TextView) findViewById(R.id.text_unicode_encoded);
        mEditTextSource = (EditText) findViewById(R.id.edittext_unicode_source);
        mBtnPrev = (Button) findViewById(R.id.btn_unicode_prev);
        mBtnNext = (Button) findViewById(R.id.btn_unicode_next);
        mEditTextSource.addTextChangedListener(mTextWatcher);
        mEditTextSource.setOnFocusChangeListener(mOnFocusChangeListener);
        updateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.unicode_checker, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_unicode_10comma:
            checkAndRefreshEncodedMode(MODE_10COMMA);
            return true;
        case R.id.menu_unicode_16comma:
            checkAndRefreshEncodedMode(MODE_16COMMA);
            return true;
        case R.id.menu_unicode_16fix:
            checkAndRefreshEncodedMode(MODE_16FIX);
            return true;
        case R.id.menu_unicode_share:
            if (mTextLen > 0) {
                shareEncodedResult();
            }
            return true;
        }
        return false;
    }

    /*----------------------------------------------------------------------*/

    public void onClickBtnPos(View v) {
        if (v == mBtnPrev) {
            mFocusPos--;
        }
        if (v == mBtnNext) {
            mFocusPos++;
        }
        updateViews();
    }

    /*----------------------------------------------------------------------*/

    private void checkAndRefreshEncodedMode(int mode) {
        if (mEncodeMode != mode) {
            mEncodeMode = mode;
            setupEncodedTextView();
            updateViews();
        }
    }

    private void setupEncodedTextView() {
        mEditableEncoded.clear();
        mEncodedDelimitAry.clear();
        for (int i = 0; i < mTextLen; i++) {
            char code = mEditableSource.charAt(i);
            mEditableEncoded.append(String.format(ENCODE_FORMAT_ARY[mEncodeMode], (long) code));
            mEncodedDelimitAry.add(mEditableEncoded.length());
            if (mEncodeMode != MODE_16FIX && i < mTextLen - 1) {
                mEditableEncoded.append(',');
            }
        }
    }

    private void updateViews() {
        if (mTextLen > 0) {
            mEditableSource.removeSpan(mEmphasis);
            mEditableSource.setSpan(mEmphasis,
                    mFocusPos, mFocusPos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            char code = mEditableSource.charAt(mFocusPos);
            mTextDetail.setText(String.format(
                    "\"%c\" : %d (&H%X)", code, (long) code, (long) code));
            mEditableEncoded.removeSpan(mEmphasis);
            int startPos = (mFocusPos == 0) ? 0 : mEncodedDelimitAry.get(mFocusPos - 1);
            int endPos = mEncodedDelimitAry.get(mFocusPos);
            if (mFocusPos > 0 && mEncodeMode != MODE_16FIX) startPos++;
            mEditableEncoded.setSpan(mEmphasis,
                    startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTextEncoded.setText(mEditableEncoded);
        } else {
            mTextDetail.setText(null);
            mTextEncoded.setText(null);
        }
        mBtnPrev.setEnabled((mFocusPos > 0));
        mBtnNext.setEnabled((mFocusPos < mTextLen - 1));
    }

    private void shareEncodedResult() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, mDateFormat.format(new Date()));
        intent.putExtra(Intent.EXTRA_TEXT, new StringBuffer()
            .append("元の文字列:\n")
            .append(mEditableSource.toString())
            .append("\n\n文字コード:\n")
            .append(mEditableEncoded.toString())
            .append('\n')
            .toString());
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.msg_unicode_share_failed, Toast.LENGTH_LONG).show();
        }
    }

}
