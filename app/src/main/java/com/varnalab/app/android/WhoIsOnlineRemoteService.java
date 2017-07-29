package com.varnalab.app.android;

import android.widget.RemoteViewsService;
import android.content.Intent;

public class WhoIsOnlineRemoteService extends RemoteViewsService {
    public WhoIsOnlineRemoteService() {
        //
    }

    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WhoIsOnlineRemoteViewsFactory(this.getApplicationContext(), intent);
    }
/*
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
*/
}
