package mph.trunksku.apps.myssh.logger;
import android.os.*;
import java.io.*;
import java.util.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.util.*;

public class VPNLog
{
	final static java.lang.Object readFileLock = new Object();
	
	private static final LinkedList<LogItem> logbuffer;
	private static LogFileHandler mLogFileHandler;
	private static HandlerThread mHandlerThread;
	static final int MAXLOGENTRIES = 1000;
	static boolean readFileLog = false;
    private static Vector<LogListener> logListener;
	static {
        logbuffer = new LinkedList<>();
        logListener = new Vector<>();
        logInformation();
    }
	
	public static void logException(LogLevel ll, String context, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        LogItem li;
        if (context != null) {
            li = new LogItem(ll, R.string.unhandled_exception_context, e.getMessage(), sw.toString(), context);
        } else {
            li = new LogItem(ll, R.string.unhandled_exception, e.getMessage(), sw.toString());
        }
        newLogItem(li);
    }

    public static void logException(Exception e) {
        logException(LogLevel.ERROR, null, e);
    }

    public static void logException(String context, Exception e) {
        logException(LogLevel.ERROR, context, e);
    }
	
	/*public static String getLastCleanLogMessage(Context c) {
        String message = mLaststatemsg;
        switch (mLastLevel) {
            case LEVEL_CONNECTED:
                String[] parts = mLaststatemsg.split(",");
               
                // Return only the assigned IP addresses in the UI
                if (parts.length >= 7) message = String.format(Locale.US, "%s %s", parts[1], parts[6]);
                break;
        }
        while (message.endsWith(",")) message = message.substring(0, message.length() - 1);
        String status = mLaststate;
        if (status.equals("NOPROCESS")) return message;
        if (mLastStateresid == R.string.state_waitconnectretry) {
            return c.getString(R.string.state_waitconnectretry, mLaststatemsg);
        }
        String prefix = c.getString(mLastStateresid);
        if (mLastStateresid == R.string.unknown_state) message = status + message;
        if (message.length() > 0) prefix += ": ";
        return prefix + message;
    }*/

    public static void initLogCache(File cacheDir) {
        mHandlerThread = new HandlerThread("LogFileWriter", Thread.MIN_PRIORITY);
        mHandlerThread.start();
        mLogFileHandler = new LogFileHandler(mHandlerThread.getLooper());
        Message m = mLogFileHandler.obtainMessage(LogFileHandler.LOG_INIT, cacheDir);
        mLogFileHandler.sendMessage(m);
    }

    public static void flushLog() {
        if (mLogFileHandler != null) mLogFileHandler.sendEmptyMessage(LogFileHandler.FLUSH_TO_DISK);
    }
	
	public synchronized static void logMessage(LogLevel level, String prefix, String message) {
        newLogItem(new LogItem(level, prefix + message));
    }

    public synchronized static void clearLog() {
        logbuffer.clear();
        logInformation();
        if (mLogFileHandler != null) mLogFileHandler.sendEmptyMessage(LogFileHandler.TRIM_LOG_FILE);
    }

    private static void logInformation() {
        String nativeAPI = "trst";
        try {
            //nativeAPI = NativeUtils.getNativeAPI();
        } catch (UnsatisfiedLinkError ignore) {
            nativeAPI = "error";
        }
		logbuffer.clear();
		
        //logInfo(R.string.mobile_info, Build.MODEL, Build.BOARD, Build.BRAND, Build.VERSION.SDK_INT, nativeAPI, Build.VERSION.RELEASE, Build.ID, Build.FINGERPRINT, "", "");
    	logInfo(new StringBuffer().append("Running on ").append(Build.BRAND).append(" ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(") ").append(Build.MANUFACTURER).append(", Android API ").append(Build.VERSION.SDK).toString());
		logInfo("OpenSSL 1.1.0c 26 Feb 2019");
		logInfo("Application version: "+Utils.vb());
	}

    public synchronized static void addLogListener(LogListener ll) {
        logListener.add(ll);
    }

    public synchronized static void removeLogListener(LogListener ll) {
        logListener.remove(ll);
    }
	
	synchronized public static LogItem[] getlogbuffer() {
        // The stoned way of java to return an array from a vector
        // brought to you by eclipse auto complete
        return logbuffer.toArray(new LogItem[logbuffer.size()]);
    }
	
	public static void logInfo(String message) {
        newLogItem(new LogItem(LogLevel.INFO, message));
    }

    public static void logDebug(String message) {
        newLogItem(new LogItem(LogLevel.DEBUG, message));
    }

    public static void logInfo(int resourceId, Object... args) {
        newLogItem(new LogItem(LogLevel.INFO, resourceId, args));
    }

    public static void logDebug(int resourceId, Object... args) {
        newLogItem(new LogItem(LogLevel.DEBUG, resourceId, args));
    }

    static void newLogItem(LogItem logItem) {
        newLogItem(logItem, false);
    }

    synchronized static void newLogItem(LogItem logItem, boolean cachedLine) {
        if (cachedLine) {
            logbuffer.addFirst(logItem);
        } else {
            logbuffer.addLast(logItem);
            if (mLogFileHandler != null) {
                Message m = mLogFileHandler.obtainMessage(LogFileHandler.LOG_MESSAGE, logItem);
                mLogFileHandler.sendMessage(m);
            }
        }
        if (logbuffer.size() > MAXLOGENTRIES + MAXLOGENTRIES / 2) {
            while (logbuffer.size() > MAXLOGENTRIES) logbuffer.removeFirst();
            if (mLogFileHandler != null) mLogFileHandler.sendMessage(mLogFileHandler.obtainMessage(LogFileHandler.TRIM_LOG_FILE));
        }
        //if (BuildConfig.DEBUG && !cachedLine && !BuildConfig.FLAVOR.equals("test"))
        //    Log.d("OpenVPN", logItem.getString(null));
        for (LogListener ll : logListener) {
            ll.newLog(logItem);
        }
    }

    public static void logError(String msg) {
        newLogItem(new LogItem(LogLevel.ERROR, msg));
    }

    public static void logWarning(int resourceId, Object... args) {
        newLogItem(new LogItem(LogLevel.WARNING, resourceId, args));
    }

    public static void logWarning(String msg) {
        newLogItem(new LogItem(LogLevel.WARNING, msg));
    }

    public static void logError(int resourceId) {
        newLogItem(new LogItem(LogLevel.ERROR, resourceId));
    }

    public static void logError(int resourceId, Object... args) {
        newLogItem(new LogItem(LogLevel.ERROR, resourceId, args));
    }

    public static void logMessageOpenVPN(LogLevel level, int ovpnlevel, String message) {
        newLogItem(new LogItem(level, ovpnlevel, message));
    }
	
	public enum LogLevel {
        INFO(2), ERROR(-2), WARNING(1), VERBOSE(3), DEBUG(4);
        protected int mValue;

        LogLevel(int value) {
            mValue = value;
        }

        public static LogLevel getEnumByValue(int value) {
            switch (value) {
                case 2:
                    return INFO;
                case -2:
                    return ERROR;
                case 1:
                    return WARNING;
                case 3:
                    return VERBOSE;
                case 4:
                    return DEBUG;
                default:
                    return null;
            }
        }

        public int getInt() {
            return mValue;
        }
    }

    public interface LogListener {
        void newLog(LogItem logItem);
    }
}
