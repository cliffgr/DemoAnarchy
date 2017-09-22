package wappier.anarchy.reciever;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import wappier.anarchy.BuildConfig;
import wappier.anarchy.constants.Constant;
import wappier.anarchy.sharepref.SharedPreferenceHelper;

/**
 * Created by ka on 21/9/2017.
 */

public class TestReceiver extends BroadcastReceiver {
    static final String WAP = "WappierBrodCast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(WAP, "onReceive()" + BuildConfig.APPLICATION_ID + " called with: " + "context = [" + context + "], intent = [" + intent + "]");
        if (intent.getAction().equals(Constant.SEND_REQUEST_ID)) {
            Log.e(WAP, "AKOUSA :" + BuildConfig.APPLICATION_ID);
            if (TextUtils.isEmpty(SharedPreferenceHelper.getSharedPreferenceString(context, Constant.UUID, ""))) {
                Log.e(WAP, "I DONT HAVE UUID :"+BuildConfig.APPLICATION_ID);
            } else {
                Log.e(WAP, "I HAVE UUID :"+BuildConfig.APPLICATION_ID);
                Intent intent1 = new Intent(Constant.RESPONSE_REQUEST_ID);
                intent1.putExtra(Constant.UUID, SharedPreferenceHelper.getSharedPreferenceString(context, Constant.UUID, ""));
                sendImplicitBroadcast(context, intent1);
            }
        } else if (intent.getAction().equals(Constant.RESPONSE_REQUEST_ID)) {
            Log.e(WAP, "Message : AKOUSA " + BuildConfig.APPLICATION_ID);
            Log.e(WAP,"ELAVA UUID"+ intent.getStringExtra(Constant.UUID));
            SharedPreferenceHelper.setSharedPreferenceString(context,Constant.UUID,intent.getStringExtra(Constant.UUID));
        }
    }

    private void sendImplicitBroadcast(Context ctx, Intent i) {
        PackageManager pm = ctx.getPackageManager();
        List<ResolveInfo> matches = pm.queryBroadcastReceivers(i, 0);
        if (!matches.isEmpty() && matches.size() > 1) {
            for (ResolveInfo resolveInfo : matches) {
                if (!resolveInfo.activityInfo.applicationInfo.packageName.equals(BuildConfig.APPLICATION_ID)) {
                    Intent explicit = new Intent(i);
                    ComponentName cn = new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name);
                    explicit.setComponent(cn);
                    ctx.sendBroadcast(explicit);
                }
            }
        }
    }
}
