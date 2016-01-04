package flappjacks.locklibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity {


    String API_SECRET_KEY = "", EMAIL = "", YELDI_USERID = "", DOB = "", GENDER = "", COUNTRY = "", STATE = "", CITY = "", IMEI = "", DEVICE_NAME = "", DEVICE_MODEL = "", ANDROID_VERSION = "", SECURE_KEY = "";
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_register);

        //============== loading dialog init ===============
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            API_SECRET_KEY = extras.getString("api_secret_key");
            EMAIL = extras.getString("email");
            YELDI_USERID = extras.getString("yeldi_userid");
            DOB = extras.getString("dob");
            GENDER = extras.getString("gender");
            COUNTRY = extras.getString("country");
            STATE = extras.getString("state");
            CITY = extras.getString("city");
            IMEI = extras.getString("imei");
            DEVICE_NAME = extras.getString("device_name");
            DEVICE_MODEL = extras.getString("device_model");
            ANDROID_VERSION = extras.getString("android_version");

            if (!TextUtils.isEmpty(API_SECRET_KEY) && !TextUtils.isEmpty(EMAIL) && !TextUtils.isEmpty(YELDI_USERID) && !TextUtils.isEmpty(DOB) && !TextUtils.isEmpty(GENDER) && !TextUtils.isEmpty(COUNTRY) && !TextUtils.isEmpty(STATE) && !TextUtils.isEmpty(CITY) && !TextUtils.isEmpty(DEVICE_NAME) && !TextUtils.isEmpty(DEVICE_MODEL) && !TextUtils.isEmpty(ANDROID_VERSION)) {
                String msg = EMAIL + YELDI_USERID + DOB + GENDER + COUNTRY + STATE + CITY + IMEI + DEVICE_NAME + DEVICE_MODEL + ANDROID_VERSION + API_SECRET_KEY;
                try {
                    SECURE_KEY = SHA1Generator.sha1(msg);

                    if (!TextUtils.isEmpty(SECURE_KEY)) {
                        if (NetworkUtil.haveNetworkConnection(getApplicationContext())) {
                            List<NameValuePair> url_parameters = setupCall();
                            callwebservice cs = new callwebservice(URLDetails.URL_REGISTER, url_parameters);
                            cs.execute();
                        }
                        else
                        {
                            DialogManager.showCustCancelDialog(RegisterActivity.this,"No internet access available!");
                        }
                    }

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }

        }
        else
        {
            DialogManager.showCustCancelDialog(RegisterActivity.this, "Error in registration!");
        }


    }

    private List<NameValuePair> setupCall() {


        List<NameValuePair> url_parameters = new ArrayList<NameValuePair>();
        url_parameters.clear();
        url_parameters.add(new BasicNameValuePair("email", EMAIL));
        url_parameters.add(new BasicNameValuePair("yeldi_userid", YELDI_USERID));
        url_parameters.add(new BasicNameValuePair("dob", DOB));
        url_parameters.add(new BasicNameValuePair("gender", GENDER));
        url_parameters.add(new BasicNameValuePair("country", COUNTRY));
        url_parameters.add(new BasicNameValuePair("state", STATE));
        url_parameters.add(new BasicNameValuePair("city", CITY));
        url_parameters.add(new BasicNameValuePair("imei", IMEI));
        url_parameters.add(new BasicNameValuePair("device_name", DEVICE_NAME));
        url_parameters.add(new BasicNameValuePair("device_model", DEVICE_MODEL));
        url_parameters.add(new BasicNameValuePair("android_version", ANDROID_VERSION));
        url_parameters.add(new BasicNameValuePair("secure_key", SECURE_KEY));

        return url_parameters;
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

            try
            {
                    JSONObject obj = new JSONObject(result.toString());
                    JSONObject register_response = obj.getJSONObject("register_response");
                    String errorCode = register_response.getString("errorCode");
                    if(errorCode.equals("1000")||errorCode.equals("1001"))
                    {
                        String user_id=register_response.getString("userid");
                        String api_key=register_response.getString("apikey");
                        if(!TextUtils.isEmpty(user_id)&&!TextUtils.isEmpty(api_key))
                        {
                            dialog.cancel();
                            Intent intent=new Intent(RegisterActivity.this,SettingsActivity.class);
                            intent.putExtra("userid",user_id);
                            intent.putExtra("apikey",api_key);
                            intent.putExtra("api_secret_key",API_SECRET_KEY);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            DialogManager.showCustCancelDialog(RegisterActivity.this, "Error in registration!");
                        }


                    }
                    else
                    {
                        DialogManager.showCustCancelDialog(RegisterActivity.this, "Error in registration!");
                    }


                Log.e("Register json",result);

            }
            catch (Exception e) {

                e.printStackTrace();
                DialogManager.showCustCancelDialog(RegisterActivity.this, "Error in registration!");
            }

//            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

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

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_register, menu);
//        return true;
//    }

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
}
