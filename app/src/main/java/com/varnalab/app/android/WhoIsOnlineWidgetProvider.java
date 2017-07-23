package com.varnalab.app.android;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;


public class WhoIsOnlineWidgetProvider extends AppWidgetProvider {
    Context context;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        if (appWidgetIds != null) {
            for (int i = 0; i < appWidgetIds.length; i++) {
                int widgetId = appWidgetIds[i];

                // Inflate layout.
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_who_is_online);

                Intent intent = new Intent(context, UpdateWidgetService.class);

                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                intent.setAction("FROM WIDGET PROVIDER");
                context.startService(intent);
            }

        }
    }

    public static class UpdateWidgetService extends IntentService {
        public UpdateWidgetService() {
            // only for debug purpose
            super("UpdateWidgetService");

        }

        @Override
        protected void onHandleIntent(Intent intent) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(UpdateWidgetService.this);

            int incomingAppWidgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);

            if (incomingAppWidgetId != INVALID_APPWIDGET_ID) {
                try {
                    updateNewsAppWidget(appWidgetManager, incomingAppWidgetId, intent);
                } catch (NullPointerException e) {
                    //
                }

            }

        }

        public void updateNewsAppWidget(AppWidgetManager appWidgetManager, int appWidgetId, Intent intent) {
            Log.v("String package name", this.getPackageName());
            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget_who_is_online);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Inflate layout.
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_who_is_online);

        // Update UI.
        remoteViews.setTextViewText(R.id.editText, "testing");
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

}