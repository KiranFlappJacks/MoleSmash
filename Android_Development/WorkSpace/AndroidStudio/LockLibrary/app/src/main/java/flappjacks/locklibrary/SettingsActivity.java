package flappjacks.locklibrary;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity {

    Switch LockSwitch, VideoSwitch;
    Animation animationScale;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    String URL[] = {URLDetails.URL_ADS_TRANSACTION_UPDATE, URLDetails.URL_SYNC_NOW, URLDetails.URL_ADS_DOWNLOAD};
    ImageView back_btn;
    String final_file;
    TextView txtClickable, moneyBag;
    TableLayout tableScore;
    int responseID = -1, DB_ID = -1;

    String[] temp = new String[20];

    String USER_ID = "", API_KEY = "", AMOUNT = "", ADS_IN_DEVICE = "", SECURE_KEY = "", AD_FILE = "",ADS_IN_DEVICE_2="";
    String HashKey = "";
    DatabaseHelper sqlhelper;
    Dialog dialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_settings);


        ActionBar bar = getActionBar();
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowTitleEnabled(false);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color)));
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.action_bar);




        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null)
        {
            USER_ID=bundle.getString("userid");
            API_KEY=bundle.getString("apikey");
            HashKey=bundle.getString("api_secret_key");
//            Toast.makeText(SettingsActivity.this, temp_uid+"\n"+temp_apikey, Toast.LENGTH_SHORT).show();


        }



//      DB object init
        sqlhelper = new DatabaseHelper(this);


        // Add Material style status bar color
        if (Build.VERSION.SDK_INT > 20) {

            try {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(
                        R.color.status_bar_color));
            } catch (Exception e) {

            }

        }


        ImageButton btn_sync = (ImageButton) findViewById(R.id.action_bar_sync);
        tableScore = (TableLayout) findViewById(R.id.table_settings_parent);
        animationScale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        settings = getSharedPreferences(Preferences.PREFS_APP_FILE, 0);
        LockSwitch = (Switch) findViewById(R.id.switch_settings_lock);
        VideoSwitch = (Switch) findViewById(R.id.switch_settings_video);
        back_btn = (ImageView) findViewById(R.id.btn_settings_back);
        txtClickable = (TextView) findViewById(R.id.txt_settings_lost_earned);
        moneyBag = (TextView) findViewById(R.id.txt_settings_money_bag);

        String lockStatus = settings.getString(Preferences.Lock_Toggle, null);
        String videoStatus = settings.getString(Preferences.Video_Toggle, null);




        if (lockStatus == null && videoStatus == null) {
            editor = settings.edit();
            editor.putString(Preferences.Video_Toggle, "false");
            editor.putString(Preferences.Lock_Toggle, "false");
            editor.commit();
        } else {
            LockSwitch.setChecked(Boolean.parseBoolean(lockStatus));
            VideoSwitch.setChecked(Boolean.parseBoolean(videoStatus));

            if (Boolean.parseBoolean(lockStatus)) {
                startService(new Intent(SettingsActivity.this, LockService.class));
            } else if (!Boolean.parseBoolean(lockStatus)) {
                stopService(new Intent(SettingsActivity.this, LockService.class));
            }

        }

        LockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor = settings.edit();
                    editor.putString(Preferences.Lock_Toggle, "true");
                    editor.commit();
                    startService(new Intent(SettingsActivity.this, LockService.class));
                }
                if (!isChecked) {
                    editor = settings.edit();
                    editor.putString(Preferences.Lock_Toggle, "false");
                    editor.commit();
                    stopService(new Intent(SettingsActivity.this, LockService.class));
                }
            }
        });

        VideoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    editor = settings.edit();
                    editor.putString(Preferences.Video_Toggle, "true");
                    editor.commit();
                }
                if (!isChecked) {
                    editor = settings.edit();
                    editor.putString(Preferences.Video_Toggle, "false");
                    editor.commit();
                }

            }
        });


        //============== loading dialog init ===============
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        DB_ID = 0;
        readFromDB();
        startSync();

        setEarnedTableScore();

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                v.startAnimation(animationScale);
//                Toast.makeText(getApplicationContext(), "sync", Toast.LENGTH_SHORT).show();

                DB_ID = 0;
                readFromDB();
                startSync();
                dialog.show();


