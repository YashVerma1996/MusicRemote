package commandoengineer.musiccontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        final EditText inputIP = (EditText) findViewById(R.id.inputIP);
        final EditText inputPort = (EditText) findViewById(R.id.inputPort);
        final Button start = (Button) findViewById(R.id.start);
        final Button stop = (Button) findViewById(R.id.stop);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = inputIP.getText().toString();
                int portNo = Integer.parseInt(inputPort.getText().toString());
                Intent intent = new Intent(MainActivity.this,ControllerService.class);
                intent.putExtra(Constants.SERVER_IP_EXTRA,ip);
                intent.putExtra(Constants.SERVER_PORT,portNo);
                MainActivity.this.startService(intent);
                start.setText("Started");
            }
        });
    }
}
