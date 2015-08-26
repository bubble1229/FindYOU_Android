package com.example.haofly.myapplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static final int msgKey1 = 1;
    private static final int msgKey2 = 2;
    private static TextView tv;
    private static Format formatter;
    LocationManager locManger;

    /**
     * Instances of static inner classes do not hold an implicit
     * reference to their outer class.
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case msgKey1:
                        Date curDate = new Date(System.currentTimeMillis());
                        String now = formatter.format(curDate);
                        tv.setText(now);
                        break;

                    case msgKey2:
                        String str2 = msg.getData().getString("result");
                        tv.setText(str2);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("aaa", "app start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 下面表示60秒，1公里的变化
        locManger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, (float)0.0000001, locationListener);


        tv = (TextView)findViewById(R.id.textView);
        formatter = new SimpleDateFormat("HH:mm");

        //new TimeThread().start();
        //new SendLocationThread().start();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v("aaa", "位置发生了变化");
            HttpURLConnection connection = null;
            String result = "";
            try {
                URL url = new URL("http://45.55.23.76:8000/location?" +
                        "latitude=" + "123" +
                        "&&longitude=" + "234"
                );
                connection = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader((connection.getInputStream()));
                BufferedReader bufferedReader = new BufferedReader(in);
                StringBuffer strBuffer = new StringBuffer();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuffer.append(line);
                }
                result = strBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    /*
    更新界面显示的时间
     */
    public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }

    public class SendLocationThread extends Thread {
        @Override
        public void run(){
            URL url = null;
            do {
                Log.v("aaa", "start SendLocationThread");
                try {

                    Log.v("aaa", "step 0.1");
                    // LocationManager locManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location loc = locManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc == null) {
                        loc = locManger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    Log.v("aaa", "step 0.2");
                    Log.v("aaa", String.valueOf(loc.getLatitude()));
                    Log.v("aaa", "step 0.3");
                    String my_url = "http://45.55.23.76:8000/location?" + "latitude=" + loc.getLatitude() + "&&longitude=" + loc.getLongitude();
                    Log.v("aaa", "step 0.5");
                    url = new URL("http://45.55.23.76:8000/location?" +
                            "latitude=" + loc.getLatitude() +
                            "&&longitude=" + loc.getLongitude()
                    );
                    Log.v("aaa", "step 0.9");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Log.v("aaa", "step 1");
                HttpURLConnection connection = null;
                String result = "";
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    InputStreamReader in = new InputStreamReader((connection.getInputStream()));
                    BufferedReader bufferedReader = new BufferedReader(in);
                    StringBuffer strBuffer = new StringBuffer();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        strBuffer.append(line);
                    }
                    result = strBuffer.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.v("aaa", "step 2");

                Message msg = new Message();
                msg.what = msgKey2;
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                msg.setData(bundle);
                mHandler.sendMessage(msg);

                Log.v("haofly", "come on");
                try {
                    Thread.sleep(1000);// * 60 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }while(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
