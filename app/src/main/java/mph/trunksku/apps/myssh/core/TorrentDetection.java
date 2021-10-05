package mph.trunksku.apps.myssh.core;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import com.shashank.sony.fancydialoglib.*;
import java.util.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.service.*;
import android.graphics.*;

public class TorrentDetection
{
	private Context context;

	private String[] items;

    private Timer timer;
	
	public TorrentDetection(Context c, String[] i) {
		context = c;
		items = i;
	}
	
	private boolean check(String uri)
	{
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try
		{
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
		catch (PackageManager.NameNotFoundException e)
		{
            app_installed = false;
        }
        return app_installed;
    }
	
   void check() {
		for (int i=0;i < items.length ;i++)
		{
			if(check(items[i])){
                timer.cancel();
				alert(items[i]);
				break;
			}
		}
	}
	
	public void init() {
		final Handler handler = new Handler();
	    timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run()
			{
				handler.post(new Runnable() {
						public void run()
						{
							check();
						}
					});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 3000);
	}
	
	void alert(String app) {
		if (OreoService.isRunning)
		{
			//context.stopService(new Intent(context, OreoService.class));
			context.startService(new Intent(context, OreoService.class).setAction("STOP"));
		}
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(app);
		new FancyAlertDialog.Builder(context).build()
			.setBackgroundColor(Color.parseColor("#F44336"))
			.setTitle("Malicious App Detected!")
			.setMessage("Please Uninstall your Torrent or Sniffing Application installed on your device to continue your subscription and enjoy our service... \n \n This Package name was identified as torrenting or sniffing is:\n \n • "+app)
			//.setMessage("1-" + torrentList[i])
			//.setPositiveButton("I Understand!", null)
			//.setAnimation(Animation.SLIDE)
			.isCancellable(false)
			.setIcon(R.mipmap.ic_info, Icon.Visible)
			.setPositiveButton("I UNDERSTAND!", new FancyAlertDialogListener() {
				@Override
				public void OnClick()
				{
					if (android.os.Build.VERSION.SDK_INT >= 21) {
						((Activity) context).finishAndRemoveTask();
					} else {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
					System.exit(0);
				}
			})
			.setAnimation(Animation.SLIDE)
			.isCancellable(false)
			.setIcon(R.mipmap.ic_info, Icon.Visible)
			.show();
	}
}
