package com.varnalab.app.android;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WhoIsOnlineActivity extends AppCompatActivity {

    private CacheHandler cache = new CacheHandler(this);

    private String TAG = WhoIsOnlineActivity.class.getSimpleName();

    private ListView listView;

    private ArrayList<HashMap<String, String>> everyoneList;
    private ArrayList<HashMap<String, String>> onlineList;

    Content[] urls = new Content[]{
        // URL to get users JSON
        new Content("everyone_url", "https://box.outofindex.com/varnalab/whois/known"),
        // URL to get online users JSON
        new Content("online_known", "https://box.outofindex.com/varnalab/whois/online/known", 3 * 60 * 60),
        // URL to get online (unknown) users JSON
        new Content("online_unknown", "https://box.outofindex.com/varnalab/whois/online/unknown", 3 * 60 * 60)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_is_online);
        setTitle(R.string.who_is_online_title);

        listView = (ListView) findViewById(R.id.list_online);

        everyoneList = new ArrayList<>();
        onlineList = new ArrayList<>();

        new GetContent().execute();

        FloatingActionButton btn_reload_online = (FloatingActionButton) findViewById(R.id.btn_reload_online);
        btn_reload_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlineList.clear();
                cache.remove("online_known");
                cache.remove("online_unknown");
                new GetContent().execute();
            }
        });
    }

    private class GetContent extends ContentHandler {

        GetContent() {
            super.context = WhoIsOnlineActivity.this;
            super.content = WhoIsOnlineActivity.this.urls;
            super.cache = WhoIsOnlineActivity.this.cache;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            Person person;

            JSONArray everyone = null;
            JSONArray online_known = null;
            JSONArray online_unknown = null;

            try {
                everyone = content[0].getArray();
                online_known = content[1].getArray();
                online_unknown = content[2].getArray();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (everyone.length() < 1) {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server.",
                            Toast.LENGTH_LONG)
                            .show();
                    }
                });
                return;
            }

            try {
                if (online_known.length() > 0) {
                    // looping through everyone and add only online
                    for (int i = 0; i < everyone.length(); i++) {
                        JSONObject o = everyone.getJSONObject(i);

                        // create new person
                        person = new Person(o);

                        everyoneList.add(person.getHashMap());

                        if (!online_known.toString().contains(person.getId())) {
                            continue;
                        }

                        // adding contact to contact list
                        onlineList.add(person.getHashMap());
                    }
                }

                // adding unknown users
                for (int i = 0; i < online_unknown.length(); i++) {
                    JSONObject o = online_unknown.getJSONObject(i);

                    // create new person
                    person = new Person(o);

                    // adding person to the list
                    onlineList.add(person.getHashMap());
                }
            } catch (final JSONException e) {
                Log.e(TAG, "JSON error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "JSON error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            // Updating parsed JSON data into ListView
            ListAdapter adapter = new SimpleAdapter(
                context,
                onlineList,
                R.layout.list_people,
                //new String[]{"name", "gravatar"},
                new String[]{"name"},
                //new int[]{R.id.name, R.id.gravatar}
                new int[]{R.id.name}
            );

            listView.setAdapter(adapter);
        }

    }
}
