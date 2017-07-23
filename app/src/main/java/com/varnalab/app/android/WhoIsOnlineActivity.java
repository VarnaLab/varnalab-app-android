package com.varnalab.app.android;

import android.app.ProgressDialog;
import android.os.AsyncTask;
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

    // URL to get users JSON
    private static String users_url = "https://box.outofindex.com/varnalab/members";

    // URL to get online members JSON
    private static String online_url = "https://box.outofindex.com/varnalab/online";

    ArrayList<HashMap<String, String>> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_is_online);
        setTitle(R.string.who_is_online_title);

        userList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_online);

        new GetOnlineList().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetOnlineList extends AsyncTask<Void, Void, Void> {

        private ProgressDialog preloader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            preloader = new ProgressDialog(WhoIsOnlineActivity.this);
            preloader.setMessage("Loading...");
            preloader.setCancelable(false);
            preloader.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();

            String userStr = cache.get("users_data");
            if (userStr == null) {
                userStr = httpHandler.makeServiceCall(users_url);
                cache.set("users_data", userStr);
            }

            cache.getAll();

            Log.i(TAG, "Response from userStr: " + userStr);

            String onlineStr = cache.get("online_data");
            if (onlineStr == null) {
                onlineStr = httpHandler.makeServiceCall(online_url);
                cache.set("online_data", onlineStr, (3 * 60 * 60)); // 3 minutes
            }
            Log.i(TAG, "Response from onlineStr: " + onlineStr);

            if (userStr != null && onlineStr != null) {
                try {
                    Person person;

                    // Getting JSON Array node for users
                    JSONArray users = new JSONArray(userStr);

                    // Getting JSON Object with Arrays for online
                    JSONObject online = new JSONObject(onlineStr);
                    JSONArray online_known = online.getJSONArray("known");
                    JSONArray online_unknown = online.getJSONArray("unknown");

                    // looping through All Users and match for online
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject o = users.getJSONObject(i);

                        // create new person
                        person = new Person(o);

                        if (!online_known.toString().contains(person.getId())) {
                           continue;
                        }

                        // adding contact to contact list
                        userList.add(person.getHashMap());
                    }

                    // adding unknown users
                    for (int i = 0; i < online_unknown.length(); i++) {
                        JSONObject o = online_unknown.getJSONObject(i);

                        // create new person
                        person = new Person(o);

                        // adding person to the list
                        userList.add(person.getHashMap());
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (preloader.isShowing()) {
                preloader.dismiss();
            }

            // Updating parsed JSON data into ListView
            ListAdapter adapter = new SimpleAdapter(
                WhoIsOnlineActivity.this,
                userList,
                R.layout.list_user,
                //new String[]{"name", "gravatar"},
                new String[]{"name"},
                //new int[]{R.id.name, R.id.gravatar}
                new int[]{R.id.name}
            );

            listView.setAdapter(adapter);
        }

    }
}
