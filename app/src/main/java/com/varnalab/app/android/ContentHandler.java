package com.varnalab.app.android;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ContentHandler {

    private static final String TAG = ContentHandler.class.getSimpleName();

    private Context mContext;
    private CacheHandler cache;

    private HashMap<String, Content> content = new HashMap<String, Content>() {{
        // URL to get users JSON
        put("everyone", new Content("https://simo.varnalab.org/api/whois/known", 7 * 24 * 60 * 60));
        // URL to get online (unknown) users JSON
        put("backers",new Content("https://simo.varnalab.org/api/finance/stats/backers", 7 * 24 * 60 * 60));
        // URL to get online users JSON
        put("online",new Content("https://simo.varnalab.org/api/whois/online", 3 * 60 * 60));
    }};

    public ContentHandler() {

    }

    public ContentHandler(Context mContext) {
        this.mContext = mContext;
        this.cache = new CacheHandler(mContext);
    }

    public HashMap<String, Content> getAllContent() {
        Iterator<Map.Entry<String, Content>> iterator = content.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Content> entry = iterator.next();
            Content entryValue = entry.getValue();

            if (cache == null) {
                entryValue.content = this.makeServiceCall(entryValue.url);
            } else {
                entryValue.content = cache.get(entry.getKey());
                if (entryValue.content == null) {
                    entryValue.content = this.makeServiceCall(entryValue.url);
                    if (entryValue.expire != null) {
                        cache.set(entry.getKey(), entryValue.content, entryValue.expire);
                    } else {
                        cache.set(entry.getKey(), entryValue.content);
                    }
                }
            }

            entry.setValue(entryValue);
        }

        return content;
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}