package wappier.anarchy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import wappier.anarchy.sharepref.SharedPreferenceHelper;
import wappier.anarchy.constants.Constant;
import wappier.anarchy.utils.AdvertisingIdClient;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView=findViewById(R.id.test_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),Constant.UUID,""));
                Intent intent= new Intent(Constant.SEND_REQUEST_ID);
                sendImplicitBroadcast(getApplicationContext(),intent);
            }
        });
    }

    private void sendImplicitBroadcast(Context ctx, Intent i) {
        PackageManager pm = ctx.getPackageManager();
        List<ResolveInfo> matches = pm.queryBroadcastReceivers(i, 0);
        if (!matches.isEmpty() && matches.size() > 1) {
            for (ResolveInfo resolveInfo : matches) {
                if (!resolveInfo.activityInfo.applicationInfo.packageName.equals(BuildConfig.APPLICATION_ID)) {
                    Intent explicit = new Intent(i);
                    ComponentName cn = new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name);
                    Log.d(TAG,"packageName : "+resolveInfo.activityInfo.applicationInfo.packageName);
                    Log.d(TAG,"name : "+resolveInfo.activityInfo.name);
                    explicit.setComponent(cn);
                    ctx.sendBroadcast(explicit);
                }
            }
        } else {
            Log.e(TAG, "I am the First app... I will Create new ID");
             new Thread(new Runnable() {
                public void run() {
                    try {
                        AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                        SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),Constant.UUID,adInfo.getId());
                        Log.d(TAG, "UUID : "+SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),Constant.UUID,adInfo.getId()));
                    } catch (Exception e) {
                        Log.e(TAG,"Error : "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
