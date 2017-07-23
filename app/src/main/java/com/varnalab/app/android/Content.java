package com.varnalab.app.android;

import org.json.JSONArray;
import org.json.JSONException;

class Content {

    public String key;

    public String url;

    public Integer expire;

    public String content;

    Content() {
        //
    }

    Content(String key, String url) {
        this.key = key;
        this.url = url;
    }

    Content(String key, String url, Integer expire) {
        this.key = key;
        this.url = url;
        this.expire = expire;
    }

    JSONArray getArray() throws JSONException {
        if (content.isEmpty()) {
            content = "";
        }
        return new JSONArray(content);
    }

}
