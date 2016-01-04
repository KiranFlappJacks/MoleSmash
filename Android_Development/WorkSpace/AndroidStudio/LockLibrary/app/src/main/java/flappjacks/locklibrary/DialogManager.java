package flappjacks.locklibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by kiran on 1/12/15.
 */
public class DialogManager {


    public static void showCustCancelDialog(final Context context,String msg) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_msg_dialog);
        dialog.setCancelable(false);
        TextView text = (TextView) dialog.findViewById(R.id.textDialog);
        text.setText(msg);
        dialog.show();

        Button declineButton = (Button) dialog
                .findViewById(R.id.declineButton);

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                ((Activity) context).finish();
            }
        });
    }

    public static void showCustDialog(final Context context,String msg) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_msg_dialog);
        dialog.setCancelable(false);
        TextView text = (TextView) dialog.findViewById(R.id.textDialog);
        text.setText(msg);
        dialog.show();

        Button declineButton = (Button) dialog
                .findViewById(R.id.declineButton);

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
//                ((Activity) context).finish();
            }
        });
    }
}
