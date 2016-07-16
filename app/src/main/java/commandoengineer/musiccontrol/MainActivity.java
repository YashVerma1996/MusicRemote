package commandoengineer.musiccontrol;

import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new MessageReceiver().execute();
    }

    public class MessageReceiver extends AsyncTask<Void, Void, Void> {

        Socket s;
        DataInputStream dIn;
        AudioManager audioManager;

        @Override
        protected Void doInBackground(Void... params) {

            String ip = "10.42.0.1";
            int PORT_NO = 6000;
            int message = 0;
            audioManager = (AudioManager) MainActivity.this.getSystemService(AUDIO_SERVICE);
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                s = new Socket(inetAddress, PORT_NO);
                dIn = new DataInputStream(s.getInputStream());

                while (message != 999) {
                    message = dIn.readInt();
                    Log.e(TAG, "Received Message " + message);
                    switch (message) {
                        case 0:
                            performMediaAction(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                            break;
                        case 1:
                            performMediaAction(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                            break;
                        case 2:
                            performMediaAction(KeyEvent.KEYCODE_MEDIA_NEXT);
                            break;
                        case 8:
                            changeVolume(AudioManager.ADJUST_LOWER);
                            break;
                        case 9:
                            changeVolume(AudioManager.ADJUST_RAISE);
                            break;
                    }
                }
                s.close();

            } catch (Exception e) {
                Log.e(TAG, "Exception " + e);
            }
            return null;
        }

        public void changeVolume(int direction) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0);
        }

        public void performMediaAction(int code) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, code));
            MainActivity.this.sendOrderedBroadcast(intent, null);

            intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, code));
            MainActivity.this.sendOrderedBroadcast(intent, null);
        }
    }
}
