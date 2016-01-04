package flappjacks.locklibrary;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by kiran on 17/10/15.
 */
public class LockService extends Service {
    BroadcastReceiver mReceiver, videoreceiver;

    // Intent myIntent;
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate()
    {

//        Toast.makeText(getApplicationContext(), "Service created", Toast.LENGTH_SHORT).show();
        KeyguardManager.KeyguardLock k1;

        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        k1 = km.newKeyguardLock("IN");
        k1.disableKeyguard();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new LockReceiver();
        registerReceiver(mReceiver, filter);

        super.onCreate();

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub

        super.onStart(intent, startId);
    }

	/*
	 * class StateListener extends PhoneStateListener{
	 *
	 * @Override public void onCallStateChanged(int state, String
	 * incomingNumber) {
	 *
	 * super.onCallStateChanged(state, incomingNumber); switch(state){ case
	 * TelephonyManager.CALL_STATE_RINGING: break; case
	 * TelephonyManager.CALL_STATE_OFFHOOK:
	 * System.out.println("call Activity off hook");
	 * getApplication().startActivity(myIntent);
	 *
	 *
	 *
	 * break; case TelephonyManager.CALL_STATE_IDLE: break; } } };
	 */

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

}
