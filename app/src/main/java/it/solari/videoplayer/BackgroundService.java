package it.solari.videoplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class BackgroundService extends Service {
    public static String BROADCAST_ACTION = "it.solari.videoPlayer";


    String TAG = "BGSRV";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        new backGroundReceiver().execute();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }


    public class backGroundReceiver extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "Background service started");
            while (true) {
                try {
                    // data initialization
                    int i = 0;
                    while (true) {
                        // String updatesUrl = "http://172.30.0.164:5984/mobiledb/_changes?filter=dataType/type&req=flight&include_docs=true&feed=longpoll&timeout=10000&since="
                        // JSONObject request = getJSONObject("");
                        i++;
                        publishProgress(String.valueOf(i));

                        Log.d(TAG, "Background service running");
                        SystemClock.sleep(1000);
                    }

                } catch (Exception e) {
                    SystemClock.sleep(2000);
                    publishProgress();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... item) {
            // ((ArrayAdapter<String>) getListAdapter()).add(item[0]);
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("DATA", item[0]);
            sendBroadcast(intent);

            Log.d(TAG, "Background service results" + item[0]);

        }

        @Override
        protected void onPostExecute(Void unused) {

        }

    }

    private static JSONObject getJSONObject(String url) throws IOException,
            MalformedURLException, JSONException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();

        InputStream in = conn.getInputStream();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(
                    new DoneHandlerInputStream(in)));
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        } finally {
            in.close();
        }
    }


}
