package com.varnalab.app.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

class ContentHandler extends AsyncTask<Void, Void, Void> {

    public Context context;
    public Content[] content;
    public CacheHandler cache;

    private ProgressDialog preloader;

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        preloader = new ProgressDialog(context);
        preloader.setMessage("Loading...");
        preloader.setCancelable(false);
        preloader.show();
    }

    @Override
    public Void doInBackground(Void... arg0) {
        HttpHandler httpHandler = new HttpHandler();

        for (int i = 0; i < content.length; i++ ) {
            content[i].content = cache.get(content[i].key);
            if (content[i].content == null) {
                content[i].content = httpHandler.makeServiceCall(content[i].url);
                if (content[i].expire != null) {
                    cache.set(content[i].key, content[i].content, content[i].expire);
                } else {
                    cache.set(content[i].key, content[i].content);
                }
            }
        }

        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        super.onPostExecute(result);

        // Dismiss the progress dialog
        if (preloader.isShowing()) {
            preloader.dismiss();
        }
    }

}