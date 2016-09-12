package com.example.android.battrysaver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {
    int level;
    int criticalLevel=-1;
    int pauseComponentsTime=-1;
    int unPauseComponentsTime=-1;
    EditText criticalBattery;
    EditText pauseTime;
    EditText unPauseTime;
    AsyncTaskRunner runner = null;
    Boolean defaultWifiStatus;
    Boolean defaultMoDataStatus;
    Boolean defaultGPSStatus;
    Boolean defaultBlueToothStatus;
    int defaultBatteryStatus;
    int defaultBrightness;
    int defaultCriticalLevel=20;
    int defaultPauseTime=20;
    int defaultUnPauseTime=5;
   /* private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
             level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            Log.e("battery level", "" + level);
        }
    };
*/
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                toggleConnections();
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace();
            }
            return null;
        }
    }


    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return ((float)level / (float)scale) * 100.0f;

    }
    public void toggleConnections() throws InterruptedException {
        //If battery level is less thn critical level thn switch off\reduce usage
        level= (int) getBatteryLevel();
        if(level<criticalLevel)
        {
            do {
                try {
                    Components.setMobileDataEnabled(this, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Components.setMobileWifiEnabled(this, false);
                Components.setMobileBluetoothEnabled(false);
                Components.setMobileGPSEnabled(this, false);
                setBrightness(0);

                Log.e("Sleeping", "Sleepinggggggggg........................................");

                Thread.sleep(pauseComponentsTime * 60 * 1000);

                //sleep fr this time
                //switch on components fr provided time
                if(defaultWifiStatus)
                    Components.setMobileWifiEnabled(this,true);
                else if (defaultMoDataStatus)
                    try {
                        Components.setMobileDataEnabled(this, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                level= (int) getBatteryLevel();
                Log.e("Sleeping", "Stop Sleepinggggggggg........................................");
                Thread.sleep(unPauseComponentsTime * 60 * 1000);

            }while (level<=criticalLevel);

            //Set system as it was before
            //update  gps, brightness & bluetooth to default value
            if(level>criticalLevel)
            {
                setBrightness(defaultBrightness);
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));





    }

    public void startMain(View v)
    {



        if(runner==null)
        {
            criticalBattery=(EditText)findViewById(R.id.editText);
            pauseTime=(EditText)findViewById(R.id.editText2);
            unPauseTime=(EditText)findViewById(R.id.editText3);
            defaultWifiStatus=getWIFIStatus();
            defaultMoDataStatus=getDataStatus();
            defaultBrightness=getCurrentBrightnessValue();
            if(criticalBattery.getText().length()>0)
            {
                criticalLevel= Integer.parseInt(criticalBattery.getText().toString());
                if(criticalLevel>=1 && criticalLevel<=100) {
                    //do nothing
                }
                else
                    criticalLevel=defaultCriticalLevel;
            }

            if(pauseTime.getText().length()>0)
            {
                pauseComponentsTime= Integer.parseInt(pauseTime.getText().toString());
                if(pauseComponentsTime>=1 && pauseComponentsTime<=100) {
                    //do nothing
                }
                else
                    pauseComponentsTime=defaultPauseTime;
            }

            if(unPauseTime.getText().length()>0)
            {
                unPauseComponentsTime= Integer.parseInt(unPauseTime.getText().toString());
                if(unPauseComponentsTime>=1 && unPauseComponentsTime<=100) {
                    //do nothing
                }
                else
                    unPauseComponentsTime=defaultUnPauseTime;
            }

            // Not set anything
            if(criticalLevel==-1)
            {
                criticalLevel=defaultCriticalLevel;
            }

            if(pauseComponentsTime==-1)
                pauseComponentsTime=defaultPauseTime;

            if(unPauseComponentsTime==-1)
                unPauseComponentsTime=defaultUnPauseTime;

            runner = new AsyncTaskRunner();
            runner.execute();
        }

    }

    public void stopMain(View v)
    {
        if(runner!=null)
        {
            runner.cancel(true);
            runner=null;

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


    public boolean getWIFIStatus()
    {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected())
            return true;
        else
            return false;
    }

    public boolean getDataStatus()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(mData.isConnected())
            return  true;
        else
            return false;
    }

    public int getCurrentBrightnessValue()
    {
        try {
            return android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setBrightness(int brightness)
    {

        ContentResolver cResolver = this.getApplicationContext().getContentResolver();
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }



}
