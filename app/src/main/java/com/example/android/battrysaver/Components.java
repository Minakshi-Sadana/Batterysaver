package com.example.android.battrysaver;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by HP on 8/29/2015.
 */
public class Components {

    public static void setMobileDataEnabled(MainActivity context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", boolean.class);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }
    public static void setMobileWifiEnabled(MainActivity context, boolean enabled)
    {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }

    public static void setMobileGPSEnabled(MainActivity context,boolean enabled )
    {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }
    public static void setMobileBluetoothEnabled(boolean enabled)
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enabled && !isEnabled) {
            bluetoothAdapter.enable();
        }
        else if(!enabled && isEnabled) {
            bluetoothAdapter.disable();
        }
    }





}


