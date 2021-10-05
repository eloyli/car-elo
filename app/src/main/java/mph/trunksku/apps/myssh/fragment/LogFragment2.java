package mph.trunksku.apps.myssh.fragment;

import android.app.*;
import android.content.*;
import android.database.*;
import android.graphics.drawable.*;
import android.os.*;
import android.os.Handler.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.logger.*;

public class LogFragment2 extends Fragment
{
    private static final String LOGTIMEFORMAT = "logtimeformat";
    private static final String VERBOSITYLEVEL = "verbositylevel";
    private static LogWindowListAdapter ladapter;

    class LogWindowListAdapter implements ListAdapter, VPNLog.LogListener, Callback {

        private static final int MESSAGE_NEWLOG = 0;

        private static final int MESSAGE_CLEARLOG = 1;

        private static final int MESSAGE_NEWTS = 2;
        private static final int MESSAGE_NEWLOGLEVEL = 3;

        public static final int TIME_FORMAT_NONE = 0;
        public static final int TIME_FORMAT_SHORT = 1;
        public static final int TIME_FORMAT_ISO = 2;
        private static final int MAX_STORED_LOG_ENTRIES = 1000;

        private Vector<LogItem> allEntries = new Vector<>();

        private Vector<LogItem> currentLevelEntries = new Vector<LogItem>();

        private Handler mHandler;

        private Vector<DataSetObserver> observers = new Vector<DataSetObserver>();

        private int mTimeFormat = 0;
        private int mLogLevel = 3;


        public LogWindowListAdapter() {
            initLogBuffer();
            if (mHandler == null) {
                mHandler = new Handler(this);
            }

            VPNLog.addLogListener(this);
        }


        private void initLogBuffer() {
            allEntries.clear();
            Collections.addAll(allEntries, VPNLog.getlogbuffer());
            initCurrentMessages();
        }

        String getLogStr() {
            String str = "";
            for (LogItem entry : allEntries) {
                str += getTime(entry, TIME_FORMAT_ISO) + entry.getString(getActivity()) + '\n';
            }
            return str;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            observers.add(observer);

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            observers.remove(observer);
        }

        @Override
        public int getCount() {
            return currentLevelEntries.size();
        }

        @Override
        public Object getItem(int position) {
            return currentLevelEntries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ((Object) currentLevelEntries.get(position)).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v;
            if (convertView == null)
                v = new TextView(getActivity());
            else
                v = (TextView) convertView;

            LogItem le = currentLevelEntries.get(position);
            String msg = le.getString(getActivity());
            String time = getTime(le, mTimeFormat);
            msg = time + msg;

            int spanStart = time.length();

            SpannableString t = new SpannableString(msg);

            //t.setSpan(getSpanImage(le,(int)v.getTextSize()),spanStart,spanStart+1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            v.setText(t);
            return v;
        }

        private String getTime(LogItem le, int time) {
            if (time != TIME_FORMAT_NONE) {
                Date d = new Date(le.getLogtime());
                java.text.DateFormat timeformat;
                if (time == TIME_FORMAT_ISO)
                    timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                else
                    timeformat = android.text.format.DateFormat.getTimeFormat(getActivity());

                return timeformat.format(d) + " ";

            } else {
                return "";
            }

        }

        private ImageSpan getSpanImage(LogItem li, int imageSize) {
            int imageRes = android.R.drawable.ic_menu_call;

            switch (li.getLogLevel()) {
                case ERROR:
                    imageRes = android.R.drawable.ic_notification_clear_all;
                    break;
                case INFO:
                    imageRes = android.R.drawable.ic_menu_compass;
                    break;
                case VERBOSE:
                    imageRes = android.R.drawable.ic_menu_info_details;
                    break;
                case WARNING:
                    imageRes = android.R.drawable.ic_menu_camera;
                    break;
            }

            Drawable d = getResources().getDrawable(imageRes);


            //d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            d.setBounds(0, 0, imageSize, imageSize);
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);

