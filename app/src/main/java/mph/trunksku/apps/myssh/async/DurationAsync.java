package mph.trunksku.apps.myssh.async;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.net.*;
import mph.trunksku.apps.myssh.*;
import org.apache.http.client.methods.*;
import org.apache.http.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.*;

public class DurationAsync extends AsyncTask<String, String, String> {
    private static final String TAG = "NetGuard.Download";

    private Context context;

    private Listener listener;
    private PowerManager.WakeLock wakeLock;

	private HttpURLConnection uRLConnection;

	private InputStream is;

	private BufferedReader buffer;

	private SharedPreferences defsp;

	private ProgressDialog pd;

	private String api;

    public interface Listener {
        void onCompleted(String config);

        void onCancelled();

        void onException(String ex);
    }

    public DurationAsync(Context context, String api, Listener listener) {
        this.context = context;
		this.api = api;
        this.listener = listener;
		defsp = ApplicationBase.getDefSharedPreferences();
    }

    @Override
    protected void onPreExecute() {
		pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();
    }

    @Override
    protected String doInBackground(String... args) {
		try {
			
            URL mURL = new URL(api);
			HttpURLConnection con = (HttpURLConnection)mURL.openConnection();
			con.connect();

			StringBuilder sb = new StringBuilder();
			char[] buf = new char[1024];
			Reader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while (true) {
				int read = reader.read(buf);
				if (read <= 0) {
					break;
				}
				sb.append(buf, 0, read);
			}
			return sb.toString();
            //return str.toString();
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
    protected void onCancelled() {
        super.onCancelled();
        Log.i(TAG, "Cancelled");
		pd.dismiss();
        listener.onCancelled();
    }

    @Override
    protected void onPostExecute(String result) {
		// wakeLock.release();
        //nm.cancel(1);
		pd.dismiss();
        if (result.equals("error")) {
            listener.onException(result);
        } else
            listener.onCompleted(result);
    }

}

