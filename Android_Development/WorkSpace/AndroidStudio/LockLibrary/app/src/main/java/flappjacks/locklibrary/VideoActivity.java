package flappjacks.locklibrary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VideoActivity extends Activity {

    Button cancelButton;
    DatabaseHelper sqlhelper;
    String END_DATE="",AD_NAME="";
    int CAMP_ID,CURRENT_VIEWS,MAX_VIEWS,MIN_SECS,UPDATE_ID=-1,ExistsINDB=-1,CALLSTATE=-1;
    double AMOUNT_PER_VIEW;
    VideoView videoView;
    String[] PausedAds=null;
    AudioManager am;

    TelephonyManager mTelManager;




    public static  Activity refVideoActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_video);
        refVideoActivity = this;





        videoView = (VideoView) findViewById(R.id.view_video);

        final MyPhoneStateListener mPhoneStateListener=new MyPhoneStateListener(videoView);
        mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


        // check from DB for images
        sqlhelper=new DatabaseHelper(this);
        String videoPath=readFromDB();
        Uri vidFile;

        if(TextUtils.isEmpty(videoPath))
        {
            vidFile = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.vid);
            CAMP_ID=000;
        }
        else
        {
                String finalPath=Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/AppMedia/"+videoPath;
                File file = new File(finalPath);
                if (file.exists())
                {
                    vidFile = Uri.parse(finalPath);
                }
                //Do something
                else
                {
                    vidFile = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.vid);
                    CAMP_ID=000;
                }
        }

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamMute(AudioManager.STREAM_RING, true);

        SharedPreferences settings=getSharedPreferences(Preferences.PREFS_APP_FILE,0);
        String videoStatus=settings.getString(Preferences.Video_Toggle,null);
        if(!Boolean.parseBoolean(videoStatus))
        {
            am.setStreamMute(AudioManager.STREAM_RING, false);
            VideoActivity.this.finish();
        }


//        vw=videoView;
        String add=vidFile.toString();
        // Uri video = Uri.parse("android.resource://com.cash4adds/"+R.raw.vid);
        //videoView.setVideoURI(video);
        Log.i("TAG", vidFile.toString());
        //videoView.setVideoURI(vidFile);
        videoView.setVideoPath(add);
        // MediaController controller=new MediaController(this);
        //controller.setAnchorView(videoView);
        //videoView.requestFocus();
        videoView.start();




        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

//
//                if(videoView.getCurrentPosition()/1000>MIN_SECS)
//                {
//                    UPDATE_ID=2;
//                    updateDB(CURRENT_VIEWS+1);
//                    VideoActivity.this.finish();
//                }
//                Toast.makeText(VideoActivity.this, "Duration :" + videoView.getCurrentPosition() / 1000, Toast.LENGTH_SHORT).show();

//                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.setStreamMute(AudioManager.STREAM_RING,false);

            }
        });
         videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				 mp.setVolume(10, 10);
//				 videoView.start();
			}
		});
        cancelButton=(Button)findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("The elapsed duration is :" + videoView.getCurrentPosition());

                UPDATE_ID=10;
                updateDB(CURRENT_VIEWS+1);

