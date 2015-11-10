package edu.utexas.cs.jacobr.wearabledatacollector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Receives its own events using a listener API designed for foreground activities. Updates a data
 * item every second while it is open. Also allows user to take a photo and send that as an asset to
 * the paired wearable.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String SHARED_PREFS_KEY = "data-collection-prefs";
    private static final String ALARM_KEY = "alarm";
    private static final String SLEEPING_KEY = "sleeping";
    public static final String RESULT = ".RESULT_READY";
    public static final String MESSAGE = ".SENSOR_DATA";

    private SharedPreferences preferences;

    private Switch alarmSwitch;
    private Switch sleepingSwitch;
    private TextView text;
    private TextView update;
    private BroadcastReceiver receiver;

    private DataCollectionAlarmReceiver alarm = new DataCollectionAlarmReceiver();

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        Log.d(TAG, "onCreate");

        preferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        alarmSwitch = (Switch) findViewById(R.id.alarmSwitch);
        sleepingSwitch = (Switch) findViewById(R.id.sleepingSwitch);
        text = (TextView) findViewById(R.id.textView);
        update = (TextView) findViewById(R.id.lastUpdate);

        alarmSwitch.setChecked(preferences.getBoolean(ALARM_KEY, false));
        sleepingSwitch.setChecked(preferences.getBoolean(SLEEPING_KEY, false));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                update.setText("Last update:" + currentDateandTime);
                String s = intent.getStringExtra(MESSAGE);
                text.setText(s);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    public void onDataCollectionAlarmToggle(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Log.d(TAG, "Enabling Data Collection");
            alarm.setAlarm(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(ALARM_KEY, true);
            editor.commit();
        } else {
            Log.d(TAG, "Disabling Data Collection");
            alarm.cancelAlarm(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(ALARM_KEY, false);
            editor.commit();
        }
    }

    public void onSleepingToggle(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Log.d(TAG, "onSleepingToggle On");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SLEEPING_KEY, true);
            editor.commit();
        } else {
            Log.d(TAG, "onSleepingToggle Off");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SLEEPING_KEY, false);
            editor.commit();
        }
    }

    public void onReadSensors(View view) {
        Intent intent = new Intent(this, DataCollectionAlarmReceiver.class);
        sendBroadcast(intent);
    }
}
