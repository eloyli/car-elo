package mph.trunksku.apps.myssh;
import android.content.*;
import android.os.*;
import android.preference.*;
import de.blinkt.openvpn.core.*;
import mph.trunksku.apps.myssh.model.*;
import mph.trunksku.apps.myssh.util.*;
import java.io.*;
import mph.trunksku.apps.myssh.logger.*;
import android.app.*;

public class ApplicationBase
{

	private static ConfigImpl utils;

	private static SharedPreferences sharedPreferences;

	private static SharedPreferences sharedPreference;

	private static Context mContext;
	
	public void init(Context c)
	{
		
		
		
		// TODO: Implement this method
		//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		 //createNotificationChannels();
		//if (BuildConfig.DEBUG)
		//     enableStrictModes();

		/* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		 AppRestrictions.getInstance(this).checkRestrictions(this);
		 }*/
		File dir = c.getDir("mph_dex", 0);
        File dir2 = c.getDir("mph_odex", 0);
        String str = dir.getAbsolutePath() + "/classes.dex";
        String str2 = dir2.getAbsolutePath() + "/classes.dex";
        File file = new File(str);
        if (file.exists() && !file.delete()) {
            System.exit(0);
        }
        dir = new File(str2);
        if (dir.exists() && !dir.delete()) {
            System.exit(0);
        }
		 mContext = c;
		PRNGFixes.apply();
		VPNLog.initLogCache(c.getCacheDir());
		StatusListener mStatus = new StatusListener();
        mStatus.init(c);
		
		sharedPreferences = new SecurePreferences(c);
		sharedPreference = PreferenceManager.getDefaultSharedPreferences(c);
		utils = new ConfigImpl();
	}

	public static boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	private void enableStrictModes() {
        StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build();
        StrictMode.setVmPolicy(policy);

    }

   /* @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(OreoService.NOTIFICATION_CHANNEL_BG_ID, getString(R.string.channel_name_background), 1);
        notificationChannel.setDescription(getString(R.string.channel_description_background));
        notificationChannel.enableLights(false);
        notificationChannel.setLightColor(Color.DKGRAY);
		notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(notificationChannel);
        NotificationChannel notificationChannel2 = new NotificationChannel(OreoService.NOTIFICATION_CHANNEL_NEWSTATUS_ID, getString(R.string.channel_name_status), 3);
        notificationChannel2.setDescription(getString(R.string.channel_description_status));
        notificationChannel2.enableLights(true);
        notificationChannel2.setLightColor(Color.BLUE);
		notificationChannel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(notificationChannel2);
    }*/
	
	public static void ExtractGo() throws IOException {
        String pdnsd_binary = new StringBuilder().append(mContext.getFilesDir().getAbsolutePath()).append("/go").toString();
        InputStream inputStream = Build.VERSION.SDK_INT >= 21 ? mContext.getAssets().open("bin/pie/go") : mContext.getAssets().open("bin/go");
        if (inputStream == null) {
            return;
        }
        File pdnsd_file = new File(pdnsd_binary);
		if(!pdnsd_file.exists()){
			FileOutputStream fileOutputStream = new FileOutputStream(pdnsd_file);
			WriteTo(inputStream, fileOutputStream);
			pdnsd_file.setExecutable(true, true);
			pdnsd_file.setReadable(true, true);
			pdnsd_file.setWritable(true, true);
			fileOutputStream.close();
			inputStream.close();
		}

		runCommand("chmod 771 " + mContext.getFilesDir().getAbsolutePath());
        runCommand("chmod 700 " + mContext.getFilesDir().getAbsolutePath() + "/go");
		//runCommand("chmod 700 " + context.getFilesDir().getAbsolutePath() + "/pdnsd.sh");
    }
	
	public static void WriteTo(InputStream inputStream, FileOutputStream fileOutputStream) throws IOException {
        byte[] arrby = new byte[2048];
        int n;
        while ((n = inputStream.read(arrby, 0, 2048)) != -1) {
            fileOutputStream.write(arrby, 0, n);
        }
        return;
    }
	
	
	public static Context getContext() {
		return mContext;
	}
	
	public static Config getUtils()
    {
        return utils;
    }

	public static SharedPreferences getSharedPreferences()
    {
        return sharedPreferences;
    }
	
	public static SharedPreferences getDefSharedPreferences()
    {
        return sharedPreference;
    }
	
	public static Boolean isDarkTheme() {
		return sharedPreference.getBoolean("use_dark_theme", true);
	}
	
	public static boolean runCommand(String command)
	{
		java.lang.Process process = null;
		DataOutputStream os = null;
		try
		{
			process = Runtime.getRuntime().exec("sh");
			try
			{

				os = new DataOutputStream(process.getOutputStream());
				os.writeBytes(command + "\n");
				os.writeBytes("exit\n");
				os.flush();
				process.waitFor();
				return true;
			}
			catch (Exception e)
			{

				Log.d("", e.getMessage());
				return false;

			}
			finally
			{
				try
				{
					if (os != null)
					{
						os.close();
					}
					process.destroy();
				}
				catch (IOException e)
				{
					// nothing
				}
			}
		}
		catch (IOException e)
		{
			Log.d("", e.getMessage());
			//SnapData.getSnapData().addLog(e.getMessage());
			return false;

		}
		finally
		{
			try
			{
				if (os != null)
				{
					os.close();
				}
				process.destroy();
			}
			catch (Exception e)
			{
				// nothing
			}
		}

	}
}