            return span;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return currentLevelEntries.isEmpty();

        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void newLog(LogItem logMessage) {
            Message msg = Message.obtain();
            assert (msg != null);
            msg.what = MESSAGE_NEWLOG;
            Bundle bundle = new Bundle();
            bundle.putParcelable("logmessage", logMessage);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        @Override
        public boolean handleMessage(Message msg) {
            // We have been called
            if (msg.what == MESSAGE_NEWLOG) {

                LogItem logMessage = msg.getData().getParcelable("logmessage");
                if (addLogMessage(logMessage))
                    for (DataSetObserver observer : observers) {
                        observer.onChanged();
                    }
            } else if (msg.what == MESSAGE_CLEARLOG) {
                for (DataSetObserver observer : observers) {
                    observer.onInvalidated();
                }
                initLogBuffer();
            } else if (msg.what == MESSAGE_NEWTS) {
                for (DataSetObserver observer : observers) {
                    observer.onInvalidated();
                }
            } else if (msg.what == MESSAGE_NEWLOGLEVEL) {
                initCurrentMessages();

                for (DataSetObserver observer : observers) {
                    observer.onChanged();
                }

            }

            return true;
        }

        private void initCurrentMessages() {
            currentLevelEntries.clear();
            for (LogItem li : allEntries) {
                if (li.getVerbosityLevel() <= mLogLevel ||
					mLogLevel == 4)
                    currentLevelEntries.add(li);
            }
        }

        /**
         * @param logmessage
         * @return True if the current entries have changed
         */
        private boolean addLogMessage(LogItem logmessage) {
            allEntries.add(logmessage);

            if (allEntries.size() > MAX_STORED_LOG_ENTRIES) {
                Vector<LogItem> oldAllEntries = allEntries;
                allEntries = new Vector<LogItem>(allEntries.size());
                for (int i = 50; i < oldAllEntries.size(); i++) {
                    allEntries.add(oldAllEntries.elementAt(i));
                }
                initCurrentMessages();
                return true;
            } else {
                if (logmessage.getVerbosityLevel() <= mLogLevel) {
                    currentLevelEntries.add(logmessage);
                    return true;
                } else {
                    return false;
                }
            }
        }

        public void clearLog() {
            // Actually is probably called from GUI Thread as result of the user
            // pressing a button. But better safe than sorry
            VPNLog.clearLog();
            VPNLog.logInfo("Log Cleared.");
            mHandler.sendEmptyMessage(MESSAGE_CLEARLOG);
        }


        public void setTimeFormat(int newTimeFormat) {
            mTimeFormat = newTimeFormat;
            mHandler.sendEmptyMessage(MESSAGE_NEWTS);
        }

        public void setLogLevel(int logLevel) {
            mLogLevel = logLevel;
            mHandler.sendEmptyMessage(MESSAGE_NEWLOGLEVEL);
        }

    }

	public static void clear() {
		ladapter.clearLog();
	}

	@Override
    public void onResume() {
        super.onResume();
       /* Intent intent = new Intent(getActivity(), OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);*/
    }
	

    @Override
    public void onStop()
	{
        super.onStop();
        getActivity().getPreferences(0).edit().putInt(LOGTIMEFORMAT, this.ladapter.mTimeFormat).putInt(VERBOSITYLEVEL, this.ladapter.mLogLevel).apply();
    }

    @Override
    public void onActivityCreated(Bundle bundle)
	{
        super.onActivityCreated(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle)
	{
        View inflate = layoutInflater.inflate(R.layout.fragment_log, viewGroup, false);
        setHasOptionsMenu(true);
        ListView listView = (ListView) inflate.findViewById(R.id.logView);
        ladapter = new LogWindowListAdapter();
        ladapter.mTimeFormat = getActivity().getPreferences(0).getInt(LOGTIMEFORMAT, 1);
        ladapter.setLogLevel(2);
        listView.setAdapter(ladapter);
		((FloatingActionButton) inflate.findViewById(R.id.clearLogs)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					ladapter.clearLog();
				}
			});
        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle)
	{
        super.onViewCreated(view, bundle);
    }

    @Override
    public void onAttach(Context context)
	{
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle bundle)
	{
        super.onCreate(bundle);
    }

    @Override
    public void onDestroy()
	{
        VPNLog.removeLogListener(this.ladapter);
        super.onDestroy();
    }
}