//                startRegisterActivity();


            }


        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animationScale);
                finish();
            }
        });

        txtClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = txtClickable.getText().toString();
//                Toast.makeText(getApplication(),text,Toast.LENGTH_SHORT).show();
                if (text.equals("Lost")) {
                    txtClickable.setText(R.string.title_clickable_earned);
                    setLostTableScore();

                } else {
                    txtClickable.setText(R.string.title_clickable_lost);
                    setEarnedTableScore();
                }

            }
        });
    }

    private void startRegisterActivity() {


        Intent intent=new Intent(SettingsActivity.this,RegisterActivity.class);
        intent.putExtra("api_secret_key",HashKey);
        intent.putExtra("email","abc123456@gmail.com");
        intent.putExtra("yeldi_userid","898");
        intent.putExtra("dob","1990/01/20");
        intent.putExtra("gender","male");
        intent.putExtra("country","india");
        intent.putExtra("state","karanataka");
        intent.putExtra("city","bangalore");
        intent.putExtra("imei","22211666");
        intent.putExtra("device_name","Motorola");
        intent.putExtra("device_model","Nexus 6");
        intent.putExtra("android_version","6.0");
        startActivity(intent);

    }

    private void readFromDB() {

        if (DB_ID == 0) {
            String ads_in_device = "", balance = "";
            try {
                Cursor cur = sqlhelper.getSyncParams();
                if (cur == null) {
                    Log.e("read sync params", "novalues");
                    ads_in_device="#";
                } else {
                    cur.moveToFirst();
                    while (!cur.isAfterLast()) {
                        ads_in_device = ads_in_device + String.valueOf(cur.getInt(0)) + "#";
                        balance = String.valueOf(cur.getInt(1));

                        System.out.println(ads_in_device);

                        cur.moveToNext();
                    }
                }

                Log.e("ads in dev - sync", ads_in_device);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (TextUtils.isEmpty(ads_in_device)) {
                ADS_IN_DEVICE = "#";
            } else {
                ADS_IN_DEVICE = ads_in_device;
            }
            if (TextUtils.isEmpty(balance)) {
                AMOUNT = "0";
            } else {
                AMOUNT = balance;
            }

//        }
//        else if(DB_ID==1)
//        {

            StringBuffer time_stamp=new StringBuffer(),amount_pview=new StringBuffer(),campid=new StringBuffer();

            try {
                Cursor cur = sqlhelper.getTransactionParams();
                if (cur == null) {
                    Log.e("TransactionParams", "novalues");
                } else {
                    cur.moveToFirst();
                    while (!cur.isAfterLast()) {

                        campid.append((cur.getInt(0)));
                        amount_pview.append((cur.getFloat(1)));
                        time_stamp.append((cur.getString(2)));

                        campid.append("\n\n");
                        amount_pview.append("\n\n");
                        time_stamp.append("\n\n");

                        System.out.println(campid);
                        Log.e("TransactionParams", "" + campid);
                        cur.moveToNext();
                    }

                    String json=formJson(campid,amount_pview,time_stamp);

                    ADS_IN_DEVICE_2=json;
                    Log.e("TransactionParams",ADS_IN_DEVICE_2);

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }


        }



    }

    private String formJson(StringBuffer campid, StringBuffer amount_pview, StringBuffer time_stamp) {

        JSONObject object=new JSONObject();

       try{

           String[] camp_id=campid.toString().split("\n\n");
           String[] amnt_p_view=amount_pview.toString().split("\n\n");
           String[] t_stamp=time_stamp.toString().split("\n\n");

           JSONObject syncRequest=new JSONObject();

           JSONArray adsList=new JSONArray();
           for(int i=0;i<camp_id.length;i++) {
               JSONObject arrobj=new JSONObject();
               arrobj.put("campid", Integer.parseInt(camp_id[i]));
               arrobj.put("amount", Double.parseDouble(amnt_p_view[i]));
               arrobj.put("transaction_date",t_stamp[i]);
               adsList.put(arrobj);
           }

           syncRequest.put("ads_list",adsList);
           object.put("sync_request",syncRequest);

       }
       catch (Exception e)
       {

       }

        Log.e("json",object.toString());

        JSONObject temp=new JSONObject();

        if(!TextUtils.isEmpty(object.toString())&&!object.toString().trim().equals(temp.toString().trim()))
        {
            return object.toString();
        }
        else
        {
            return "0";
        }

    }

    private void startSync() {


        if(!TextUtils.isEmpty(USER_ID)&&!TextUtils.isEmpty(API_KEY)&&!TextUtils.isEmpty(HashKey))
        {
            responseID = 0;
            String msg = USER_ID + API_KEY + ADS_IN_DEVICE_2 + HashKey;
            callAPI(URLDetails.URL_ADS_TRANSACTION_UPDATE, msg);
        }
        else
        {
            DialogManager.showCustCancelDialog(SettingsActivity.this,"User Data not available!");
            dialog.cancel();
        }


    }

    private List<NameValuePair> setupCall(String msg) {

        List<NameValuePair> url_parameters = new ArrayList<NameValuePair>();
        url_parameters.clear();


        try {


            SECURE_KEY = SHA1Generator.sha1(msg);


            if (responseID == 0) {
                url_parameters.add(new BasicNameValuePair("userid", USER_ID));
                url_parameters.add(new BasicNameValuePair("apikey", API_KEY));
                url_parameters.add(new BasicNameValuePair("adsin_device", ADS_IN_DEVICE_2));
                url_parameters.add(new BasicNameValuePair("secure_key", SECURE_KEY));
            }
            if (responseID == 1) {
                url_parameters.add(new BasicNameValuePair("userid", USER_ID));
                url_parameters.add(new BasicNameValuePair("apikey", API_KEY));
                url_parameters.add(new BasicNameValuePair("amount", AMOUNT));
                url_parameters.add(new BasicNameValuePair("adsin_device", ADS_IN_DEVICE));
                url_parameters.add(new BasicNameValuePair("secure_key", SECURE_KEY));
            } else if (responseID == 2) {

                SECURE_KEY = SHA1Generator.sha1(msg);

                url_parameters = new ArrayList<NameValuePair>();
                url_parameters.add(new BasicNameValuePair("userid", USER_ID));
                url_parameters.add(new BasicNameValuePair("apikey", API_KEY));
                url_parameters.add(new BasicNameValuePair("ad_file", AD_FILE));
                url_parameters.add(new BasicNameValuePair("secure_key", SECURE_KEY));
            }

        } catch (Exception e) {
            Log.e("Algorithm Exception", e.getMessage());
        }

        return url_parameters;
    }


    private void callAPI(String url, String msg) {


        if(NetworkUtil.haveNetworkConnection(getApplicationContext()))
        {
            List<NameValuePair> url_parameters = setupCall(msg);
            callwebservice cs = new callwebservice(url, url_parameters);
            cs.execute();
        }
        else
        {
            DialogManager.showCustCancelDialog(SettingsActivity.this,"No internet access available!");
            dialog.cancel();
//            this.finish();
        }



    }

    class callwebservice extends AsyncTask<Void, Void, String> {

        String api;
        List<NameValuePair> url_parameters;

        public callwebservice(String url, List<NameValuePair> url_parameters) {
            this.api = url;
            this.url_parameters = url_parameters;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            Log.e("auto logout called", "auto logout called");
        }

        @Override
        protected String doInBackground(Void... params) {
            String data = null;
            try {

                data = callservice(api, url_parameters);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String status, logout;
            try {
                if (responseID == 0) {
                    JSONObject obj = new JSONObject(result.toString());
                    JSONObject adstransaction_response = obj.getJSONObject("adstransaction_response");
                    String errorCode = adstransaction_response.getString("errorCode");
                    if (errorCode.equals("70000")) {
                        String amount = adstransaction_response.getString("amount");
                        String jMessage = adstransaction_response.getString("message");
                        String balance = adstransaction_response.getString("balance");
                        result = "Ads transaction update success!";

                        //================== Delete table ads_transaction ===================

                        try
                        {
                            sqlhelper.deleteTransactionData();
                        }
                        catch (Exception e)
                        {
                            Log.e("Delete data problem","not deleted!\n"+e.getMessage());
                        }

                        //================== call sync_now api ===================
                        responseID = 1;
                        String msg = USER_ID + API_KEY + AMOUNT + ADS_IN_DEVICE + HashKey;
                        callAPI(URLDetails.URL_SYNC_NOW, msg);
                    } else if (errorCode.equals("70001")) {
                        String message = adstransaction_response.getString("message");
                        result = "Internal error! Please try again!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("70002")) {
                        result = "Internal error! Invalid request!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("70003")) {
                        result = " Internal error! Invalid user id!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("70004")) {
                        result = " Internal error! Required fields missing!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("70005")) {
                        result = " Internal error! Total views reached for the ad!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("70006")) {
                        result = " Internal error! Budget limit is exceeded for the ad!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    }

//                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();

                } else if (responseID == 1) {
                    JSONObject obj = new JSONObject(result.toString());
                    JSONObject jSyncResponse = obj.getJSONObject("sync_response");
                    String errorCode = jSyncResponse.getString("errorCode");

                    StringBuffer stringBuffer = new StringBuffer();

                    if (errorCode.equals("7000")) {

                    //================== check for pause/resume ads =========================

                        sqlhelper.deletePausedData();

                        JSONArray paused_imageads_list=jSyncResponse.getJSONArray("paused_imageads_list");
                        JSONArray paused_videoads_list=jSyncResponse.getJSONArray("paused_videoads_list");
                        int[] imagePausedArr=new int[paused_imageads_list.length()+paused_videoads_list.length()],videoPausedArr;
                        for(int i=0;i<paused_imageads_list.length();i++)
                        {
                            imagePausedArr[i]=paused_imageads_list.getInt(i);
                            Log.e("PausedImageList",""+paused_imageads_list.getInt(i));
                        }

                        int x=paused_imageads_list.length();

                        for(int i=0;i<paused_videoads_list.length();i++)
                        {
                            imagePausedArr[x]=paused_videoads_list.getInt(i);
                            x=x+1;
                        }


                        Log.e("paused_images",""+imagePausedArr.length);
//                        Log.e("paused_videos",""+videoPausedArr.length);


                        writeToDB(imagePausedArr);


                    //========================================================================

                        String message = jSyncResponse.getString("message");
                        double balance = Double.parseDouble(jSyncResponse.getString("balance"));
                        String totalads = jSyncResponse.getString("totalads");
                        if (!TextUtils.isEmpty(totalads) && Integer.parseInt(totalads) > 0) {
                            JSONArray image_ads = jSyncResponse.getJSONArray("image_ads");
                            for (int i = 0; i < image_ads.length(); i++) {
                                JSONObject arrObj = image_ads.getJSONObject(i);
                                int campid = arrObj.getInt("campid");
                                String name = arrObj.getString("name");
                                int amount_per_view = arrObj.getInt("amount_per_view");
                                int total_views_allowed = arrObj.getInt("total_views_allowed");
                                String start_date = arrObj.getString("start_date");
                                String end_date = arrObj.getString("end_date");
                                int start_time = arrObj.getInt("start_time");
                                int end_time = arrObj.getInt("end_time");
                                String ad_file = arrObj.getString("ad_file");
                                String website=arrObj.getString("website");
                                String contact=arrObj.getString("contact");
                                stringBuffer.append(ad_file);
                                stringBuffer.append("\n\n");
                                //insert to DB

                                try {


                                    sqlhelper.saveAllData(campid, name, amount_per_view,
                                            total_views_allowed, start_date, end_date, start_time, end_time,
                                            ad_file, false, 0, "image", "-", balance,website,contact);
                                    sqlhelper.saveLostData(campid,name,amount_per_view,0);


                                } catch (SQLiteException ex) {
                                    ex.printStackTrace();
                                    Log.e("unique key constraint", ex.getMessage());
                                }

                            }

                            JSONArray video_ads = jSyncResponse.getJSONArray("video_ads");
                            for (int i = 0; i < video_ads.length(); i++) {
                                JSONObject arrObj = video_ads.getJSONObject(i);
                                int campid = arrObj.getInt("campid");
                                String name = arrObj.getString("name");
                                int amount_per_view = arrObj.getInt("amount_per_view");
                                int total_views_allowed = arrObj.getInt("total_views_allowed");
                                String start_date = arrObj.getString("start_date");
                                String end_date = arrObj.getString("end_date");
                                int start_time = arrObj.getInt("start_time");
                                int end_time = arrObj.getInt("end_time");
                                String ad_file = arrObj.getString("ad_file");
//                                String website=arrObj.getString("website");
//                                String contact=arrObj.getString("contact");
                                stringBuffer.append(ad_file);
                                stringBuffer.append("\n\n");


                                //insert to DB

                                try {


                                    sqlhelper.saveAllData(campid, name, amount_per_view,
                                            total_views_allowed, start_date, end_date, start_time, end_time,
                                            ad_file, false, 0, "video", "-", balance, "", "");

                                    sqlhelper.saveLostData(campid,name,amount_per_view,0);

                                } catch (SQLiteException ex) {
                                    ex.printStackTrace();
                                    Log.e("unique key constraint", ex.getMessage());
                                }
                            }
                        }
                        result = "success";
                        String str = getResources().getString(R.string.title_money_bag);
                        moneyBag.setText(str + " " + balance);
                        dialog.cancel();
                    } else if (errorCode.equals("7001")) {
                        String message = jSyncResponse.getString("message");
                        result = "Internal error! Please try again!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("7002")) {
                        result = "Internal error! Invalid request!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("7003")) {
                        result = " Internal error! Required fields missing!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("7004")) {
                        result = " Internal error! Missing parameters!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    } else if (errorCode.equals("7005")) {
                        result = " Internal error! Invalid ads in device!";
                        dialog.cancel();
                        DialogManager.showCustDialog(SettingsActivity.this, result);
                    }

//                    Toast.makeText(getApplicationContext(), "" + result,Toast.LENGTH_SHORT).show();

                    //================== call download api ===================
                    responseID = 2;
                    String[] file_names = stringBuffer.toString().split("\n\n");
//                        Toast.makeText(getApplicationContext(), file_names.length+""+stringBuffer.toString(), Toast.LENGTH_SHORT).show();
//                    temp=file_names;
                    if (file_names.length > 0) {

                        for (int i = 0; i < file_names.length; i++) {
                            String msg = USER_ID + API_KEY + file_names[i] + HashKey;
                            AD_FILE = file_names[i];
                            callAPI(URLDetails.URL_ADS_DOWNLOAD, msg);
                            Log.e("File Download", "" + i);

                        }

                    }

                } else if (responseID == 2) {
                    setEarnedTableScore();
//                    int j=-1;
                    JSONObject obj = new JSONObject(result.toString());
                    JSONObject adsdownload_response = obj.getJSONObject("adsdownload_response");
                    String errorCode = adsdownload_response.getString("errorCode");
                    if (errorCode.equals("20000")) {
                        String ads_file = adsdownload_response.getString("ads_file");
                        int campid = adsdownload_response.getInt("campid");
                        FileDownload fd = new FileDownload(campid,ads_file,getApplicationContext());
                        fd.execute();
                    }


                }
            } catch (Exception e) {

                e.printStackTrace();
                dialog.cancel();
                DialogManager.showCustDialog(SettingsActivity.this, "Error in server!");
            }

//            Log.e("Arr Length", temp.length + "");
            // DialogManager.showAlertDialog(getBaseContext(),
            // result.toString());


        }


    }

    private void writeToDB(int[] imagePausedArr) {



        try{

            for(int i=0;i<imagePausedArr.length;i++)
            {
                sqlhelper.savePausedData(imagePausedArr[i]);
            }



        }
        catch(Exception e)
        {

        }


    }

    private void setLostTableScore() {


        ScrollView scrollView = new ScrollView(getApplicationContext());
        TableRow headerRow = (TableRow) findViewById(R.id.table_settings_row_one);
        tableScore.removeAllViews();
        TextView firstTextView = (TextView) headerRow.getChildAt(0);
        TextView secondTextView = (TextView) headerRow.getChildAt(1);
        TextView thirdTextView = (TextView) headerRow.getChildAt(2);
        secondTextView.setText(R.string.title_views_left);
        thirdTextView.setText(R.string.title_money_left);
        tableScore.addView(headerRow);

        TableRow row = new TableRow(getApplicationContext());
        TableLayout tlayout = new TableLayout(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.33f);
        tlayout.addView(row);




        try {
            Cursor cur = sqlhelper.getAllLostdata();
            if (cur == null) {
                Log.e("Max table", "novalues");
            }
            else
            {
                cur.moveToFirst();
                while(!cur.isAfterLast()) {
                    String addname = cur.getString(0);
                    double amt = cur.getFloat(1);
                    int lost_v=cur.getInt(2);


                    if(lost_v>0)
                    {

                        row = new TableRow(getApplicationContext());
                        TextView text1 = new TextView(getApplicationContext());
                        TextView text2 = new TextView(getApplicationContext());
                        TextView text3 = new TextView(getApplicationContext());


                        text1.setLayoutParams(lp);
                        text1.setGravity(Gravity.CENTER);
                        text1.setTextColor(Color.BLACK);
                        text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                        text2.setLayoutParams(lp);
                        text2.setGravity(Gravity.CENTER);
                        text2.setTextColor(Color.BLACK);
                        text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                        text3.setLayoutParams(lp);
                        text3.setGravity(Gravity.CENTER);
                        text3.setTextColor(Color.BLACK);
                        text3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


                        if (!TextUtils.isEmpty(addname)) {
                            text1.setText(addname);
                            row.addView(text1);
                        } else {
                            text1.setText("-");
                            row.addView(text1);
                        }

                        if (!TextUtils.isEmpty(String.valueOf(lost_v))) {
                            text2.setText(String.valueOf(lost_v));
                            row.addView(text2);
                        } else {
                            text2.setText("-");
                            row.addView(text2);
                        }

                        if (!TextUtils.isEmpty(String.valueOf(amt))) {
                            text3.setText(String.valueOf(amt*lost_v));
                            row.addView(text3);
                        } else {
                            text3.setText("-");
                            row.addView(text3);
                        }

                        tlayout.addView(row);
                    }



                    Log.e("Max table", addname);
                    cur.moveToNext();
                }
            }
            scrollView.addView(tlayout);
            tableScore.addView(scrollView);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }


//        TableRow row = new TableRow(getApplicationContext());
//        TableLayout tlayout = new TableLayout(getApplicationContext());
//        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.33f);
//        tlayout.addView(row);
//        for (int i = 0; i < 25; i++) {
//            row = new TableRow(getApplicationContext());
//            TextView text1 = new TextView(getApplicationContext());
//            TextView text2 = new TextView(getApplicationContext());
//            TextView text3 = new TextView(getApplicationContext());
//
//
//            text1.setLayoutParams(lp);
//            text1.setGravity(Gravity.CENTER);
//            text1.setTextColor(Color.BLACK);
//            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//
//            text2.setLayoutParams(lp);
//            text2.setGravity(Gravity.CENTER);
//            text2.setTextColor(Color.BLACK);
//            text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//
//            text3.setLayoutParams(lp);
//            text3.setGravity(Gravity.CENTER);
//            text3.setTextColor(Color.BLACK);
//            text3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//
//            text1.setText("adname");
//            row.addView(text1);
//            text2.setText("missed");
//            row.addView(text2);
//            text3.setText("lost");
//            row.addView(text3);
//            tlayout.addView(row);
//        }
//        scrollView.addView(tlayout);
//        tableScore.addView(scrollView);

    }

    private void setEarnedTableScore() {



        int lost_views=-1;

        ScrollView scrollView = new ScrollView(getApplicationContext());
        TableRow headerRow = (TableRow) findViewById(R.id.table_settings_row_one);
        tableScore.removeAllViews();
        TextView firstTextView = (TextView) headerRow.getChildAt(0);
        TextView secondTextView = (TextView) headerRow.getChildAt(1);
        TextView thirdTextView = (TextView) headerRow.getChildAt(2);
        secondTextView.setText(R.string.title_views);
        thirdTextView.setText(R.string.title_earned);
        tableScore.addView(headerRow);
        TableRow row = new TableRow(getApplicationContext());
        TableLayout tlayout = new TableLayout(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.33f);
        tlayout.addView(row);






        try {
            Cursor cur = sqlhelper.getEarnedTableParams();
            if (cur == null) {
                Log.e("Earned table", "novalues");
            }
            else {
                cur.moveToFirst();
                while(!cur.isAfterLast()) {
                    String addname = cur.getString(0);
                    int cmp_id=cur.getInt(3);
                    int no_views = getFinalViewCount(cur.getInt(1), cmp_id);
                    Log.e("ACtual views",cur.getInt(1)+" "+no_views);
                    double amt = cur.getFloat(2);




                    row = new TableRow(getApplicationContext());
                    TextView text1 = new TextView(getApplicationContext());
                    TextView text2 = new TextView(getApplicationContext());
                    TextView text3 = new TextView(getApplicationContext());


                    text1.setLayoutParams(lp);
                    text1.setGravity(Gravity.CENTER);
                    text1.setTextColor(Color.BLACK);
                    text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                    text2.setLayoutParams(lp);
                    text2.setGravity(Gravity.CENTER);
                    text2.setTextColor(Color.BLACK);
                    text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                    text3.setLayoutParams(lp);
                    text3.setGravity(Gravity.CENTER);
                    text3.setTextColor(Color.BLACK);
                    text3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


                    if (!TextUtils.isEmpty(addname)) {
                        text1.setText(addname);
                        row.addView(text1);
                    } else {
                        text1.setText("-");
                        row.addView(text1);
                    }

                    if (!TextUtils.isEmpty(String.valueOf(no_views))) {
                        text2.setText(String.valueOf(no_views));
                        row.addView(text2);
                    } else {
                        text2.setText("-");
                        row.addView(text2);
                    }

                    if (!TextUtils.isEmpty(String.valueOf(amt))) {
                        text3.setText(String.valueOf(amt*no_views));
                        row.addView(text3);
                    } else {
                        text3.setText("-");
                        row.addView(text3);
                    }

                    tlayout.addView(row);

                    Log.e("Earned table", addname);
                    cur.moveToNext();
                }
            }
            scrollView.addView(tlayout);
            tableScore.addView(scrollView);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    private int getFinalViewCount(int actualViews, int cmp_id) {

        int views=0;

        try
        {
            Cursor cur = sqlhelper.getLostdata(cmp_id);
            if (cur == null) {
                Log.e("Max table", "novalues");
                views=actualViews;
            }
            else {
                cur.moveToFirst();
                while (!cur.isAfterLast()) {


                    int lost_v = cur.getInt(2);


                    if (lost_v > 0 && lost_v<=actualViews) {

                        views=actualViews-lost_v;

                    }
                    else
                    {
                        views=actualViews;
                    }


                    cur.moveToNext();
                }
            }
        }
        catch (Exception e)
        {

        }


        return views;
    }

    public String callservice(String url, List<NameValuePair> url_data) {
        String jobj = null;
        try {

            JsonParser parser = new JsonParser();
            jobj = parser.getJsonFromUrlLogOut(url, url_data);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobj;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_settings, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    //test change for commit
}
