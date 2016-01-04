package flappjacks.locklibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kiran on 18/10/15.
 */
public class LockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {



        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            //wasScreenOn=false;
            Intent lockintent = new Intent(context,LockScreenActivity.class);
            lockintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(lockintent);


        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {



        }

    }
}
