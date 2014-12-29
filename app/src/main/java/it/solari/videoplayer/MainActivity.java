package it.solari.videoplayer;


import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.HorizontalScrollView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {

    MediaPlayer mediaPlayer;
    SurfaceHolder surfaceHolder;
    SurfaceView playerSurfaceView;
    private View mMainView;
    String videoSrc = "/mnt/media_rw/udisk/bbb_sunflower_1080p_60fps_normal.mp4";
    // String videoSrc = "http://172.16.0.21:8080/";
    private WebView webView;

    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Log.d("BRD", "EVENT RECEIVED:" + extras.getString("DATA"));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start background service
        Intent i = new Intent(this, BackgroundService.class);
        // i.putExtra("KEY1", "Value to be used by the service");
        startService(i);
/*	  
      Intent intent = new Intent();
	  intent.setAction("it.solari.android.testevent");
	  intent.putExtra("DATA","TEST");
 
	  sendBroadcast(intent); 

*/

        registerReceiver(broadcastReceiver, new IntentFilter(BackgroundService.BROADCAST_ACTION));


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mMainView = findViewById(R.id.MainActivity);

        mMainView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        // exit from standby

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        Settings.System.putInt(
                this.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION,
                0 //0 means off, 1 means on
        );
        Settings.System.putInt(
                getContentResolver(),
                Settings.System.USER_ROTATION,
                Surface.ROTATION_0
        );


        // disable keyguard, deprecated

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

        // webview

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;

            }
        });

        // webView.loadUrl("http://www.dinamicarts.com/poste/");
        webView.loadUrl("http://127.0.0.1/sito/index.html");

        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(MainActivity.this);
        }

        //initialize the VideoView
        myVideoView = (VideoView) findViewById(R.id.videoView1);

        // create a progress bar while the video file is loading
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            //set the media controller in the VideoView
            myVideoView.setMediaController(mediaControls);

            //set the uri of the video to be played
            myVideoView.setVideoPath(videoSrc);


        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }


        myVideoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView.setOnPreparedListener(new OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                // close the progress bar and play the video
                progressDialog.dismiss();
                //if we have a position on savedInstanceState, the video playback should start from here
                myVideoView.seekTo(position);
                if (position == 0) {
                    myVideoView.start();
                } else {
                    //if we come from a resumed activity, video playback will be paused
                    myVideoView.pause();
                }
            }
        });

        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                myVideoView.start();
            }
        });


        final HorizontalScrollView myView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        // myView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        //myView.setSmoothScrollingEnabled(true);
        // myView.smoothScrollBy(-1000,0);
        // scrolling

        postDelayed(new Runnable() {
            public void run() {
                myView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                Log.d("AAA", "aaaaa");
            }
        }, 5000L);

        //final TextView textView1 = (TextView) findViewById(R.id.textView1);
        //textView1.setSelected(true);


    }

    private void postDelayed(Runnable runnable, long l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }

}
