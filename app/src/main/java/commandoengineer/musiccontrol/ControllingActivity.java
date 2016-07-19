package commandoengineer.musiccontrol;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ControllingActivity extends AppCompatActivity {
    public static final String  TAG = "ControllingActivity";

    Socket s;
    DataOutputStream dOut ;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);

        Button vol_up = (Button) findViewById(R.id.vol_hi);
        Button vol_down = (Button) findViewById(R.id.vol_lo);
        Button prev = (Button) findViewById(R.id.prev);
        Button play = (Button) findViewById(R.id.play);
        Button next = (Button) findViewById(R.id.next);

        vol_up.setOnClickListener(new ClickListener());
        vol_down.setOnClickListener(new ClickListener());
        prev.setOnClickListener(new ClickListener());
        play.setOnClickListener(new ClickListener());
        next.setOnClickListener(new ClickListener());

        new GetIPTask().execute();
        new ServerTask().execute();
    }

    public class GetIPTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            Log.e(TAG,"Doing");
            String result = "";
            try {
                result = InetAddress.getLocalHost().toString();
                Log.e(TAG,"Getting result="+result);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            TextView ipDisplay = (TextView) findViewById(R.id.ipDisplay);
            ipDisplay.setText(s);
        }
    }

    public class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int message = 0;
            switch(v.getId()){
                case R.id.vol_hi :  message = 9;
                    break;
                case R.id.vol_lo : message = 8;
                    break;
                case R.id.prev : message = 0;
                    break;
                case R.id.play : message = 1;
                    break;
                case R.id.next : message = 2;
                    break;
            }

            Log.e(TAG,"Button Clicked= "+message);
            Log.e(TAG,"Writing Msg = "+message);
            try {
                dOut.writeInt(message);
                dOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class CustomRunnable implements Runnable{
        int msg ;
        public CustomRunnable(int msg){
            this.msg = msg;
        }
        @Override
        public void run() {
            try {
                Log.e(TAG,"Writing Msg = "+msg);
                dOut.writeInt(msg);
                dOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public class ServerTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            int PORT_NO = 6000;

            try {
                ServerSocket ss = new ServerSocket(PORT_NO);
                s = ss.accept();
                dOut = new DataOutputStream(s.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
