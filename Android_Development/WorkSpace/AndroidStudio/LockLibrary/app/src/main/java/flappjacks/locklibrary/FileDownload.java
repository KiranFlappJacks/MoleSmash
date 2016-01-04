package flappjacks.locklibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by kiran on 29/11/15.
 */
public class FileDownload extends AsyncTask<Void, Void, String> {

    String image_url;
    DatabaseHelper sqlhelper;
    Context ctx;
    int campid;

    public FileDownload(int campid,String image_url,Context ctx) {
        this.image_url=image_url;
        this.ctx=ctx;
        this.campid=campid;
    }

    @Override
    protected String doInBackground(Void... params) {


        int count;
        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
            Log.e("step 1", "step 1");
            URL url = new URL(image_url);
            URLConnection conection = url.openConnection();
            conection.connect();
            Log.e("step 2", "step 2");
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/AppMedia");
            if (!folder.exists())
                folder.mkdir();
            String filename = image_url
                    .substring(image_url.lastIndexOf("/") + 1);
            OutputStream output = new FileOutputStream(folder + "/" + filename);

            String final_file = Environment.getExternalStorageDirectory().toString()
                    + "/AppMedia/" + filename;

            byte data[] = new byte[16*1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                // publishProgress(""+(int)((total*100)/lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

            updateDB();
//            Toast.makeText(getApplicationContext(), "Download complete!",
//                    Toast.LENGTH_SHORT).show();
            Log.e("step 3", "step 3");
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
//            Toast.makeText(getApplicationContext(), e.getMessage().toString(),
//                    Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    private void updateDB() {

        sqlhelper=new DatabaseHelper(ctx);
        sqlhelper.updateAdStatus(campid,true);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.e("FileDownload","complete");
    }


}
