package com.varnalab.app.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class WhoIsOnlineRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private String TAG = WhoIsOnlineRemoteViewsFactory.class.getSimpleName();

    private Context mContext;

    private ArrayList<Person> records;

    public WhoIsOnlineRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    // Initialize the data set.
    public void onCreate() {
        // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        records = new ArrayList<Person>();

        getAllOnline();
    }

    // Given the position (index) of a WidgetItem in the array, use the item's text value in
    // combination with the app widget item XML file to construct a RemoteViews object.
    public RemoteViews getViewAt(int position) {

        // position will always range from 0 to getCount() - 1.
        // Construct a RemoteViews item based on the app widget item XML file, and set the
        // text based on the position.
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_people);

        // feed row
        Person row = records.get(position);

        remoteView.setTextViewText(R.id.name, row.getName());

        // end feed row

        // Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in ListViewWidgetProvider.
        Bundle extras = new Bundle();

        extras.putInt(WhoIsOnlineWidgetProvider.EXTRA_ITEM, position);

        Intent fillInIntent = new Intent();
        //fillInIntent.putExtra("homescreen_meeting", data);
        fillInIntent.putExtras(extras);

        // Make it possible to distinguish the individual on-click action of a given item
        remoteView.setOnClickFillInIntent(R.id.widget_list_view, fillInIntent);

        // Return the RemoteViews object.
        return remoteView;
    }

    public int getCount() {
        Log.e("size=", records.size() + "");
        return records.size();
    }

    public void onDataSetChanged() { getAllOnline(); }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public void onDestroy() {
        records.clear();
    }

    public boolean hasStableIds() {
        return true;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    private void getAllOnline() {
        Person person;
        ContentHandler contentHandler = new ContentHandler(mContext);
        HashMap<String, Content> content = contentHandler.getAllContent();

        records.clear();

        JSONArray everyone = null;
        JSONArray online = null;
        JSONArray online_unknown = null;

        try {
            everyone = content.get("everyone").getArray();;
            online = content.get("online").getObject().getJSONArray("known");
            online_unknown = content.get("online").getObject().getJSONArray("unknown");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (everyone.length() < 1) {
            Log.e(TAG, "Couldn't get json from server.");
            return;
        }

        try {
            if (online.length() > 0) {
                // looping through everyone and add only online
                for (int i = 0; i < everyone.length(); i++) {
                    JSONObject o = everyone.getJSONObject(i);

                    // create new person
                    person = new Person(o);

                    if (!online.toString().contains(person.getId())) {
                        continue;
                    }

                    // adding person to list
                    records.add(person);
                }
            }

            // adding unknown users
            for (int i = 0; i < online_unknown.length(); i++) {
                JSONObject o = online_unknown.getJSONObject(i);

                // create new person
                person = new Person(o);

                // adding person to the list
                records.add(person);
            }
        } catch (final JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
    }

}
