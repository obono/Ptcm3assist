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
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Application;

public class MyApplication extends Application {

    public static final String URL_BASE = "http://smileboom.com/special/ptcm3/";

    private static final String URL_CMD_HTML = URL_BASE + "reference/index.php";
    private static final String URL_SPU_PNG = URL_BASE + "image/ss-story-attachment-1.png";
    private static final String URL_BG_PNG = URL_BASE + "image/ss-story-attachment-2.png";
    private static final String FNAME_CMD_HTML = "cmd.html";
    private static final String FNAME_SPU_PNG = "spu.png";
    private static final String FNAME_BG_PNG = "bg.png";

    private static final String ID_CONTENTAREA = "contentarea";
    private static final String TAG_TABLE = "table";
    private static final String CLASS_HEAD = "head";

    private ArrayList<Command> mCommands;
    private ArrayList<String> mCategories;

    /*-----------------------------------------------------------------------*/

    public MyApplication() {
        super();
        // do something;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // do something
    }

    @Override
    public void onTerminate() {
        // do something;
        super.onTerminate();
    }

    /*-----------------------------------------------------------------------*/

    public boolean isHaveResources() {
        return (getFileStreamPath(FNAME_CMD_HTML).exists() &&
                getFileStreamPath(FNAME_SPU_PNG).exists() &&
                getFileStreamPath(FNAME_BG_PNG).exists());
    }

    public boolean downloadResources() {
        mCommands = null;
        mCategories = null;
        return (downloadFile(URL_CMD_HTML, FNAME_CMD_HTML) &&
                downloadFile(URL_SPU_PNG, FNAME_SPU_PNG) &&
                downloadFile(URL_BG_PNG, FNAME_BG_PNG));
    }

    public ArrayList<Command> getCommandList() {
        return mCommands;
    }

    public ArrayList<String> getCategoryList() {
        return mCategories;
    }

    public boolean buildCommandList() {
        parseCommandHtml();
        return (mCommands != null && mCategories != null);
    }

    /*-----------------------------------------------------------------------*/

    private boolean downloadFile(String url, String fileName) {
        InputStream in = null;
        OutputStream out = null;
        try {
            out = openFileOutput(fileName, MODE_PRIVATE);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            in = httpResponse.getEntity().getContent();
            byte[] buffer = new byte[1024 * 1024];
            int length;
            while ((length = in.read(buffer)) >= 0) {
                out.write(buffer, 0, length);  
            }
            out.close(); 
            in.close();
        } catch (Exception e){
            e.printStackTrace();
            getFileStreamPath(fileName).delete();
            return false;
        }
        return true;
    }

    private void parseCommandHtml() {
        mCommands = new ArrayList<Command>();
        mCategories = new ArrayList<String>();
        int categoryId = -1;
        try {
            InputStream in = openFileInput(FNAME_CMD_HTML);
            Document document = Jsoup.parse(in, "UTF-8", URL_CMD_HTML);
            in.close();
            Element divContentArea = document.getElementById(ID_CONTENTAREA);
            for (Element e : divContentArea.children()) {
                if (e.tagName().equals(TAG_TABLE)) {
                    if (e.className().equals("")) {
                        mCommands.add(new Command(e, categoryId));
                    } else if (e.className().equals(CLASS_HEAD)) {
                        mCategories.add(e.child(0).text());
                        categoryId++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            mCommands = null;
            mCategories = null;
        }
    }
}
