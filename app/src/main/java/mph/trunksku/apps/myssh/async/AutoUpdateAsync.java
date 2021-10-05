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

public class AutoUpdateAsync extends AsyncTask<String, String, String> {
    private static final String TAG = "NetGuard.Download";

    private Context context;

    private Listener listener;
    private PowerManager.WakeLock wakeLock;

	private HttpURLConnection uRLConnection;

	private InputStream is;

	private BufferedReader buffer;

	private SharedPreferences defsp;

    public interface Listener {
        void onCompleted(String config);

        void onCancelled();

        void onException(String ex);
    }

    public AutoUpdateAsync(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
		defsp = ApplicationBase.getDefSharedPreferences();
    }

    @Override
    protected void onPreExecute() {
	}

    @Override
    protected String doInBackground(String... args) {
		try {
			String api = defsp.getString("custom_update_url", "");
			if(!api.startsWith("http")){
				api = new StringBuilder().append("http://").append(defsp.getString("custom_update_url", "")).toString();
			}
            URL oracle = new URL(api);
			HttpClient Client = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(oracle.toURI());
			HttpResponse response = Client.execute(httpget);
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
			{
				str.append(line);
			}
			in.close();
            return str.toString();
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
        listener.onCancelled();
    }

    @Override
    protected void onPostExecute(String result) {
		// wakeLock.release();
        //nm.cancel(1);
        if (result.equals("error")) {
            listener.onException(result);
        } else
            listener.onCompleted(result);
    }

}

