package flappjacks.locklibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kiran on 29/11/15.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public DatabaseHelper(Context context)
    {
        super(context, "C4CLibDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("TAG", "Helper started");

        String allquery = "create table if not exists AdsCampain(campid  integer primary key,addname text not null,amount_per_view real not null,total_views_allowed integer,start_date text not null,end_date text not null,start_time integer not null,end_time integer not null,ad_file text not null,isactive text not null,noofviews integer,ad_type text not null,last_seen text not null,balance real not null,website text,contact text)";
        String transactionquery="create table if not exists AdsTransaction(campid integer not null,amount_per_view real not null,last_seen text primary key not null)";
        String lostData="create table if not exists lostData(campid integer primary key,addname text not null,amount_per_view real not null,lost_views integer not null)";
        String pausedData="create table if not exists pausedData(campid integer primary key)";

        db.execSQL(allquery);
        db.execSQL(transactionquery);
        db.execSQL(lostData);
        db.execSQL(pausedData);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //function to save data
    public void saveAllData(int cmpid, String addname, double amount_pview,
                            int totalvallowed, String start_date, String end_date, int start_time, int endtime,
                            String addfile, boolean isactive, int noofviews, String ad_type, String last_seen, double balance, String website, String contact) {
        Log.i("TAG", "creating records started....");
        db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("campid", cmpid);
        values.put("addname", addname);
        values.put("amount_per_view", amount_pview);
        values.put("total_views_allowed", totalvallowed);
        values.put("start_date", start_date);
        values.put("end_date", end_date);
        values.put("start_time", start_time);
        values.put("end_time", endtime);
        values.put("ad_file", addfile);
        values.put("isactive", isactive);
        values.put("noofviews", noofviews);
        values.put("ad_type", ad_type);
        values.put("last_seen", last_seen);
        values.put("balance", balance);
        values.put("website", website);
        values.put("contact", contact);
        db.insert("AdsCampain", null, values);
        Log.i("TAG", "Records has been saved");

    }

    public Cursor getLostdata(int cmp_id)
    {
        String query = "select addname,amount_per_view,lost_views from LostData where campid ="+cmp_id;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        return cursor;
    }

    public Cursor getAllLostdata()
    {
        String query = "select addname,amount_per_view,lost_views from LostData";
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        return cursor;
    }


    public void updateLostViews(int camp_id,int num_views)
    {

        Log.i("TAG", "updating num of views");
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("campid", camp_id);
        values.put("lost_views", num_views);

        db.update("LostData", values, "campid" + " = ?",
                new String[] {String.valueOf(camp_id)});
        Log.i("TAG", "Updated");

    }



    public void saveLostData(int cmpid,String adname, double amount_pview,int lost_views) {
        Log.i("TAG", "creating records started....");
        db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("campid", cmpid);
        values.put("addname",adname);
        values.put("amount_per_view", amount_pview);
        values.put("lost_views", lost_views);
        db.insert("LostData", null, values);
        Log.i("Transaction success", "Records has been saved");

    }


    public void saveTransactionData(int cmpid, double amount_pview,String last_seen) {
        Log.i("TAG", "creating records started....");
        db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("campid", cmpid);
        values.put("amount_per_view", amount_pview);
        values.put("last_seen", last_seen);
        db.insert("AdsTransaction", null, values);
        Log.i("Transaction success", "Records has been saved");

    }

    public Cursor getActiveAds(String ad_type, String excludeList)
    {
        String query = "select ad_file,campid,total_views_allowed,end_date,Min(noofviews),start_time,amount_per_view,addname,website,contact from AdsCampain where ad_type = '"+ad_type+"' and isactive=1 and campid not in "+excludeList;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        return cursor;
    }

    public Cursor getSyncParams()
    {
        String query = "select campid,balance from AdsCampain";
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        return cursor;
    }

    public Cursor getEarnedTableParams()
    {
        String query = "select addname,noofviews,amount_per_view,campid from AdsCampain";
        Cursor cursor = getReadableDatabase().rawQuery(query, null);

        Log.e("earned table","earned");
        return cursor;
    }

    public Cursor getMaxTableParams()
    {
        String query = "select addname,noofviews,amount_per_view,total_views_allowed from AdsCampain";
        Cursor cursor = getReadableDatabase().rawQuery(query, null);

        Log.e("Total points table","total points");
        return cursor;
    }

    public Cursor getTransactionParams()
    {
        String query = "select campid,amount_per_view,last_seen from AdsTransaction";
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        return cursor;
    }

    public void updateAdStatus(int camp_id,boolean is_active)
    {

        Log.i("TAG", "updating ad status!");
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("campid", camp_id);
        values.put("isactive", is_active);

        db.update("AdsCampain", values, "campid" + " = ?",
                new String[] { String.valueOf(camp_id) });
        Log.i("TAG", "Updated");

    }

    public void updateNumViews(int camp_id,int num_views,String cur_date)
    {

        Log.i("TAG", "updating num of views");
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("campid", camp_id);
        values.put("noofviews", num_views);
        values.put("last_seen",cur_date);

        db.update("AdsCampain", values, "campid" + " = ?",
                new String[] {String.valueOf(camp_id)});
        Log.i("TAG", "Updated");

    }


    public void deleteTransactionData()
    {

        db=getWritableDatabase();
        Log.e("Delete data", "ads transaction deleted!");
        db.delete("AdsTransaction", null, null);
        Log.e("Delete data", "ads transaction deleted!");
    }


    public void savePausedData(int cmpid) {
        Log.i("TAG", "creating records started....");
        db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("campid", cmpid);
        db.insert("pausedData", null, values);
        Log.i("Paused ads success", "Records has been saved");

    }


    public Cursor getPausedData()
    {
        String query = "select campid from pausedData";
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        return cursor;
    }

    public void deletePausedData()
    {

        db=getWritableDatabase();
        db.delete("pausedData", null, null);
        Log.e("Delete paused data", "Paused deleted!");
    }
}