package mph.trunksku.apps.myssh.async;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v7.app.*;
import android.text.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.db.*;
import mph.trunksku.apps.myssh.fragment.*;
import mph.trunksku.apps.myssh.service.*;
import mph.trunksku.apps.myssh.util.*;
import org.json.*;

import android.support.v7.app.AlertDialog;

public class ApiAsync extends AsyncTask<String, String, String>
{
    private HttpURLConnection uRLConnection;

    private InputStream is;

    private BufferedReader buffer;

    private ProgressDialog pd;

    private Context mContext;

    private SharedPreferences sp;

    private String TAG;

	private DataBaseHelper db;

	private SharedPreferences defsp;

    public ApiAsync(Context c){
        mContext = c;
		db = new DataBaseHelper(c);
        sp = ApplicationBase.getSharedPreferences();
		defsp = ApplicationBase.getDefSharedPreferences();
    }

    @Override
    protected void onPreExecute()
    {
        // TODO: Implement this method
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setMessage("Updating, Please wait...");
        pd.show();
    }

    @Override
    protected String doInBackground(String[] p1)
    {
        // TODO: Implement this method
        try {
			String api = defsp.getString("custom_update_url", "");
			if(!api.startsWith("http")){
				api = new StringBuilder().append("http://").append(defsp.getString("custom_update_url", "")).toString();
			}
            URL url = new URL("http://"+api);
            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setRequestMethod("GET");
            is = uRLConnection.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            return strBuilder.toString();
        } catch (Exception e) {
            return "error";
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ignored) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        // TODO: Implement this method
        pd.dismiss();
        if(!TextUtils.isEmpty(result)){
            if(result.equals("error")){
                Toast.makeText(mContext, "Can't Update, Maybe try again later!", 0).show();
            }else{
                try{
                    JSONObject obj = new JSONObject(XxTea.decryptBase64StringToString(result, "a3e4782c"));
                    if(obj.getInt("UpdateVersion") == sp.getInt("CurrentConfigVersion", 0)){
                        Toast.makeText(mContext, "Already used the config version.", 0).show();
                    }else{
                        db.updateData("1", result);
                        sp.edit().putInt("ServerSpin0", 0).commit();
						sp.edit().putInt("ServerSpin1", 0).commit();
						sp.edit().putInt("NetworkSpin0", 0).commit();
						sp.edit().putInt("NetworkSpin1", 0).commit();
						AlertDialog.Builder adb = new AlertDialog.Builder(mContext)
                            .setCancelable(false)
                            .setTitle("What's New");
                        StringBuffer sb = new StringBuffer();
                        JSONArray jarr = obj.getJSONArray("ReleaseNotes");
                        for(int i=0;i<jarr.length();i++)
                        {
                            sb.append(jarr.getString(i)+"\n");
                        }
                        adb.setMessage(sb.toString())
                            .setPositiveButton("Update Now", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface p1, int p2){
                                    // TODO: Implement this method
                                    if(OreoService.isRunning){
                                        mContext.stopService(new Intent(mContext, OreoService.class));
                                    }
									HomeFragment.upRefresh();
                                    //mRestart(mContext);
                                }
                            })
                            .setNegativeButton("Update Later", null)
                            .create().show();
                        sp.edit().putInt("CurrentConfigVersion", obj.getInt("UpdateVersion")).commit();
                    }
                }catch(Exception e){
                    LogFragment.addLog(e.getMessage());
                }
            }
        }
    }

    public void mRestart(Context c) {
        try {
            if (c != null) {
                PackageManager pm = c.getPackageManager();
                if (pm != null) {
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                        c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        int mPendingIntentId = android.os.Process.myPid();
                        PendingIntent mPendingIntent = PendingIntent
                            .getActivity(c, mPendingIntentId, mStartActivity,
                                         PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }
}

