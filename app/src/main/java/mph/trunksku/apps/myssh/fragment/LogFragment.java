package mph.trunksku.apps.myssh.fragment;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.view.*;
import android.widget.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.*;
import mph.trunksku.apps.myssh.logger.*;
import mph.trunksku.apps.myssh.util.*;

public class LogFragment extends Fragment 
{

	private SharedPreferences sp;

    private static LogView mLogView;
    private LinearLayout mScrollView;

	private View mView;

	private ListView mLogListView;

	@Override
    public void onCreate(Bundle bundle)
	{
        super.onCreate(bundle);
		sp = ApplicationBase.getSharedPreferences();
        setHasOptionsMenu(true);
	}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_log, container, false);
		mLogListView = (ListView) mView.findViewById(R.id.logView);
	 	mLogView = new LogView(getActivity(), mLogListView);
        mLogListView.setClickable(true);
        mLogListView.setFocusable(true);
		mLogListView.setStackFromBottom(false);
		mLogListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		((FloatingActionButton) mView.findViewById(R.id.clearLogs)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					LogView.arrayList.clear();
					addLog(new StringBuffer().append("Running on ").append(Build.BRAND).append(" ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(") ").append(Build.MANUFACTURER).append(", Android API ").append(Build.VERSION.SDK).toString());
					addLog("OpenSSL 1.1.0c 26 Feb 2019");
					addLog("Application version: " + Utils.vb());
					addLog("Log Cleared");
				}
			});
		return this.mView;
    }

    public static LogView getLogView()
	{
        return mLogView;
    }

	/*@Override
	 public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
	 {
	 menuInflater.inflate(R.menu.menu_log, menu);
	 super.onCreateOptionsMenu(menu, menuInflater);
	 }

	 @Override
	 public boolean onOptionsItemSelected(MenuItem menuItem)
	 {
	 String data = LogView.arrayList.toString().replace(", ", "\n");
	 switch (menuItem.getItemId())
	 {
	 case R.id.xCopy:
	 copyToClipboard(getActivity(), data);
	 return true;
	 case R.id.xClear:
	 LogView.arrayList.clear();
	 addLog(new StringBuffer().append("Running on ").append(Build.BRAND).append(" ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(") ").append(Build.MANUFACTURER).append(", Android API ").append(Build.VERSION.SDK).toString());
	 addLog("Application version: " + Utils.vb());
	 addLog("Log Cleared");
	 return true;
	 case R.id.xShare:
	 Intent shareIntent = new Intent(Intent.ACTION_SEND);
	 shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 shareIntent.setType("text/plain");
	 shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
	 startActivity(shareIntent); 
	 return true;
	 default:
	 return super.onOptionsItemSelected(menuItem);
	 }
	 }*/

	public static void copyToClipboard(Context context, String str)
	{
        if (android.os.Build.VERSION.SDK_INT >= 11)
		{
            ((android.content.ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("CDCEVPN-log", str));
        }
		else
		{
            ((android.text.ClipboardManager) context.getSystemService("clipboard")).setText(str);
        }
        Toast.makeText(context, "Copy to Clipboard", 0).show();
    }

	public static void clear()
	{
		LogView.arrayList.clear();
		addLog(new StringBuffer().append("Running on ").append(Build.BRAND).append(" ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(") ").append(Build.MANUFACTURER).append(", Android API ").append(Build.VERSION.SDK).toString());
		addLog("OpenSSL 1.1.0c 26 Feb 2019");
		addLog("Application version: " + Utils.vb());
	}

	public static void addLog(String str)
	{
		Log.i("EasySSH", str);
	}

	public static void addLog(String tag, String str)
	{
		Log.i(tag, str);
	}

	public void onStart()
	{
        super.onStart();
        LogWrapper logWrapper = new LogWrapper();
		Log.setLogNode(logWrapper);
		MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
		logWrapper.setNext(msgFilter);
		// this.logFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_window);
		msgFilter.setNext(getLogView());
		
	}

	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		
	}

}

