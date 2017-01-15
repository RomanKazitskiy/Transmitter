//package com.ololo.myapplication2;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}
package com.ololo.myapplication2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView text0;
    private TextView text1;

    private EditText editIPAddress;
    private EditText editSoket;
    private EditText editIntensity;

    private Button bStart;
    private Button bStop;

    private WifiManager wManager;

    private static final int REQUEST_ENABLE_WIFI = 1;

    private Handler handlerDelayStart;
    private static final long DELAY_START = 1000;

    private MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handlerDelayStart = new Handler();

        Intent intent = new Intent(this,MyService.class);
        bindService(intent,sConnection,BIND_AUTO_CREATE);

        text0 = (TextView)findViewById(R.id.text0);
        text1 = (TextView)findViewById(R.id.text1);

        editIPAddress = (EditText)findViewById(R.id.edit_IP_address);
        editIPAddress.setText(MyService.DEVICE_IP_ADDRESS);
        editSoket = (EditText)findViewById(R.id.edit_Socket);
        editSoket.setText(MyService.SOCKET);
        editIntensity = (EditText)findViewById(R.id.edit_Intensity);
        editIntensity.setText(MyService.Intensity);
        editIPAddress.setKeyListener(key_IP);
        editSoket.setKeyListener(key);
        editIntensity.setKeyListener(key);

        bStart = (Button)findViewById(R.id.button1);
        bStop = (Button)findViewById(R.id.button2);
        bStart.setOnClickListener(onClickList);
        bStop.setOnClickListener(onClickList);

        wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(getStatistics, makeService());

        if (MyService.flag) {
            bStart.setEnabled(false);
        }
        else {
            bStop.setEnabled(false);
        }

        if (!wManager.isWifiEnabled()) {
            Intent enableWiFiIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            startActivityForResult(enableWiFiIntent,REQUEST_ENABLE_WIFI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getStatistics);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sConnection);
        myService = null;
    }

    private final ServiceConnection sConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            myService = ((MyService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myService = null;
        }

    };

    private final BroadcastReceiver getStatistics = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MyService.ACTION_DATA_AVAILABLE.equals(action)) {
                text0.setText(intent.getStringExtra(MyService.EXTRA_DATA));
            } else if(MyService.ACTION_TIMER_AVAILABLE.equals(action)) {
                text1.setText(intent.getStringExtra(MyService.EXTRA_TIMER));
            }
        }

    };

    private static IntentFilter makeService() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(MyService.ACTION_TIMER_AVAILABLE);
        return intentFilter;
    }

    private OnClickListener onClickList = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1:
                    MyService.DEVICE_IP_ADDRESS = editIPAddress.getText().toString();
                    MyService.SOCKET = editSoket.getText().toString();
                    MyService.periodSend = Integer.valueOf(editIntensity.getText().toString());
                    myService.startMyServce();
                    bStart.setEnabled(false);
                    bStop.setEnabled(true);
                    break;
                case R.id.button2:
                    MyService.flag = false;
                    myService.udp.stopThread();
                    //	myService.sfUdpTread.isCancelled();
                    myService.timerSecond.stopThread();
                    //	myService.sfTimerSeconds.isCancelled();
                    bStop.setEnabled(false);
                    handlerDelayStart.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bStart.setEnabled(true);
                        }
                    }, DELAY_START);

                    break;
            }
        }
    };

    private NumberKeyListener key_IP = new NumberKeyListener() {
        @Override
        public int getInputType() {
            return InputType.TYPE_MASK_VARIATION;
        }

        @Override
        protected char[] getAcceptedChars() {
            return new char[]
                    {'.', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        }
    };
    private NumberKeyListener key = new NumberKeyListener() {
        @Override
        public int getInputType() {
            return InputType.TYPE_MASK_VARIATION;
        }

        @Override
        protected char[] getAcceptedChars() {
            return new char[]
                    {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        }
    };
}