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
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

public class CommandDetailActivity extends Activity {

    public static final String INTENT_EXT_INDEX = "index";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int itemIndex = 0;
        if (intent != null) {
            itemIndex = intent.getIntExtra(INTENT_EXT_INDEX, 0);
        }

        MyApplication app = (MyApplication) getApplication();
        Display disp = getWindowManager().getDefaultDisplay();
        Command command = app.getCommandList().get(itemIndex);
        View contentView = command.setupDetailViews(this, R.layout.command_detail, disp.getWidth());
        setContentView(contentView);
    }
}
