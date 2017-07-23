package com.varnalab.app.android;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

class Person {

    private String id = "";
    private String name = "";
    private String avatar = "@drawable/ic_logo";

    public Person() {
        //
    }

    public Person(String name) {
        this.setName(name);
    }

    public Person(JSONObject o) throws JSONException {
        if (o.has("id")) {
            this.setId(o.getString("id"));
        }

        if (o.has("name")) {
            this.setName(o.getString("name"));
        }

        if (o.has("host")) {
            this.setName(o.getString("host"));
        }

        if (o.has("gravatar")) {
            this.setAvatar(o.getString("gravatar"));
        }
    }

    public HashMap<String, String> getHashMap() {
        HashMap<String, String> o = new HashMap<>();

        o.put("id", this.id);
        o.put("name", this.name);
        o.put("gravatar", this.avatar);

        return o;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
