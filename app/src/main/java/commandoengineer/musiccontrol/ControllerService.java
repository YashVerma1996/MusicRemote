package commandoengineer.musiccontrol;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by yash on 16/7/16.
 */
public class ControllerService extends IntentService {
    public static final String TAG = "ControllerService";

    Socket s;
    DataInputStream dIn;
    AudioManager audioManager;

    public ControllerService() {
        super(TAG);

        Log.e(TAG,"Constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ip = "10.42.0.1" ;
        if(intent.hasExtra(Constants.SERVER_IP_EXTRA))
            ip = intent.getStringExtra(Constants.SERVER_IP_EXTRA);
        int PORT_NO = intent.getIntExtra(Constants.SERVER_PORT, 6000);

        Log.e(TAG,"Service started with ip:"+ip+":"+PORT_NO);

        int message = 0;

        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            s = new Socket(inetAddress, PORT_NO);
            dIn = new DataInputStream(s.getInputStream());
            audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

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
    }

    public void changeVolume(int direction) {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0);
    }

    public void performMediaAction(int code) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, code));
        this.sendOrderedBroadcast(intent, null);

        intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, code));
        this.sendOrderedBroadcast(intent, null);
    }

}

