package dk.itu.pervasive.mobile.socket;

import android.os.AsyncTask;

/**
 * Created by centos on 4/7/14.
 */
public class RestartCycleTask extends AsyncTask<Void,Void,Void>{

    RequestDelegate _delegate;

    public RestartCycleTask( RequestDelegate d ){
        _delegate = d;
    }
    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        _delegate.onRequestFailure();
    }
}
