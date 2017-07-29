package com.varnalab.app.android;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;


public class WhoIsOnlineWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String EXTRA_ITEM = "com.varnalab.app.android.EXTRA_ITEM";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(UPDATE_MEETING_ACTION)) {
            Log.i("received", intent.getAction());
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context,WhoIsOnlineWidgetProvider.class)
            );
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
       }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            // Set up the intent that starts the ListViewService, which will provide the views for this collection.
            Intent intent = new Intent(context, WhoIsOnlineRemoteService.class);

            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_who_is_online);

            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            remoteView.setRemoteAdapter(appWidgetIds[i], R.id.widget_list_view, intent);

            // Trigger listview item click
            Intent startActivityIntent = new Intent(context,PeopleActivity.class);

            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    startActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            remoteView.setPendingIntentTemplate(R.id.widget_list_view, startActivityPendingIntent);

            // The empty view is displayed when the collection has no items.
            // It should be in the same layout used to instantiate the RemoteViews  object above.
            remoteView.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

            // Do additional processing specific to this app widget...
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteView);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}