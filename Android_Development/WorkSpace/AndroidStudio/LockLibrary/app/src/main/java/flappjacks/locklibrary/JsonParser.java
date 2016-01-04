package flappjacks.locklibrary;

import android.util.Log;



import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiran on 19/11/15.
 */
public class JsonParser {

    StringBuilder builder = new StringBuilder();
    JSONObject jobj;

    public String getJsonFromUrl(String url, String ossid,String status)
            throws JSONException {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        String json = null;

        try {

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//			parameters.add(new BasicNameValuePair("ossid", ossid));
//			parameters.add(new BasicNameValuePair("status", status));
            post.setEntity(new UrlEncodedFormEntity(parameters));
            HttpResponse response = client.execute(post);
            Log.i("RES", response.toString());
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                // System.out.println(line);
                builder.append(line);
            }
            json = builder.toString();
            Log.i("MSG", json);
            jobj = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobj.toString();

    }

    public String getJsonFromUrlLogOut(String url,List<NameValuePair> url_parameters)
            throws JSONException {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        String json = null;

        try {

//            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//			parameters.add(new BasicNameValuePair("ossid", ossid));
            post.setEntity(new UrlEncodedFormEntity(url_parameters));
            HttpResponse response = client.execute(post);
            Log.i("RES", response.toString());
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                // System.out.println(line);
                builder.append(line);
            }
            json = builder.toString();
            Log.i("MSG", json);
            jobj = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobj.toString();

    }

}
