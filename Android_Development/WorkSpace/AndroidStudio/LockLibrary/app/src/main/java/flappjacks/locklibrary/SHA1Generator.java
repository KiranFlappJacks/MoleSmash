package flappjacks.locklibrary;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kiran on 25/11/15.
 */
public class SHA1Generator {


    public static String sha1(String input) throws NoSuchAlgorithmException {

        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        Log.e("input", input);
        Log.e("SHA1",sb.toString());
        return sb.toString();

    }
}
