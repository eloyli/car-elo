package mph.trunksku.apps.myssh.logger;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.widget.*;
import java.text.*;
import java.util.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.util.*;

/** Simple TextView which is used to output log data received through the LogNode interface.
 */
public class LogView implements LogNode
{

	private ArrayAdapter<Spanned> adapter;
    public static ArrayList<Spanned> arrayList;

	private Context mContext;

	class Takbo implements Runnable {
        private final StringBuilder outputBuilder;

		private LogView mLogView;

        Takbo(LogView logView, StringBuilder stringBuilder) {
            mLogView = logView;
			outputBuilder = stringBuilder;
        }

        @Override
        public void run() {
            mLogView.addLog(outputBuilder.toString());
        }
    }
	
    public LogView(Context context, ListView lv) {
        mContext = context;
        arrayList = new ArrayList();
        adapter = new ArrayAdapter(context, R.layout.layout_logtext, arrayList);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
		addLog(new StringBuffer().append("Running on ").append(Build.BRAND).append(" ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(") ").append(Build.MANUFACTURER).append(", Android API ").append(Build.VERSION.SDK).toString());
		addLog("OpenSSL 1.1.0c 26 Feb 2019");
		addLog("Application version: "+Utils.vb());
        
    }
	
    

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {


        String priorityStr = null;

        // For the purposes of this View, we want to print the priority as readable text.
        switch(priority) {
            case android.util.Log.VERBOSE:
                priorityStr = "VERBOSE";
                break;
            case android.util.Log.DEBUG:
                priorityStr = "DEBUG";
                break;
            case android.util.Log.INFO:
                priorityStr = "INFO";
                break;
            case android.util.Log.WARN:
                priorityStr = "WARN";
                break;
            case android.util.Log.ERROR:
                priorityStr = "ERROR";
                break;
            case android.util.Log.ASSERT:
                priorityStr = "ASSERT";
                break;
            default:
                break;
        }

        // Handily, the Log class has a facility for converting a stack trace into a usable string.
        String exceptionStr = null;
        if (tr != null) {
            exceptionStr = android.util.Log.getStackTraceString(tr);
        }

        // Take the priority, tag, message, and exception, and concatenate as necessary
        // into one usable line of text.
        final StringBuilder outputBuilder = new StringBuilder();

        String delimiter = "\t";
        appendIfNotNull(outputBuilder, priorityStr, delimiter);
        appendIfNotNull(outputBuilder, tag, delimiter);
        appendIfNotNull(outputBuilder, msg, delimiter);
        appendIfNotNull(outputBuilder, exceptionStr, delimiter);

        // In case this was originally called from an AsyncTask or some other off-UI thread,
        // make sure the update occurs within the UI thread.
        ((Activity) mContext).runOnUiThread( (
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Display the text we just generated within the LogView.
				new Takbo(LogView.this, outputBuilder).run();
				//addLog(outputBuilder.toString());
			}
		})));

        if (mNext != null) {
            mNext.println(priority, tag, msg, tr);
        }
    }

    public LogNode getNext() {
        return mNext;
    }

    public void setNext(LogNode node) {
        mNext = node;
    }

    /** Takes a string and adds to it, with a separator, if the bit to be added isn't null. Since
     * the logger takes so many arguments that might be null, this method helps cut out some of the
     * agonizing tedium of writing the same 3 lines over and over.
     * @param source StringBuilder containing the text to append to.
     * @param addStr The String to append
     * @param delimiter The String to separate the source and appended strings. A tab or comma,
     *                  for instance.
     * @return The fully concatenated String as a StringBuilder
     */
    private StringBuilder appendIfNotNull(StringBuilder source, String addStr, String delimiter) {
        if (addStr != null) {
            if (addStr.length() == 0) {
                delimiter = "";
            }

            return source.append(addStr).append(delimiter);
        }
        return source;
    }

    // The next LogNode in the chain.
    LogNode mNext;

    /** Outputs the string as a new line of log data in the LogView. */
    /*public void appendToLog(String s) {
        append("\n" + s);
    }*/
	public void addLog(String str) {
		//new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date())
		Calendar calendar= Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        if (str.contains("\n")) {
            String[] split = str.split("\n");
            for (String str2 : split) {
                arrayList.add(Html.fromHtml(new StringBuffer().append("[").append(dateFormat.format(calendar.getTime())).append("] ").append(str2).toString()));
				adapter.notifyDataSetChanged();
			}
            return;
        }
		arrayList.add(Html.fromHtml(new StringBuffer().append("[").append(dateFormat.format(calendar.getTime())).append("] ").append(str).toString()));
		adapter.notifyDataSetChanged();
    }
	

}
