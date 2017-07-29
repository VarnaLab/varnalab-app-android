package com.varnalab.app.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Content {

    public String url;

    public Integer expire;

    public String content;

    Content() {
        //
    }

    Content(String url) {
        this.url = url;
    }

    Content(String url, Integer expire) {
        this.url = url;
        this.expire = expire;
    }

    JSONArray getArray() throws JSONException {
        if (content.isEmpty()) {
            content = "";
        }
        return new JSONArray(content);
    }

    JSONObject getObject() throws JSONException {
        if (content.isEmpty()) {
            content = "";
        }
        return new JSONObject(content);
    }

}
