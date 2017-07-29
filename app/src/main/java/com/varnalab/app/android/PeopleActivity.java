package com.varnalab.app.android;

import android.content.Context;
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

public class PeopleActivity extends AppCompatActivity {

    private CacheHandler cache = new CacheHandler(this);

    private String TAG = PeopleActivity.class.getSimpleName();

    private ListView listView;

    private ArrayList<HashMap<String, String>> everyoneList;
    private ArrayList<HashMap<String, String>> backersList;
    private ArrayList<HashMap<String, String>> onlineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        setTitle(R.string.who_is_online_title);

        listView = (ListView) findViewById(R.id.list_online);

        everyoneList = new ArrayList<>();
        onlineList = new ArrayList<>();

        new GetContent(this).execute();

        FloatingActionButton btn_reload_online = (FloatingActionButton) findViewById(R.id.btn_reload_online);
        btn_reload_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlineList.clear();
                cache.remove("online_known");
                cache.remove("online_unknown");
                new GetContent(PeopleActivity.this).execute();
            }
        });
    }

    private class GetContent extends PreloadHandler {

        GetContent(Context mContext) {
            super.mContext = mContext;
        }

        @Override
        public Void doInBackground(Void... arg0) {
            Person person;
            ContentHandler contentHandler = new ContentHandler(mContext);
            HashMap<String, Content> content = contentHandler.getAllContent();

            JSONArray everyone = null;
            JSONArray backers = null;
            JSONArray online = null;
            JSONArray online_unknown = null;

            try {
                everyone = content.get("everyone").getArray();
                backers = content.get("backers").getArray();
                online = content.get("online").getObject().getJSONArray("known");
                online_unknown = content.get("online").getObject().getJSONArray("unknown");
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
                return null;
            }

            try {
                if (online.length() > 0) {
                    // looping through everyone and add only online
                    for (int i = 0; i < everyone.length(); i++) {
                        JSONObject o = everyone.getJSONObject(i);

                        // create new person
                        person = new Person(o);

                        everyoneList.add(person.getHashMap());

                        if (!online.toString().contains(person.getId())) {
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

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            // Updating parsed JSON data into ListView
            ListAdapter adapter = new SimpleAdapter(
                mContext,
                onlineList,
                R.layout.list_people,
                //new String[]{"name", "gravatar"},
                new String[]{"name"},
                //new int[]{R.id.name, R.id.gravatar}
                new int[]{R.id.name}
            );

            listView.setAdapter(adapter);

            super.onPostExecute(result);
        }

    }
}
