package xyz.doikki.dkplayer.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static Toast mtoast;

    public static void ShowMsg(Context context,String msg){
        if(mtoast == null){
            mtoast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else{
            mtoast.setText(msg);
        }
        mtoast.show();
    }
}
