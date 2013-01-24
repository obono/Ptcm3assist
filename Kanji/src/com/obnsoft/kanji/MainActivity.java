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

package com.obnsoft.kanji;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int FOCUS_FGCOL = Color.rgb(64, 64, 0);
    private static final int FOCUS_BGCOL = Color.rgb(232, 232, 128);
    private static final char IDEOGRAPHICS_SPACE = 0x3000;

    private int mTextLen = 0;
    private int mFocusPos = 0;

    private Editable    mEditable;
    private TextView    mLblMagnify;
    private TextView    mLblLicense;
    private TextView    mLblUnicode;
    private EditText    mTxtEnter;
    private ImageButton mBtnPrev;
    private ImageButton mBtnNext;

    /*----------------------------------------------------------------------*/

    private InputFilter mInputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                Spanned dest, int dstart, int dend) {
            boolean modified = false;
            StringBuffer sb = new StringBuffer(end - start);
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (c == ' ' || c == IDEOGRAPHICS_SPACE) {
                    modified = true;
                } else {
                    sb.append(c);
                }
            }
            if (modified) {
                String s = sb.toString();
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(s);
                    TextUtils.copySpansFrom((Spanned) source, start, end, null, sp, 0);
                    return sp;
                }
                return s;
            }
            return null; // keep original
        }
    };

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
            mEditable = s;
            mTextLen = s.length();
            if (mFocusPos > mTextLen - 1) {
                mFocusPos = mTextLen - 1;
            }
            if (mFocusPos < 0) {
                mFocusPos = 0;
            }
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);
        mLblMagnify = (TextView) findViewById(R.id.lbl_magnify);
        mLblLicense = (TextView) findViewById(R.id.lbl_license);
        mLblUnicode = (TextView) findViewById(R.id.lbl_unicode);
        mTxtEnter = (EditText) findViewById(R.id.txt_enter);
        mBtnPrev = (ImageButton) findViewById(R.id.btn_prev);
        mBtnNext = (ImageButton) findViewById(R.id.btn_next);

        Display disp = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int size = Math.min(disp.getWidth(), disp.getHeight()) / 2;
        mLblMagnify.setWidth(size);
        mLblMagnify.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        mLblMagnify.setTextColor(FOCUS_FGCOL);
        mLblMagnify.setBackgroundColor(FOCUS_BGCOL);

        mLblLicense.setText(new StringBuffer()
                .append(getString(R.string.app_code)).append(" \"")
                .append(getString(R.string.app_name)).append("\" ")
                .append(getVersion()).append("\n\n")
                .append(getString(R.string.license)).toString());
        mLblLicense.setTextColor(FOCUS_FGCOL);

        mTxtEnter.setFilters(new InputFilter[] {mInputFilter});
        mTxtEnter.addTextChangedListener(mTextWatcher);
        mTxtEnter.setOnFocusChangeListener(mOnFocusChangeListener);

        updateViews();
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

    public void onClickBtnExit(View v) {
        finish();
    }

    /*----------------------------------------------------------------------*/

    private void updateViews() {
        if (mTextLen > 0) {
            mEditable.removeSpan(mEmphasis);
            mEditable.setSpan(mEmphasis,
                    mFocusPos, mFocusPos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            String str = mEditable.subSequence(mFocusPos, mFocusPos + 1).toString();
            long code = str.charAt(0);
            mLblMagnify.setText(str);
            mLblLicense.setVisibility(View.INVISIBLE);
            mLblUnicode.setText("U+".concat(String.format("%04X", code)));
        } else {
            mLblMagnify.setText(null);
            mLblUnicode.setText(null);
        }
        mBtnPrev.setEnabled((mFocusPos > 0));
        mBtnNext.setEnabled((mFocusPos < mTextLen - 1));
    }

    private String getVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            return "Version ".concat(packageInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
