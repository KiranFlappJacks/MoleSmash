package flappjacks.locklibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fima.glowpadview.GlowPadView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LockScreenActivity extends Activity {

    RelativeLayout lockLayout;
    DatabaseHelper sqlhelper;
    String END_DATE="",WEBSITE="",CONTACT="";
    int CAMP_ID,CURRENT_VIEWS,MAX_VIEWS,UPDATE_ID=-1;
    double AMOUNT_PER_VIEW;
    GlowPadView mGlowPadView;


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.GONE);

        setContentView(R.layout.activity_lock_screen);

        lockLayout=(RelativeLayout)findViewById(R.id.relative_lock_screen);
        mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);
        final Animation animationScale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);

        mGlowPadView.setOnTriggerListener(new GlowPadView.OnTriggerListener() {
            @Override
            public void onGrabbed(View v, int handle) {

            }

            @Override
            public void onReleased(View v, int handle) {

            }

            @Override
            public void onTrigger(View v, int target) {


                final int resId = mGlowPadView.getResourceIdForTarget(target);
                if(resId==R.drawable.screenunlock_icon)
                {
                    UPDATE_ID=2;
                    updateDB(CURRENT_VIEWS+1);
                    finish();
                }

                else if(resId==R.drawable.screenlockpage_website_icon){

                    if(TextUtils.isEmpty(WEBSITE))
                    {
                        Toast.makeText(getApplicationContext(),"Website not defined!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE));
                        startActivity(browserIntent);
                    }

                }

                else if(resId==R.drawable.screenlockpage_email_icon){


                    if(TextUtils.isEmpty(CONTACT))
                    {
                        Toast.makeText(getApplicationContext(),"Contact not defined!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+CONTACT));
                        startActivity(intent);
                    }



                }

            }

            @Override
            public void onGrabbedStateChange(View v, int handle) {

            }

            @Override
            public void onFinishFinalAnimation() {

            }
        });

        // check from DB for images
        sqlhelper=new DatabaseHelper(this);
        String imagePath=readFromDB();

        if(TextUtils.isEmpty(imagePath))
        {
            lockLayout.setBackgroundResource(R.drawable.image);
            CAMP_ID=000;
        }
        else
        {
            String finalPath= Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/AppMedia/"+imagePath;

            File file = new File(finalPath);
            if (file.exists())
            {
                Bitmap bitmap = BitmapFactory.decodeFile(finalPath);
                BitmapDrawable bd = new BitmapDrawable(bitmap);
                lockLayout.setBackground(bd);
            }
            //Do somehting
            else
            {
                lockLayout.setBackgroundResource(R.drawable.image);
                CAMP_ID=0;
            }
            // Do something else.


        }


//
//        img_unlock.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//                v.startAnimation(animationScale);
//
//                UPDATE_ID=2;
//                updateDB(CURRENT_VIEWS+1);
//
//                finish();
//                return false;
//            }
//        });
    }

    private void updateDB(int value) {

        try {
            if (UPDATE_ID==1){
                sqlhelper.updateAdStatus(value,false);
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
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("update error",ex.getMessage());
        }
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
                valuesInPaused=valuesInPaused.toString().substring(0,valuesInPaused.length()-1);
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



    private String readFromDB() {


        String temp=getPausedData();
        Log.e("Paused DB",temp);

        String path="";
        try {
            String excludeList= temp;
            Cursor cur = sqlhelper.getActiveAds("image", excludeList);
            if (cur == null) {
                Log.i("TAGCUR", "novalues");
            }
            else {
                cur.moveToFirst();
                while(!cur.isAfterLast())
                {
                    CAMP_ID=(cur.getInt(1));
                    MAX_VIEWS=(cur.getInt(2));
                    END_DATE = (cur.getString(3));
                    CURRENT_VIEWS=(cur.getInt(4));
                    AMOUNT_PER_VIEW=(cur.getFloat(6));
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
                    } else if (CURRENT_VIEWS < MAX_VIEWS) {
                        path = (cur.getString(0));
                        path = path.substring(7, path.length());
                        WEBSITE=cur.getString(8);
                        CONTACT=cur.getString(9);


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
}
