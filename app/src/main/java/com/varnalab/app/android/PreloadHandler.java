package com.varnalab.app.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

class PreloadHandler extends AsyncTask<Void, Void, Void> {

    protected Context mContext;

    private ProgressDialog preloader;

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        preloader = new ProgressDialog(mContext);
        preloader.setMessage("Loading...");
        preloader.setCancelable(false);
        preloader.show();
    }

    @Override
    public Void doInBackground(Void... arg0) {
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