//                if(videoView.getCurrentPosition()/1000>MIN_SECS)
//                {
//                    UPDATE_ID=2;
//                    updateDB(CURRENT_VIEWS+1);
//                }
//                else if(videoView.getCurrentPosition()/1000<=MIN_SECS)
//                {
//
//                }
//                Toast.makeText(VideoActivity.this, "Duration :" + videoView.getCurrentPosition() / 1000, Toast.LENGTH_SHORT).show();
                am.setStreamMute(AudioManager.STREAM_RING,false);
                VideoActivity.this.finish();


            }
        });

    }





    private String readFromDB() {

        String path="";
        ExistsINDB=-1;
        try {

            String temp=getPausedData();
            Log.e("Paused DB",temp);

            String excludeList= temp;
            getPausedData();

            Cursor cur = sqlhelper.getActiveAds("video",excludeList);
            if (cur == null) {
                Log.i("TAGCUR", "novalues");
                ExistsINDB=0;
            }
            else {
                cur.moveToFirst();
                while(!cur.isAfterLast())
                {
                    CAMP_ID=(cur.getInt(1));
                    MAX_VIEWS=(cur.getInt(2));
                    END_DATE=(cur.getString(3));
                    CURRENT_VIEWS=(cur.getInt(4));
                    MIN_SECS=(cur.getInt(5));
                    AD_NAME=(cur.getString(6));


//                    Toast.makeText(getApplicationContext(),""+MIN_SECS,Toast.LENGTH_SHORT).show();
                    if (CURRENT_VIEWS >= MAX_VIEWS && !TextUtils.isEmpty(END_DATE)) {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date1 = sdf.parse(END_DATE.toString().trim());
                        int x = sdf.format(date1).compareTo(sdf.format(c.getTime()));
                        Log.e("date", "" + x);


                        if (x <= 0) {
                            UPDATE_ID = 1;
                            updateDB(CAMP_ID);
                        }

                    }
                    else if(CURRENT_VIEWS<MAX_VIEWS)
                    {
                        path=(cur.getString(0));
                        path=path.substring(7,path.length());
                        AMOUNT_PER_VIEW=(cur.getFloat(6));
                        ExistsINDB=1;
                    }
                    System.out.println(path);
                    Log.i("TAGCUR", path);
                    cur.moveToNext();
                }
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return path;
    }

    private String getPausedData() {

        StringBuffer stringBuffer=new StringBuffer();
        String valuesInPaused="(";

        try{
            Cursor cur = sqlhelper.getPausedData();
            if (cur == null) {
                Log.i("TAGCUR", "novalues");
                valuesInPaused=valuesInPaused+")";
            }
            else {
                cur.moveToFirst();
                while(!cur.isAfterLast())
                {
                    valuesInPaused=valuesInPaused+(cur.getInt(0))+",";

                    cur.moveToNext();
                }
                valuesInPaused.toString().substring(0,valuesInPaused.length()-1);
                valuesInPaused=valuesInPaused+")";
            }


        }
        catch(Exception e)
        {

        }

        if(valuesInPaused.equals("(") || valuesInPaused.equals(")"))
            return "()";
        else
            return valuesInPaused;

    }

    private void updateDB(int value) {


        try {
            if (UPDATE_ID==1){
                sqlhelper.updateAdStatus(value, false);
            }
            else if (UPDATE_ID==2) {
                Calendar c=Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String cur_date=sdf.format(c.getTime());
                Log.e(cur_date, cur_date);
                try {

                    if(CAMP_ID!=000)
                    {
                        sqlhelper.updateNumViews(CAMP_ID,value,cur_date);
                        sqlhelper.saveTransactionData(CAMP_ID,AMOUNT_PER_VIEW,cur_date);
                    }
                    else
                    {
                        Log.e("Demo transaction","no score");
                    }

                } catch (SQLiteException ex) {
                    ex.printStackTrace();
                    Log.e("unique key constraint", ex.getMessage());
                }
            }
            else if(UPDATE_ID==10)
            {
                Calendar c=Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String cur_date=sdf.format(c.getTime());
                Log.e(cur_date, cur_date);
                try {

                    if(CAMP_ID!=000)
                    {
                        sqlhelper.updateNumViews(CAMP_ID,value,cur_date);
                        updateLostData(CAMP_ID);
                    }
                    else
                    {
                        Log.e("Demo transaction","no score");
                    }

                } catch (SQLiteException ex) {
                    ex.printStackTrace();
                    Log.e("unique key constraint", ex.getMessage());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("update error", ex.getMessage());
        }
    }

    public void updateLostData(int camp_id) {


        int Lost_VIEWS=-1;

        try {
            Cursor cur = sqlhelper.getLostdata(camp_id);
            if (cur == null) {
                Log.i("TAGCUR", "novalues");
            }
            else {
                cur.moveToFirst();
                while(!cur.isAfterLast())
                {
                    Lost_VIEWS=cur.getInt(2);
                    cur.moveToNext();
                }

                //============= update views =====================
                if(Lost_VIEWS>=0)
                {
                    sqlhelper.updateLostViews(camp_id,Lost_VIEWS+1);
                }


            }




        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }


    public int getPlayDuration()
    {
        VideoView abc= (VideoView) findViewById(R.id.view_video);
        return  abc.getCurrentPosition()/1000;
    }

//    public static void disableVideo()
//    {
////        videoView.getCurrentPosition();
//
//        VideoActivity videoActivity=new VideoActivity();
//        int x=videoActivity.getPlayDuration();
//
////        updateLostData(8);
//        Toast.makeText(refVideoActivity,"Bye bye! "+x,Toast.LENGTH_LONG).show();
//        refVideoActivity.finish();
//    }

    @Override
    protected void onPause() {
        super.onPause();


        try{


//            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String state2 = TelephonyManager.EXTRA_STATE_OFFHOOK;
//            int state = tm.getCallState();

            int duration=videoView.getCurrentPosition()/1000;
            Log.e("Duration 1",""+duration+" "+MIN_SECS);


            if(CALLSTATE==111 || state2==TelephonyManager.EXTRA_STATE_OFFHOOK )
            {
                if(duration>MIN_SECS)
                {
                    UPDATE_ID=2;
                    updateDB(CURRENT_VIEWS+1);
                    Toast.makeText(VideoActivity.this, "Duration :" +duration, Toast.LENGTH_SHORT).show();
                }
                else if(duration<=MIN_SECS)
                {
                    UPDATE_ID=10;
                    updateDB(CURRENT_VIEWS+1);
                }

                Log.e("Duration 2",+duration+" "+MIN_SECS);
//                Toast.makeText(VideoActivity.this, "Duration :" +duration, Toast.LENGTH_SHORT).show();
//                VideoActivity.this.finish();

            }


            am.setStreamMute(AudioManager.STREAM_RING,false);

        }
        catch(Exception e)
        {
            Log.e("On recieve error",e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        am.setStreamMute(AudioManager.STREAM_RING, false);

    }

    protected class MyPhoneStateListener extends PhoneStateListener
    {

        VideoView video;
        public MyPhoneStateListener(VideoView videoView) {
            this.video=videoView;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {

//            videoView = (VideoView) findViewById(R.id.view_video);
            int duration=video.getCurrentPosition()/1000;
            switch (state)
            {
                case TelephonyManager.CALL_STATE_RINGING:
//                    Toast.makeText(VideoActivity.this, "ringing" , Toast.LENGTH_SHORT).show();
//                    getPlayDuration()
                    // do whatever you want here
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:;
                    // do whatever you want here
//                    Toast.makeText(VideoActivity.this, "off hook "+duration , Toast.LENGTH_SHORT).show();
                    CALLSTATE=111;
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
//                    Toast.makeText(VideoActivity.this, "idle "+duration , Toast.LENGTH_SHORT).show();
                    CALLSTATE=222;
                    // do whatever you want here
                    break;

//                case TelephonyManager.EXTRA_STATE_OFFHOOK :
            }

        }
    }
}