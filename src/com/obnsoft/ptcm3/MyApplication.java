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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

public class MyApplication extends Application {

    public static final String URL_BASE = "http://smileboom.com/special/ptcm3/";

    private static final String URL_CMD_HTML = URL_BASE + "reference/index.php";
    private static final String URL_SPU_PNG = URL_BASE + "image/ss-story-attachment-1.png";
    private static final String URL_BG_PNG = URL_BASE + "image/ss-story-attachment-2.png";
    private static final String URL_FONT_ZIP = URL_BASE + "media/font/smilebasicfont_20150617.zip";
    private static final String TARGET_TTF = "smilebasicfont/SMILEBASIC.ttf";
    private static final String FNAME_CMD_HTML = "cmd.html";
    private static final String FNAME_SPU_PNG = "spu.png";
    private static final String FNAME_BG_PNG = "bg.png";
    private static final String FNAME_FONT_TTF = "font.ttf";

    private static final String ID_CONTENTAREA = "contentarea";
    private static final String TAG_TABLE = "table";
    private static final String TAG_H3 = "h3";

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
                getFileStreamPath(FNAME_BG_PNG).exists() &&
                getFileStreamPath(FNAME_FONT_TTF).exists());
    }

    public boolean downloadResources(boolean force) {
        mCommands = null;
        mCategories = null;
        return (downloadFile(URL_CMD_HTML, FNAME_CMD_HTML, force) &&
                downloadFile(URL_SPU_PNG, FNAME_SPU_PNG, force) &&
                downloadFile(URL_BG_PNG, FNAME_BG_PNG, force) &&
                downloadZipFile(URL_FONT_ZIP, TARGET_TTF, FNAME_FONT_TTF, force));
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

    public Bitmap getSpriteCharacterImage() {
        return loadImageFile(FNAME_SPU_PNG);
    }

    public Bitmap getBgCharacterImage() {
        return loadImageFile(FNAME_BG_PNG);
    }

    public Typeface getSBFontTypeFace() {
        Typeface ret = null;
        try {
            ret = Typeface.createFromFile(getFileStreamPath(FNAME_FONT_TTF));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /*-----------------------------------------------------------------------*/

    private boolean downloadFile(String url, String fileName, boolean force) {
        if (!force && getFileStreamPath(fileName).exists()) {
            return true;
        }
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

    private boolean downloadZipFile(String url, String target, String fileName, boolean force) {
        if (!force && getFileStreamPath(fileName).exists()) {
            return true;
        }
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            ZipInputStream zin = new ZipInputStream(httpResponse.getEntity().getContent());
            for (ZipEntry entry = zin.getNextEntry(); entry != null; entry = zin.getNextEntry()) {
                if (target.equals(entry.getName())) {
                    OutputStream out = openFileOutput(fileName, MODE_PRIVATE);
                    byte[] buffer = new byte[1024 * 1024];
                    int length;
                    while ((length = zin.read(buffer)) >= 0) {
                        out.write(buffer, 0, length);  
                    }
                    out.close(); 
                    break;
                }
            }
            zin.close();
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
                    }
                } else if (e.tagName().equals(TAG_H3)) {
                    mCategories.add(e.text());
                    categoryId++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            mCommands = null;
            mCategories = null;
        }
    }

    private Bitmap loadImageFile(String fileName) {
        Bitmap bitmap = null;
        try {
            InputStream in = openFileInput(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

}
