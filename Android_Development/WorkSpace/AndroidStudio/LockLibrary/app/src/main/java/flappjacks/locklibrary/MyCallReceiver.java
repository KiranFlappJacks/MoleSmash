package flappjacks.locklibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by kiran on 18/10/15.
 */
public class MyCallReceiver extends BroadcastReceiver
{

    boolean isVideoShowing =false;
    Intent i ;

    @Override
    public void onReceive(Context context, Intent intent) {



        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.d("IncomingReceiver:", state);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            //MainActivity.lockref.finish();
            isVideoShowing = true;
            Log.d("Ringing", "Phone is ringing");
            i = new Intent(context, VideoActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            //to make the activity wait of some sec after the call screen
            try{
                Thread.sleep(1500);

                context.startActivity(i);

            }
            catch(Exception e){
                e.printStackTrace();
            }


        }

        else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) || state.equals(TelephonyManager.EXTRA_STATE_IDLE))
        {

            if(VideoActivity.refVideoActivity != null)
            {
                isVideoShowing = false;
                VideoActivity.refVideoActivity.finish();
//                VideoActivity videoActivity=new VideoActivity();
//                videoActivity.disableVideo();
            }

        }















//		if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//            // This code will execute when the phone has an incoming call
//
//            // get the phone number
//            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            Toast.makeText(context, "Call from:" +incomingNumber, Toast.LENGTH_LONG).show();
//
//
//            Intent call=new Intent(context,VideoActivity.class);
//            call.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//			call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			call.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            context.startActivity(call);
//
//        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
//                TelephonyManager.EXTRA_STATE_IDLE)
//                || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
//                        TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//            // This code will execute when the call is disconnected
//            Toast.makeText(context, "Detected call hangup event", Toast.LENGTH_LONG).show();
//
//        }
    }

}